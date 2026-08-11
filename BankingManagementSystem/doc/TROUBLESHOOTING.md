# Troubleshooting Guide

## 1. Overview

This document provides troubleshooting procedures for common problems in the Banking Management System.

It covers:

- Application startup issues
- Database connectivity
- Redis connectivity
- API errors
- Validation failures
- Global exception handling
- HikariCP connection pool issues
- Caching problems
- Actuator health issues
- Unit test failures
- Postman test failures
- Performance issues

---

# 2. Troubleshooting Approach

When an issue occurs, follow this general process:

```text
Identify the Problem
        |
        v
Check HTTP Status / Error
        |
        v
Check Application Logs
        |
        v
Check Configuration
        |
        v
Check Database / Redis
        |
        v
Reproduce the Issue
        |
        v
Identify Root Cause
        |
        v
Apply Fix
        |
        v
Retest
        |
        v
Document the Resolution
```

---

# 3. Application Does Not Start

## Symptoms

The Spring Boot application fails during startup.

Possible messages may include:

```text
Application failed to start
BeanCreationException
DataSource configuration error
Port already in use
Redis connection error
```

## Possible Causes

- Incorrect application configuration
- Database unavailable
- Incorrect database credentials
- Redis unavailable
- Port already in use
- Missing dependency
- Incorrect Java version
- Incorrect environment variable

## Troubleshooting Steps

### Step 1

Check the first error in the application console.

Do not focus only on the final error message. The first `Caused by` message often provides the actual reason.

### Step 2

Verify Java:

```bash
java -version
```

### Step 3

Verify Maven:

```bash
mvn -version
```

### Step 4

Verify environment variables.

### Step 5

Verify SQL Server is running.

### Step 6

Verify Redis is available if Redis caching is enabled.

### Step 7

Verify the configured application port.

---

# 4. Port Already in Use

## Symptoms

The application fails to start with a message indicating that port `8080` is already in use.

## Windows

Find the process using port 8080:

```bash
netstat -ano | findstr :8080
```

The command returns the process ID.

Then check the process:

```bash
tasklist | findstr <PID>
```

If appropriate, stop the process:

```bash
taskkill /PID <PID> /F
```

Alternatively, configure another port:

```yaml
server:
  port: 8081
```

---

# 5. Database Connection Failure

## Symptoms

The application starts but cannot communicate with SQL Server.

Possible errors include:

```text
Cannot create PoolableConnectionFactory
Connection refused
Login failed
Cannot open database
Connection timed out
```

## Possible Causes

- SQL Server is not running
- Incorrect database URL
- Incorrect username
- Incorrect password
- Incorrect database name
- Network problem
- Firewall issue
- JDBC driver problem

## Troubleshooting Steps

1. Verify SQL Server is running.
2. Verify the database exists.
3. Verify the JDBC URL.
4. Verify the database username.
5. Verify the database password.
6. Verify the SQL Server port.
7. Check application logs.
8. Check HikariCP startup messages.

---

# 6. Datasource Configuration

Check the application configuration.

Example:

```yaml
spring:
  datasource:
    url: ${DEV_DB_URL}
    username: ${DEV_DB_USERNAME}
    password: ${DEV_DB_PASSWORD}
    driver-class-name: com.microsoft.sqlserver.jdbc.SQLServerDriver
```

Verify that the environment variables actually exist.

Do not commit actual credentials into Git.

---

# 7. Tables Are Not Created

## Symptoms

The application starts successfully but expected database tables do not exist.

## Possible Causes

- Entity is missing `@Entity`
- Entity package is not scanned
- Incorrect datasource configuration
- Connected to the wrong database
- Hibernate configuration is incorrect
- Table name is different from expected name

## Troubleshooting Steps

### Check Entity

Verify:

```java
@Entity
public class Employee {
}
```

### Check Table Mapping

Verify:

```java
@Table(name = "employeeInfo")
```

or the actual table name used by the project.

### Check JPA Configuration

For development:

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update
```

### Check Database

Verify that the application is connected to the expected database.

---

# 8. Verify Tables Using SQL

Employee table:

```sql
SELECT *
FROM employeeInfo;
```

Account table:

```sql
SELECT *
FROM accounts;
```

If the table names differ in the actual database, use the actual names.

---

# 9. Employee API Returns 404

## Example

```http
GET /api/v1/employees/100
```

Response:

```text
404 Not Found
```

## Possible Causes

- Employee ID does not exist
- Incorrect ID
- Incorrect endpoint
- Database record was deleted

## Troubleshooting

Check the database:

```sql
SELECT *
FROM employeeInfo
WHERE id = 100;
```

If no record exists, `404 Not Found` is the expected result.

---

# 10. Account API Returns 404

Example:

```http
GET /api/v1/accounts/100/balance
```

Check:

```sql
SELECT *
FROM accounts
WHERE id = 100;
```

If the account does not exist, the API should return:

```text
404 Not Found
```

---

# 11. 400 Bad Request

## Meaning

The client sent invalid request data.

Common causes:

- Missing required field
- Blank field
- Invalid email
- Negative salary
- Invalid amount
- Incorrect JSON structure
- Validation failure

Example:

```json
{
  "name": "",
  "email": "invalid-email",
  "salary": -500
}
```

Expected:

```text
400 Bad Request
```

---

# 12. Validation Error

The application uses Bean Validation annotations.

Examples:

```java
@NotBlank
@Email
@NotNull
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

If validation fails, `MethodArgumentNotValidException` is handled by the global exception handler.

---

# 13. 409 Conflict

## Meaning

The request conflicts with existing data.

In this application, a common example is duplicate employee email.

Example:

```text
Email:
sai@gmail.com
```

If the email already exists and another employee is created using the same email, the application returns:

```text
409 Conflict
```

## Troubleshooting

Check the database:

```sql
SELECT *
FROM employeeInfo
WHERE email = 'sai@gmail.com';
```

---

# 14. 422 Unprocessable Entity

## Meaning

The request is valid syntactically but violates a business rule.

Example:

```text
Account Balance = ₹1,000
Withdrawal = ₹5,000
```

The withdrawal should not be allowed.

The application can return:

```text
422 Unprocessable Entity
```

The error message should explain the business rule violation without exposing internal implementation details.

---

# 15. 500 Internal Server Error

## Meaning

An unexpected error occurred on the server.

Possible causes:

- Programming error
- Database failure
- Unexpected exception
- Configuration problem
- External service failure

## Troubleshooting Steps

1. Check application logs.
2. Find the exception.
3. Check the stack trace internally.
4. Check database connectivity.
5. Check Redis connectivity.
6. Check recent code changes.
7. Reproduce the issue.
8. Fix the root cause.
9. Run tests.
10. Retest the API.

The complete stack trace should not be returned to API clients.

---

# 16. Global Exception Handler Problems

The application uses:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
}
```

Commonly handled exceptions include:

```text
ResourceNotFoundException
DuplicateResourceException
MethodArgumentNotValidException
Exception
```

## If the expected error response is not returned

Check:

1. The exception class.
2. The `@ExceptionHandler` method.
3. The exception type.
4. The HTTP status.
5. The `ErrorResponse` structure.
6. The request path.
7. Application logs.

---

# 17. Error Response Validation

The standard error response contains:

```json
{
  "timestamp": "2026-08-11T10:00:00",
  "status": 400,
  "message": "Validation failed",
  "errors": [
    "email: must be a valid email"
  ],
  "path": "/api/v1/employees"
}
```

Verify that:

| Field | Expected |
|---|---|
| timestamp | Present |
| status | Correct HTTP status |
| message | User-friendly |
| errors | Validation details when applicable |
| path | Requested API path |

---

# 18. HikariCP Connection Pool Problems

## Symptoms

Possible symptoms include:

- Slow requests
- Connection timeout
- Requests waiting for database connections
- High pending connections
- Increased API response time

## Check Configuration

Example:

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 5
      minimum-idle: 1
      connection-timeout: 30000
      max-lifetime: 1800000
```

## Check Metrics

Useful metrics include:

```text
hikaricp.connections.active
hikaricp.connections.idle
hikaricp.connections.pending
hikaricp.connections.max
hikaricp.connections.min
```

---

# 19. Connection Pool Exhaustion

If all available connections are being used:

```text
Application
    |
    v
HikariCP
    |
    +-- Connection 1 - Busy
    +-- Connection 2 - Busy
    +-- Connection 3 - Busy
    +-- Connection 4 - Busy
    +-- Connection 5 - Busy
    |
    v
New Request
    |
    v
Waiting for Connection
```

## Troubleshooting

1. Check active connections.
2. Check pending connections.
3. Check slow database operations.
4. Check transaction duration.
5. Check application load.
6. Check SQL Server capacity.
7. Perform load testing.
8. Tune the pool based on test results.

Do not increase the pool size without checking database capacity.

---

# 20. Redis Connection Problem

## Symptoms

The application cannot connect to Redis.

Possible causes:

- Redis is not running
- Incorrect host
- Incorrect port
- Incorrect credentials
- Network problem
- Redis configuration problem

## Troubleshooting

Verify:

```text
REDIS_HOST
REDIS_PORT
```

Typical local Redis port:

```text
6379
```

Check application logs for Redis connection errors.

---

# 21. Redis Cache Not Working

## Symptoms

Repeated GET requests always access the database.

## Troubleshooting

Check whether caching is enabled:

```java
@EnableCaching
```

Check the read method:

```java
@Cacheable(value = "employees", key = "#id")
public EmployeeResponse getEmployeeById(Long id) {
    ...
}
```

Verify the cache name:

```text
employees
```

Verify the cache key:

```text
#id
```

Verify Redis connectivity.

---

# 22. Stale Cache Data

## Problem

The API returns old data after an update.

## Possible Cause

The cached value was not updated or evicted.

For update operations:

```java
@CachePut(value = "employees", key = "#id")
```

For delete operations:

```java
@CacheEvict(value = "employees", key = "#id")
```

Verify that the correct cache key is being used.

---

# 23. Cache Miss

A cache miss occurs when the requested data is not available in the cache.

The expected flow is:

```text
Request
   |
   v
Cache
   |
   +---- Hit ----> Return Cached Data
   |
   +---- Miss
          |
          v
      Database
          |
          v
       Cache
          |
          v
       Response
```

A cache miss is not necessarily an error.

---

# 24. Actuator Health Failure

Check:

```http
GET /actuator/health
```

If the status is:

```json
{
  "status": "DOWN"
}
```

inspect the detailed health information if available.

Check:

```text
Database
Redis
Custom health indicators
Application configuration
```

---

# 25. Actuator Metrics Not Available

If:

```text
/actuator/metrics
```

does not work, verify:

1. Actuator dependency exists.
2. Endpoint exposure is configured.
3. Application restarted after configuration changes.
4. The URL is correct.

Example configuration:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
```

Only expose the endpoints required by the application.

---

# 26. Liveness Probe Failure

Endpoint:

```text
/actuator/health/liveness
```

If liveness fails:

1. Check application logs.
2. Check JVM health.
3. Check application startup.
4. Check resource usage.
5. Restart the application only when appropriate.

---

# 27. Readiness Probe Failure

Endpoint:

```text
/actuator/health/readiness
```

A readiness failure generally means the application should not receive traffic.

Check:

```text
Database
Redis
Application initialization
External dependencies
Configuration
```

---

# 28. Unit Test Failure

Run:

```bash
mvn test
```

If a test fails, inspect:

```text
Test class
Test method
Expected value
Actual value
Mock configuration
Exception message
```

---

# 29. Mockito Test Failure

Common annotations:

```java
@ExtendWith(MockitoExtension.class)
@Mock
@InjectMocks
```

Example:

```java
@Mock
private EmployeeRepository employeeRepository;

@InjectMocks
private EmployeeServiceImpl employeeService;
```

Check whether the mocked repository method is configured correctly.

Example:

```java
when(employeeRepository.findById(1L))
        .thenReturn(Optional.of(employee));
```

---

# 30. Postman Status Code Test Failure

If a Postman test expecting `200` fails:

1. Check actual response status.
2. Check request URL.
3. Check request method.
4. Check request body.
5. Check application logs.
6. Check database data.
7. Check authentication if applicable.

Example test:

```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});
```

---

# 31. Postman Response Time Test Failure

A response-time test may fail because of:

- Slow database query
- Database connection acquisition
- Cache miss
- High application load
- JVM activity
- Local system resource usage
- Network latency

Example:

```javascript
pm.test("Response time is less than 500ms", function () {
    pm.expect(pm.response.responseTime).to.be.below(500);
});
```

The threshold should be based on the application's actual performance requirements.

Do not increase the threshold simply to make the test pass without investigating performance.

---

# 32. Postman Response Validation Failure

If response validation fails:

1. Check the actual response.
2. Compare it with the expected structure.
3. Check DTO mapping.
4. Check JSON property names.
5. Check whether fields are missing.
6. Update the test only if the API contract has intentionally changed.

Example:

```javascript
const response = pm.response.json();

pm.expect(response).to.have.property("id");
pm.expect(response).to.have.property("name");
pm.expect(response).to.have.property("email");
```

---

# 33. Employee Data Not Returned

If an employee exists in the database but the API does not return it:

Check:

1. Database connection.
2. Table name.
3. Entity mapping.
4. Repository query.
5. Employee ID.
6. DTO mapping.
7. Service method.
8. Controller response.

Verify directly:

```sql
SELECT *
FROM employeeInfo;
```

---

# 34. Account Balance Not Updated

If deposit or withdrawal does not update the balance:

Check:

1. Account ID.
2. Account exists.
3. Request amount.
4. Service business logic.
5. Repository save operation.
6. Transaction configuration.
7. Database record.

Verify:

```sql
SELECT id, balance
FROM accounts
WHERE id = 1;
```

---

# 35. Duplicate Employee Email Problem

If duplicate email validation does not work:

Check the repository method.

Example:

```java
Optional<Employee> findByEmail(String email);
```

Check the service:

```text
Check email
    |
    +---- Exists ----> DuplicateResourceException
    |
    +---- Does not exist
              |
              v
         Save Employee
```

Expected response for duplicate email:

```text
409 Conflict
```

---

# 36. Database Query Performance Problem

If an API is slow:

1. Check API response time.
2. Check database query execution time.
3. Check HikariCP connection acquisition.
4. Check database indexes.
5. Check the amount of data being returned.
6. Check pagination.
7. Check cache hit/miss behavior.
8. Test again under load.

For employee listing, pagination should be used instead of retrieving a very large dataset at once.

---

# 37. High Memory Usage

Check:

```text
/actuator/metrics/jvm.memory.used
```

Possible causes:

- Large response objects
- Large collections loaded into memory
- Cache size too large
- Memory leaks
- High concurrent requests

Check:

```text
JVM Heap
GC activity
Thread count
Cache size
```

---

# 38. High CPU Usage

Check:

```text
/actuator/metrics/process.cpu.usage
```

Possible causes:

- High request volume
- Expensive processing
- Slow algorithms
- Excessive logging
- Database-related application processing
- Garbage collection

Check application logs and metrics.

---

# 39. Logging Troubleshooting

When troubleshooting an incident, identify:

```text
Timestamp
Endpoint
HTTP method
HTTP status
Error message
Request/correlation ID if available
Relevant application logs
```

Do not log:

```text
Passwords
Tokens
Database credentials
Sensitive personal information
```

---

# 40. Generic Troubleshooting Checklist

When an API is failing, check the following:

- [ ] Application is running
- [ ] Correct URL is being used
- [ ] Correct HTTP method is being used
- [ ] Request body is valid
- [ ] Required headers are present
- [ ] Database is available
- [ ] Redis is available
- [ ] Requested record exists
- [ ] Validation rules are satisfied
- [ ] Business rules are satisfied
- [ ] Application logs are checked
- [ ] Actuator health is checked
- [ ] Relevant metrics are checked
- [ ] Postman request is correct

---

# 41. Troubleshooting Decision Flow

```text
                    API Failure
                        |
                        v
                Check HTTP Status
                        |
        +---------------+---------------+
        |               |               |
        v               v               v
       4xx             5xx        No Response
        |               |               |
        v               v               v
Check Request       Check Logs      Check Server
        |               |               |
        v               v               v
Check Data          Check DB       Check Network
        |               |               |
        v               v               v
Check Business      Check Redis    Check Port
Rules
```

---

# 42. Root Cause Documentation

When an issue is resolved, document:

```text
Issue
Impact
Root Cause
Resolution
Testing Performed
Preventive Action
Jira Ticket
```

Example:

```text
Issue:
Employee API returned 500.

Impact:
Employee retrieval was unavailable.

Root Cause:
Database connection configuration was incorrect.

Resolution:
Corrected the datasource configuration.

Testing:
Employee GET API and Actuator health endpoint were verified.

Preventive Action:
Environment configuration was documented.

Jira:
BANK-XXX
```

Replace `BANK-XXX` with the actual Jira ticket.

---

# 43. Troubleshooting Maintenance

This document should be updated whenever:

- A new production issue is identified.
- A new error pattern is discovered.
- A troubleshooting procedure changes.
- Infrastructure changes.
- Database configuration changes.
- Redis configuration changes.
- Monitoring configuration changes.
- A new operational dependency is introduced.

The troubleshooting guide should reflect the current application behavior.
