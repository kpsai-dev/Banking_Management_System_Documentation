# Postman Employee API - API Design Document

## 1. Overview

The Postman Employee API is a Spring Boot REST application used to manage departments and employees.

The API provides CRUD operations for:

- Departments
- Employees

Employees are associated with departments using a many-to-one relationship.

---

## 2. Base URL

Local development base URL:

```text
http://localhost:8080
```

API base path:

```text
/api
```

---

## 3. Content Type

Requests containing a body use:

```http
Content-Type: application/json
```

Responses are returned in JSON format except DELETE operations, which return no response body.

---

# 4. Department Endpoints

## 4.1 Create Department

Creates a new department.

### Endpoint

```http
POST /api/departments
```

### Request Body

```json
{
  "name": "Information Technology",
  "location": "Bangalore"
}
```

### Validation

- `name` is required.
- Department name must not be blank.
- Department name is unique.

### Success Response

**Status: 201 Created**

```json
{
  "id": 1,
  "name": "Information Technology",
  "location": "Bangalore"
}
```

---

## 4.2 Get All Departments

Returns all departments.

### Endpoint

```http
GET /api/departments
```

### Success Response

**Status: 200 OK**

```json
[
  {
    "id": 1,
    "name": "Information Technology",
    "location": "Bangalore"
  }
]
```

If no departments exist:

```json
[]
```

---

## 4.3 Get Department By ID

Returns a department using its ID.

### Endpoint

```http
GET /api/departments/{id}
```

### Path Parameter

| Parameter | Type | Description |
|---|---|---|
| id | Long | Department ID |

### Example

```http
GET /api/departments/1
```

### Success Response

**Status: 200 OK**

```json
{
  "id": 1,
  "name": "Information Technology",
  "location": "Bangalore"
}
```

### Error Response

**Status: 404 Not Found**

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Department not found with id: 100"
}
```

---

## 4.4 Update Department

Updates an existing department.

### Endpoint

```http
PUT /api/departments/{id}
```

### Path Parameter

| Parameter | Type | Description |
|---|---|---|
| id | Long | Department ID |

### Request Body

```json
{
  "name": "Information Technology",
  "location": "Bengaluru"
}
```

### Success Response

**Status: 200 OK**

```json
{
  "id": 1,
  "name": "Information Technology",
  "location": "Bengaluru"
}
```

### Error Response

Returns `404 Not Found` if the department does not exist.

---

## 4.5 Delete Department

Deletes an existing department.

### Endpoint

```http
DELETE /api/departments/{id}
```

### Path Parameter

| Parameter | Type | Description |
|---|---|---|
| id | Long | Department ID |

### Success Response

```text
204 No Content
```

The response does not contain a body.

### Error Response

Returns `404 Not Found` if the department does not exist.

---

# 5. Employee Endpoints

## 5.1 Create Employee

Creates an employee and associates the employee with an existing department.

### Endpoint

```http
POST /api/employees/department/{departmentId}
```

### Path Parameter

| Parameter | Type | Description |
|---|---|---|
| departmentId | Long | Department assigned to employee |

### Request Body

```json
{
  "name": "Rahul Sharma",
  "email": "rahul@example.com",
  "designation": "Software Engineer",
  "salary": 50000
}
```

### Validation

- `name` must not be blank.
- `email` must not be blank.
- `email` must be a valid email address.
- `designation` must not be blank.
- `salary` is required.
- `salary` must be greater than zero.
- Employee email is unique.

### Success Response

**Status: 201 Created**

```json
{
  "id": 1,
  "name": "Rahul Sharma",
  "email": "rahul@example.com",
  "designation": "Software Engineer",
  "salary": 50000,
  "department": {
    "id": 1,
    "name": "Information Technology",
    "location": "Bangalore"
  }
}
```

### Error Response

Returns `404 Not Found` if `departmentId` does not identify an existing department.

---

## 5.2 Get All Employees

Returns all employees.

### Endpoint

```http
GET /api/employees
```

### Success Response

**Status: 200 OK**

```json
[
  {
    "id": 1,
    "name": "Rahul Sharma",
    "email": "rahul@example.com",
    "designation": "Software Engineer",
    "salary": 50000,
    "department": {
      "id": 1,
      "name": "Information Technology",
      "location": "Bangalore"
    }
  }
]
```

---

## 5.3 Get Employee By ID

Returns an employee using its ID.

### Endpoint

```http
GET /api/employees/{id}
```

### Path Parameter

| Parameter | Type | Description |
|---|---|---|
| id | Long | Employee ID |

### Example

```http
GET /api/employees/1
```

### Success Response

**Status: 200 OK**

```json
{
  "id": 1,
  "name": "Rahul Sharma",
  "email": "rahul@example.com",
  "designation": "Software Engineer",
  "salary": 50000,
  "department": {
    "id": 1,
    "name": "Information Technology",
    "location": "Bangalore"
  }
}
```

### Error Response

Returns `404 Not Found` if the employee does not exist.

---

## 5.4 Update Employee

Updates an employee and its department association.

### Endpoint

```http
PUT /api/employees/{id}/department/{departmentId}
```

### Path Parameters

| Parameter | Type | Description |
|---|---|---|
| id | Long | Employee ID |
| departmentId | Long | Department ID |

### Request Body

```json
{
  "name": "Rahul Sharma",
  "email": "rahul@example.com",
  "designation": "Senior Software Engineer",
  "salary": 65000
}
```

### Success Response

**Status: 200 OK**

```json
{
  "id": 1,
  "name": "Rahul Sharma",
  "email": "rahul@example.com",
  "designation": "Senior Software Engineer",
  "salary": 65000,
  "department": {
    "id": 1,
    "name": "Information Technology",
    "location": "Bangalore"
  }
}
```

### Error Responses

- `404 Not Found` if employee does not exist.
- `404 Not Found` if department does not exist.
- `400 Bad Request` for validation errors.

---

## 5.5 Delete Employee

Deletes an employee.

### Endpoint

```http
DELETE /api/employees/{id}
```

### Path Parameter

| Parameter | Type | Description |
|---|---|---|
| id | Long | Employee ID |

### Success Response

```text
204 No Content
```

### Error Response

Returns `404 Not Found` if the employee does not exist.

---

# 6. HTTP Status Codes

| Status | Meaning | Usage |
|---|---|---|
| 200 | OK | Successful GET and PUT |
| 201 | Created | Successful POST |
| 204 | No Content | Successful DELETE |
| 400 | Bad Request | Request validation failure |
| 404 | Not Found | Employee or department not found |
| 500 | Internal Server Error | Unexpected server-side failure |

---

# 7. Request Flow

```text
Client / Postman
       |
       v
REST Controller
       |
       v
Service Layer
       |
       v
Repository Layer
       |
       v
SQL Server Database
```

The controller receives the HTTP request, the service handles business logic, the repository performs database operations, and the response is returned to the client.

---

# 8. Postman Testing

The endpoints are maintained in the:

```text
Postman Employee API
```

collection.

The collection contains modules for:

- Departments
- Employees
- Cleanup

Environment variables include:

```text
baseUrl
token
departmentId
employeeId
```

Automated tests validate:

- HTTP status codes
- Response schemas
- Returned IDs
- Updated values
- Request chaining

The collection can also be executed through Newman CLI.
