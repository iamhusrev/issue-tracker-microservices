# App Domain Model

Shared library module containing entities, DTOs, enums, and utility classes used across all microservices.

## Overview

This is a non-runnable library module (no `@SpringBootApplication`). It is included as a Maven dependency by the business services.

## Contents

### Entities
- **BaseEntity** - Common audit fields (id, insertDateTime, lastUpdateDateTime, isDeleted)
- **User** - User entity (firstName, lastName, userName, password, role, gender)
- **Role** - Role entity (description)
- **Project** - Project entity (projectCode, projectName, assignedManager, dates, status)
- **Task** - Task entity (taskSubject, taskDetail, taskStatus, assignedEmployee, project)

### DTOs
- **UserDTO** - User data transfer object with validation
- **ProjectDTO** - Project data transfer object with validation
- **TaskDTO** - Task data transfer object with validation
- **RoleDTO** - Role data transfer object
- **UserResponseDTO** - Wrapper for user responses from Feign calls

### Enums
- **Gender** - MALE, FEMALE
- **Status** - OPEN, IN_PROGRESS, UAT_TEST, COMPLETE

### Utilities
- **MapperUtil** - ModelMapper wrapper for entity-DTO conversions
- **ResponseWrapper** - Standardized API response wrapper (success, message, code, data)

## Usage

Add as a dependency in other modules:

```xml
<dependency>
    <groupId>com.iamhusrev</groupId>
    <artifactId>app-domain-model</artifactId>
</dependency>
```
