# Task Service

Microservice for task management within projects, including assignment to employees and status tracking.

## Overview

- **Port:** 9092
- **Base Path:** `/api/v1/task`
- **Database:** PostgreSQL

## API Endpoints

| Method | Path | Description |
|---|---|---|
| GET | `/api/v1/task` | List all tasks |
| GET | `/api/v1/task/{taskId}` | Get task by ID |
| POST | `/api/v1/task` | Create a new task |
| PUT | `/api/v1/task` | Update a task |
| DELETE | `/api/v1/task/{taskId}` | Soft delete a task |
| GET | `/api/v1/task/employee/pending-tasks/{userName}` | Get pending tasks for employee |
| GET | `/api/v1/task/employee/archive/{userName}` | Get completed tasks for employee |
| PUT | `/api/v1/task/employee/update/` | Update task status |

## Swagger UI

Available at: http://localhost:9092/swagger-ui.html

## Features

- Full CRUD with status lifecycle (OPEN -> IN_PROGRESS -> UAT_TEST -> COMPLETE)
- Employee-specific task views (pending/archived)
- Bulk task operations (delete/complete by project)
- Task count queries (completed/non-completed per project)
- Inter-service communication with User Service via Feign
- Circuit breaker with fallback handlers
- Transaction management (@Transactional)
- Global exception handling
- Input validation (JSR-303)

## Inter-Service Communication

Calls **User Service** via Feign to:
- Resolve employee details for task filtering

## Dependencies

- `app-domain-model` - Shared entities, DTOs
- `app-client-management` - Feign clients (UserClientService)

## Running

```bash
mvn spring-boot:run
```

Requires Discovery Service, Config Server, PostgreSQL, and User Service.
