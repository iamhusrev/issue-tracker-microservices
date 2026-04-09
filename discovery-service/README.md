# Discovery Service

Eureka-based service registry that enables dynamic service discovery for all microservices.

## Overview

- **Port:** 8761
- **Framework:** Spring Cloud Netflix Eureka Server
- **Dashboard:** http://localhost:8761

## Features

- Service registration and deregistration
- Health check monitoring
- Instance status tracking
- Self-preservation mode

## Configuration

| Property | Value |
|---|---|
| `server.port` | 8761 |
| `eureka.client.register-with-eureka` | false |
| `eureka.client.fetch-registry` | false |

Config Server lookup is disabled for this service (`spring.cloud.config.enabled: false`) since it must start before Config Server.

## Running

```bash
mvn spring-boot:run
```

This service must be started **first** before any other service.
