# Frequently Asked Questions (FAQ)

## 1. What is the Banking Management System?

The Banking Management System is a Spring Boot REST API application that provides APIs for employee management and banking account operations.

The application includes:

- Employee management
- Account management
- Deposit operations
- Withdrawal operations
- Balance checking
- Request validation
- Global exception handling
- Redis caching
- HikariCP connection pooling
- Spring Boot Actuator monitoring
- Unit testing
- Postman API testing

---

## 2. What technology is used for the backend?

The application uses Spring Boot for backend development.

The main technologies include:

```text
Java
Spring Boot
Spring MVC
Spring Data JPA
Hibernate
SQL Server
HikariCP
Redis
Spring Cache
Spring Boot Actuator
JUnit
Mockito
Postman
Maven
Git
```

---

## 3. What is the base URL of the application?

For local development, the default base URL is:

```text
http://localhost:8080
```

The API version is:

```text
/api/v1
```

Therefore, an employee API can be accessed using:

```text
http://localhost:8080/api/v1/employees
```

---

## 4. What APIs are available for employees?

The Employee API provides the following endpoints:

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/api/v1/employees` | Get all employees with pagination |
| GET | `/api/v1/employees/{id}` | Get employee by ID |
| POST | `/api/v1/employees` | Create employee |
| PUT | `/api/v1/employees/{id}` | Update employee |
| DELETE | `/api/v1/employees/{id}` | Delete employee |

---

## 5. What APIs are available for accounts?

The Account API provides:

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/v1/accounts/{id}/deposit` | Deposit money |
| POST | `/api/v1/accounts/{id}/withdraw` | Withdraw money |
| GET | `/api/v1/accounts/{id}/balance` | Get account balance |

---

## 6. What HTTP status codes are used?

The application uses standard HTTP status codes.

| Status | Meaning |
|---|---|
| 200 | Successful request |
| 201 | Resource created |
| 204 | Resource deleted successfully |
| 400 | Invalid request |
| 404 | Resource not found |
| 409 | Duplicate/conflicting resource |
| 422 | Business rule violation |
| 500 | Unexpected server error |

---

## 7. Why is 201 returned when creating an employee?

The HTTP status:

```text
201 Created
```

indicates that a new resource was successfully created.

The Employee API uses:

```http
POST /api/v1/employees
```

and returns:

```text
201 Created
```

on successful creation.

---

## 8. Why is 204 returned when deleting an employee?

The status:

```text
204 No Content
```

means the operation was successful but there is no response body to return.

The Delete Employee API uses:

```http
DELETE /api/v1/employees/{id}
```

and returns:

```text
204 No Content
```

when the employee is successfully deleted.

---

## 9. Why do we use DTOs?

DTO stands for Data Transfer Object.

DTOs are used to transfer data between the client and application.

Examples:

```text
EmployeeRequest
EmployeeResponse
```

DTOs help to:

- Control API data
- Separate API models from database entities
- Apply validation
- Avoid exposing internal entity details
- Maintain a clean API contract

---

## 10. Why don't we directly return entities?

Returning entities directly from controllers can tightly couple the API to the database model.

Using DTOs provides better separation:

```text
Database Entity
      |
      v
Service Layer
      |
      v
Response DTO
      |
      v
Client
```

This makes the API easier to maintain when the database model changes.

---

## 11. What is `mapToResponse()`?

`mapToResponse()` is a custom method created in the application.

It is not a built-in Java, Spring, or JPA method.

Its purpose is to convert an entity into a response DTO.

Example:

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

The flow is:

```text
Employee Entity
      |
      v
mapToResponse()
      |
      v
EmployeeResponse
```

---

## 12. Why do DTO classes use constructors?

DTOs may use constructors to create objects in different ways.

A no-argument constructor allows an object to be created without immediately providing values.

Example:

```java
EmployeeRequest request = new EmployeeRequest();
```

An all-argument constructor allows all fields to be provided at once.

Example:

```java
new EmployeeResponse(
    1L,
    "Sai",
    "sai@gmail.com",
    "IT",
    50000.0
);
```

The exact constructors required depend on how the DTO is used by Spring, Jackson, tests, or application code.

---

## 13. Why do we use Lombok `@Builder`?

`@Builder` provides the builder pattern.

Instead of:

```java
new EmployeeResponse(
    1L,
    "Sai",
    "sai@gmail.com",
    "IT",
    50000.0
);
```

we can write:

```java
EmployeeResponse.builder()
        .id(1L)
        .name("Sai")
        .email("sai@gmail.com")
        .department("IT")
        .salary(50000.0)
        .build();
```

The builder is easier to read and reduces problems caused by constructor parameter order.

---

## 14. Why not simply use `@Data`?

Lombok `@Data` generates:

```text
@Getter
@Setter
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
```

It is useful when a class genuinely needs all of these generated methods.

However, using separate annotations provides more control.

For example:

```java
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
```

makes it explicit which features the class requires.

The choice depends on the project's coding standards.

---

## 15. What is the Repository Layer?

The Repository Layer is responsible for database access.

Example:

```java
public interface EmployeeRepository
        extends JpaRepository<Employee, Long> {
}
```

The repository communicates with the database through Spring Data JPA and Hibernate.

---

## 16. Are `JpaRepository` methods default methods?

The commonly used methods such as:

```java
save()
findById()
findAll()
deleteById()
existsById()
count()
```

are provided by Spring Data JPA.

They are not methods that we manually implement in the repository interface.

Spring Data creates the required implementation at runtime.

For example:

```java
employeeRepository.findById(id);
```

can be used even though we did not implement `findById()` ourselves.

---

## 17. Why do we use `JpaRepository`?

`JpaRepository` provides common CRUD and JPA-related operations.

For example:

```java
public interface EmployeeRepository
        extends JpaRepository<Employee, Long> {
}
```

This gives access to methods such as:

```text
save()
findById()
findAll()
delete()
deleteById()
existsById()
count()
```

This significantly reduces boilerplate database code.

---

## 18. What is Global Exception Handling?

Global exception handling provides a centralized way to handle application exceptions.

The application uses:

```java
@RestControllerAdvice
```

Example:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
}
```

Instead of writing exception handling separately in every controller, exceptions can be handled in one central class.

---

## 19. Which exceptions are handled globally?

The application handles exceptions such as:

```text
ResourceNotFoundException
DuplicateResourceException
MethodArgumentNotValidException
Exception
```

The mapping is:

| Exception | Status |
|---|---:|
| ResourceNotFoundException | 404 |
| DuplicateResourceException | 409 |
| MethodArgumentNotValidException | 400 |
| BusinessException | 422 |
| Generic Exception | 500 |

---

## 20. What is `ErrorResponse`?

`ErrorResponse` is the standard structure used when returning an error to the client.

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

It contains:

| Field | Purpose |
|---|---|
| timestamp | Time of error |
| status | HTTP status |
| message | User-friendly message |
| errors | Validation errors |
| path | Requested endpoint |

---

## 21. Why don't we expose stack traces to users?

Internal implementation details should not be exposed through API responses.

For example, the client should not receive:

```text
SQLServerException
NullPointerException
HibernateException
```

Instead, the client receives a user-friendly message:

```json
{
  "status": 500,
  "message": "An unexpected error occurred"
}
```

Detailed technical information should be available in application logs.

---

## 22. What is Bean Validation?

Bean Validation is used to validate incoming request data.

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

---

## 23. What happens when validation fails?

If the client sends invalid data, Spring throws a validation exception.

The global exception handler handles:

```text
MethodArgumentNotValidException
```

The API returns:

```text
400 Bad Request
```

with a user-friendly error response.

---

## 24. Why is duplicate email 409?

A duplicate email represents a conflict with existing data.

For example:

```text
Existing email:
sai@gmail.com
```

If another employee is created using:

```text
sai@gmail.com
```

the application returns:

```text
409 Conflict
```

---

## 25. Why is insufficient balance 422?

Insufficient balance is a business rule violation.

For example:

```text
Balance = ₹1,000
Withdrawal = ₹5,000
```

The request may be syntactically valid, but the operation is not allowed by the application's business rules.

Therefore:

```text
422 Unprocessable Entity
```

can be returned.

---

## 26. What is HikariCP?

HikariCP is the database connection pool used by Spring Boot.

Instead of creating a new database connection for every request, HikariCP maintains reusable connections.

Example:

```text
HikariCP Pool
|
+-- Connection 1
+-- Connection 2
+-- Connection 3
+-- Connection 4
+-- Connection 5
|
v
SQL Server
```

---

## 27. What is `maximum-pool-size`?

It defines the maximum number of database connections that can exist in the pool.

Example:

```yaml
maximum-pool-size: 5
```

means the pool can use up to five database connections.

The value should be determined through testing and database capacity rather than simply choosing a large number.

---

## 28. What is `minimum-idle`?

It defines the minimum number of idle connections that the pool attempts to maintain.

Example:

```yaml
minimum-idle: 1
```

means the pool attempts to maintain at least one idle connection.

---

## 29. What is `connection-timeout`?

It defines how long a request waits for a database connection before timing out.

Example:

```yaml
connection-timeout: 30000
```

means:

```text
30,000 milliseconds = 30 seconds
```

---

## 30. What is Redis?

Redis is an in-memory data store that can be used as a distributed cache.

In this project, Redis is used to reduce repeated database access for frequently requested data.

Example:

```text
Client
  |
  v
Service
  |
  v
Redis
  |
  +---- Cache Hit ----> Response
  |
  +---- Cache Miss
          |
          v
       Database
```

---

## 31. Do we need to use Redis annotations for every method?

No.

Different cache annotations are used for different operations.

Typical usage:

```text
GET / Read
    |
    v
@Cacheable

UPDATE
    |
    v
@CachePut

DELETE
    |
    v
@CacheEvict
```

A method does not automatically need a caching annotation simply because it reads or writes data.

Caching should be applied where it provides value.

---

## 32. Is `employees` in `@Cacheable` the database table name?

Not necessarily.

Example:

```java
@Cacheable(value = "employees", key = "#id")
```

Here:

```text
employees
```

is the Spring cache name.

It does not have to match the database table name.

For example:

```text
Database table:
employeeInfo

Redis cache:
employees
```

These can be different.

---

## 33. What is `@Cacheable`?

`@Cacheable` is generally used for read operations.

Example:

```java
@Cacheable(value = "employees", key = "#id")
public EmployeeResponse getEmployeeById(Long id) {
    ...
}
```

On the first request, the method can access the database.

On subsequent requests with the same key, the cached value can be returned.

---

## 34. What is `@CachePut`?

`@CachePut` is generally used when updating data.

Example:

```java
@CachePut(value = "employees", key = "#id")
public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {
    ...
}
```

The method executes and the returned value is placed into the cache.

---

## 35. What is `@CacheEvict`?

`@CacheEvict` removes data from the cache.

Example:

```java
@CacheEvict(value = "employees", key = "#id")
public void deleteEmployee(Long id) {
    ...
}
```

When the employee is deleted, its cached value is also removed.

---

## 36. What is Spring Boot Actuator?

Spring Boot Actuator provides monitoring and management endpoints.

Examples:

```text
/actuator/health
/actuator/info
/actuator/metrics
```

It can provide information about:

```text
Application health
JVM memory
CPU
Threads
HTTP requests
Garbage collection
HikariCP
```

---

## 37. What is `/actuator/health`?

It provides the health status of the application.

Example:

```http
GET /actuator/health
```

Basic response:

```json
{
  "status": "UP"
}
```

Depending on configuration, it can also provide information about components such as databases and Redis.

---

## 38. What are liveness and readiness probes?

Liveness checks whether the application is alive.

```text
/actuator/health/liveness
```

Readiness checks whether the application is ready to receive traffic.

```text
/actuator/health/readiness
```

These endpoints are especially useful with Kubernetes.

---

## 39. Why should Actuator endpoints not all be public?

Actuator endpoints can expose operational information.

Some endpoints may provide information about:

```text
Application
JVM
Metrics
Environment
Beans
Configuration
```

Therefore, only the required endpoints should be exposed and appropriate security controls should be applied.

---

## 40. What is Postman used for?

Postman is used to test the application's REST APIs.

It can be used to:

- Send HTTP requests
- Verify status codes
- Validate responses
- Test validation
- Test exceptions
- Test response times
- Chain requests
- Use environment variables
- Run collections

---

## 41. What is a Postman environment variable?

An environment variable stores reusable values.

Example:

```text
baseUrl = http://localhost:8080
```

The API request can use:

```text
{{baseUrl}}/api/v1/employees
```

This avoids hardcoding the URL in every request.

---

## 42. What is request chaining in Postman?

Request chaining allows one request to use data returned by another request.

For example, after creating an employee, store its ID:

```javascript
let json = pm.response.json();

pm.environment.set("employeeId", json.id);
```

Then use:

```text
{{baseUrl}}/api/v1/employees/{{employeeId}}
```

---

## 43. What is unit testing?

Unit testing tests a small part of the application independently.

In this project, the service layer is tested using:

```text
JUnit
Mockito
```

Common annotations include:

```java
@ExtendWith(MockitoExtension.class)
@Mock
@InjectMocks
```

---

## 44. What is Mockito used for?

Mockito is used to create mock objects.

For example, while testing the employee service, the repository can be mocked:

```java
@Mock
private EmployeeRepository employeeRepository;
```

This allows the service to be tested without actually connecting to the database.

---

## 45. What is `@InjectMocks`?

`@InjectMocks` creates the class being tested and injects its mocked dependencies.

Example:

```java
@InjectMocks
private EmployeeServiceImpl employeeService;
```

If the service depends on:

```java
EmployeeRepository
```

the mocked repository can be injected into the service.

---

## 46. What is Arrange-Act-Assert?

Arrange-Act-Assert is a common structure for unit tests.

### Arrange

Prepare the test data and mocks.

### Act

Execute the method being tested.

### Assert

Verify the result.

Example:

```text
Arrange
   |
   v
Prepare employee and mock repository
   |
   v
Act
   |
   v
Call service method
   |
   v
Assert
   |
   v
Verify returned result
```

---

## 47. What is code coverage?

Code coverage indicates how much of the application code is executed by tests.

For example:

```text
80% coverage
```

means approximately 80% of the measured code was executed during testing.

The project targets a minimum of:

```text
80%
```

Code coverage should be used as a quality indicator rather than the only measure of test quality.

---

## 48. What is a Runbook?

A runbook contains procedures for common operational tasks.

It can explain:

```text
How to start the application
How to check health
How to troubleshoot database issues
How to troubleshoot Redis
How to investigate connection pool issues
How to verify APIs
How to restart the application
```

The runbook helps developers and support teams follow consistent procedures.

---

## 49. What is the project documentation structure?

The documentation is organized under the `docs` directory.

Example:

```text
docs/
│
├── API-DESIGN.md
├── SYSTEM-ARCHITECTURE.md
├── RUNBOOK.md
├── DEPLOYMENT.md
├── DATABASE-SCHEMA.md
├── TROUBLESHOOTING.md
└── ENVIRONMENT-SETUP.md
```

Additional project documentation includes:

```text
FAQ.md
CHANGELOG.md
README.md
```

---

## 50. Why is documentation stored in Markdown?

Markdown is lightweight, readable, and works well with Git.

It can be viewed:

- Directly in IntelliJ
- On GitHub
- In GitLab
- In other Markdown viewers

It also allows documentation to remain close to the source code.

---

## 51. How should documentation changes be handled?

Documentation should be updated whenever there is a meaningful project change.

Examples:

```text
New API
API response change
Database schema change
Configuration change
Deployment change
New troubleshooting procedure
New dependency
New operational process
```

Documentation changes should be committed along with the related code change when appropriate.

---

## 52. What should never be added to documentation?

Do not add sensitive information such as:

```text
Real passwords
Database credentials
API keys
JWT secrets
Private keys
Production tokens
Confidential customer information
```

Use placeholders instead:

```text
<username>
<password>
<repository-url>
<api-key>
```

---

## 53. How do I report a new issue?

When reporting an issue, provide:

```text
Issue Description
Environment
Endpoint
Request
Expected Result
Actual Result
HTTP Status
Error Message
Application Logs
Steps to Reproduce
```

Example:

```text
Issue:
GET employee API returns 500.

Endpoint:
GET /api/v1/employees/1

Expected:
200 OK

Actual:
500 Internal Server Error

Steps:
1. Start application.
2. Open Postman.
3. Call GET employee API.
4. Observe response.
```

---

## 54. How do I verify that the application is working?

Use the following checklist:

```text
1. Start SQL Server
2. Start Redis
3. Start Spring Boot application
4. Check application logs
5. Open /actuator/health
6. Verify status is UP
7. Test Employee API
8. Test Account API
9. Test validation
10. Test exception handling
```

---

## 55. What should I check if an API is slow?

Check:

```text
API response time
Database query
HikariCP connections
Redis cache hit/miss
JVM memory
CPU
Thread count
Application logs
Database performance
```

Also perform load testing to determine whether the issue occurs only under higher traffic.

---

## 56. What should I do if the application returns 500?

Follow these steps:

```text
1. Check application logs
2. Identify the exception
3. Check database connectivity
4. Check Redis connectivity
5. Check configuration
6. Reproduce the issue
7. Fix the root cause
8. Run unit tests
9. Retest the API
```

Do not expose internal exception details to the API client.

---

## 57. What should I do before deployment?

Before deployment:

```text
1. Pull latest source code
2. Review changes
3. Run unit tests
4. Build the application
5. Verify database configuration
6. Verify Redis configuration
7. Verify environment variables
8. Verify deployment artifact
9. Verify rollback procedure
10. Deploy
11. Check health
12. Test critical APIs
13. Monitor application
```

---

## 58. How are database and Redis used together?

The database is the persistent data store.

Redis is used as a cache.

```text
SQL Server
    |
    | Persistent Data
    v
Database

Redis
    |
    | Temporary / Cached Data
    v
Cache
```

The database remains the source of persistent application data, while Redis can improve performance for suitable read operations.

---

## 59. What happens when Redis has a cache miss?

The application can follow this flow:

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
SQL Server
   |
   v
Service
   |
   v
Store Result in Redis
   |
   v
Client
```

---

## 60. How is cache invalidated?

For an update:

```java
@CachePut
```

can update the cached value.

For a deletion:

```java
@CacheEvict
```

can remove the cached value.

This helps prevent stale cached data.

---

## 61. What is the recommended project flow?

The overall application flow is:

```text
Client
   |
   v
Controller
   |
   v
Validation
   |
   v
Service
   |
   +---- Redis Cache
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

Exceptions are handled centrally through:

```text
GlobalExceptionHandler
```

Monitoring is provided through:

```text
Spring Boot Actuator
```

Testing is performed using:

```text
JUnit
Mockito
Postman
```

---

## 62. Who should maintain this FAQ?

The development team should maintain this document.

Update it when:

- A frequently asked question is identified.
- A common issue is discovered.
- Application behavior changes.
- Configuration changes.
- APIs change.
- Deployment procedures change.

The FAQ should contain concise answers to common questions and link to more detailed documentation where appropriate.
