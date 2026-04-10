# User Service

Microservice responsible for user management including CRUD operations and role-based user organization.

## Overview

- **Port:** 9093
- **Base Path:** `/api/user`
- **Database:** PostgreSQL

## API Endpoints

| Method | Path | Description |
|---|---|---|
| GET | `/api/user` | List all users (sorted by first name) |
| GET | `/api/user/{userName}` | Get user by username |
| POST | `/api/user` | Create a new user |
| PUT | `/api/user` | Update an existing user |
| DELETE | `/api/user/{userName}` | Soft delete a user |

## Swagger UI

Available at: http://localhost:9093/swagger-ui.html

## Features

- Full CRUD operations with validation
- Soft delete pattern (logical deletion)
- Circuit breaker with Resilience4j fallbacks
- Zipkin span tagging in fallback handlers for observability
- Global exception handling
- Input validation (JSR-303)
- Prometheus metrics export

## Dependencies

- `app-domain-model` - Shared entities, DTOs
- `app-client-management` - Feign clients

## Running

```bash
mvn spring-boot:run
```

Requires Discovery Service, Config Server, and PostgreSQL.
