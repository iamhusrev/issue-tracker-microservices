# Config Server

Centralized configuration server using Spring Cloud Config with native file-based profile.

## Overview

- **Port:** 8888
- **Framework:** Spring Cloud Config Server
- **Config Source:** `config-repo/` directory (native profile)

## Features

- Centralized configuration for all microservices
- Native file system backend (no Git required)
- Registers with Eureka for service discovery
- Environment-specific configuration support

## Configuration Files

Located in `config-repo/`:

| File | Purpose |
|---|---|
| `application.yml` | Global defaults (DB, Eureka, Resilience4j, Actuator, Zipkin) |
| `user-service.yml` | User service database URL and Feign settings |
| `project-service.yml` | Project service database URL and Feign settings |
| `task-service.yml` | Task service database URL and Feign settings |
| `gateway-service.yml` | Gateway routes and discovery locator config |

## Running

```bash
mvn spring-boot:run
```

Requires Discovery Service to be running first.
