# App Client Management

Shared library module providing OpenFeign client interfaces and circuit breaker fallback handlers for inter-service communication.

## Overview

This is a non-runnable library module. It defines Feign clients that enable type-safe, declarative REST calls between microservices, along with fallback handlers for resilience.

## Feign Clients

| Client | Target Service | Base Path |
|---|---|---|
| `UserClientService` | user-service | `/api/user` |
| `ProjectClientService` | project-service | `/api/project` |
| `TaskClientService` | task-service | `/api/task` |

## Fallback Handlers

Each Feign client has a corresponding fallback class that activates when the target service is unavailable:

- **UserClientFallback** - Returns SERVICE_UNAVAILABLE responses for user operations
- **ProjectClientFallback** - Returns SERVICE_UNAVAILABLE responses for project operations
- **TaskClientFallback** - Returns SERVICE_UNAVAILABLE responses for task operations

## Usage

Add as a dependency in service modules:

```xml
<dependency>
    <groupId>com.iamhusrev</groupId>
    <artifactId>app-client-management</artifactId>
</dependency>
```

Requires `@EnableFeignClients` on the consuming service's application class.
