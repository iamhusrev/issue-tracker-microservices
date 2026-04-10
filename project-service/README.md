# Project Service

Microservice for project lifecycle management, including assignment to managers and status tracking.

## Overview

- **Port:** 9091
- **Base Path:** `/api/project`
- **Database:** PostgreSQL

## API Endpoints

| Method | Path | Description |
|---|---|---|
| GET | `/api/project` | List all projects |
| GET | `/api/project/{code}` | Get project by project code |
| POST | `/api/project` | Create a new project |
| PUT | `/api/project` | Update an existing project |
| DELETE | `/api/project/{code}` | Soft delete a project |
| GET | `/api/project/details/{userName}` | Get all projects for a manager |
| PUT | `/api/project/manager/complete/{code}` | Mark project as complete |

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
