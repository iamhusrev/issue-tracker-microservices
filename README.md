# Issue Tracker Microservices

A Spring Boot 3.2.5 microservices backend for an issue / task tracking system, with a Next.js 15 frontend in `ui/`.

## Architecture

```
                    ┌─────────────────────┐
  Browser / UI ───► │   Gateway  :8762    │
                    └──────────┬──────────┘
                               │  lb://
           ┌───────────────────┼───────────────────┐
           ▼                   ▼                   ▼
   ┌──────────────┐   ┌──────────────┐   ┌──────────────┐
   │ user-service │   │project-svc   │   │  task-svc    │
   │    :9093     │   │   :9091      │   │   :9092      │
   └──────┬───────┘   └──────┬───────┘   └──────┬───────┘
          │                  │ Feign             │ Feign
          └──────────────────┴───────────────────┘
                               │
                         ┌─────▼─────┐
                         │ PostgreSQL │
                         │   :5432   │
                         └───────────┘

  Config Server :8888  ◄── all services fetch config
  Eureka         :8761  ◄── all services register
  Zipkin         :9411  ◄── distributed tracing
  Prometheus     :9090  ◄── metrics scrape
  Grafana        :3000  ◄── dashboards
```

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.2.5, Spring Cloud 2023.0.1 |
| Auth | Spring Security + JWT (jjwt 0.12.6) |
| Service Discovery | Eureka |
| API Gateway | Spring Cloud Gateway |
| Config | Spring Cloud Config Server (native) |
| Resilience | Resilience4j (circuit breaker, retry, timeout) |
| Tracing | Zipkin (micrometer-tracing-bridge-brave) |
| Metrics | Prometheus + Grafana |
| Database | PostgreSQL 16 |
| Build | Maven 3.9 (multi-module) |
| Frontend | Next.js 15 + TypeScript (see `ui/`) |

## Quickstart (Docker)

```bash
docker-compose -f docker-compose/services.yml up --build
```

| Service | URL |
|---|---|
| API Gateway | http://localhost:8762 |
| Eureka | http://localhost:8761 |
| Zipkin | http://localhost:9411 |
| Grafana | http://localhost:3000 (admin/admin) |
| UI | http://localhost:3000 (via `cd ui && npm run dev`) |

## API Base URL

All client requests go through the gateway:

```
http://localhost:8762/iamhusrev/dev/{service}/{path}
```

| Prefix | Routes to |
|---|---|
| `/iamhusrev/dev/auth/` | user-service `/api/v1/auth/` |
| `/iamhusrev/dev/user/` | user-service `/api/v1/user/` |
| `/iamhusrev/dev/project/` | project-service `/api/v1/project/` |
| `/iamhusrev/dev/task/` | task-service `/api/v1/task/` |

### Auth

```
POST /iamhusrev/dev/auth/login    Body: { userName, passWord }  → { accessToken, refreshToken }
POST /iamhusrev/dev/auth/refresh  Body: { refreshToken }        → { accessToken, refreshToken }
```

All protected endpoints require header: `USER_TOKEN: <accessToken>`

## Local Development

```bash
# 1. Start infrastructure (Postgres, Eureka, Config Server)
docker-compose -f docker-compose/services.yml up -d postgres discovery-service config-server

# 2. Run services individually (in order)
mvn -pl user-service    -am spring-boot:run
mvn -pl project-service -am spring-boot:run
mvn -pl task-service    -am spring-boot:run
mvn -pl gateway-service -am spring-boot:run

# 3. Start UI
cd ui && npm run dev
```

## Environment Variables

| Variable | Default | Used by |
|---|---|---|
| `DB_HOST` | `localhost` | All business services |
| `DB_PORT` | `5432` | All business services |
| `DB_USERNAME` | `postgres` | All business services |
| `DB_PASSWORD` | `postgres` | All business services |
| `JWT_SECRET` | `change-me-in-production-32-chars!!` | user-service |
| `CONFIG_SERVER_HOST` | `localhost` | All services |
| `EUREKA_HOST` | `localhost` | All services |
| `ZIPKIN_HOST` | `localhost` | All services |
