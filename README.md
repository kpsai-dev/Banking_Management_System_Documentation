# Banking Management System

A Spring Boot REST API application for managing employees and banking accounts.

The project demonstrates REST API development, validation, exception handling, database connectivity, caching, monitoring, unit testing, API testing, and deployment practices.

---

## 1. Project Overview

The Banking Management System provides APIs for:

- Employee management
- Banking account management
- Deposit operations
- Withdrawal operations
- Account balance checking
- Request validation
- Global exception handling
- Redis caching
- HikariCP connection pooling
- Application monitoring
- Unit testing
- Postman API testing

The application follows a layered architecture to keep the code maintainable and easy to understand.

---

# 2. Technology Stack

| Technology | Purpose |
|---|---|
| Java | Programming language |
| Spring Boot | Backend framework |
| Spring Web | REST API development |
| Spring Data JPA | Database access |
| Hibernate | ORM |
| SQL Server | Relational database |
| HikariCP | Database connection pooling |
| Redis | Distributed caching |
| Spring Cache | Cache abstraction |
| Spring Boot Actuator | Application monitoring |
| JUnit | Unit testing |
| Mockito | Mocking dependencies |
| Maven | Build and dependency management |
| Git | Version control |
| Postman | API testing |
| IntelliJ IDEA | Development environment |

---

# 3. Architecture

The application follows a layered architecture.

```text
                    Client
                      |
                      v
               Controller Layer
                      |
                      v
                 Service Layer
                      |
                      v
               Repository Layer
                      |
                      v
              Spring Data JPA
                      |
                      v
                  Hibernate
                      |
                      v
                  HikariCP
                      |
                      v
                  SQL Server
```

Redis is used as a caching layer:

```text
                 Service Layer
                      |
                      v
                 Redis Cache
                      |
              +-------+-------+
              |               |
           Cache Hit       Cache Miss
              |               |
              |               v
              |          Repository
              |               |
              |               v
              |           SQL Server
              |               |
              +---------------+
                      |
                      v
                   Response
```

---

# 4. Project Structure

```text
BankingManagementSystem/
│
├── src/
│   │
│   ├── main/
│   │   │
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── Tns/
│   │   │           └── BankingManagementSystem/
│   │   │               │
│   │   │               ├── controller/
│   │   │               │
│   │   │               ├── service/
│   │   │               │
│   │   │               ├── repository/
│   │   │               │
│   │   │               ├── entity/
│   │   │               │
│   │   │               ├── dto/
│   │   │               │
│   │   │               ├── exception/
│   │   │               │
│   │   │               ├── config/
│   │   │               │
│   │   │               └── BankingManagementSystemApplication.java
│   │   │
│   │   └── resources/
│   │       └── application.yml
│   │
│   └── test/
│       └── java/
│
├── docs/
│   ├── API-DESIGN.md
│   ├── SYSTEM-ARCHITECTURE.md
│   ├── RUNBOOK.md
│   ├── DEPLOYMENT.md
│   ├── DATABASE-SCHEMA.md
│   ├── TROUBLESHOOTING.md
│   ├── ENVIRONMENT-SETUP.md
│   └── FAQ.md
│
├── CHANGELOG.md
├── pom.xml
└── README.md
```

---

# 5. Employee APIs

## Get All Employees

```http
GET /api/v1/employees
```

Supports pagination.

Example:

```text
GET /api/v1/employees?page=0&size=10
```

Response:

```text
200 OK
```

---

## Get Employee By ID

```http
GET /api/v1/employees/{id}
```

Example:

```text
GET /api/v1/employees/1
```

Possible responses:

```text
200 OK
404 Not Found
```

---

## Create Employee

```http
POST /api/v1/employees
```

Example request:

```json
{
  "name": "Sai",
  "email": "sai@gmail.com",
  "department": "IT",
  "salary": 50000
}
```

Successful response:

```text
201 Created
```

Possible errors:

```text
400 Bad Request
409 Conflict
422 Unprocessable Entity
```

---

## Update Employee

```http
PUT /api/v1/employees/{id}
```

Example:

```text
PUT /api/v1/employees/1
```

Possible responses:

```text
200 OK
404 Not Found
409 Conflict
422 Unprocessable Entity
```

---

## Delete Employee

```http
DELETE /api/v1/employees/{id}
```

Successful response:

```text
204 No Content
```

If the employee does not exist:

```text
404 Not Found
```

---

# 6. Banking Account APIs

## Deposit

```http
POST /api/v1/accounts/{id}/deposit
```

Example request:

```json
{
  "amount": 5000
}
```

Successful response:

```text
200 OK
```

---

## Withdraw

```http
POST /api/v1/accounts/{id}/withdraw
```

Example request:

```json
{
  "amount": 1000
}
```

Successful response:

```text
200 OK
```

A business rule violation such as insufficient balance can return:

```text
422 Unprocessable Entity
```

---

## Get Balance

```http
GET /api/v1/accounts/{id}/balance
```

Possible responses:

```text
200 OK
404 Not Found
```

Example response:

```json
{
  "accountId": 1,
  "balance": 25000
}
```

---

# 7. Validation

The application uses Bean Validation for incoming request data.

Common annotations include:

```java
@NotBlank
@NotNull
@Email
@Positive
```

Example:

```java
@NotBlank(message = "Name is required")
private String name;

@Email(message = "Invalid email")
private String email;

@Positive(message = "Salary must be positive")
private Double salary;
```

Invalid input returns:

```text
400 Bad Request
```

---

# 8. Global Exception Handling

The application uses centralized exception handling through:

```java
@RestControllerAdvice
```

The main exceptions handled include:

```text
ResourceNotFoundException
DuplicateResourceException
MethodArgumentNotValidException
BusinessException
Exception
```

Exception mapping:

| Exception | Status |
|---|---:|
| ResourceNotFoundException | 404 |
| DuplicateResourceException | 409 |
| MethodArgumentNotValidException | 400 |
| BusinessException | 422 |
| Exception | 500 |

---

# 9. Error Response

The application follows a common error response structure.

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

The application should not expose internal implementation details such as stack traces, database credentials, or internal exception information.

---

# 10. Database

The application uses:

```text
Microsoft SQL Server
```

Main application tables include:

```text
employeeInfo
accounts
```

The exact table structure should be verified against the current entity mappings and database schema.

Database communication flow:

```text
Service
   |
   v
Repository
   |
   v
Spring Data JPA
   |
   v
Hibernate
   |
   v
HikariCP
   |
   v
SQL Server
```

---

# 11. HikariCP

HikariCP is used for database connection pooling.

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

Important settings:

| Property | Purpose |
|---|---|
| maximum-pool-size | Maximum number of database connections |
| minimum-idle | Minimum idle connections |
| connection-timeout | Maximum wait time for a connection |
| max-lifetime | Maximum lifetime of a connection |

Pool values should be tuned using load testing and database capacity.

---

# 12. Redis Caching

Redis is used as a distributed cache.

Spring Cache annotations include:

```java
@Cacheable
@CachePut
@CacheEvict
```

Example:

```java
@Cacheable(value = "employees", key = "#id")
public EmployeeResponse getEmployeeById(Long id) {
    ...
}
```

For updates:

```java
@CachePut(value = "employees", key = "#id")
```

For deletions:

```java
@CacheEvict(value = "employees", key = "#id")
```

The value:

```text
employees
```

is the Spring cache name.

It does not necessarily represent the database table name.

---

# 13. Spring Boot Actuator

Actuator provides monitoring and management endpoints.

Important endpoints include:

```text
/actuator/health
/actuator/info
/actuator/metrics
```

Liveness and readiness endpoints can also be configured:

```text
/actuator/health/liveness
/actuator/health/readiness
```

Useful monitoring information includes:

```text
JVM memory
CPU
Threads
Garbage collection
HTTP requests
HikariCP connections
```

Actuator endpoints should be appropriately secured and only required endpoints should be exposed.

---

# 14. Unit Testing

The service layer is tested using:

```text
JUnit
Mockito
```

Common annotations:

```java
@ExtendWith(MockitoExtension.class)
@Mock
@InjectMocks
```

Tests follow the Arrange-Act-Assert pattern:

```text
Arrange
   |
   v
Act
   |
   v
Assert
```

Example:

```text
Arrange:
Prepare employee and mock repository.

Act:
Call service method.

Assert:
Verify the returned result.
```

Run tests using:

```bash
mvn test
```

Test reports are generally available under:

```text
target/surefire-reports
```

---

# 15. Postman Testing

Postman is used for API testing.

The Postman collection should contain:

```text
Banking Management System
│
├── Employee APIs
│   ├── Get All Employees
│   ├── Get Employee
│   ├── Create Employee
│   ├── Update Employee
│   └── Delete Employee
│
└── Account APIs
    ├── Deposit
    ├── Withdraw
    └── Balance
```

Postman can be used to test:

- Status codes
- Response bodies
- Response schema
- Validation
- Exception handling
- Response time
- Request chaining

---

# 16. Postman Environment

Recommended environment variables:

| Variable | Example |
|---|---|
| baseUrl | `http://localhost:8080` |
| employeeId | `1` |
| accountId | `1` |
| token | JWT token if authentication is added |

Example request:

```text
{{baseUrl}}/api/v1/employees
```

---

# 17. Request Chaining

IDs returned from API responses can be stored as Postman environment variables.

Example:

```javascript
let json = pm.response.json();

pm.environment.set("employeeId", json.id);
```

The variable can then be used:

```text
{{baseUrl}}/api/v1/employees/{{employeeId}}
```

This avoids manually copying IDs between requests.

---

# 18. Load Testing

The application can be tested under load using Postman.

Important metrics include:

```text
Requests per second
Average response time
P90
P95
P99
Error percentage
CPU usage
Memory usage
HikariCP connections
```

Load testing can help determine suitable HikariCP pool settings.

The goal is to find a configuration that provides acceptable performance without unnecessarily consuming database connections.

---

# 19. Environment Configuration

Environment-specific configuration should be supplied using environment variables.

Example:

```yaml
spring:
  datasource:
    url: ${DEV_DB_URL}
    username: ${DEV_DB_USERNAME}
    password: ${DEV_DB_PASSWORD}

  data:
    redis:
      host: ${REDIS_HOST}
      port: ${REDIS_PORT}
```

Typical variables:

```text
DEV_DB_URL
DEV_DB_USERNAME
DEV_DB_PASSWORD
REDIS_HOST
REDIS_PORT
```

Never commit real passwords or secrets to Git.

---

# 20. Running the Application

## Using IntelliJ IDEA

Open:

```text
BankingManagementSystemApplication.java
```

Run the main class.

---

## Using Maven

From the project root:

```bash
mvn spring-boot:run
```

---

## Using the JAR

First build:

```bash
mvn clean package
```

Then run:

```bash
java -jar target/BankingManagementSystem-0.0.1-SNAPSHOT.jar
```

The exact JAR filename depends on the project configuration.

---

# 21. Verify Application Health

After starting the application, open:

```text
http://localhost:8080/actuator/health
```

Expected response:

```json
{
  "status": "UP"
}
```

Then test an API:

```text
GET http://localhost:8080/api/v1/employees
```

---

# 22. Development Workflow

Recommended development workflow:

```text
Pull Latest Code
       |
       v
Configure Environment
       |
       v
Start SQL Server
       |
       v
Start Redis
       |
       v
Start Spring Boot
       |
       v
Check Actuator Health
       |
       v
Run Unit Tests
       |
       v
Test APIs Using Postman
       |
       v
Implement Changes
       |
       v
Run Tests
       |
       v
Build Application
       |
       v
Commit Changes
```

---

# 23. Build the Application

Run:

```bash
mvn clean package
```

This performs:

```text
Clean
Compile
Test
Package
```

If successful, the JAR is created under:

```text
target/
```

---

# 24. Troubleshooting

For common issues, refer to:

```text
docs/TROUBLESHOOTING.md
```

Common problems include:

```text
Application startup failure
Database connection failure
Redis connection failure
Missing database tables
HikariCP exhaustion
Cache problems
Validation errors
500 errors
Postman test failures
Performance problems
```

---

# 25. Deployment

For deployment instructions, refer to:

```text
docs/DEPLOYMENT.md
```

The deployment documentation covers:

```text
Application build
Environment configuration
Database configuration
Redis configuration
JAR deployment
Docker deployment
Kubernetes health probes
Deployment verification
Rollback
```

---

# 26. Database Documentation

For database details, refer to:

```text
docs/DATABASE-SCHEMA.md
```

It contains information about:

```text
Tables
Columns
Primary keys
Constraints
JPA mappings
Database queries
HikariCP
Database troubleshooting
```

---

# 27. System Architecture

For detailed architecture information, refer to:

```text
docs/SYSTEM-ARCHITECTURE.md
```

It explains:

```text
Controller Layer
Service Layer
Repository Layer
DTO Layer
Entity Layer
Database
Redis
HikariCP
Exception Handling
Actuator
Testing
```

---

# 28. API Documentation

For detailed API specifications, refer to:

```text
docs/API-DESIGN.md
```

The document contains:

```text
Endpoints
HTTP methods
Request bodies
Response formats
Status codes
Validation
Error responses
Postman testing
```

---

# 29. Environment Setup

For complete development environment setup, refer to:

```text
docs/ENVIRONMENT-SETUP.md
```

It covers:

```text
Java
Maven
IntelliJ IDEA
Git
SQL Server
Redis
Environment variables
Application configuration
Postman
Testing
```

---

# 30. Runbook

For common operational procedures, refer to:

```text
docs/RUNBOOK.md
```

The runbook covers:

```text
Application startup
Application restart
Health checks
Database troubleshooting
Redis troubleshooting
HikariCP monitoring
Cache troubleshooting
Load testing
Incident troubleshooting
```

---

# 31. FAQ

Frequently asked questions are documented in:

```text
docs/FAQ.md
```

Topics include:

```text
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

# 32. Changelog

Project changes are documented in:

```text
CHANGELOG.md
```

The changelog records meaningful changes such as:

```text
New features
API changes
Bug fixes
Database changes
Configuration changes
Caching changes
Monitoring changes
Deployment changes
Documentation changes
```

---

# 33. HTTP Status Code Reference

| Status | Meaning |
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

# 34. Security Guidelines

Never commit sensitive information such as:

```text
Database passwords
Redis passwords
API keys
JWT secrets
Private keys
Production credentials
Authentication tokens
```

Use environment variables or secure secret management.

The application should also avoid exposing internal implementation details through API error responses.

---

# 35. Documentation

Complete project documentation is available under the `docs` directory.

| Document | Description |
|---|---|
| [API Design](docs/API-DESIGN.md) | API endpoints and specifications |
| [System Architecture](docs/SYSTEM-ARCHITECTURE.md) | Application architecture and component flow |
| [Runbook](docs/RUNBOOK.md) | Common operational procedures |
| [Deployment](docs/DEPLOYMENT.md) | Deployment procedures |
| [Database Schema](docs/DATABASE-SCHEMA.md) | Database structure and mappings |
| [Troubleshooting](docs/TROUBLESHOOTING.md) | Common problems and solutions |
| [Environment Setup](docs/ENVIRONMENT-SETUP.md) | Development environment setup |
| [FAQ](docs/FAQ.md) | Frequently asked questions |
| [Changelog](CHANGELOG.md) | Project change history |

---

# 36. Project Status

Current project capabilities include:

```text
Employee CRUD APIs
Banking Account APIs
Request Validation
Global Exception Handling
Standard Error Responses
Redis Caching
HikariCP Connection Pooling
Spring Boot Actuator
Liveness and Readiness Monitoring
Service Unit Testing
Postman API Testing
Load Testing
Project Documentation
```

---

# 37. Future Improvements

Potential future enhancements include:

```text
Authentication and Authorization
JWT Security
Role-Based Access Control
API Documentation with OpenAPI/Swagger
Database Migration using Flyway or Liquibase
Docker Compose
Kubernetes Deployment
CI/CD Pipeline
Centralized Logging
Distributed Tracing
Advanced Monitoring
Automated Integration Testing
```

These features should be added according to project requirements.

---

# 38. License

Add the project's applicable license information here.

Example:

```text
This project is intended for educational and development purposes.
```

Update this section if the project uses a specific open-source or company license.

---

# 39. Author

```text
Author: <Your Name>
Project: Banking Management System
```

Replace `<Your Name>` with the appropriate project author or team name.

---

# 40. Quick Start

For a quick local setup:

```text
1. Clone the repository.
2. Open the project in IntelliJ IDEA.
3. Configure the required Java version.
4. Configure database environment variables.
5. Start SQL Server.
6. Start Redis.
7. Run the Spring Boot application.
8. Open /actuator/health.
9. Verify the status is UP.
10. Open Postman.
11. Configure {{baseUrl}}.
12. Test the Employee APIs.
13. Test the Account APIs.
```

The application should then be ready for development and API testing.
