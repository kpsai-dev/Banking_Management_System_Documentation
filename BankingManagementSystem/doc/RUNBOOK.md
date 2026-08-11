# Runbook

## 1. Overview

This runbook provides standard procedures for operating, monitoring, and troubleshooting the Banking Management System.

It is intended to help developers and support teams perform common application operations consistently.

---

# 2. Application Information

| Item | Value |
|---|---|
| Application | Banking Management System |
| Framework | Spring Boot |
| Database | SQL Server |
| ORM | Spring Data JPA / Hibernate |
| Connection Pool | HikariCP |
| Cache | Redis |
| API Testing | Postman |
| Monitoring | Spring Boot Actuator |
| Default Port | 8080 |

---

# 3. Starting the Application

## 3.1 Start From IntelliJ IDEA

1. Open the Banking Management System project.
2. Open `BankingManagementSystemApplication.java`.
3. Run the Spring Boot application.
4. Check the console for successful startup messages.
5. Verify that the application is listening on port `8080`.

The default application URL is:

```text
http://localhost:8080
```

---

## 3.2 Start Using Maven

Open a terminal in the project directory.

Run:

```bash
mvn spring-boot:run
```

The application should start successfully.

---

# 4. Stop the Application

If the application is running from IntelliJ:

1. Open the Run/Console window.
2. Click the Stop button.

If the application is running from a terminal:

```text
Ctrl + C
```

---

# 5. Verify Application Health

Spring Boot Actuator provides health information.

Use:

```http
GET /actuator/health
```

Example:

```text
http://localhost:8080/actuator/health
```

Expected response:

```json
{
  "status": "UP"
}
```

If health information includes additional components, the response may contain database, Redis, and other health information.

---

# 6. Check Application Information

The application information endpoint is:

```http
GET /actuator/info
```

Example:

```text
http://localhost:8080/actuator/info
```

This endpoint can provide application and build information when configured.

---

# 7. Check Application Metrics

Spring Boot Actuator exposes application metrics through:

```http
GET /actuator/metrics
```

Example:

```text
http://localhost:8080/actuator/metrics
```

Useful metrics can include:

```text
JVM memory
CPU usage
HTTP requests
Thread count
Garbage collection
HikariCP connections
```

---

# 8. Check JVM Memory

Use the JVM memory metric:

```text
/actuator/metrics/jvm.memory.used
```

Example:

```http
GET /actuator/metrics/jvm.memory.used
```

This can help identify increasing memory consumption.

---

# 9. Check JVM Threads

Use:

```text
/actuator/metrics/jvm.threads.live
```

Example:

```http
GET /actuator/metrics/jvm.threads.live
```

This provides information about the number of currently live JVM threads.

---

# 10. Check HTTP Request Metrics

HTTP server request metrics can be inspected using:

```text
/actuator/metrics/http.server.requests
```

Example:

```http
GET /actuator/metrics/http.server.requests
```

This can help identify:

- Number of requests
- Response times
- HTTP status codes
- Frequently accessed endpoints

---

# 11. Check HikariCP Connection Pool

The application uses HikariCP for database connection pooling.

Important configuration values include:

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 5
      minimum-idle: 1
      connection-timeout: 30000
      max-lifetime: 1800000
```

Important HikariCP metrics include:

```text
hikaricp.connections
hikaricp.connections.active
hikaricp.connections.idle
hikaricp.connections.pending
hikaricp.connections.max
hikaricp.connections.min
```

These metrics help determine whether the connection pool is sufficient for the current load.

---

# 12. Connection Pool Exhaustion

## Symptoms

Possible symptoms include:

- Slow API responses
- Requests waiting for database connections
- Connection timeout errors
- High number of pending connections
- Increased response time

Example error:

```text
Connection is not available, request timed out
```

## Actions

1. Check HikariCP metrics.
2. Check database availability.
3. Check active connections.
4. Check whether long-running database operations exist.
5. Check application load.
6. Review `maximum-pool-size`.
7. Perform load testing before increasing the pool size.
8. Verify that increasing the pool does not overload SQL Server.

Do not increase the connection pool size blindly.

---

# 13. Check Database Connectivity

If database-related errors occur:

1. Verify SQL Server is running.
2. Verify database name.
3. Verify database URL.
4. Verify username and password.
5. Verify JDBC driver configuration.
6. Check firewall/network connectivity.
7. Check HikariCP metrics.
8. Restart the application after correcting configuration if necessary.

Example configuration:

```yaml
spring:
  datasource:
    url: ${DEV_DB_URL}
    driver-class-name: com.microsoft.sqlserver.jdbc.SQLServerDriver
```

---

# 14. Check Redis Connectivity

The application uses Redis for caching.

If Redis is configured as a local service, verify that Redis is running and accessible.

Check:

```text
Redis host
Redis port
Redis credentials, if configured
Network connectivity
```

Typical Redis port:

```text
6379
```

If Redis is unavailable, check the application logs and Actuator health information.

---

# 15. Cache Troubleshooting

## Problem

The application is not returning cached data.

## Check

1. Verify Redis is running.
2. Verify Redis configuration.
3. Verify caching is enabled.
4. Check whether `@Cacheable` is present on the appropriate read method.
5. Check the cache name.
6. Check the cache key.
7. Verify that the method is being called through the Spring proxy.
8. Check Redis data.

Example:

```java
@Cacheable(value = "employees", key = "#id")
public EmployeeResponse getEmployeeById(Long id) {
    ...
}
```

Remember:

```text
employees
```

is the cache name.

It is not necessarily the database table name.

---

# 16. Employee API Operations

## Get All Employees

```http
GET /api/v1/employees
```

Example:

```text
http://localhost:8080/api/v1/employees?page=0&size=10
```

Expected successful response:

```text
200 OK
```

---

## Get Employee

```http
GET /api/v1/employees/{id}
```

Example:

```text
http://localhost:8080/api/v1/employees/1
```

Expected:

```text
200 OK
```

If the employee does not exist:

```text
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

Expected:

```text
201 Created
```

---

## Update Employee

```http
PUT /api/v1/employees/{id}
```

Example:

```text
http://localhost:8080/api/v1/employees/1
```

Expected:

```text
200 OK
```

---

## Delete Employee

```http
DELETE /api/v1/employees/{id}
```

Expected:

```text
204 No Content
```

---

# 17. Banking API Operations

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

Expected:

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

Expected:

```text
200 OK
```

If the withdrawal violates a business rule, such as insufficient balance:

```text
422 Unprocessable Entity
```

---

## Check Balance

```http
GET /api/v1/accounts/{id}/balance
```

Expected:

```text
200 OK
```

If the account does not exist:

```text
404 Not Found
```

---

# 18. Common HTTP Errors

## 400 Bad Request

### Possible Causes

- Invalid request body
- Missing required field
- Invalid email
- Negative salary
- Invalid amount

### Action

Check the request body and validation messages.

---

## 404 Not Found

### Possible Causes

- Employee ID does not exist
- Account ID does not exist
- Incorrect URL

### Action

Verify the ID and endpoint.

---

## 409 Conflict

### Possible Cause

Duplicate employee email.

### Action

Check whether the email already exists in the database.

---

## 422 Unprocessable Entity

### Possible Cause

Business rule violation.

Example:

```text
Withdrawal amount > account balance
```

### Action

Check the business rule and request data.

---

## 500 Internal Server Error

### Possible Causes

- Unexpected application exception
- Database problem
- Configuration problem
- External service problem
- Programming error

### Action

1. Check application logs.
2. Check stack trace.
3. Check database connectivity.
4. Check Redis connectivity.
5. Check recent code/configuration changes.
6. Do not expose internal details to API clients.

---

# 19. Global Exception Handling

The application uses:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
}
```

The handler manages common exceptions centrally.

Main exceptions include:

```text
ResourceNotFoundException
DuplicateResourceException
MethodArgumentNotValidException
Exception
```

The API returns a standard error response.

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

Internal implementation details should not be exposed to API clients.

---

# 20. Logging

Application logs should be checked whenever an unexpected problem occurs.

Important log levels:

```text
INFO
WARN
ERROR
DEBUG
```

Use:

```text
INFO
```

for normal application events.

Use:

```text
WARN
```

for potentially problematic situations.

Use:

```text
ERROR
```

for exceptions and failures.

Avoid logging:

```text
Passwords
Authentication tokens
Sensitive personal information
Database credentials
```

---

# 21. Load Testing

Load testing can be performed using Postman's load testing functionality.

Important metrics to monitor:

| Metric | Purpose |
|---|---|
| Requests/second | Measures throughput |
| Average response time | Measures normal response performance |
| P90 | Response time for 90% of requests |
| P95 | Response time for 95% of requests |
| P99 | Response time for 99% of requests |
| Error % | Measures failed requests |
| CPU | Measures CPU utilization |
| Memory | Measures memory utilization |

Example load test observations:

```text
Total requests: 3313
Requests/second: approximately 54
Average response time: approximately 288 ms
P90: approximately 309 ms
P95: approximately 313 ms
P99: approximately 337 ms
Error percentage: 0%
```

The exact values depend on the machine, database, application configuration, and load test configuration.

---

# 22. Load Testing HikariCP

When testing connection pool settings:

1. Start the application.
2. Configure an initial HikariCP pool size.
3. Run a Postman load test.
4. Observe response time.
5. Observe error percentage.
6. Observe HikariCP metrics.
7. Increase or decrease the pool size.
8. Repeat the test.
9. Compare the results.

Example configuration:

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 5
      minimum-idle: 1
```

The objective is not to use the largest possible pool.

The objective is to find a pool size that provides good performance without unnecessarily consuming database connections.

---

# 23. Application Restart Procedure

Restart the application when required after configuration or deployment changes.

### Procedure

1. Stop the current application.
2. Verify that the process has stopped.
3. Start the application again.
4. Check startup logs.
5. Check `/actuator/health`.
6. Test an API endpoint.
7. Check database connectivity.
8. Check Redis connectivity if caching is enabled.

---

# 24. Configuration Change Procedure

Before changing configuration:

1. Identify the required configuration change.
2. Record the current value.
3. Change the configuration.
4. Restart the application if required.
5. Verify application health.
6. Test the affected functionality.
7. Monitor metrics.
8. Document the change.

Important configuration areas include:

```text
Database
HikariCP
Redis
Cache
Actuator
Server
Logging
```

---

# 25. Database Change Procedure

Before making database changes:

1. Verify the target database.
2. Take an appropriate backup if required.
3. Review the SQL statement.
4. Test the change in a non-production environment.
5. Apply the change.
6. Verify the affected tables.
7. Test the application.
8. Document the change.

Avoid making untested database changes directly in production.

---

# 26. Cache Clear Procedure

When cached data becomes stale, the cache may need to be cleared.

The application can use Spring Cache eviction.

Example:

```java
@CacheEvict(value = "employees", key = "#id")
public void deleteEmployee(Long id) {
    ...
}
```

For an entire cache:

```java
@CacheEvict(value = "employees", allEntries = true)
public void clearEmployeeCache() {
}
```

Cache clearing should be performed carefully because it can temporarily increase database traffic.

---

# 27. Health Check Procedure

When investigating an application problem:

### Step 1

Check:

```text
/actuator/health
```

### Step 2

Check application logs.

### Step 3

Check database connectivity.

### Step 4

Check Redis connectivity.

### Step 5

Check HikariCP metrics.

### Step 6

Check JVM metrics.

### Step 7

Test the affected API using Postman.

---

# 28. Incident Troubleshooting Flow

Use the following sequence when an API is failing:

```text
API Failure
    |
    v
Check HTTP Status
    |
    +---- 400
    |      |
    |      v
    |   Check Request
    |
    +---- 404
    |      |
    |      v
    |   Check Resource ID
    |
    +---- 409
    |      |
    |      v
    |   Check Duplicate Data
    |
    +---- 422
    |      |
    |      v
    |   Check Business Rules
    |
    +---- 500
           |
           v
      Check Logs
           |
           v
      Check Database
           |
           v
      Check Redis
           |
           v
      Check Configuration
```

---

# 29. Production Safety Guidelines

Before performing production operations:

- Verify the target environment.
- Verify the database.
- Verify the requested change.
- Take backups when appropriate.
- Avoid untested configuration changes.
- Avoid exposing sensitive information.
- Monitor the application after changes.
- Record important operational changes.

---

# 30. Quick Reference

## Application

```text
http://localhost:8080
```

## Health

```text
/actuator/health
```

## Metrics

```text
/actuator/metrics
```

## Employee APIs

```text
GET    /api/v1/employees
GET    /api/v1/employees/{id}
POST   /api/v1/employees
PUT    /api/v1/employees/{id}
DELETE /api/v1/employees/{id}
```

## Account APIs

```text
POST /api/v1/accounts/{id}/deposit
POST /api/v1/accounts/{id}/withdraw
GET  /api/v1/accounts/{id}/balance
```

## Main Dependencies

```text
Spring Boot
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
```

---

# 31. Runbook Maintenance

This runbook should be updated whenever:

- A new operational procedure is introduced.
- Infrastructure configuration changes.
- Database configuration changes.
- Redis configuration changes.
- HikariCP configuration changes.
- New monitoring endpoints are introduced.
- API endpoints change.
- A recurring production issue is identified.
- A troubleshooting procedure is improved.

The runbook should always reflect the current application configuration and operational procedures.
