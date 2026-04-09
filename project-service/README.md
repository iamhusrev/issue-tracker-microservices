# Project Service

Microservice for project lifecycle management, including assignment to managers and status tracking.

## Overview

- **Port:** 9091
- **Base Path:** `/api/v1/project`
- **Database:** PostgreSQL

## API Endpoints

| Method | Path | Description |
|---|---|---|
| GET | `/api/v1/project` | List all projects |
| GET | `/api/v1/project/{code}` | Get project by project code |
| POST | `/api/v1/project` | Create a new project |
| PUT | `/api/v1/project` | Update an existing project |
| DELETE | `/api/v1/project/{code}` | Soft delete a project |
| GET | `/api/v1/project/details/{userName}` | Get all projects for a manager |
| PUT | `/api/v1/project/manager/complete/{code}` | Mark project as complete |

## Swagger UI

Available at: http://localhost:9091/swagger-ui.html

## Features

- Full CRUD with project code uniqueness enforcement
- Manager-based project assignment
- Project completion workflow
- Inter-service communication with User Service via Feign
- Circuit breaker with fallback handlers
- Transaction management (@Transactional)
- Global exception handling
- Input validation (JSR-303)

## Inter-Service Communication

Calls **User Service** via Feign to:
- Resolve manager details when listing project details

## Dependencies

- `app-domain-model` - Shared entities, DTOs
- `app-client-management` - Feign clients (UserClientService)

## Running

```bash
mvn spring-boot:run
```

Requires Discovery Service, Config Server, PostgreSQL, and User Service.
