# Database Schema Documentation

## 1. Overview

The Banking Management System uses a relational database to store employee and banking account information.

The application uses:

- Microsoft SQL Server
- Spring Data JPA
- Hibernate
- HikariCP

The application communicates with the database through the following flow:

```text
Application
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

---

# 2. Database

The application uses Microsoft SQL Server as the relational database.

The database name depends on the environment configuration.

The database connection is configured through the Spring Boot datasource configuration.

Example:

```yaml
spring:
  datasource:
    url: ${DEV_DB_URL}
    username: ${DEV_DB_USERNAME}
    password: ${DEV_DB_PASSWORD}
```

Sensitive database credentials should be provided through environment variables and should not be committed to source control.

---

# 3. Employee Table

## Table Name

```text
employeeInfo
```

> Verify the exact table name against the `@Table` annotation in the current `Employee` entity.

## Purpose

The employee table stores employee information used by the Employee Management APIs.

---

## 3.1 Employee Table Structure

| Column | Description |
|---|---|
| id | Unique identifier for the employee |
| name | Employee name |
| email | Employee email address |
| department | Employee department |
| salary | Employee salary |

The exact SQL data types depend on the JPA entity configuration.

---

# 4. Employee Primary Key

The employee ID is the primary key.

Example entity mapping:

```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
```

The `@Id` annotation identifies the primary key.

The `@GeneratedValue` annotation allows the database to generate the ID.

---

# 5. Employee Email

The email field identifies the employee's email address.

The application validates the email using Bean Validation.

Example:

```java
@Email
private String email;
```

The application also checks for duplicate email addresses.

If an email already exists, the API returns:

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

# 6. Employee Validation

Employee request data is validated before being processed.

Typical validations include:

```text
Name should not be blank
Email should be valid
Salary should be positive
Required fields should be present
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

Invalid input results in:

```text
400 Bad Request
```

---

# 7. Accounts Table

## Table Name

```text
accounts
```

## Purpose

The accounts table stores banking account information.

It is used by the following APIs:

```text
Deposit
Withdraw
Balance
```

---

# 8. Accounts Table Structure

| Column | Description |
|---|---|
| id | Unique identifier for the account |
| balance | Current account balance |

> Verify the exact columns and data types against the current `Account` entity before using this document as a production database specification.

---

# 9. Account Primary Key

The account ID is the primary key.

Example:

```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
```

The ID uniquely identifies each account.

---

# 10. Account Balance

The balance represents the current amount available in the account.

Example:

```text
Account ID: 1
Balance: ₹25,000
```

A deposit increases the balance.

A withdrawal decreases the balance.

---

# 11. Deposit Operation

The deposit API is:

```http
POST /api/v1/accounts/{id}/deposit
```

Example request:

```json
{
  "amount": 5000
}
```

The application performs the following operations:

```text
Request
   |
   v
Find Account
   |
   v
Validate Amount
   |
   v
Increase Balance
   |
   v
Save Account
   |
   v
Database
```

Example:

```text
Existing Balance = ₹10,000
Deposit          = ₹5,000

New Balance      = ₹15,000
```

---

# 12. Withdrawal Operation

The withdrawal API is:

```http
POST /api/v1/accounts/{id}/withdraw
```

Example:

```json
{
  "amount": 1000
}
```

The application checks the account balance before performing the withdrawal.

Example:

```text
Existing Balance = ₹10,000
Withdrawal       = ₹3,000

New Balance      = ₹7,000
```

If the withdrawal violates a business rule such as insufficient balance, the operation is rejected.

The application can return:

```text
422 Unprocessable Entity
```

---

# 13. Account Balance API

The balance API is:

```http
GET /api/v1/accounts/{id}/balance
```

The application retrieves the account and returns its current balance.

Example:

```json
{
  "accountId": 1,
  "balance": 25000
}
```

If the account does not exist:

```text
404 Not Found
```

---

# 14. Entity-to-Table Mapping

JPA annotations are used to map Java entities to database tables.

Example:

```java
@Entity
@Table(name = "accounts")
public class Account {
}
```

The mapping is:

```text
Java Entity
     |
     v
JPA / Hibernate
     |
     v
Database Table
```

For example:

```text
Account Entity
      |
      v
accounts Table

Employee Entity
      |
      v
employeeInfo Table
```

The exact table name is determined by the entity mapping and application configuration.

---

# 15. Repository Layer

The application uses Spring Data JPA repositories to access database records.

Example:

```java
public interface EmployeeRepository
        extends JpaRepository<Employee, Long> {
}
```

And:

```java
public interface AccountRepository
        extends JpaRepository<Account, Long> {
}
```

Common repository methods include:

```text
save()
findById()
findAll()
delete()
deleteById()
existsById()
count()
```

These standard CRUD methods are provided by Spring Data JPA.

---

# 16. Custom Repository Methods

When the application requires a query that is not provided by standard repository methods, a custom repository method can be defined.

For example:

```java
Optional<Employee> findByEmail(String email);
```

Spring Data JPA can generate the query based on the method name.

The exact custom methods should match the current repository implementation.

---

# 17. Database Query Flow

For a request such as:

```http
GET /api/v1/employees/1
```

the flow is:

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
EmployeeRepository
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

The result is then returned through the same application layers.

---

# 18. HikariCP Connection Pool

HikariCP manages database connections for the application.

Instead of opening a new database connection for every request, the application maintains a pool of reusable connections.

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

The exact values should be tuned according to application load and database capacity.

---

# 19. HikariCP Settings

## maximum-pool-size

Defines the maximum number of database connections in the pool.

Example:

```yaml
maximum-pool-size: 5
```

This means the pool can have up to five active connections.

---

## minimum-idle

Defines the minimum number of idle connections that the pool attempts to maintain.

Example:

```yaml
minimum-idle: 1
```

---

## connection-timeout

Defines how long a request waits for a connection before timing out.

Example:

```yaml
connection-timeout: 30000
```

The value is in milliseconds.

Therefore:

```text
30000 ms = 30 seconds
```

---

## max-lifetime

Defines the maximum lifetime of a connection in the pool.

Example:

```yaml
max-lifetime: 1800000
```

The value is in milliseconds.

Therefore:

```text
1800000 ms = 30 minutes
```

---

# 20. Database Constraints

Database constraints help maintain data integrity.

Typical constraints include:

```text
Primary Key
Unique Constraint
Not Null Constraint
Check Constraint
```

The exact constraints should match the current database schema and entity configuration.

---

# 21. Primary Key

A primary key uniquely identifies each database record.

Employee example:

```text
employeeInfo.id
```

Account example:

```text
accounts.id
```

Primary keys should contain unique values.

---

# 22. Unique Constraint

Employee email can be protected from duplicates.

Example JPA configuration:

```java
@Column(unique = true)
private String email;
```

A unique constraint ensures that duplicate email values are not stored in the database.

The application should also perform an application-level duplicate check to provide a user-friendly response.

---

# 23. Data Integrity

The application should ensure that invalid data is not stored in the database.

Examples:

```text
Employee email should be valid
Employee salary should be positive
Account balance should follow business rules
Employee ID should be unique
Account ID should be unique
```

Both application validation and database constraints can be used where appropriate.

---

# 24. Sample Employee Data

Example:

| id | name | email | department | salary |
|---:|---|---|---|---:|
| 1 | Sai | sai@gmail.com | IT | 50000 |
| 2 | Ravi | ravi@gmail.com | HR | 45000 |
| 3 | Kumar | kumar@gmail.com | Finance | 55000 |

These values are sample documentation data and may not match the actual database.

---

# 25. Sample Account Data

Example:

| id | balance |
|---:|---:|
| 1 | 25000 |
| 2 | 50000 |
| 3 | 10000 |

These values are sample documentation data and may not match the actual database.

---

# 26. Database Verification Queries

To verify employee records:

```sql
SELECT *
FROM employeeInfo;
```

To verify account records:

```sql
SELECT *
FROM accounts;
```

To find an employee by ID:

```sql
SELECT *
FROM employeeInfo
WHERE id = 1;
```

To find an account by ID:

```sql
SELECT *
FROM accounts
WHERE id = 1;
```

---

# 27. Verify Employee Email

To check whether an email exists:

```sql
SELECT *
FROM employeeInfo
WHERE email = 'sai@gmail.com';
```

This can help troubleshoot duplicate email errors.

---

# 28. Verify Account Balance

To check an account balance:

```sql
SELECT id, balance
FROM accounts
WHERE id = 1;
```

This can be used to verify deposit and withdrawal operations.

---

# 29. Deposit Verification

After calling:

```http
POST /api/v1/accounts/1/deposit
```

verify the database:

```sql
SELECT id, balance
FROM accounts
WHERE id = 1;
```

Compare the database balance with the API response.

---

# 30. Withdrawal Verification

After calling:

```http
POST /api/v1/accounts/1/withdraw
```

verify:

```sql
SELECT id, balance
FROM accounts
WHERE id = 1;
```

Confirm that the balance was reduced correctly.

---

# 31. Database and Cache Consistency

When Redis caching is enabled, database and cache values should remain consistent.

For update operations:

```text
Update Database
      |
      v
Update Cache
```

For delete operations:

```text
Delete Database Record
      |
      v
Evict Cache Entry
```

For read operations:

```text
Check Cache
     |
     +---- Hit ----> Return Cached Data
     |
     +---- Miss
             |
             v
         Database
             |
             v
         Store Cache
             |
             v
         Return Data
```

---

# 32. Database Backup

Before performing significant database changes, an appropriate database backup should be taken according to the organization's database backup policy.

Backup procedures should be tested before being relied upon for recovery.

---

# 33. Database Migration

For production systems, schema changes should preferably be managed through a controlled database migration process.

Examples of database migration tools include:

```text
Flyway
Liquibase
```

A migration process provides:

- Version-controlled schema changes
- Repeatable deployments
- Better rollback planning
- Consistent environments

---

# 34. Development vs Production Database

Development and production databases should be kept separate.

```text
Development
     |
     v
Development Database

Production
     |
     v
Production Database
```

Never point a local development application to a production database unless explicitly authorized and protected by appropriate controls.

---

# 35. Database Security

Database credentials must not be stored directly in source code.

Avoid:

```yaml
spring:
  datasource:
    username: admin
    password: password123
```

Prefer:

```yaml
spring:
  datasource:
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
```

Sensitive values should be provided through environment variables or secure secret management.

---

# 36. Database Troubleshooting

If database operations fail, check the following:

1. SQL Server is running.
2. Database exists.
3. Database URL is correct.
4. Username is correct.
5. Password is correct.
6. JDBC driver is available.
7. Network connection is available.
8. HikariCP is initialized.
9. Database tables exist.
10. Entity mappings are correct.

---

# 37. Tables Not Created

If expected tables are not created:

### Check Entity

Verify that the class contains:

```java
@Entity
```

### Check Table Mapping

Verify:

```java
@Table(name = "employeeInfo")
```

or the appropriate table name.

### Check JPA Configuration

For development:

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update
```

### Check Database URL

Make sure the application is connected to the expected database.

### Check Application Logs

Look for Hibernate and datasource startup messages.

---

# 38. Schema Documentation Maintenance

This document should be updated whenever:

- A table is added.
- A table is removed.
- A column is added.
- A column is removed.
- A column type changes.
- A constraint changes.
- A relationship changes.
- Entity mappings change.
- Database migration scripts change.

The documented schema should always match the actual database schema.
