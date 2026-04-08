# Proje Yapısı

Bu doküman, issue-tracker-microservices monorepo'sunun tüm bileşenlerini, klasör yapılarını ve yapılandırma dosyalarını açıklar.

---

## Genel Bakış

```
issue-tracker-microservices/
├── app-domain-model/          # Ortak domain: entity, DTO, enum
├── app-client-management/     # Feign clients + fallback handler'ları
├── discovery-service/         # Eureka Server (:8761)
├── config-server/             # Spring Cloud Config Server (:8888)
├── gateway-service/           # API Gateway (:8762)
├── user-service/              # Kullanıcı & Auth servisi (:9093)
├── project-service/           # Proje servisi (:9091)
├── task-service/              # Görev servisi (:9092)
├── config-repo/               # Merkezi konfigürasyon dosyaları
├── docker-compose/            # Docker Compose + Grafana provisioning
├── ui/                        # Next.js 15 frontend (:3000)
├── pom.xml                    # Root Maven POM (multi-module)
├── Dockerfile                 # Parametrik multi-stage Dockerfile
├── README.md
├── CLAUDE.md
├── PROJECT_STRUCTURE.md       # ← Bu dosya
└── MICROSERVICES.md
```

---

## Port Tablosu

| Servis | Port |
|---|---|
| Eureka (discovery-service) | 8761 |
| Config Server | 8888 |
| API Gateway | 8762 |
| user-service | 9093 |
| project-service | 9091 |
| task-service | 9092 |
| Zipkin | 9411 |
| Prometheus | 9090 |
| Grafana | 3000 |
| PostgreSQL | 5432 |
| Next.js UI | 3000 (geliştirme) |

---

## Backend Modülleri

### `app-domain-model` — Ortak Domain Kütüphanesi

```
app-domain-model/src/main/java/com/iamhusrev/
├── dto/
│   ├── UserDTO.java
│   ├── ProjectDTO.java
│   ├── TaskDTO.java
│   └── RoleDTO.java
├── entity/
│   ├── BaseEntity.java        # id, audit alanları, is_deleted (soft delete)
│   ├── User.java
│   ├── Project.java
│   ├── Task.java
│   ├── Role.java
│   └── ResponseWrapper.java   # Standart API yanıt sarmalayıcı
└── enums/
    ├── Status.java             # OPEN | IN_PROGRESS | UAT_TEST | COMPLETE
    └── Gender.java             # MALE | FEMALE
```

**Bağımlılık:** Tüm business service'ler bu modüle bağımlıdır.

---

### `app-client-management` — Feign Client Kütüphanesi

```
app-client-management/src/main/java/com/iamhusrev/service/
├── UserClientService.java      # @FeignClient → user-service /user
├── ProjectClientService.java   # @FeignClient → project-service /project
├── TaskClientService.java      # @FeignClient → task-service /task
├── UserClientFallback.java     # Circuit breaker fallback
├── ProjectClientFallback.java
└── TaskClientFallback.java
```

**Bağımlılık:** project-service ve task-service bu modülü kullanarak diğer servislere çağrı yapar.

---

### `discovery-service` — Eureka Server

```
discovery-service/src/main/java/com/iamhusrev/
└── DiscoveryServiceApplication.java

discovery-service/src/main/resources/
└── application.yml    # register-with-eureka: false, fetch-registry: false
```

---

### `config-server` — Spring Cloud Config Server

```
config-server/src/main/java/com/iamhusrev/
└── ConfigServerApplication.java

config-server/src/main/resources/
└── application.yml    # profiles.active: native, search-locations: file:./../config-repo
```

Config dosyalarını `config-repo/` klasöründen okur.

---

### `gateway-service` — API Gateway

```
gateway-service/src/main/java/com/iamhusrev/
└── GatewayServiceApplication.java

gateway-service/src/main/resources/
└── application.yml    # spring.config.import: config-server
```

Routing konfigürasyonu `config-repo/gateway-service.yml` içindedir.

**Rotalar:**

| Gelen Yol | Hedef | Yeniden Yazma |
|---|---|---|
| `/iamhusrev/dev/auth/**` | user-service | `/auth/{segment}` |
| `/iamhusrev/dev/user/**` | user-service | `/user/{segment}` |
| `/iamhusrev/dev/project/**` | project-service | `/project/{segment}` |
| `/iamhusrev/dev/task/**` | task-service | `/task/{segment}` |

---

### `user-service` — Kullanıcı & Auth Servisi (:9093)

```
user-service/src/main/java/com/iamhusrev/
├── UserServiceApplication.java
├── controller/
│   ├── UserController.java         # GET/POST/PUT/DELETE /user/
│   ├── AuthController.java         # POST /auth/login, /auth/refresh
│   └── UserFallbackHandler.java
├── service/
│   ├── UserService.java
│   └── RoleService.java
├── repository/
│   ├── UserRepository.java
│   └── RoleRepository.java
├── security/
│   ├── SecurityConfig.java         # Spring Security + CORS
│   ├── JwtUtil.java                # Token üretme & doğrulama
│   ├── JwtAuthFilter.java          # USER_TOKEN header okuma
│   └── UserDetailsServiceImpl.java
├── dto/
│   ├── AuthRequest.java
│   ├── AuthResponse.java
│   └── RefreshRequest.java
├── exception/
│   └── UserServiceException.java
└── util/
    └── MapperUtil.java

user-service/src/main/resources/
├── application.yml    # app name, port, config import
└── data.sql           # Rol seed verisi (Admin, Manager, Employee)
```

---

### `project-service` — Proje Servisi (:9091)

```
project-service/src/main/java/com/iamhusrev/
├── ProjectServiceApplication.java
├── controller/
│   ├── ProjectController.java      # GET/POST/PUT/DELETE /project/
│   └── ProjectFallbackHandler.java
├── service/
│   └── ProjectService.java
├── repository/
│   └── ProjectRepository.java
├── exception/
│   └── ProjectServiceException.java
└── util/
    └── MapperUtil.java
```

Yönetici bilgisi için `UserClientService` (Feign) kullanır.

---

### `task-service` — Görev Servisi (:9092)

```
task-service/src/main/java/com/iamhusrev/
├── TaskServiceApplication.java
├── controller/
│   ├── TaskController.java         # GET/POST/PUT/DELETE /task/
│   └── TaskFallbackHandler.java
├── service/
│   └── TaskService.java
├── repository/
│   └── TaskRepository.java
└── util/
    └── MapperUtil.java
```

Çalışan ve proje bilgisi için `UserClientService` + `ProjectClientService` kullanır.

---

## `config-repo/` — Merkezi Konfigürasyon

```
config-repo/
├── application.yml          # Tüm servisler: JPA, Eureka, Resilience4j, Zipkin varsayılanları
├── user-service.yml         # DB bağlantısı, JWT secret/expiration, Feign
├── project-service.yml      # DB bağlantısı, Feign
├── task-service.yml         # DB bağlantısı, Feign
├── gateway-service.yml      # Route tanımları
└── discovery-service.yml    # Eureka sunucu ayarları
```

Servislerin `application.yml` dosyaları yalnızca `spring.application.name`, `server.port` ve `spring.config.import` içerir; geri kalan tüm konfigürasyonu config-server'dan çekerler.

---

## `docker-compose/` — Docker Ortamı

```
docker-compose/
├── services.yml             # Ana Compose dosyası (tüm servisler)
├── .env                     # Port ve credential değişkenleri
└── grafana/
    └── provisioning/
        └── datasources/     # Prometheus datasource auto-provisioning
```

**Başlatma sırası (depends_on):**
1. `postgres` + `zipkin`
2. `discovery-service`
3. `config-server` (discovery sağlıklı olunca)
4. `user-service`, `project-service`, `task-service` (config-server sağlıklı + DB sağlıklı olunca)
5. `gateway-service` (config-server sağlıklı + discovery ayakta olunca)
6. `prometheus`, `grafana`

---

## `ui/` — Next.js 15 Frontend

```
ui/
├── src/
│   ├── app/
│   │   ├── layout.tsx                   # Root layout: Provider hiyerarşisi
│   │   ├── page.tsx                     # / → /dashboard yönlendirme
│   │   ├── (auth)/login/page.tsx        # Giriş sayfası
│   │   └── (admin)/                     # Korumalı sayfalar (auth guard)
│   │       ├── layout.tsx               # Token kontrolü → /login
│   │       ├── dashboard/page.tsx
│   │       ├── users/page.tsx
│   │       ├── projects/
│   │       │   ├── page.tsx
│   │       │   └── [code]/page.tsx      # Proje detayı
│   │       ├── tasks/page.tsx
│   │       ├── manager/projects/page.tsx
│   │       └── employee/
│   │           ├── my-tasks/page.tsx
│   │           └── archive/page.tsx
│   ├── components/ui/
│   │   ├── Button.tsx
│   │   ├── Input.tsx
│   │   ├── Select.tsx
│   │   ├── Modal.tsx
│   │   └── Badge.tsx                    # StatusBadge
│   ├── hooks/
│   │   ├── useUsers.ts                  # TanStack Query hooks
│   │   ├── useProjects.ts
│   │   └── useTasks.ts
│   ├── layout/
│   │   ├── AppSidebar.tsx
│   │   └── AppHeader.tsx
│   ├── messages/
│   │   ├── tr.json                      # Türkçe (varsayılan)
│   │   └── en.json
│   ├── provider/
│   │   ├── ReactQueryProvider.tsx       # staleTime:60s, gcTime:300s
│   │   └── SidebarProvider.tsx
│   ├── services/
│   │   ├── api-client.ts               # Axios + USER_TOKEN header + 401 refresh
│   │   ├── auth-service.ts
│   │   ├── user-service.ts
│   │   ├── project-service.ts
│   │   └── task-service.ts
│   ├── store/
│   │   └── auth-store.ts               # Zustand: token + userName (localStorage)
│   ├── types/
│   │   ├── index.ts
│   │   ├── common.ts                   # ApiResponse<T>, Status, Gender
│   │   ├── auth.ts
│   │   ├── user.ts
│   │   ├── project.ts
│   │   └── task.ts
│   ├── utils/
│   │   ├── api-endpoints.ts            # Tüm endpoint sabitleri
│   │   └── i18n.ts                     # i18next başlatma
│   └── views/
│       ├── dashboard/DashboardView.tsx  # KPI + ApexCharts
│       ├── users/UsersView.tsx
│       ├── projects/ProjectsView.tsx
│       ├── tasks/TasksView.tsx
│       ├── manager/ManagerProjectsView.tsx
│       └── employee/
│           ├── MyTasksView.tsx
│           └── ArchiveView.tsx
├── .env.local                          # NEXT_PUBLIC_API_URL
└── package.json
```

### Provider Hiyerarşisi

```
ReactQueryProvider
  └── SidebarProvider
        └── (admin layout: AuthProvider logic)
              └── sayfa içerikleri
```

### Ortam Değişkenleri (UI)

| Değişken | Varsayılan | Açıklama |
|---|---|---|
| `NEXT_PUBLIC_API_URL` | `http://localhost:8762/iamhusrev/dev` | Gateway base URL |

---

## Environment Variables (Backend)

| Değişken | Varsayılan | Kullanan Servisler |
|---|---|---|
| `DB_HOST` | `localhost` | user, project, task |
| `DB_PORT` | `5432` | user, project, task |
| `DB_USERNAME` | `postgres` | user, project, task |
| `DB_PASSWORD` | `postgres` | user, project, task |
| `JWT_SECRET` | `change-me-in-production-32-chars!!` | user-service |
| `CONFIG_SERVER_HOST` | `localhost` | tüm servisler |
| `EUREKA_HOST` | `localhost` | tüm servisler |
| `ZIPKIN_HOST` | `localhost` | tüm servisler |
| `SERVER_PORT` | servis varsayılanı | tüm servisler |
