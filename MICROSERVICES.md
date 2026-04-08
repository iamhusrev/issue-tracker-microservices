# Microservices Mimarisi

Bu doküman, microservices kavramlarını ve bu projedeki uygulamalarını Türkçe olarak açıklar.

---

## 1. Microservis Nedir?

Geleneksel **monolitik** mimaride tüm özellikler tek bir uygulamada birleştirilir. Tek bir değişiklik tüm uygulamanın yeniden deploy edilmesini gerektirir; herhangi bir bileşenin çökmesi tüm sistemi etkiler.

**Microservices** mimarisinde uygulama, her biri:
- **Tek bir sorumluluğa** sahip olan,
- **Bağımsız olarak deploy edilebilen**,
- **Kendi veritabanını** yöneten,
- **HTTP/mesaj kuyruğu üzerinden iletişim kuran**

küçük servislere bölünür.

| | Monolith | Microservices |
|---|---|---|
| Deploy | Tek seferinde tümü | Her servis bağımsız |
| Ölçeklendirme | Tümü birden | Sadece yoğun servis |
| Hata izolasyonu | Tek hata tümünü çökertir | Diğer servisler çalışmaya devam eder |
| Takım yapısı | Tek büyük ekip | Küçük, domain odaklı ekipler |
| Teknoloji seçimi | Tek yığın | Servis başına farklı teknoloji |

---

## 2. Bu Projedeki Microservis Desenleri

### 2.1 API Gateway

**Tanım:** Dış dünyaya tek bir giriş noktası. Tüm istemci istekleri buradan geçer; gateway doğru servise yönlendirir.

**Bu Projedeki Uygulama:**
- Bileşen: `gateway-service` (Spring Cloud Gateway, :8762)
- İstemci yalnızca `localhost:8762` ile konuşur; arka taraftaki servis adreslerini bilmek zorunda değildir.
- Yol yeniden yazma (RewritePath) ile temiz URL'ler:

```
İstemci isteği:   GET  /iamhusrev/dev/user/john
Gateway yazar:    GET  /user/john  →  user-service:9093
```

---

### 2.2 Service Discovery

**Tanım:** Servislerin birbirinin adresini dinamik olarak bulması. Servisler sabit IP/port yerine isimle çağrılır.

**Bu Projedeki Uygulama:**
- Bileşen: `discovery-service` (Netflix Eureka, :8761)
- Her servis başladığında Eureka'ya kaydolur.
- Gateway ve Feign client'lar `lb://user-service` gibi servis adıyla çağrı yapar; Eureka adres döner.
- Birden fazla instance varsa load balancing otomatik çalışır.

```
project-service  →  Eureka'ya sor: "user-service nerede?"
Eureka           →  "user-service: 10.0.0.5:9093"
project-service  →  10.0.0.5:9093'e istek gönder
```

---

### 2.3 Centralized Configuration

**Tanım:** Tüm servislerin konfigürasyonunu tek bir yerden yönetme. Konfigürasyon değiştiğinde servisi yeniden build etmeye gerek yok.

**Bu Projedeki Uygulama:**
- Bileşen: `config-server` (Spring Cloud Config, :8888)
- Konfigürasyonlar: `config-repo/` klasöründe YAML dosyaları
- Her servis başlarken `spring.config.import: http://config-server:8888` üzerinden kendi konfigürasyonunu çeker.

```
config-repo/
├── application.yml        ← tüm servislerin paylaştığı konfigürasyon
├── user-service.yml       ← user-service'e özel
├── project-service.yml    ← project-service'e özel
└── gateway-service.yml    ← gateway route'ları
```

---

### 2.4 Inter-Service Communication (Feign)

**Tanım:** Servislerin birbirine HTTP üzerinden çağrı yapması.

**Bu Projedeki Uygulama:**
- Kütüphane: `app-client-management` (OpenFeign)
- `project-service` ve `task-service`, `user-service`'e kullanıcı bilgisi sormak için Feign kullanır.

```java
// project-service, bir kullanıcının projelerini listelemek için user-service'e sorar
UserResponseDTO user = userClientService.getUserByUserName(userName);
```

Feign, HTTP isteğini otomatik olarak HTTP çağrısına dönüştürür; geliştirici sanki yerel bir Java metodu çağırıyor gibi yazar.

---

### 2.5 Circuit Breaker (Devre Kesici)

**Tanım:** Bir servis yanıt vermediğinde veya hata oranı yükseldiğinde otomatik olarak "devre kesen" ve fallback yanıt dönen mekanizma.

**Bu Projedeki Uygulama:**
- Kütüphane: Resilience4j
- Konfigürasyon (`config-repo/application.yml`):

```yaml
resilience4j:
  circuitbreaker:
    configs:
      default:
        slidingWindowSize: 10           # Son 10 isteğe bak
        minimumNumberOfCalls: 5         # En az 5 istek olmadan açılmaz
        failureRateThreshold: 50        # %50 hata oranında devre açılır
        waitDurationInOpenState: 10s    # 10 saniye bekle, sonra tekrar dene
        permittedNumberOfCallsInHalfOpenState: 3
```

**Devre Durumları:**
```
CLOSED (normal) → hata oranı %50'yi geçerse → OPEN (fallback döner)
OPEN → 10 saniye sonra → HALF-OPEN (3 deneme)
HALF-OPEN → başarılı → CLOSED | başarısız → OPEN
```

Her controller metodu `@CircuitBreaker` annotasyonu taşır ve bir fallback metodu işaret eder:

```java
@GetMapping
@CircuitBreaker(name = "user-service", fallbackMethod = "getUsersFallback")
public ResponseEntity<ResponseWrapper> getUsers() { ... }

public ResponseEntity<ResponseWrapper> getUsersFallback(Throwable t) {
    return fallbackHandler.handleListFallback(t);  // Boş liste döner
}
```

---

### 2.6 Retry (Yeniden Deneme)

**Tanım:** Geçici ağ hatalarında isteği otomatik olarak tekrar gönderme.

**Bu Projedeki Uygulama:**

```yaml
resilience4j:
  retry:
    configs:
      default:
        maxAttempts: 3        # En fazla 3 deneme
        waitDuration: 500ms   # Denemeler arası 500ms bekle
```

---

### 2.7 Timeout (Zaman Aşımı)

**Tanım:** Bir servis çok uzun süre yanıt vermezse isteği iptal etme.

**Bu Projedeki Uygulama:**

```yaml
resilience4j:
  timelimiter:
    configs:
      default:
        timeoutDuration: 3s   # 3 saniyede yanıt gelmezse hata fırlat
```

---

### 2.8 Shared Library (Ortak Kütüphane)

**Tanım:** Birden fazla servisin kullandığı ortak kod/model kütüphanesi.

**Bu Projedeki Uygulama:**
- Modül: `app-domain-model`
- İçerik: JPA entity'leri, DTO'lar, enum'lar, `ResponseWrapper`
- Tüm business service'ler bu Maven modülüne bağımlıdır.

> **Not:** Ortak kütüphane kolaylık sağlar ancak microservices bağımsızlığını bir miktar azaltır. Gerçek bağımsızlık için her servis kendi modelini tanımlamalıdır. Bu proje, öğrenme amaçlı paylaşımlı model kullanmaktadır.

---

### 2.9 Distributed Tracing (Dağıtık İzleme)

**Tanım:** Birden fazla servise yayılan bir isteğin tüm adımlarını tek bir `traceId` ile takip etme.

**Bu Projedeki Uygulama:**
- Bileşen: Zipkin (:9411)
- Kütüphane: `micrometer-tracing-bridge-brave`
- Örnekleme: %100 (`sampling.probability: 1.0`)

Bir istek `gateway → project-service → user-service` üzerinden geçtiğinde, Zipkin'de üç span tek bir trace olarak görünür; gecikme hangi servisten kaynaklandığı anlaşılır.

---

### 2.10 Metrics & Monitoring (Metrik & İzleme)

**Tanım:** Servislerin sağlık durumunu, yanıt sürelerini ve hata oranlarını merkezi olarak izleme.

**Bu Projedeki Uygulama:**
- **Prometheus** (:9090): Her servisin `/actuator/prometheus` endpoint'inden metrik toplar.
- **Grafana** (:3000): Prometheus verilerini görselleştirir. Datasource otomatik provisionlanır.
- Her servisin tüm actuator endpoint'leri açıktır: `management.endpoints.web.exposure.include: "*"`

---

## 3. İstek Akış Diyagramı

```
┌──────────┐
│  Tarayıcı│
└────┬─────┘
     │ HTTP GET /iamhusrev/dev/project/details/john
     ▼
┌────────────────┐
│ gateway-service│  RewritePath → /project/details/john
│    :8762       │
└────┬───────────┘
     │ lb://project-service (Eureka'ya sor)
     ▼
┌────────────────────┐
│  project-service   │  ProjectService.listAllProjectDetails("john")
│     :9091          │
└────┬───────────────┘
     │ Feign: GET /user/john → lb://user-service
     ▼
┌────────────────┐
│  user-service  │  UserController.getUserByUserName("john")
│    :9093       │
└────────────────┘
     │ UserDTO {"firstName":"John",...}
     ▼
┌────────────────────┐
│  project-service   │  Kullanıcı bilgisini projelere ekler
└────────────────────┘
     │ List<ProjectDTO>
     ▼
┌──────────────────────┐
│    Tarayıcı / UI     │  ResponseWrapper{data: [...], success: true}
└──────────────────────┘
```

---

## 4. Config Yönetimi Akışı

```
Servis Başlar
     │
     ▼
spring.config.import: http://config-server:8888
     │
     ▼
Config Server ← config-repo/{servis-adı}.yml okur
     │
     ▼
Konfigürasyon servisin belleğine yüklenir
(DB bağlantısı, JWT secret, Feign timeout, vb.)
     │
     ▼
Eureka'ya kayıt: "Ben user-service, adresim 10.0.0.5:9093"
     │
     ▼
Servis istekleri almaya hazır
```

---

## 5. Resilience4j + Feign: Hata Senaryosu

```
project-service → [Feign] → user-service
                                │
                         user-service DOWN
                                │
                         Retry: 3 deneme × 500ms
                                │
                         Hata oranı %50'yi geçti
                                │
                         Circuit OPEN (10s)
                                │
                         Fallback çalışır:
                         UserClientFallback.getUserByUserName()
                         → null / boş yanıt döner
                                │
                         project-service null'ı handle eder,
                         kısmi veriyle yanıt döner
```

Bu sayede `user-service` çöktüğünde `project-service` de çökmez; sistem **bölünmüş hata toleransı (partial failure)** ile çalışmaya devam eder.

---

## 6. Proje Özetindeki Desen Haritası

| Microservis Deseni | Bileşen | Teknoloji |
|---|---|---|
| API Gateway | `gateway-service` | Spring Cloud Gateway |
| Service Discovery | `discovery-service` | Netflix Eureka |
| Centralized Config | `config-server` + `config-repo/` | Spring Cloud Config |
| Synchronous Communication | `app-client-management` | OpenFeign |
| Circuit Breaker | tüm controller'lar | Resilience4j |
| Retry & Timeout | tüm Feign çağrıları | Resilience4j |
| Shared Domain Model | `app-domain-model` | Maven modülü |
| Distributed Tracing | tüm servisler | Zipkin + Micrometer |
| Metrics Collection | tüm servisler | Prometheus + Grafana |
| Security & Auth | `user-service` | Spring Security + JWT |
