# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**Issue Tracker Microservices** — a Spring Boot 3.2.5 / Spring Cloud 2023.0.1 application built with Java 17 and Maven. It models a ticketing system with users, projects, and tasks.

## Build & Run Commands

### Maven (local development)
```bash
# Build all modules
mvn clean package

# Build a specific module (e.g., user-service)
mvn -pl user-service -am clean package

# Skip tests
mvn clean package -DskipTests

# Run tests
mvn clean verify
```

### Docker (full stack)
```bash
# Start all services
cd docker-compose
docker-compose -f services.yml up --build

# Build a specific service image from project root
docker build -f Dockerfile --build-arg MODULE=user-service -t user-service:dev .
```

### Service startup order (local)
1. `discovery-service` (Eureka, port 8761)
2. `config-server` (port 8888)
3. Any order: `user-service` (9093), `project-service` (9091), `task-service` (9092)
4. `gateway-service` (port 8762)

## Architecture

### Module Structure

| Module | Role |
|---|---|
| `app-domain-model` | Shared DTOs, entities, enums (User, Project, Task, Role, Status, Gender) |
| `app-client-management` | OpenFeign client interfaces + Resilience4j fallback handlers |
| `discovery-service` | Eureka Server — service registry |
| `config-server` | Spring Cloud Config Server — reads configs from `/config-repo/` |
| `gateway-service` | Spring Cloud Gateway — single entry point, routes to business services |
| `user-service` | User & role CRUD |
| `project-service` | Project CRUD, manager assignment |
| `task-service` | Task CRUD, employee assignment, status lifecycle |

### Inter-Service Communication

- All service-to-service calls go through **OpenFeign** clients defined in `app-client-management`.
- Service discovery is **Eureka-based** (no hardcoded URLs).
- **Resilience4j** wraps every Feign call with a circuit breaker (50% failure threshold, 10s open window) + retry (3 attempts, 500ms wait) + timeout (3s).
- Fallback classes in `app-client-management` return safe defaults when a downstream service is unavailable.

### Configuration

- All service-level `application.yml` files only declare the app name, port, and config server import URL.
- Shared config lives in `/config-repo/application.yml` (JPA, Eureka, Resilience4j, Zipkin defaults).
- Service-specific overrides live in `/config-repo/<service-name>.yml`.
- In Docker, services resolve hosts via env vars: `CONFIG_SERVER_HOST`, `EUREKA_HOST`, `DB_HOST`, `ZIPKIN_HOST`.

### API Gateway Routes

Each route rewrites `/iamhusrev/dev/{service}/{rest}` → `/api/v1/{service}/{rest}` on the target.

| Path prefix | Target service |
|---|---|
| `/iamhusrev/dev/auth/**` | `user-service` (`/api/v1/auth/`) |
| `/iamhusrev/dev/user/**` | `user-service` (`/api/v1/user/`) |
| `/iamhusrev/dev/project/**` | `project-service` (`/api/v1/project/`) |
| `/iamhusrev/dev/task/**` | `task-service` (`/api/v1/task/`) |

### Auth (Spring Security + JWT)

- Added to `user-service`. New files in `user-service/src/main/java/com/iamhusrev/security/`.
- Login: `POST /iamhusrev/dev/auth/login` → `{ accessToken, refreshToken }`
- Refresh: `POST /iamhusrev/dev/auth/refresh` → `{ accessToken, refreshToken }`
- Auth header: `USER_TOKEN: <accessToken>` (not `Authorization: Bearer`)
- JWT secret configured in `config-repo/user-service.yml` via `${JWT_SECRET}` env var.

### Data Model

All entities extend `BaseEntity` (id, audit timestamps, `is_deleted` soft-delete flag).

- `User` → has a `Role`; can be a project manager or task assignee
- `Project` → has an `assignedManager` (User), a status, and a project code
- `Task` → has `taskStatus` (OPEN / IN_PROGRESS / UAT_TEST / COMPLETE), `assignedEmployee` (User), and belongs to a Project

### Observability

- **Zipkin** (port 9411): distributed tracing at 100% sample rate via `micrometer-tracing-bridge-brave`.
- **Prometheus** (port 9090): metrics scraped from `/actuator/prometheus`.
- **Grafana** (port 3000, admin/admin): provisioned Prometheus datasource under `docker-compose/grafana/`.
- All actuator endpoints are fully exposed (`management.endpoints.web.exposure.include: "*"`).

## Frontend (UI)

Located in `ui/` — Next.js 15 App Router, TypeScript, Tailwind CSS 4, TanStack Query, Zustand.

```bash
cd ui
npm install --legacy-peer-deps
npm run dev          # http://localhost:3000
npm run build
```

Requires Node.js 18+. Use `nvm use 20` if on an older default.

**Key files:**
- `src/utils/api-endpoints.ts` — all endpoint constants (points to gateway via `NEXT_PUBLIC_API_URL`)
- `src/services/api-client.ts` — Axios instance with `USER_TOKEN` header + 401 refresh interceptor
- `src/store/auth-store.ts` — Zustand auth state (persisted to `localStorage`)
- `src/hooks/` — TanStack Query hooks per domain (useUsers, useProjects, useTasks)
- `src/views/` — page content components (DashboardView, UsersView, ProjectsView, etc.)

**Pages:** `/login` → `/dashboard`, `/users`, `/projects/[code]`, `/tasks`, `/manager/projects`, `/employee/my-tasks`, `/employee/archive`

**i18n:** `src/messages/tr.json` (default) + `en.json`. Init in `src/utils/i18n.ts`.

`.env.local`:
```
NEXT_PUBLIC_API_URL=http://localhost:8762/iamhusrev/dev
```
