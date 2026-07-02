# Issue Tracker Microservices

A production-ready issue tracking system built with **Spring Boot 3.2.5** and **Spring Cloud 2023.0.1**, demonstrating modern microservices architecture patterns. Clone, `docker compose up`, log in — every piece (service discovery, config server, gateway, circuit breaker, message broker, tracing, metrics) runs out of the box.

**Companion UI:** [`iamhusrev/issue-tracker-ui`](https://github.com/iamhusrev/issue-tracker-ui) — Next.js 15 + React 19, talks to this gateway.

## Architecture

```
   +-----------------+          +---------------------------+
   |   Next.js UI    |  --->    |      API Gateway          |
   |  (port 3000)    |          |  Spring Cloud Gateway     |
   +-----------------+          |  JWT filter, circuit      |
                                |  breaker, retry, CORS     |
                                |        (port 8762)        |
                                +------+----------+---------+
                                       |          |
                 +---------------------+----------+-----------------+
                 |                     |                            |
         +-------+------+     +-------+--------+          +---------+-------+
         | User Service |     | Project Service|          |  Task Service   |
         | JWT auth     |     |    CRUD        |          |    CRUD         |
         |  (port 9093) |     |  (port 9091)   |          |  (port 9092)    |
         +-------+------+     +-------+--------+          +---------+-------+
                 |                    |                             |
                 +--------------------+-----------------------------+
                                      |                      |
                         +------------+-------+     +--------+---------+
                         |  PostgreSQL 16     |     |    RabbitMQ      |
                         |  (port 5433)       |     |  events (5673)   |
                         +--------------------+     +--------+---------+
                                                             |
                                                    +--------+---------+
                                                    | Notification Svc |
                                                    |  (port 9094)     |
                                                    +------------------+

   +-------------------+   +------------------+   +------------------+
   | Discovery (Eureka)|   |  Config Server   |   |     Zipkin       |
   |   (port 8761)     |   |   (port 8888)    |   |   (port 9411)    |
   +-------------------+   +------------------+   +------------------+

   +------------------+   +------------------+
   |   Prometheus     |   |    Grafana       |
   |   (port 9090)    |   |   (port 3030)    |
   +------------------+   +------------------+
```

## Tech Stack

| Technology | Purpose |
|---|---|
| Spring Boot 3.2.5 | Application framework |
| Spring Cloud 2023.0.1 | Microservices infrastructure |
| Spring Cloud Gateway | API Gateway & routing |
| Netflix Eureka | Service discovery |
| Spring Cloud Config | Centralized configuration |
| OpenFeign | Declarative REST clients |
| Resilience4j | Circuit breaker & fault tolerance |
| PostgreSQL 16 | Relational database |
| Zipkin + Micrometer | Distributed tracing |
| Prometheus + Grafana | Metrics & monitoring |
| SpringDoc OpenAPI | API documentation |
| Docker & Docker Compose | Containerization |
| Lombok | Boilerplate reduction |
| ModelMapper | Object mapping |

## Modules

| Module | Description | Port |
|---|---|---|
| `discovery-service` | Eureka service registry | 8761 |
| `config-server` | Centralized configuration server | 8888 |
| `gateway-service` | API Gateway (routing, JWT auth, circuit breaker, retry, CORS, fallback) | 8762 |
| `user-service` | User management + JWT auth (register / login / refresh) | 9093 |
| `project-service` | Project management CRUD | 9091 |
| `task-service` | Task management CRUD | 9092 |
| `notification-service` | Event-driven RabbitMQ listener (no DB) | 9094 |
| `app-domain-model` | Shared entities, DTOs, JWT utility, tenant context | - |
| `app-client-management` | Feign clients & fallbacks | - |

## Getting Started

### Prerequisites

- **Docker** & **Docker Compose** (only this is required for the quickstart)
- Java 17+ and Maven 3.9+ (only if you want to run services outside Docker)

### Quickstart — `docker compose up` and you're done

```bash
# 1. Clone this repo
git clone https://github.com/iamhusrev/issue-tracker-microservices.git
cd issue-tracker-microservices

# 2. Bring up the whole stack (from the repo root — no flags, no cd)
docker compose up --build
```

The root `compose.yaml` `include:`s `docker-compose/services.yml`, so a plain `docker compose up` from the repo root builds and starts everything. Ports/credentials come from the root `.env` (already populated with sane defaults) — edit it to override, e.g. `GATEWAY_PORT=18762`.

First boot takes a few minutes on a cold build (each service compiles from source), then Maven downloads + Eureka registration + healthcheck propagation settle. `docker compose` waits for each service to be *healthy* before starting the next — so by the time the logs settle, everything is actually reachable.

Open:

| What | Where | Credentials |
|---|---|---|
| **Eureka dashboard** (confirm every service is UP) | http://localhost:8761 | — |
| Gateway health | http://localhost:8762/actuator/health | — |
| Zipkin (trace requests end-to-end) | http://localhost:9411 | — |
| RabbitMQ management | http://localhost:15673 | guest / guest |
| Prometheus | http://localhost:9090 | — |
| Grafana | http://localhost:3030 | admin / admin |

### Default credentials (seeded automatically)

On first boot `user-service` runs `DataInitializer` which seeds three roles (Admin, Manager, Employee) and a default admin account:

| Username | Password | Role |
|---|---|---|
| `admin` | `admin123` | Admin |

You can log in immediately — no manual setup required.

### Companion frontend

The web UI lives in a separate repo: https://github.com/iamhusrev/issue-tracker-ui

```bash
# In a second terminal, next to this repo:
git clone https://github.com/iamhusrev/issue-tracker-ui.git
cd issue-tracker-ui
cp .env.example .env.local
npm install
npm run dev     # opens http://localhost:3000
```

The UI's `.env.example` already points at `http://localhost:8762/iamhusrev/dev` (the gateway), so it works with the docker-compose stack unchanged.

### Run services outside Docker (optional)

```bash
# Build all modules
mvn clean install -DskipTests

# Start in order (each in its own terminal):
cd discovery-service && mvn spring-boot:run   # Eureka, port 8761
cd config-server     && mvn spring-boot:run   # Config, port 8888
cd user-service      && mvn spring-boot:run
cd project-service   && mvn spring-boot:run
cd task-service      && mvn spring-boot:run
cd notification-service && mvn spring-boot:run
cd gateway-service   && mvn spring-boot:run   # Gateway, port 8762
```

You'll still need Postgres, RabbitMQ and Zipkin running locally (or use `docker compose up database rabbitmq zipkin` for just those).

## API Endpoints

All endpoints are accessible through the Gateway at `http://localhost:8762`.

### User Service

| Method | Gateway Path | Description |
|---|---|---|
| GET | `/iamhusrev/dev/user/api/user` | List all users |
| GET | `/iamhusrev/dev/user/api/user/{userName}` | Get user by username |
| POST | `/iamhusrev/dev/user/api/user` | Create user |
| PUT | `/iamhusrev/dev/user/api/user` | Update user |
| DELETE | `/iamhusrev/dev/user/api/user/{userName}` | Delete user |

### Project Service

| Method | Gateway Path | Description |
|---|---|---|
| GET | `/iamhusrev/dev/project/api/project` | List all projects |
| GET | `/iamhusrev/dev/project/api/project/{code}` | Get project by code |
| POST | `/iamhusrev/dev/project/api/project` | Create project |
| PUT | `/iamhusrev/dev/project/api/project` | Update project |
| DELETE | `/iamhusrev/dev/project/api/project/{code}` | Delete project |
| GET | `/iamhusrev/dev/project/api/project/details/{userName}` | Project details by manager |
| PUT | `/iamhusrev/dev/project/api/project/manager/complete/{code}` | Complete project |

### Task Service

| Method | Gateway Path | Description |
|---|---|---|
| GET | `/iamhusrev/dev/task/api/task` | List all tasks |
| GET | `/iamhusrev/dev/task/api/task/{taskId}` | Get task by ID |
| POST | `/iamhusrev/dev/task/api/task` | Create task |
| PUT | `/iamhusrev/dev/task/api/task` | Update task |
| DELETE | `/iamhusrev/dev/task/api/task/{taskId}` | Delete task |
| GET | `/iamhusrev/dev/task/api/task/employee/pending-tasks/{userName}` | Employee pending tasks |
| GET | `/iamhusrev/dev/task/api/task/employee/archive/{userName}` | Employee archived tasks |
| PUT | `/iamhusrev/dev/task/api/task/employee/update/` | Update task status |

## Monitoring & Observability

| Tool | URL | Credentials |
|---|---|---|
| Eureka Dashboard | http://localhost:8761 | — |
| Swagger UI (User) | http://localhost:9093/swagger-ui.html | — |
| Swagger UI (Project) | http://localhost:9091/swagger-ui.html | — |
| Swagger UI (Task) | http://localhost:9092/swagger-ui.html | — |
| Zipkin Dashboard | http://localhost:9411 | — |
| RabbitMQ Management | http://localhost:15673 | guest / guest |
| Prometheus | http://localhost:9090 | — |
| Grafana | http://localhost:3030 | admin / admin |
| Actuator (per service) | http://localhost:{port}/actuator | — |

## Troubleshooting

- **"UI got a timeout error right after I started everything"** — give it 60–90 seconds. Every service has to register with Eureka before the gateway can route to it. Open http://localhost:8761 and wait until all 6 services show as **UP** before hitting the UI again.
- **"Gateway returns 503 / service warming up" JSON** — the circuit breaker fallback kicked in. The downstream service isn't healthy yet; wait and retry. This is intentional (better than an open-ended hang).
- **Port already in use** — edit the root `.env`, override the conflicting port (e.g. `GATEWAY_PORT=18762`), and retry `docker compose up`.
- **Stale data / schema issues** — `docker compose down -v` nukes the `pgdata` volume for a fresh start (this deletes seeded data).

## Key Patterns Implemented

- **Service Discovery** - Eureka for dynamic service registration and lookup
- **Centralized Configuration** - Config Server with native file-based config
- **API Gateway** - Spring Cloud Gateway with path-based routing
- **Circuit Breaker** - Resilience4j with fallback handlers for fault tolerance
- **Distributed Tracing** - Zipkin + Micrometer Brave for request tracing
- **Inter-Service Communication** - OpenFeign declarative REST clients
- **Metrics & Monitoring** - Prometheus scraping + Grafana dashboards
- **Input Validation** - Jakarta Bean Validation (JSR-303)
- **Global Exception Handling** - @RestControllerAdvice for consistent error responses
- **Soft Delete** - Logical deletion pattern across all entities
