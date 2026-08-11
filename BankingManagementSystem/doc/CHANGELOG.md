# Changelog

All notable changes to the Banking Management System are documented in this file.

The format is based on the following structure:

```text
Version
Date
Added
Changed
Fixed
Removed
Security
Documentation
```

---

# [Unreleased]

## Added

- Continued development of the Banking Management System.
- Employee management APIs.
- Banking account APIs.
- Request validation.
- Global exception handling.
- Standardized error responses.
- Redis caching support.
- HikariCP connection pooling configuration.
- Spring Boot Actuator monitoring.
- Unit testing using JUnit and Mockito.
- Postman API testing.
- Project documentation.

## Changed

- Improved API response handling.
- Improved exception handling.
- Improved validation error responses.
- Improved database connection management.
- Added caching for suitable read operations.

## Documentation

Added project documentation for:

- API design
- System architecture
- Runbook
- Deployment
- Database schema
- Troubleshooting
- Environment setup
- FAQ

---

# Version 1.0.0

## Overview

Initial documented version of the Banking Management System.

---

## Added

### Employee API

Added the following employee endpoints:

```http
GET /api/v1/employees
GET /api/v1/employees/{id}
POST /api/v1/employees
PUT /api/v1/employees/{id}
DELETE /api/v1/employees/{id}
```

### Account API

Added the following banking account endpoints:

```http
POST /api/v1/accounts/{id}/deposit
POST /api/v1/accounts/{id}/withdraw
GET /api/v1/accounts/{id}/balance
```

### Validation

Added request validation using Bean Validation.

Common validation annotations include:

```java
@NotBlank
@NotNull
@Email
@Positive
```

Invalid requests return:

```text
400 Bad Request
```

### Exception Handling

Added centralized exception handling using:

```java
@RestControllerAdvice
```

Supported exceptions include:

```text
ResourceNotFoundException
DuplicateResourceException
MethodArgumentNotValidException
BusinessException
Exception
```

### Error Response

Added a standardized error response structure containing:

```text
timestamp
status
message
errors
path
```

Example:

```json
{
  "timestamp": "2026-08-11T10:00:00",
  "status": 404,
  "message": "Employee not found",
  "errors": [],
  "path": "/api/v1/employees/100"
}
```

---

## HTTP Status Codes

The application uses the following status codes:

| Status | Purpose |
|---|---|
| 200 | Successful operation |
| 201 | Resource created |
| 204 | Resource deleted |
| 400 | Invalid request |
| 404 | Resource not found |
| 409 | Duplicate/conflicting resource |
| 422 | Business rule violation |
| 500 | Unexpected server error |

---

# Caching

## Added

Added Spring Cache support.

Caching annotations include:

```java
@Cacheable
@CachePut
@CacheEvict
```

Redis is used as the distributed cache.

Example:

```java
@Cacheable(value = "employees", key = "#id")
```

The cache name:

```text
employees
```

represents the Spring cache name and does not necessarily represent the database table name.

---

# Database Connection Pooling

## Added

Configured HikariCP as the database connection pool.

Example configuration:

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 5
      minimum-idle: 1
      connection-timeout: 30000
      max-lifetime: 1800000
```

The values should be tuned based on load testing and database capacity.

---

# Monitoring

## Added

Added Spring Boot Actuator.

Important endpoints include:

```text
/actuator/health
/actuator/info
/actuator/metrics
```

Liveness and readiness endpoints are also supported when configured:

```text
/actuator/health/liveness
/actuator/health/readiness
```

---

# Testing

## Added

Added service-layer unit testing using:

```text
JUnit
Mockito
```

Common Mockito annotations include:

```java
@ExtendWith(MockitoExtension.class)
@Mock
@InjectMocks
```

Tests follow the:

```text
Arrange
Act
Assert
```

pattern.

---

# Postman

## Added

Created Postman API testing coverage for:

```text
Employee APIs
Account APIs
Validation
Exception Handling
Response Validation
Response Time
```

Environment variables include:

```text
baseUrl
employeeId
accountId
```

Request chaining can be used to store IDs returned by API requests.

---

# Documentation

## Added

Created project documentation under the `docs` directory.

```text
docs/
│
├── API-DESIGN.md
├── SYSTEM-ARCHITECTURE.md
├── RUNBOOK.md
├── DEPLOYMENT.md
├── DATABASE-SCHEMA.md
├── TROUBLESHOOTING.md
├── ENVIRONMENT-SETUP.md
└── FAQ.md
```

Additional documentation:

```text
README.md
CHANGELOG.md
```

---

# Error Handling Improvements

## Added

Implemented user-friendly error responses.

The API does not expose internal implementation details such as:

```text
Stack traces
Database credentials
Internal exception details
Sensitive configuration
```

Detailed technical information should remain available through application logs.

---

# API Documentation

## Added

Documented API endpoints with:

- HTTP methods
- URL paths
- Request bodies
- Response codes
- Error responses
- Validation behavior
- Environment variables
- Postman testing information

---

# Deployment Documentation

## Added

Documented:

- Environment setup
- Maven build
- JAR deployment
- Docker deployment
- Kubernetes health probes
- Database configuration
- Redis configuration
- Deployment verification
- Rollback procedure

---

# Database Documentation

## Added

Documented:

```text
Employee table
Account table
Primary keys
Validation
Database queries
HikariCP
Database troubleshooting
Database backup considerations
Database migration considerations
```

---

# Troubleshooting Documentation

## Added

Documented troubleshooting procedures for:

```text
Application startup failures
Port conflicts
Database connection failures
Redis connection failures
Missing database tables
400 errors
404 errors
409 errors
422 errors
500 errors
HikariCP exhaustion
Cache problems
Actuator problems
Unit test failures
Postman test failures
Performance problems
```

---

# Environment Documentation

## Added

Documented development environment requirements:

```text
Java
Maven
IntelliJ IDEA
Git
SQL Server
Redis
Postman
```

Also documented:

```text
Environment variables
Datasource configuration
Redis configuration
HikariCP configuration
JPA configuration
Application startup
Testing
```

---

# FAQ Documentation

## Added

Created an FAQ covering commonly asked questions about:

```text
Project architecture
DTOs
Repositories
JpaRepository
Exception handling
Validation
Redis
Caching
HikariCP
Actuator
Postman
Unit testing
Deployment
Troubleshooting
```

---

# Change Management

All significant application changes should be documented in this file.

Examples include:

- New APIs
- API modifications
- Database changes
- Configuration changes
- Dependency changes
- Caching changes
- Monitoring changes
- Deployment changes
- Security changes
- Bug fixes

---

# Jira Integration

Where applicable, changes should reference the corresponding Jira ticket.

Example:

```text
Jira: BANK-123
```

A Jira ticket can be referenced for:

```text
Feature
Bug Fix
Database Change
Configuration Change
Deployment Change
Documentation Change
```

Replace the example ticket number with the actual Jira ticket.

---

# Changelog Entry Format

Use the following format for future changes:

```markdown
# Version X.X.X - YYYY-MM-DD

## Added

- New feature or component.

## Changed

- Existing functionality that was modified.

## Fixed

- Bug or issue that was resolved.

## Removed

- Functionality that was removed.

## Security

- Security-related changes.

## Documentation

- Documentation updates.

## Jira

- BANK-XXX
```

---

# Example Future Entry

```markdown
# Version 1.1.0 - YYYY-MM-DD

## Added

- Added employee search API.
- Added employee department filtering.

## Changed

- Improved employee pagination.

## Fixed

- Fixed duplicate email validation.

## Documentation

- Updated API documentation.

## Jira

- BANK-123
```

---

# Maintenance Guidelines

The changelog should be updated whenever a meaningful project change is completed.

Do not add every small code-level modification.

Record changes that are useful for developers, testers, operations teams, and stakeholders.

The changelog should provide a clear history of how the application has evolved.
