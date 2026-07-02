# CLAUDE.md

Guidance for Claude Code (and humans) working in this repo.

## What this is

`issue-tracker-microservices` — a Spring Cloud microservices backend for an issue/project/task tracker with multi-tenant (per-organization) data isolation and JWT auth. Maven multi-module monorepo. The frontend is a separate repo (`iamhusrev/issue-tracker-ui`, Next.js).

- **Java 17**, **Spring Boot 3.2.5**, **Spring Cloud 2023.0.1**
- Group `com.iamhusrev`, everything under package `com.iamhusrev`

## Run everything (one command)

From the repo root:

```bash
docker compose up --build
```

Root `compose.yaml` just `include:`s `docker-compose/services.yml`, which builds all services from source (multi-stage `Dockerfile`, one image per module via `--build-arg MODULE=<name>`) and starts Postgres, RabbitMQ, Zipkin, Prometheus and Grafana. Startup order is enforced with healthchecks + `depends_on: condition: service_healthy` (discovery → config-server → business services → gateway). Cold `--build` compiles the Maven tree per service, so first boot takes a few minutes.

Env comes from the root `.env` (auto-loaded next to `compose.yaml`); every value also has a default baked into `services.yml`, so a fresh clone boots with no setup.

Default login (seeded by `DataInitializer`): **`admin` / `admin123`**.

### Build without Docker

No Maven wrapper is committed — use a local `mvn` (or the `maven:3.9.9-eclipse-temurin-17` image):

```bash
mvn clean install -DskipTests            # whole tree
mvn -pl user-service -am -DskipTests package   # one module + its deps
```

## Module map

| Module | Role |
|---|---|
| `app-domain-model` | **Shared library.** JPA entities (`BaseEntity` with audit + `organizationId` + Hibernate `organizationFilter`, `User/Role/Organization/Project/Task/RefreshToken`), DTOs, `ResponseWrapper` envelope, enums, `JwtUtil`/tenant classes, RabbitMQ events + `RabbitMQConfig`, `MapperUtil` (ModelMapper). No main class. |
| `app-client-management` | **Shared Feign clients** (`UserClientService`/`ProjectClientService`/`TaskClientService`) + fallbacks + `FeignTenantInterceptor` (propagates tenant headers). No main class. |
| `config-server` | Spring Cloud Config, **native** profile, serves `config-repo/`. |
| `discovery-service` | Eureka server. Does **not** self-register and does **not** use config-server (boots first). |
| `gateway-service` | Spring Cloud Gateway (WebFlux). JWT validation is a code `GlobalFilter`, not YAML. |
| `user-service` | Auth (`/api/auth`) + users (`/api/user`). Issues JWTs, owns `DataInitializer`. |
| `project-service` | Projects (`/api/project`). Calls user-service via Feign. |
| `task-service` | Tasks (`/api/task`). Calls user-service via Feign. |
| `notification-service` | RabbitMQ consumer only (no DB, no controller); logs events. |

## How the pieces talk

- **Config**: services import config via `spring.config.import: optional:configserver:http://${CONFIG_SERVER_HOST:localhost}:8888/`. config-server serves the mounted `config-repo/` (`file:///app/config-repo`). Per-service files: `config-repo/<service>.yml`; shared: `config-repo/application.yml` (datasource creds, rabbit, eureka, resilience4j, tracing, `jwt.secret`).
- **Discovery**: all register with Eureka at `${EUREKA_HOST}:8761`.
- **Gateway routing**: prefix `/iamhusrev/dev/{auth,user,project,task}/**` → `lb://<service>`, with circuit breakers (`fallbackUri: forward:/fallback/...`), retry, and CORS. Auth paths are open; everything else needs a Bearer token.
- **Auth flow**: user-service issues access (15 min) + refresh (7 days) JWTs (HS256, shared `jwt.secret`). Gateway `JwtAuthenticationFilter` validates the token, **strips `Authorization`**, and injects `X-User-Id` / `X-User-Name` / `X-Organization-Id` / `X-User-Role`. Downstream `TenantFilter` reads those into `TenantContext`, and `TenantHibernateAspect` enables the per-org Hibernate filter.
- **Messaging**: RabbitMQ topic exchange `issue-tracker.events`, durable `notification.queue` bound with `#` (catch-all). user/project/task services publish via `EventPublisher`; notification-service consumes.
- **Feign gotcha**: `ResponseWrapper.data` is typed `Object`, so Feign deserializes it as a `LinkedHashMap`. Convert with `objectMapper.convertValue(wrapper.getData(), Xxx.class)` — do **not** cast directly (see `ProjectService`/`TaskService`).

## Data

- **One shared Postgres DB** `ticketing-app` for user/project/task (no per-service DB). notification-service excludes `DataSourceAutoConfiguration`.
- Schema comes from JPA **`ddl-auto: update`** + Java `DataInitializer` seeding (roles Admin/Manager/Employee, a default org, the admin user). **No Flyway/Liquibase.** The `docker-compose/init/*.sql` scripts are not used.
- Persistence: Postgres data lives in the `pgdata` named volume. `docker compose down` keeps it; only `down -v` wipes it (**don't run `-v` unless you mean to lose data**).

## Ports

gateway 8762 · discovery 8761 · config-server 8888 · user 9093 · project 9091 · task 9092 · notification 9094 · Postgres 5432 (host) · RabbitMQ 5673 / mgmt 15673 · Zipkin 9411 · Prometheus 9090 · Grafana 3030.

Observability: Prometheus scrapes `/actuator/prometheus`; Grafana provisions the Prometheus datasource (no dashboards yet); traces go to Zipkin (sampling 1.0).

## Conventions

- Constructor injection (`@RequiredArgsConstructor`), no field injection.
- DTOs at the REST boundary, wrapped in `ResponseWrapper`; never expose entities.
- `@Transactional` on service methods that write.
- Bean Validation (`@Valid`, `@NotBlank`, …); errors mapped by `@RestControllerAdvice GlobalExceptionHandler` (present in user/project/task).
- Mapping via `MapperUtil` (ModelMapper).

## Gotchas / current state

- **Feign + tenant propagation**: `spring.cloud.openfeign.circuitbreaker.enabled` is kept **off** in `project-service.yml`/`task-service.yml`. Turning it on makes Spring Cloud run each Feign call on a separate thread (to enforce the resilience4j TimeLimiter), which drops the `TenantContext` ThreadLocal that `FeignTenantInterceptor` reads → the `X-User-*` headers aren't sent → user-service returns 403. Resilience is still provided by the controller-level `@CircuitBreaker` + fallback handlers.
- **Known pre-existing bugs (not yet fixed):** (1) service methods that throw on an *empty* result (e.g. `ProjectService.listAllProjectDetails` → "no project assigned") trip the controller `@CircuitBreaker`, so the client sees `503` instead of an empty list/404. (2) The multi-tenant **write** path doesn't stamp `organization_id` on newly created rows, so the org Hibernate filter hides them from subsequent reads.
- config-server port `8888` is **hardcoded** in `services.yml` (not env-overridable, unlike other ports).
- gateway excludes `com.iamhusrev.event`/`util` from component scan (avoids pulling in RabbitMQ/DataSource).
- **No tests exist yet.** When adding: JUnit 5 + Mockito + AssertJ, test slices (`@WebMvcTest`/`@DataJpaTest`), Testcontainers Postgres (not H2).
