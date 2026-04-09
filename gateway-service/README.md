# Gateway Service

Spring Cloud Gateway providing a single entry point for all API requests with path-based routing.

## Overview

- **Port:** 8762
- **Framework:** Spring Cloud Gateway
- **Base Path:** `/iamhusrev/dev/`

## Routes

| Route ID | Path Pattern | Target Service |
|---|---|---|
| user-service | `/iamhusrev/dev/user/**` | user-service |
| project-service | `/iamhusrev/dev/project/**` | project-service |
| task-service | `/iamhusrev/dev/task/**` | task-service |

All routes use load-balanced URIs (`lb://`) via Eureka discovery and `RewritePath` filters to strip the gateway prefix.

## Features

- Path-based routing with URL rewriting
- Load balancing via Eureka service discovery
- Discovery locator for automatic route detection
- Distributed tracing integration (Zipkin)

## Configuration

Gateway routes are managed via Config Server in `config-repo/gateway-service.yml`.

## Running

```bash
mvn spring-boot:run
```

Requires Discovery Service and Config Server to be running.
