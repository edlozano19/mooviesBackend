# User API Testing Guide

## Base URL
```
http://localhost:8081/api/users
```

## Available Endpoints

### 1. Create User
**POST** `/api/users`

**Request Body:**
```json
{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "securePassword123",
  "firstName": "John",
  "lastName": "Doe"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "username": "johndoe",
  "email": "john@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "isActive": true,
  "createdAt": "2025-09-17T22:30:00",
  "updatedAt": "2025-09-17T22:30:00"
}
```

### 2. Get User by ID
**GET** `/api/users/{id}`

**Example:** `GET /api/users/1`

**Response (200 OK):**
```json
{
  "id": 1,
  "username": "johndoe",
  "email": "john@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "isActive": true,
  "createdAt": "2025-09-17T22:30:00",
  "updatedAt": "2025-09-17T22:30:00"
}
```

### 3. Search User by Username or Email
**GET** `/api/users/search?query={username_or_email}`

**Example:** `GET /api/users/search?query=johndoe`

**Response (200 OK):**
```json
{
  "id": 1,
  "username": "johndoe",
  "email": "john@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "isActive": true,
  "createdAt": "2025-09-17T22:30:00",
  "updatedAt": "2025-09-17T22:30:00"
}
```

### 4. Get All Active Users
**GET** `/api/users`

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "username": "johndoe",
    "email": "john@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "isActive": true,
    "createdAt": "2025-09-17T22:30:00",
    "updatedAt": "2025-09-17T22:30:00"
  }
]
```

### 5. Update User
**PUT** `/api/users/{id}`

**Request Body (partial updates allowed):**
```json
{
  "firstName": "Johnny",
  "lastName": "Smith"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "username": "johndoe",
  "email": "john@example.com",
  "firstName": "Johnny",
  "lastName": "Smith",
  "isActive": true,
  "createdAt": "2025-09-17T22:30:00",
  "updatedAt": "2025-09-17T22:35:00"
}
```

### 6. Update Password
**PUT** `/api/users/{id}/password`

**Request Body:**
```json
{
  "currentPassword": "securePassword123",
  "newPassword": "newSecurePassword456"
}
```

**Response (200 OK):**
```json
{
  "message": "Password updated successfully"
}
```

### 7. Deactivate User (Soft Delete)
**DELETE** `/api/users/{id}`

**Response (200 OK):**
```json
{
  "message": "User deactivated successfully"
}
```

### 8. Reactivate User
**PUT** `/api/users/{id}/reactivate`

**Response (200 OK):**
```json
{
  "message": "User reactivated successfully"
}
```

## Error Responses

### 400 Bad Request
```json
{
  "error": "Failed to create user: Username already exists"
}
```

### 404 Not Found
```json
{
  "error": "User not found with ID: 999"
}
```

### 409 Conflict
```json
{
  "error": "Email already exists: john@example.com"
}
```

### 500 Internal Server Error
```json
{
  "error": "Internal server error"
}
```

## Testing with curl

### Create a user:
```bash
curl -X POST http://localhost:8081/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "firstName": "Test",
    "lastName": "User"
  }'
```

### Get user by ID:
```bash
curl http://localhost:8081/api/users/1
```

### Search user:
```bash
curl "http://localhost:8081/api/users/search?query=testuser"
```

### Update user:
```bash
curl -X PUT http://localhost:8081/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Updated",
    "lastName": "Name"
  }'
```

### Deactivate user:
```bash
curl -X DELETE http://localhost:8081/api/users/1
```

### Reactivate user:
```bash
curl -X PUT http://localhost:8081/api/users/1/reactivate
```

## Testing with Postman

1. Import the endpoints into Postman
2. Set the base URL to `http://localhost:8081`
3. Make sure to set `Content-Type: application/json` for POST/PUT requests
4. Test the complete user lifecycle: create → read → update → deactivate
