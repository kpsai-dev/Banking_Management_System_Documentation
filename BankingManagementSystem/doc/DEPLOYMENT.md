# Deployment Documentation

## 1. Overview

This document describes the deployment procedure for the Banking Management System.

The application is a Spring Boot REST API that uses SQL Server as the database and Redis for caching.

The deployment process covers:

- Application build
- Configuration
- Database configuration
- Redis configuration
- JAR deployment
- Application startup
- Health verification
- Monitoring
- Rollback

---

# 2. Application Architecture

The deployment architecture is:

```text
                    Client
                      |
                      v
                Spring Boot API
                      |
          +-----------+-----------+
          |                       |
          v                       v
       Redis                  SQL Server
       Cache                  Database
```

The Spring Boot application communicates with SQL Server through HikariCP and Spring Data JPA.

Redis is used for caching frequently accessed data.

---

# 3. Deployment Prerequisites

Before deploying the application, verify that the following are available.

| Requirement | Purpose |
|---|---|
| Java | Runs the Spring Boot application |
| Maven | Builds the application |
| SQL Server | Stores application data |
| Redis | Provides distributed caching |
| Git | Source code management |
| Environment variables | Stores environment-specific configuration |

---

# 4. Required Java Version

Verify the installed Java version:

```bash
java -version
```

The Java version should match the version configured in the project's `pom.xml`.

Example:

```text
Java 21
```

Use the Java version required by the project.

---

# 5. Verify Maven

Check Maven installation:

```bash
mvn -version
```

The command should display the Maven version and Java version.

---

# 6. Get the Source Code

Clone the project repository:

```bash
git clone <repository-url>
```

Move into the project directory:

```bash
cd BankingManagementSystem
```

Replace `<repository-url>` with the actual repository URL.

---

# 7. Verify Project Structure

The project should contain a structure similar to:

```text
BankingManagementSystem/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   └── resources/
│   │
│   └── test/
│
├── pom.xml
└── README.md
```

The main configuration file is:

```text
src/main/resources/application.yml
```

---

# 8. Configure Environment Variables

Environment-specific values should not be hardcoded in the application.

For example:

```yaml
spring:
  datasource:
    url: ${DEV_DB_URL}
```

The corresponding environment variable should contain the SQL Server JDBC URL.

Example:

```text
DEV_DB_URL=jdbc:sqlserver://localhost:1433;databaseName=BankingDB;encrypt=true;trustServerCertificate=true
```

Other environment variables may include:

```text
DEV_DB_USERNAME
DEV_DB_PASSWORD
REDIS_HOST
REDIS_PORT
```

The exact variables should match the project's `application.yml`.

---

# 9. Database Preparation

Before starting the application, verify that SQL Server is available.

Check:

- SQL Server service
- Database server
- Database name
- Username
- Password
- Network connectivity

Example database configuration:

```yaml
spring:
  datasource:
    url: ${DEV_DB_URL}
    username: ${DEV_DB_USERNAME}
    password: ${DEV_DB_PASSWORD}
    driver-class-name: com.microsoft.sqlserver.jdbc.SQLServerDriver
```

---

# 10. Database Schema Creation

The application uses JPA/Hibernate for entity mapping.

For development environments, Hibernate can create or update tables depending on the configured value of:

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update
```

For production environments, database schema changes should preferably be managed using a proper database migration strategy.

Do not rely on automatic schema modification for critical production databases without reviewing the impact.

---

# 11. Redis Preparation

Redis must be available when the application is configured to use Redis as its cache.

Typical Redis configuration:

```yaml
spring:
  data:
    redis:
      host: ${REDIS_HOST}
      port: ${REDIS_PORT}
```

The default Redis port is:

```text
6379
```

Verify that the application can connect to Redis before deployment.

---

# 12. Build the Application

From the project root directory, run:

```bash
mvn clean package
```

This performs the following:

1. Cleans previous build files.
2. Compiles the source code.
3. Runs tests.
4. Packages the application.

If the build succeeds, Maven creates a JAR file inside:

```text
target/
```

Example:

```text
target/BankingManagementSystem-0.0.1-SNAPSHOT.jar
```

The actual file name depends on the project's Maven configuration.

---

# 13. Skip Tests During Build

If there is a specific reason to skip tests:

```bash
mvn clean package -DskipTests
```

However, tests should normally be executed before deployment.

The recommended command is:

```bash
mvn clean package
```

---

# 14. Verify the Build

After the build completes, check the `target` directory:

```text
target/
└── BankingManagementSystem-0.0.1-SNAPSHOT.jar
```

Verify that the JAR file exists before starting deployment.

---

# 15. Run the JAR

The application can be started using:

```bash
java -jar target/BankingManagementSystem-0.0.1-SNAPSHOT.jar
```

The application should start and display Spring Boot startup messages.

---

# 16. Configure Application Port

The default application port is:

```text
8080
```

It can be configured in `application.yml`:

```yaml
server:
  port: 8080
```

The application can then be accessed using:

```text
http://localhost:8080
```

---

# 17. Verify Application Startup

After starting the application, check the console logs.

Look for messages indicating that:

- Spring Boot started successfully.
- The embedded server started.
- The application connected to the database.
- HikariCP initialized successfully.
- Redis connected successfully if Redis is configured.

---

# 18. Health Check

After deployment, verify the Actuator health endpoint:

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

If the application uses readiness and liveness probes, verify:

```text
/actuator/health/liveness
/actuator/health/readiness
```

---

# 19. API Verification

After confirming that the application is healthy, test the main APIs.

## Employee API

```http
GET /api/v1/employees
```

Expected:

```text
200 OK
```

## Employee By ID

```http
GET /api/v1/employees/1
```

Expected:

```text
200 OK
```

## Account Balance

```http
GET /api/v1/accounts/1/balance
```

Expected:

```text
200 OK
```

Use Postman to perform complete API verification.

---

# 20. Postman Verification

After deployment, import the project Postman collection.

Set:

```text
baseUrl=http://localhost:8080
```

Run the following requests:

```text
Employee APIs
    |
    +-- Get All Employees
    +-- Get Employee
    +-- Create Employee
    +-- Update Employee
    +-- Delete Employee

Account APIs
    |
    +-- Deposit
    +-- Withdraw
    +-- Balance
```

Verify:

- Status codes
- Response body
- Validation
- Error handling
- Response time

---

# 21. Verify Database Connectivity

After deployment, verify that API operations are reflected in SQL Server.

For example, after creating an employee:

```sql
SELECT *
FROM employeeInfo;
```

Verify that the new record exists.

For account operations:

```sql
SELECT *
FROM accounts;
```

Verify that the account balance is updated correctly.

---

# 22. Verify Redis Caching

If Redis caching is enabled:

1. Call a cacheable GET API.
2. Check the response.
3. Call the same API again.
4. Verify cache behavior.
5. Monitor cache metrics if available.

Example cacheable method:

```java
@Cacheable(value = "employees", key = "#id")
public EmployeeResponse getEmployeeById(Long id) {
    ...
}
```

The cache name:

```text
employees
```

is the Spring cache name.

---

# 23. Verify HikariCP

Check Actuator metrics for HikariCP.

Useful metrics include:

```text
hikaricp.connections.active
hikaricp.connections.idle
hikaricp.connections.pending
hikaricp.connections.max
hikaricp.connections.min
```

These metrics help determine whether the configured connection pool is sufficient.

---

# 24. Production Configuration

Production configuration should be different from local development configuration.

Typical production considerations include:

```text
Database credentials
Database URL
Redis host
Redis credentials
Server port
Logging level
Actuator exposure
HikariCP configuration
Cache configuration
Security configuration
```

Sensitive values should be provided through environment variables or a secure configuration system.

---

# 25. Do Not Hardcode Secrets

Do not store sensitive values directly in source code.

Avoid:

```yaml
spring:
  datasource:
    username: admin
    password: mypassword
```

Prefer:

```yaml
spring:
  datasource:
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
```

This prevents sensitive credentials from being committed to Git.

---

# 26. Deployment Using Docker

The Spring Boot application can also be packaged as a Docker image.

Example Dockerfile:

```dockerfile
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/BankingManagementSystem-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

The Java version should match the version required by the project.

---

# 27. Build Docker Image

After building the application:

```bash
mvn clean package
```

Build the Docker image:

```bash
docker build -t banking-management-system .
```

Verify the image:

```bash
docker images
```

---

# 28. Run Docker Container

Run:

```bash
docker run -p 8080:8080 banking-management-system
```

The application should then be available at:

```text
http://localhost:8080
```

Environment variables can be passed to the container using:

```bash
docker run \
  -p 8080:8080 \
  -e DEV_DB_URL="<database-url>" \
  -e DEV_DB_USERNAME="<username>" \
  -e DEV_DB_PASSWORD="<password>" \
  banking-management-system
```

Do not place real production credentials in documentation.

---

# 29. Kubernetes Deployment

The application can be deployed to Kubernetes as a container.

A typical architecture is:

```text
                 Kubernetes Cluster
                        |
                        v
                 Service / Ingress
                        |
                        v
                 Spring Boot Pods
                   /          \
                  /            \
                 v              v
              Redis          SQL Server
```

The exact Kubernetes configuration depends on the deployment environment.

---

# 30. Liveness Probe

The liveness probe checks whether the application is alive.

Example endpoint:

```text
/actuator/health/liveness
```

A Kubernetes configuration can use:

```yaml
livenessProbe:
  httpGet:
    path: /actuator/health/liveness
    port: 8080
  initialDelaySeconds: 30
  periodSeconds: 10
```

---

# 31. Readiness Probe

The readiness probe checks whether the application is ready to receive traffic.

Example:

```text
/actuator/health/readiness
```

Example configuration:

```yaml
readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 8080
  initialDelaySeconds: 30
  periodSeconds: 10
```

---

# 32. Deployment Verification Checklist

After deployment, verify:

- [ ] Application started successfully
- [ ] Port is accessible
- [ ] Database connection is successful
- [ ] Redis connection is successful
- [ ] HikariCP initialized
- [ ] Actuator health is UP
- [ ] Liveness endpoint is working
- [ ] Readiness endpoint is working
- [ ] Employee APIs work
- [ ] Account APIs work
- [ ] Validation works
- [ ] Global exception handling works
- [ ] Redis caching works
- [ ] Logs are being generated
- [ ] No unexpected errors are present

---

# 33. Rollback Procedure

If a deployment introduces a critical problem:

1. Stop or remove the problematic application version.
2. Deploy the previously working version.
3. Verify application startup.
4. Check database connectivity.
5. Check Redis connectivity.
6. Verify Actuator health.
7. Test critical APIs.
8. Monitor application logs.
9. Document the incident.

Example:

```text
Current Version
      |
      | Deployment problem
      v
Rollback
      |
      v
Previous Stable Version
      |
      v
Health Check
      |
      v
API Verification
```

---

# 34. Database Rollback

Database changes require special attention.

Before applying production database changes:

- Create an appropriate backup.
- Test the migration.
- Verify rollback steps.
- Confirm application compatibility.

Do not automatically roll back application code while ignoring incompatible database changes.

---

# 35. Post-Deployment Monitoring

After deployment, monitor:

```text
Application Health
CPU Usage
Memory Usage
JVM Heap
Garbage Collection
Thread Count
HTTP Request Metrics
Response Time
Error Rate
HikariCP Connections
Redis Cache
Database Performance
```

The application can be monitored through Spring Boot Actuator and the organization's monitoring platform.

---

# 36. Common Deployment Problems

## Application Does Not Start

Check:

```text
Java version
Configuration
Database connection
Redis connection
Port availability
Application logs
```

---

## Database Connection Failure

Check:

```text
Database server
Database URL
Username
Password
JDBC driver
Network connection
Firewall
```

---

## Redis Connection Failure

Check:

```text
Redis service
Redis host
Redis port
Network connectivity
Redis credentials
Application configuration
```

---

## Port Already in Use

If port `8080` is already being used, either stop the process using that port or configure another application port.

Example:

```yaml
server:
  port: 8081
```

---

# 37. Deployment Best Practices

1. Build the application before deployment.
2. Run unit tests.
3. Verify database connectivity.
4. Verify Redis connectivity.
5. Do not hardcode secrets.
6. Use environment-specific configuration.
7. Monitor the application after deployment.
8. Verify health endpoints.
9. Test critical APIs.
10. Maintain a rollback plan.
11. Document deployment changes.
12. Use versioned application artifacts.

---

# 38. Deployment Checklist

## Before Deployment

- [ ] Code reviewed
- [ ] Unit tests passed
- [ ] Application builds successfully
- [ ] Database migration reviewed
- [ ] Configuration verified
- [ ] Environment variables configured
- [ ] Redis availability verified
- [ ] Deployment artifact generated
- [ ] Rollback plan prepared

## During Deployment

- [ ] Application deployed
- [ ] Application started
- [ ] Logs checked
- [ ] Database connection verified
- [ ] Redis connection verified

## After Deployment

- [ ] Health check passed
- [ ] Readiness check passed
- [ ] Liveness check passed
- [ ] Employee APIs tested
- [ ] Account APIs tested
- [ ] Error handling tested
- [ ] Cache verified
- [ ] Metrics monitored
- [ ] No critical errors found

---

# 39. Deployment Maintenance

This document should be updated whenever:

- Deployment procedures change.
- Infrastructure changes.
- Docker configuration changes.
- Kubernetes configuration changes.
- Database deployment procedures change.
- Environment variables change.
- Application startup procedures change.
- Monitoring procedures change.
- Rollback procedures change.

The deployment documentation should always reflect the current deployment process.
