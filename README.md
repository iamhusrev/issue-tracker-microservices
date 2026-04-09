# Issue Tracker Microservices

A production-ready issue tracking system built with **Spring Boot 3.2.5** and **Spring Cloud 2023.0.1**, demonstrating modern microservices architecture patterns.

## Architecture

```
                          +------------------+
                          |   API Gateway    |
                          |    (port 8762)   |
                          +--------+---------+
                                   |
                  +----------------+----------------+
                  |                |                 |
          +-------+------+ +------+-------+ +-------+------+
          | User Service | |Project Service| | Task Service |
          |  (port 9093) | |  (port 9091)  | |  (port 9092) |
          +--------------+ +---------------+ +--------------+
                  |                |                 |
                  +----------------+----------------+
                                   |
                          +--------+---------+
                          |   PostgreSQL     |
                          |   (port 5432)    |
                          +------------------+

    +------------------+     +------------------+     +------------------+
    | Discovery Service|     |  Config Server   |     |     Zipkin       |
    |  Eureka (8761)   |     |    (port 8888)   |     |   (port 9411)    |
    +------------------+     +------------------+     +------------------+

    +------------------+     +------------------+
    |   Prometheus     |     |    Grafana       |
    |   (port 9090)    |     |   (port 3000)    |
    +------------------+     +------------------+
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
| `gateway-service` | API Gateway with path-based routing | 8762 |
| `user-service` | User management CRUD | 9093 |
| `project-service` | Project management CRUD | 9091 |
| `task-service` | Task management CRUD | 9092 |
| `app-domain-model` | Shared entities, DTOs, utilities | - |
| `app-client-management` | Feign clients & fallbacks | - |

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.9+
- Docker & Docker Compose

### Run with Docker Compose (Recommended)

```bash
cd docker-compose
docker compose -f services.yml up --build
```

This starts all services with proper dependency ordering:
1. PostgreSQL + Zipkin
2. Discovery Service (Eureka)
3. Config Server
4. User, Project, Task Services
5. Gateway Service
6. Prometheus + Grafana

### Run Locally

```bash
# Build all modules
mvn clean install -DskipTests

# Start in order:
# 1. Discovery Service
cd discovery-service && mvn spring-boot:run

# 2. Config Server
cd config-server && mvn spring-boot:run

# 3. Business Services (in any order)
cd user-service && mvn spring-boot:run
cd project-service && mvn spring-boot:run
cd task-service && mvn spring-boot:run

# 4. Gateway
cd gateway-service && mvn spring-boot:run
```

## API Endpoints

All endpoints are accessible through the Gateway at `http://localhost:8762`.

### User Service

| Method | Gateway Path | Description |
|---|---|---|
| GET | `/iamhusrev/dev/user/api/v1/user` | List all users |
| GET | `/iamhusrev/dev/user/api/v1/user/{userName}` | Get user by username |
| POST | `/iamhusrev/dev/user/api/v1/user` | Create user |
| PUT | `/iamhusrev/dev/user/api/v1/user` | Update user |
| DELETE | `/iamhusrev/dev/user/api/v1/user/{userName}` | Delete user |

### Project Service

| Method | Gateway Path | Description |
|---|---|---|
| GET | `/iamhusrev/dev/project/api/v1/project` | List all projects |
| GET | `/iamhusrev/dev/project/api/v1/project/{code}` | Get project by code |
| POST | `/iamhusrev/dev/project/api/v1/project` | Create project |
| PUT | `/iamhusrev/dev/project/api/v1/project` | Update project |
| DELETE | `/iamhusrev/dev/project/api/v1/project/{code}` | Delete project |
| GET | `/iamhusrev/dev/project/api/v1/project/details/{userName}` | Project details by manager |
| PUT | `/iamhusrev/dev/project/api/v1/project/manager/complete/{code}` | Complete project |

### Task Service

| Method | Gateway Path | Description |
|---|---|---|
| GET | `/iamhusrev/dev/task/api/v1/task` | List all tasks |
| GET | `/iamhusrev/dev/task/api/v1/task/{taskId}` | Get task by ID |
| POST | `/iamhusrev/dev/task/api/v1/task` | Create task |
| PUT | `/iamhusrev/dev/task/api/v1/task` | Update task |
| DELETE | `/iamhusrev/dev/task/api/v1/task/{taskId}` | Delete task |
| GET | `/iamhusrev/dev/task/api/v1/task/employee/pending-tasks/{userName}` | Employee pending tasks |
| GET | `/iamhusrev/dev/task/api/v1/task/employee/archive/{userName}` | Employee archived tasks |
| PUT | `/iamhusrev/dev/task/api/v1/task/employee/update/` | Update task status |

## Monitoring & Observability

| Tool | URL | Credentials |
|---|---|---|
| Eureka Dashboard | http://localhost:8761 | - |
| Swagger UI (User) | http://localhost:9093/swagger-ui.html | - |
| Swagger UI (Project) | http://localhost:9091/swagger-ui.html | - |
| Swagger UI (Task) | http://localhost:9092/swagger-ui.html | - |
| Zipkin Dashboard | http://localhost:9411 | - |
| Prometheus | http://localhost:9090 | - |
| Grafana | http://localhost:3000 | admin/admin |
| Actuator (per service) | http://localhost:{port}/actuator | - |

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
