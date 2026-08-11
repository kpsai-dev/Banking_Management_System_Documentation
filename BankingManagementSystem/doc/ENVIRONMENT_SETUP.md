# Environment Setup Guide

## 1. Overview

This document explains how to set up the Banking Management System development environment.

The application is built using Spring Boot and uses SQL Server as the database and Redis for caching.

The setup includes:

- Java
- Maven
- Spring Boot
- SQL Server
- Redis
- IntelliJ IDEA
- Git
- Environment variables
- Application configuration
- Database configuration
- Redis configuration
- Running and testing the application

---

# 2. System Requirements

The following software is required:

| Software | Purpose |
|---|---|
| Java | Run the Spring Boot application |
| Maven | Build the project |
| IntelliJ IDEA | Development environment |
| Git | Source code management |
| SQL Server | Application database |
| Redis | Distributed caching |
| Postman | API testing |

---

# 3. Java Setup

The project requires a Java version compatible with the version configured in the `pom.xml`.

Check the installed Java version:

```bash
java -version
```

Example:

```text
java version "21"
```

The exact Java version should be verified from the project's `pom.xml`.

---

# 4. JAVA_HOME

The `JAVA_HOME` environment variable should point to the Java installation directory.

On Windows, verify:

```cmd
echo %JAVA_HOME%
```

If it is not configured, set it to the installed JDK directory.

Example:

```text
C:\Program Files\Java\jdk-21
```

The exact path depends on the local Java installation.

---

# 5. Maven Setup

Verify Maven:

```bash
mvn -version
```

The output should show:

```text
Apache Maven
Java version
Java home
Operating system
```

If Maven is not available, install Maven and configure it in the system PATH.

---

# 6. IntelliJ IDEA Setup

Open IntelliJ IDEA and select:

```text
File
  |
  v
Open
  |
  v
BankingManagementSystem
```

Wait for IntelliJ to import the Maven project.

The project should contain:

```text
pom.xml
src/main/java
src/main/resources
src/test/java
```

---

# 7. Configure Project SDK

In IntelliJ IDEA:

```text
File
  |
  v
Project Structure
  |
  v
Project
```

Select the Java version required by the project.

Make sure the Project SDK matches the Java version configured in `pom.xml`.

---

# 8. Maven Dependencies

The project's dependencies are defined in:

```text
pom.xml
```

Typical dependencies used by the project include:

```text
Spring Boot Web
Spring Data JPA
Validation
SQL Server JDBC Driver
Lombok
Spring Cache
Redis
Spring Boot Actuator
Spring Boot Test
Mockito
```

The exact dependency versions should be taken from the project's `pom.xml`.

---

# 9. Download Maven Dependencies

After opening the project, IntelliJ should automatically download Maven dependencies.

You can also run:

```bash
mvn clean install
```

This downloads required dependencies and builds the project.

---

# 10. Git Setup

Verify Git:

```bash
git --version
```

Clone the project:

```bash
git clone <repository-url>
```

Move into the project:

```bash
cd BankingManagementSystem
```

Replace `<repository-url>` with the actual repository URL.

---

# 11. SQL Server Setup

SQL Server is used as the application's relational database.

Before starting the application:

1. Start SQL Server.
2. Verify the SQL Server instance.
3. Verify the database exists.
4. Verify the username.
5. Verify the password.
6. Verify the SQL Server port.
7. Verify that the application can connect to the database.

---

# 12. Create the Database

Create the required database in SQL Server.

Example:

```sql
CREATE DATABASE BankingDB;
```

The actual database name should match the datasource configuration.

---

# 13. Verify Database

After creating the database:

```sql
SELECT name
FROM sys.databases;
```

Verify that the application database is present.

---

# 14. SQL Server JDBC URL

A SQL Server JDBC URL can look like:

```text
jdbc:sqlserver://localhost:1433;databaseName=BankingDB;encrypt=true;trustServerCertificate=true
```

The actual JDBC URL should be configured through the application's environment variables.

---

# 15. Environment Variables

Environment variables should be used for environment-specific configuration.

Typical variables include:

```text
DEV_DB_URL
DEV_DB_USERNAME
DEV_DB_PASSWORD
REDIS_HOST
REDIS_PORT
```

The exact names should match the current `application.yml`.

---

# 16. Database Environment Variables

Example:

```text
DEV_DB_URL=jdbc:sqlserver://localhost:1433;databaseName=BankingDB;encrypt=true;trustServerCertificate=true
DEV_DB_USERNAME=your_username
DEV_DB_PASSWORD=your_password
```

Do not commit actual passwords to Git.

---

# 17. Redis Setup

Redis is used as the distributed cache.

The application requires a Redis server when Redis caching is enabled.

Typical Redis configuration:

```yaml
spring:
  data:
    redis:
      host: ${REDIS_HOST}
      port: ${REDIS_PORT}
```

Typical local values are:

```text
REDIS_HOST=localhost
REDIS_PORT=6379
```

---

# 18. Verify Redis

Verify that Redis is running and accessible.

The default Redis port is:

```text
6379
```

If Redis is unavailable, the application may report connection errors depending on the configured cache and health-check behavior.

---

# 19. Application Configuration

The main configuration file is:

```text
src/main/resources/application.yml
```

Typical configuration sections include:

```text
Server
Datasource
JPA/Hibernate
HikariCP
Redis
Cache
Actuator
Logging
```

---

# 20. Example application.yml

A simplified configuration can look like:

```yaml
server:
  port: 8080

spring:
  datasource:
    url: ${DEV_DB_URL}
    username: ${DEV_DB_USERNAME}
    password: ${DEV_DB_PASSWORD}
    driver-class-name: com.microsoft.sqlserver.jdbc.SQLServerDriver

    hikari:
      maximum-pool-size: 5
      minimum-idle: 1
      connection-timeout: 30000
      max-lifetime: 1800000

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

  data:
    redis:
      host: ${REDIS_HOST}
      port: ${REDIS_PORT}
```

The exact configuration should match the current project.

---

# 21. HikariCP Configuration

The application uses HikariCP for database connection pooling.

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

Important settings:

```text
maximum-pool-size
minimum-idle
connection-timeout
max-lifetime
```

The values should be tuned based on load testing.

---

# 22. JPA Configuration

The application uses Spring Data JPA and Hibernate.

Development configuration can use:

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update
```

This allows Hibernate to update the database schema based on entity mappings.

For production environments, schema changes should preferably be managed using a controlled migration process.

---

# 23. Verify Entity Scanning

Entities should be located in a package scanned by the Spring Boot application.

Example:

```text
com.Tns.BankingManagementSystem.entity
```

An entity should contain:

```java
@Entity
```

Example:

```java
@Entity
@Table(name = "employeeInfo")
public class Employee {
}
```

---

# 24. Run the Application From IntelliJ

Open:

```text
BankingManagementSystemApplication.java
```

Run the main method.

Example:

```java
@SpringBootApplication
public class BankingManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(
                BankingManagementSystemApplication.class,
                args
        );
    }
}
```

---

# 25. Run the Application Using Maven

From the project root:

```bash
mvn spring-boot:run
```

The application should start on:

```text
http://localhost:8080
```

---

# 26. Verify Application Startup

Check the console for successful startup.

Important things to verify:

```text
Spring Boot started
Tomcat started
HikariCP initialized
Database connection successful
Redis connection successful
```

The exact startup messages depend on the application configuration.

---

# 27. Verify Actuator Health

Open:

```text
http://localhost:8080/actuator/health
```

Expected basic response:

```json
{
  "status": "UP"
}
```

If detailed health information is enabled, additional component information may be displayed.

---

# 28. Verify Actuator Metrics

Open:

```text
http://localhost:8080/actuator/metrics
```

This endpoint can expose metrics such as:

```text
JVM memory
CPU usage
Threads
HTTP requests
Garbage collection
HikariCP connections
```

Only expose the actuator endpoints required by the application.

---

# 29. Postman Setup

Install and open Postman.

Create an environment.

Example:

| Variable | Initial Value |
|---|---|
| baseUrl | http://localhost:8080 |
| employeeId | 1 |
| accountId | 1 |

Use:

```text
{{baseUrl}}
```

in API requests.

Example:

```text
{{baseUrl}}/api/v1/employees
```

---

# 30. Test Employee API

Test:

```http
GET /api/v1/employees
```

Example:

```text
http://localhost:8080/api/v1/employees?page=0&size=10
```

Expected:

```text
200 OK
```

---

# 31. Test Employee Creation

Send:

```http
POST /api/v1/employees
```

Example body:

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

# 32. Test Employee Validation

Send invalid data:

```json
{
  "name": "",
  "email": "invalid-email",
  "department": "IT",
  "salary": -100
}
```

Expected:

```text
400 Bad Request
```

---

# 33. Test Duplicate Email

Create an employee using an email that already exists.

Expected:

```text
409 Conflict
```

Example error:

```json
{
  "status": 409,
  "message": "Employee with this email already exists"
}
```

---

# 34. Test Account APIs

## Deposit

```http
POST /api/v1/accounts/{id}/deposit
```

Example body:

```json
{
  "amount": 5000
}
```

## Withdraw

```http
POST /api/v1/accounts/{id}/withdraw
```

Example body:

```json
{
  "amount": 1000
}
```

## Balance

```http
GET /api/v1/accounts/{id}/balance
```

---

# 35. Test Redis Caching

If Redis caching is enabled:

1. Call a cacheable GET API.
2. Call the same API again.
3. Verify that the second request can use cached data.
4. Check Redis if required.
5. Verify cache metrics if configured.

Example:

```java
@Cacheable(value = "employees", key = "#id")
public EmployeeResponse getEmployeeById(Long id) {
    ...
}
```

---

# 36. Test Cache Update

After updating an employee, verify that cached data is updated.

Example:

```java
@CachePut(value = "employees", key = "#id")
```

Then call:

```http
GET /api/v1/employees/{id}
```

Verify that the updated information is returned.

---

# 37. Test Cache Eviction

After deleting an employee:

```java
@CacheEvict(value = "employees", key = "#id")
```

Verify that the employee is no longer returned from the cache.

The database should also no longer contain the employee record.

---

# 38. Run Unit Tests

Run:

```bash
mvn test
```

The test suite should complete successfully.

Unit tests should cover:

```text
Happy paths
Edge cases
Exceptions
Validation
Business rules
```

---

# 39. Check Test Results

After running:

```bash
mvn test
```

Maven displays the test results in the console.

Test reports are generally available under:

```text
target/surefire-reports
```

---

# 40. Build the Application

Run:

```bash
mvn clean package
```

This:

1. Cleans the previous build.
2. Compiles the source code.
3. Runs tests.
4. Packages the application.

The generated JAR will be located inside:

```text
target/
```

---

# 41. Run the Generated JAR

After building:

```bash
java -jar target/BankingManagementSystem-0.0.1-SNAPSHOT.jar
```

The actual JAR filename depends on the project's Maven configuration.

---

# 42. Common Environment Setup Problems

## Java Not Found

Error:

```text
'java' is not recognized
```

Solution:

1. Install Java.
2. Configure `JAVA_HOME`.
3. Add Java to PATH.
4. Restart the terminal.
5. Run:

```bash
java -version
```

---

# 43. Maven Not Found

Error:

```text
'mvn' is not recognized
```

Solution:

1. Install Maven.
2. Configure Maven in PATH.
3. Verify:

```bash
mvn -version
```

---

# 44. Database Connection Error

Check:

```text
SQL Server running
Database exists
Database URL
Username
Password
Port
JDBC driver
Environment variables
```

---

# 45. Redis Connection Error

Check:

```text
Redis running
REDIS_HOST
REDIS_PORT
Network connectivity
Application configuration
```

---

# 46. Dependency Error

If a dependency is shown in red in IntelliJ:

1. Open `pom.xml`.
2. Check the dependency declaration.
3. Verify the version.
4. Reload Maven.
5. Run:

```bash
mvn clean install
```

6. Restart IntelliJ if necessary.

For Spring Boot dependencies, use versions compatible with the Spring Boot version used by the project.

---

# 47. Lombok Problems

If Lombok annotations are not recognized:

Check that Lombok is included in the Maven dependencies.

Then verify IntelliJ annotation processing settings.

Typical symptoms include:

```text
Cannot resolve method getName()
Cannot resolve method builder()
Cannot resolve constructor
```

After correcting the configuration, reload Maven and rebuild the project.

---

# 48. Environment Setup Checklist

## Java

- [ ] Java installed
- [ ] Correct Java version configured
- [ ] `JAVA_HOME` configured
- [ ] `java -version` works

## Maven

- [ ] Maven installed
- [ ] Maven available in PATH
- [ ] `mvn -version` works

## IntelliJ

- [ ] Project opened
- [ ] Correct Project SDK selected
- [ ] Maven project imported
- [ ] Dependencies downloaded

## SQL Server

- [ ] SQL Server running
- [ ] Database created
- [ ] Credentials configured
- [ ] JDBC URL configured
- [ ] Connection verified

## Redis

- [ ] Redis available
- [ ] Redis host configured
- [ ] Redis port configured
- [ ] Application can connect to Redis

## Application

- [ ] `application.yml` configured
- [ ] Environment variables configured
- [ ] Application starts successfully
- [ ] Actuator health is UP

## Testing

- [ ] Postman configured
- [ ] Employee APIs tested
- [ ] Account APIs tested
- [ ] Validation tested
- [ ] Exception handling tested
- [ ] Unit tests passed

---

# 49. Recommended Development Workflow

Use the following workflow when working on the project:

```text
Pull Latest Code
       |
       v
Open Project
       |
       v
Verify Environment
       |
       v
Configure Environment Variables
       |
       v
Start SQL Server
       |
       v
Start Redis
       |
       v
Run Spring Boot Application
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
Develop Changes
       |
       v
Run Tests Again
       |
       v
Build Application
       |
       v
Commit Changes
```

---

# 50. Environment Security

Never commit the following information to Git:

```text
Database passwords
Redis passwords
API keys
JWT secrets
Private keys
Production credentials
```

Use:

```text
Environment Variables
Secret Management
Application Configuration
```

instead.

---

# 51. Git Ignore

Sensitive local configuration files should be excluded from version control when appropriate.

Example `.gitignore` entries:

```text
.env
*.log
target/
.idea/
```

Do not blindly ignore configuration files that are required by the team. Follow the project's repository standards.

---

# 52. Environment Maintenance

This document should be updated whenever:

- Java version changes.
- Spring Boot version changes.
- Maven version requirements change.
- SQL Server configuration changes.
- Redis configuration changes.
- Environment variables change.
- New dependencies are added.
- New development tools are required.
- Application startup procedure changes.

The environment setup documentation should always represent the current project requirements.
