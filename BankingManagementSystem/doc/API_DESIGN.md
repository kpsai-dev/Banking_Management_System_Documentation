# API Design Document

## 1. Overview

The Banking Management System provides REST APIs for managing employees and performing banking account operations.

The APIs follow REST principles and use JSON for request and response data.

---

## 2. Base URL

For local development:

```text
http://localhost:8080
```

API version:

```text
/api/v1
```

---

# 3. Employee APIs

## 3.1 Get All Employees

### Endpoint

```http
GET /api/v1/employees
```

### Description

Retrieves a paginated list of employees.

### Query Parameters

| Parameter | Description | Example |
|---|---|---|
| page | Page number | 0 |
| size | Number of records per page | 10 |

### Example Request

```http
GET /api/v1/employees?page=0&size=10
```

### Success Response

```text
200 OK
```

### Example Response

```json
{
  "content": [
    {
      "id": 1,
      "name": "Sai",
      "email": "sai@gmail.com",
      "department": "IT",
      "salary": 50000
    }
  ],
  "page": 0,
  "size": 10
}
```

---

## 3.2 Get Employee by ID

### Endpoint

```http
GET /api/v1/employees/{id}
```

### Description

Retrieves a single employee using the employee ID.

### Path Parameter

| Parameter | Description |
|---|---|
| id | Employee ID |

### Example Request

```http
GET /api/v1/employees/1
```

### Success Response

```text
200 OK
```

### Example Response

```json
{
  "id": 1,
  "name": "Sai",
  "email": "sai@gmail.com",
  "department": "IT",
  "salary": 50000
}
```

### Employee Not Found

```text
404 Not Found
```

### Example Error Response

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

## 3.3 Create Employee

### Endpoint

```http
POST /api/v1/employees
```

### Content-Type

```text
application/json
```

### Description

Creates a new employee.

### Request Body

```json
{
  "name": "Sai",
  "email": "sai@gmail.com",
  "department": "IT",
  "salary": 50000
}
```

### Success Response

```text
201 Created
```

### Possible Error Responses

```text
400 Bad Request
409 Conflict
422 Unprocessable Entity
```

### 400 - Validation Failure

Returned when the request contains invalid data.

Example:

```json
{
  "name": "",
  "email": "invalid-email",
  "department": "IT",
  "salary": -100
}
```

### 409 - Duplicate Email

Returned when an employee already exists with the requested email.

### 422 - Business Rule Violation

Returned when the request violates an application business rule.

---

## 3.4 Update Employee

### Endpoint

```http
PUT /api/v1/employees/{id}
```

### Description

Updates an existing employee.

### Path Parameter

| Parameter | Description |
|---|---|
| id | Employee ID |

### Example Request

```http
PUT /api/v1/employees/1
```

### Request Body

```json
{
  "name": "Sai Kumar",
  "email": "saikumar@gmail.com",
  "department": "Development",
  "salary": 60000
}
```

### Success Response

```text
200 OK
```

### Possible Error Responses

```text
400 Bad Request
404 Not Found
409 Conflict
422 Unprocessable Entity
```

---

## 3.5 Delete Employee

### Endpoint

```http
DELETE /api/v1/employees/{id}
```

### Description

Deletes an existing employee.

### Example Request

```http
DELETE /api/v1/employees/1
```

### Success Response

```text
204 No Content
```

### Employee Not Found

```text
404 Not Found
```

---

# 4. Account APIs

## 4.1 Deposit Money

### Endpoint

```http
POST /api/v1/accounts/{id}/deposit
```

### Description

Deposits the specified amount into an existing account.

### Path Parameter

| Parameter | Description |
|---|---|
| id | Account ID |

### Example Request

```http
POST /api/v1/accounts/1/deposit
```

### Request Body

```json
{
  "amount": 5000
}
```

### Success Response

```text
200 OK
```

### Possible Error Responses

```text
400 Bad Request
404 Not Found
422 Unprocessable Entity
```

---

## 4.2 Withdraw Money

### Endpoint

```http
POST /api/v1/accounts/{id}/withdraw
```

### Description

Withdraws the specified amount from an existing account.

### Example Request

```http
POST /api/v1/accounts/1/withdraw
```

### Request Body

```json
{
  "amount": 1000
}
```

### Success Response

```text
200 OK
```

### Business Rule Violation

If the account does not have sufficient balance:

```text
422 Unprocessable Entity
```

---

## 4.3 Get Account Balance

### Endpoint

```http
GET /api/v1/accounts/{id}/balance
```

### Description

Returns the current balance of an account.

### Example Request

```http
GET /api/v1/accounts/1/balance
```

### Success Response

```text
200 OK
```

### Example Response

```json
{
  "accountId": 1,
  "balance": 25000
}
```

### Account Not Found

```text
404 Not Found
```

---

# 5. Error Response

All application exceptions are handled centrally by the `GlobalExceptionHandler`.

The API uses a standard error response format.

### Structure

```json
{
  "timestamp": "2026-08-11T10:00:00",
  "status": 404,
  "message": "Employee not found",
  "errors": [],
  "path": "/api/v1/employees/100"
}
```

### Fields

| Field | Description |
|---|---|
| timestamp | Time at which the error occurred |
| status | HTTP status code |
| message | User-friendly error message |
| errors | Detailed validation errors when applicable |
| path | API endpoint that generated the error |

---

# 6. HTTP Status Codes

| Status Code | Meaning | Example |
|---|---|---|
| 200 | OK | Successful GET, PUT, deposit, or withdraw |
| 201 | Created | Employee created successfully |
| 204 | No Content | Employee deleted successfully |
| 400 | Bad Request | Invalid input or validation failure |
| 404 | Not Found | Employee or account does not exist |
| 409 | Conflict | Duplicate email |
| 422 | Unprocessable Entity | Business rule violation |
| 500 | Internal Server Error | Unexpected application error |

---

# 7. Validation

Request DTOs use Bean Validation annotations where applicable.

Examples include:

```java
@NotBlank
@Email
@Positive
```

Invalid requests result in:

```text
400 Bad Request
```

Example:

```json
{
  "name": "",
  "email": "abc",
  "salary": -500
}
```

The `GlobalExceptionHandler` converts the validation exception into the standard error response.

---

# 8. Exception Mapping

| Exception | HTTP Status | Purpose |
|---|---:|---|
| `ResourceNotFoundException` | 404 | Requested resource does not exist |
| `DuplicateResourceException` | 409 | Resource already exists |
| `BusinessException` | 422 | Business rule violation |
| `MethodArgumentNotValidException` | 400 | Request validation failure |
| `Exception` | 500 | Unexpected error |

---

# 9. API Testing

The APIs can be tested using Postman.

The Postman collection should be organized as:

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

---

# 10. Environment Variables

The Postman environment should contain:

| Variable | Example |
|---|---|
| `baseUrl` | `http://localhost:8080` |
| `employeeId` | `1` |
| `accountId` | `1` |
| `token` | JWT token, if authentication is added |

Requests should use:

```text
{{baseUrl}}/api/v1/employees
```

instead of hardcoding:

```text
http://localhost:8080/api/v1/employees
```

---

# 11. Request Chaining

The ID returned by a create request can be stored as a Postman environment variable.

Example test script:

```javascript
let json = pm.response.json();

pm.environment.set("employeeId", json.id);
```

The stored ID can then be used in:

```text
{{baseUrl}}/api/v1/employees/{{employeeId}}
```

This allows requests to be chained without manually copying IDs.

---

# 12. API Documentation Maintenance

This document must be updated whenever:

- An endpoint is added or removed.
- An HTTP method changes.
- Request fields change.
- Response fields change.
- HTTP status codes change.
- Validation rules change.
- Error response structure changes.

API changes should also be associated with the appropriate Jira ticket.
