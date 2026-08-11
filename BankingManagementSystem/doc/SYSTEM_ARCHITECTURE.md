# System Architecture Document

## 1. Overview

The Banking Management System is a Spring Boot REST API application.

The application follows a layered architecture to separate responsibilities between different components.

The main layers are:

- Controller Layer
- Service Layer
- Repository Layer
- Entity Layer
- DTO Layer
- Exception Handling Layer
- Configuration Layer
- Database Layer

---

## 2. High-Level Architecture

The overall flow of the application is:

```text
Client
  |
  | HTTP Request
  v
Controller
  |
  v
Service
  |
  v
Repository
  |
  v
Database
```

The response follows the reverse flow:

```text
Database
  |
  v
Repository
  |
  v
Service
  |
  v
Controller
  |
  | HTTP Response
  v
Client
```

---

# 3. Architecture Diagram

```text
                         +----------------------+
                         |       Client         |
                         |   Postman / Browser  |
                         +----------+-----------+
                                    |
                                    | HTTP Request
                                    v
                         +----------------------+
                         |    Controller Layer  |
                         |   REST Controllers   |
                         +----------+-----------+
                                    |
                                    v
                         +----------------------+
                         |     Service Layer    |
                         | Business Logic       |
                         +----------+-----------+
                                    |
                                    v
                         +----------------------+
                         |   Repository Layer   |
                         | Spring Data JPA      |
                         +----------+-----------+
                                    |
                                    v
                         +----------------------+
                         |      Database         |
                         |      SQL Server       |
                         +----------------------+
```

Supporting components:

```text
                  +--------------------------+
                  |   Global Exception       |
                  |       Handler             |
                  | @RestControllerAdvice    |
                  +------------+-------------+
                               |
                               v
                  +--------------------------+
                  |      ErrorResponse       |
                  +--------------------------+

                  +--------------------------+
                  |       Redis Cache         |
                  +--------------------------+
                               |
                               v
                  +--------------------------+
                  |      Service Layer        |
                  +--------------------------+

                  +--------------------------+
                  |        HikariCP           |
                  |    Connection Pool        |
                  +------------+-------------+
                               |
                               v
                  +--------------------------+
                  |        Database           |
                  +--------------------------+
```

---

# 4. Project Package Structure

The project follows a package-based layered structure.

```text
src/
└── main/
    ├── java/
    │   └── com/
    │       └── Tns/
    │           └── BankingManagementSystem/
    │               │
    │               ├── controller/
    │               │
    │               ├── service/
    │               │
    │               ├── repository/
    │               │
    │               ├── entity/
    │               │
    │               ├── dto/
    │               │
    │               ├── exception/
    │               │
    │               ├── config/
    │               │
    │               └── BankingManagementSystemApplication.java
    │
    └── resources/
        ├── application.yml
        └── ...
```

---

# 5. Controller Layer

## Purpose

The Controller Layer handles HTTP requests and sends HTTP responses.

Controllers should not contain complex business logic.

The controller communicates with the service layer.

### Example

```java
@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    public Page<EmployeeResponse> getAllEmployees(Pageable pageable) {
        return employeeService.getAllEmployees(pageable);
    }
}
```

### Responsibilities

- Receive HTTP requests
- Read path variables
- Read query parameters
- Read request bodies
- Validate request data
- Call service methods
- Return HTTP responses
- Define API endpoints

---

# 6. Service Layer

## Purpose

The Service Layer contains the application's business logic.

It acts as an intermediate layer between the Controller and Repository.

### Example

```java
@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Override
    public EmployeeResponse getEmployeeById(Long id) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Employee not found"));

        return mapToResponse(employee);
    }
}
```

### Responsibilities

- Implement business logic
- Perform validation related to business rules
- Call repositories
- Handle transactions
- Convert entities to DTOs
- Convert DTOs to entities
- Throw application-specific exceptions

---

# 7. Repository Layer

## Purpose

The Repository Layer communicates with the database.

The project uses Spring Data JPA repositories.

### Example

```java
@Repository
public interface EmployeeRepository
        extends JpaRepository<Employee, Long> {
}
```

`JpaRepository` provides many commonly used database operations.

Examples include:

```text
save()
findById()
findAll()
deleteById()
existsById()
count()
```

The repository methods are provided by Spring Data JPA and do not need to be manually implemented for standard CRUD operations.

---

# 8. Entity Layer

## Purpose

Entities represent database tables.

An entity is mapped to a database table using JPA annotations.

### Example

```java
@Entity
@Table(name = "employeeInfo")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    private String department;

    private Double salary;
}
```

### Important Annotations

| Annotation | Purpose |
|---|---|
| `@Entity` | Marks the class as a JPA entity |
| `@Table` | Specifies the database table |
| `@Id` | Specifies the primary key |
| `@GeneratedValue` | Generates the primary key |
| `@Column` | Configures column properties |

---

# 9. DTO Layer

DTO stands for Data Transfer Object.

DTOs are used to transfer data between the client and application layers.

The project uses separate request and response DTOs.

Examples:

```text
EmployeeRequest
EmployeeResponse
```

### EmployeeRequest

Used when receiving data from the client.

```java
public class EmployeeRequest {

    private String name;

    private String email;

    private String department;

    private Double salary;
}
```

### EmployeeResponse

Used when sending employee data to the client.

```java
public class EmployeeResponse {

    private Long id;

    private String name;

    private String email;

    private String department;

    private Double salary;
}
```

### Why DTOs Are Used

DTOs help to:

- Control what data is exposed
- Separate API models from database entities
- Apply validation
- Prevent direct exposure of entity objects
- Maintain a clean API contract

---

# 10. Entity-to-DTO Mapping

The service layer converts entities into response DTOs.

For example:

```java
private EmployeeResponse mapToResponse(Employee employee) {

    return EmployeeResponse.builder()
            .id(employee.getId())
            .name(employee.getName())
            .email(employee.getEmail())
            .department(employee.getDepartment())
            .salary(employee.getSalary())
            .build();
}
```

The `mapToResponse()` method is a custom method created in the application.

It is not a built-in Java or Spring method.

Its purpose is to convert:

```text
Employee Entity
       |
       v
EmployeeResponse DTO
```

---

# 11. Global Exception Handling

The application uses a centralized exception handling mechanism.

The main class is:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
}
```

This allows exceptions from controllers and services to be handled in one place.

### Main exceptions

```text
ResourceNotFoundException
DuplicateResourceException
MethodArgumentNotValidException
Exception
```

### Example

```java
@ExceptionHandler(ResourceNotFoundException.class)
public ResponseEntity<ErrorResponse> handleNotFound(
        ResourceNotFoundException ex,
        HttpServletRequest request) {

    return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(...);
}
```

---

# 12. Error Response

The application uses a common error response structure.

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

The error response contains:

| Field | Description |
|---|---|
| timestamp | Time of the error |
| status | HTTP status code |
| message | User-friendly message |
| errors | Validation errors |
| path | Request path |

---

# 13. Validation Layer

The application uses Bean Validation for validating incoming request data.

Common annotations include:

```java
@NotBlank
@Email
@Positive
@NotNull
```

Example:

```java
@NotBlank(message = "Name is required")
private String name;

@NotBlank(message = "Email is required")
@Email(message = "Invalid email")
private String email;

@Positive(message = "Salary must be positive")
private Double salary;
```

If validation fails, the application returns:

```text
400 Bad Request
```

---

# 14. Business Exception Handling

Some errors are not simple validation errors.

They are business rule violations.

For example:

```text
Account balance = ₹1000
Withdrawal = ₹5000
```

The operation should not be allowed.

The service can throw a business exception:

```java
throw new BusinessException("Insufficient balance");
```

The global exception handler converts it into:

```text
422 Unprocessable Entity
```

---

# 15. Duplicate Resource Handling

The employee email field is unique.

If a user attempts to create another employee with an existing email, the application throws:

```text
DuplicateResourceException
```

The global exception handler returns:

```text
409 Conflict
```

Example:

```json
{
  "status": 409,
  "message": "Employee with this email already exists"
}
```

---

# 16. Database Layer

The application uses SQL Server as the database.

The database stores persistent application data.

Main tables include:

```text
employeeInfo
accounts
```

The exact table names depend on the JPA entity configuration.

---

# 17. Database Communication Flow

The database communication flow is:

```text
Controller
    |
    v
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
JDBC
    |
    v
SQL Server
```

Hibernate generates SQL queries based on the JPA operations.

---

# 18. HikariCP Connection Pool

The application uses HikariCP for database connection pooling.

Instead of creating a new database connection for every request, the application maintains a pool of reusable connections.

```text
Application
     |
     v
HikariCP Connection Pool
     |
     +---- Connection 1
     +---- Connection 2
     +---- Connection 3
     +---- Connection 4
     +---- Connection 5
     |
     v
SQL Server
```

Important configuration properties include:

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 5
      minimum-idle: 1
      connection-timeout: 30000
      max-lifetime: 1800000
```

The exact values should be tuned according to application load and database capacity.

---

# 19. Redis Caching

Redis is used as a distributed cache.

The cache can reduce repeated database access for frequently requested data.

Example:

```text
Client
  |
  v
Controller
  |
  v
Service
  |
  +----------> Redis Cache
  |
  v
Repository
  |
  v
Database
```

For a cached read:

```text
Client
   |
   v
Service
   |
   v
Redis
   |
   | Cache Hit
   v
Response
```

For a cache miss:

```text
Client
   |
   v
Service
   |
   v
Redis
   |
   | Cache Miss
   v
Repository
   |
   v
Database
   |
   v
Redis
   |
   v
Response
```

---

# 20. Spring Cache Annotations

The application can use Spring Cache annotations such as:

```java
@Cacheable
@CachePut
@CacheEvict
```

### @Cacheable

Used for read operations.

```java
@Cacheable(value = "employees", key = "#id")
public EmployeeResponse getEmployeeById(Long id) {
    ...
}
```

### @CachePut

Used when updating an existing cached value.

```java
@CachePut(value = "employees", key = "#id")
public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {
    ...
}
```

### @CacheEvict

Used when deleting cached data.

```java
@CacheEvict(value = "employees", key = "#id")
public void deleteEmployee(Long id) {
    ...
}
```

The value:

```text
employees
```

is the cache name.

It is not the database table name.

---

# 21. Actuator

Spring Boot Actuator provides monitoring and management endpoints.

Examples include:

```text
/actuator/health
/actuator/info
/actuator/metrics
```

The health endpoint can be used to check whether the application is running correctly.

Example:

```text
GET /actuator/health
```

---

# 22. JVM Monitoring

Actuator can expose JVM metrics such as:

```text
JVM memory
Garbage collection
Threads
CPU usage
Heap usage
```

These metrics can help identify performance and resource problems.

---

# 23. Application Configuration

Application configuration is stored in:

```text
src/main/resources/application.yml
```

Typical configuration areas include:

```text
Server
Database
JPA/Hibernate
HikariCP
Redis
Cache
Actuator
Logging
```

---

# 24. Request Processing Flow

## Employee GET Request

Example:

```text
GET /api/v1/employees/1
```

Flow:

```text
1. Client sends HTTP request
          |
          v
2. EmployeeController receives request
          |
          v
3. EmployeeService processes request
          |
          v
4. Redis cache is checked
          |
          +---- Cache Hit ----> Return response
          |
          +---- Cache Miss
                    |
                    v
5. EmployeeRepository.findById()
                    |
                    v
6. Hibernate/JPA
                    |
                    v
7. SQL Server
                    |
                    v
8. Employee Entity returned
                    |
                    v
9. Entity converted to EmployeeResponse
                    |
                    v
10. Response returned to client
```

---

# 25. Employee Creation Flow

Example:

```text
POST /api/v1/employees
```

Flow:

```text
Client
   |
   v
EmployeeController
   |
   | EmployeeRequest
   v
Validation
   |
   v
EmployeeService
   |
   v
Check duplicate email
   |
   v
EmployeeRepository.save()
   |
   v
Hibernate
   |
   v
SQL Server
   |
   v
Employee Entity
   |
   v
EmployeeResponse
   |
   v
201 Created
```

---

# 26. Employee Update Flow

Example:

```text
PUT /api/v1/employees/1
```

Flow:

```text
Client
   |
   v
EmployeeController
   |
   v
EmployeeService
   |
   v
Find Employee
   |
   v
Update Entity
   |
   v
Repository.save()
   |
   v
Database
   |
   v
Update Redis Cache
   |
   v
EmployeeResponse
   |
   v
200 OK
```

---

# 27. Employee Delete Flow

Example:

```text
DELETE /api/v1/employees/1
```

Flow:

```text
Client
   |
   v
EmployeeController
   |
   v
EmployeeService
   |
   v
Check Employee
   |
   v
Repository.delete()
   |
   v
Database
   |
   v
Remove Employee From Redis
   |
   v
204 No Content
```

---

# 28. Banking Transaction Flow

## Deposit

```text
Client
   |
   v
AccountController
   |
   v
AccountService
   |
   v
Find Account
   |
   v
Increase Balance
   |
   v
Save Account
   |
   v
Database
   |
   v
200 OK
```

## Withdraw

```text
Client
   |
   v
AccountController
   |
   v
AccountService
   |
   v
Find Account
   |
   v
Check Balance
   |
   +---- Insufficient Balance
   |          |
   |          v
   |      BusinessException
   |          |
   |          v
   |      422 Response
   |
   +---- Sufficient Balance
              |
              v
         Reduce Balance
              |
              v
         Save Account
              |
              v
           Database
              |
              v
           200 OK
```

---

# 29. Unit Testing Architecture

The service layer is tested using unit tests.

Mockito is used to mock dependencies.

Typical structure:

```text
Service Test
     |
     +---- Mock Repository
     |
     +---- Test Service
     |
     +---- Verify Result
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

---

# 30. Postman Testing

Postman is used for API-level testing.

The collection contains:

```text
Employee APIs
    |
    +-- GET employees
    +-- GET employee by ID
    +-- POST employee
    +-- PUT employee
    +-- DELETE employee

Account APIs
    |
    +-- Deposit
    +-- Withdraw
    +-- Balance
```

Postman tests can verify:

- HTTP status codes
- Response body
- Response fields
- Response time
- Validation errors
- Error responses

---

# 31. Load Testing

The API can be tested under multiple concurrent users using Postman's load testing functionality.

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
```

These metrics help determine whether the configured HikariCP connection pool and application can handle the expected load.

---

# 32. Security Considerations

The application should not expose sensitive implementation details to API clients.

For example, database exceptions should not be returned directly to the user.

Instead of returning:

```text
SQLServerException: Connection refused...
```

the application should return a user-friendly response such as:

```json
{
  "status": 500,
  "message": "An unexpected error occurred"
}
```

Detailed technical information should be available in application logs rather than API responses.

---

# 33. Logging

Application logs are used for troubleshooting and monitoring.

Different log levels can be used:

```text
DEBUG
INFO
WARN
ERROR
```

Examples:

```text
INFO  - Employee created successfully
WARN  - Employee not found
ERROR - Unexpected database error
```

Sensitive information such as passwords, tokens, and confidential data should not be logged.

---

# 34. Monitoring Architecture

The monitoring flow is:

```text
Application
    |
    +---- Actuator
    |       |
    |       +---- Health
    |       +---- Metrics
    |       +---- JVM Metrics
    |
    +---- HikariCP
    |       |
    |       +---- Connection Pool Metrics
    |
    +---- Redis
    |       |
    |       +---- Cache Metrics
    |
    +---- Logs
```

---

# 35. Deployment Architecture

A typical deployment environment can be represented as:

```text
                    Client
                      |
                      v
                Load Balancer
                      |
                      v
              Spring Boot App
                      |
          +-----------+-----------+
          |                       |
          v                       v
       Redis                   SQL Server
       Cache                   Database
```

The Spring Boot application can be deployed using a JAR file or containerized using Docker.

---

# 36. Kubernetes Readiness and Liveness

The application can expose Kubernetes health probes through Spring Boot Actuator.

### Liveness

Determines whether the application process is alive.

```text
/actuator/health/liveness
```

### Readiness

Determines whether the application is ready to receive traffic.

```text
/actuator/health/readiness
```

Kubernetes can use these endpoints to determine whether a pod should continue running or receive traffic.

---

# 37. Architecture Principles

The project follows these principles:

1. Separation of concerns
2. Layered architecture
3. RESTful API design
4. DTO-based API communication
5. Centralized exception handling
6. Repository abstraction
7. Database connection pooling
8. Caching for frequently accessed data
9. Centralized validation
10. Application monitoring
11. Automated testing
12. Clear API documentation

---

# 38. Summary

The Banking Management System uses a layered Spring Boot architecture.

The main application flow is:

```text
Client
   |
   v
Controller
   |
   v
Service
   |
   v
Repository
   |
   v
JPA / Hibernate
   |
   v
HikariCP
   |
   v
SQL Server
```

Additional components improve the application:

```text
Redis
  -> Caching

GlobalExceptionHandler
  -> Centralized error handling

Bean Validation
  -> Input validation

Spring Boot Actuator
  -> Monitoring

JUnit + Mockito
  -> Unit testing

Postman
  -> API testing and load testing
```

This architecture provides a maintainable structure in which each layer has a clear responsibility.
