# Expense Tracker API

A Spring Boot REST API for account registration, JWT-based authentication, and managing personal expenses. Expense records are scoped to the authenticated user.

## Technology stack

- Java 21
- Spring Boot 4.1.1
- Spring MVC and Jakarta Bean Validation
- Spring Security OAuth2 Resource Server with signed JWTs
- Spring Data JPA / Hibernate
- PostgreSQL 17 for local and deployed data
- Maven Wrapper for builds and tests
- H2, JUnit, Mockito, and MockMvc for isolated automated tests
- Docker Compose for local PostgreSQL

## Requirements

- JDK 21
- Docker Desktop (for the included PostgreSQL service), or a PostgreSQL 17 database
- PowerShell on Windows, or a shell that can run the Maven wrapper

## Local setup (Windows PowerShell)

Set local database credentials and a private JWT signing key in the current PowerShell session. Use a password of your choice and a JWT secret with at least 32 UTF-8 bytes; do not commit real credentials.

```powershell
$env:POSTGRES_DB = "expense_tracker"
$env:POSTGRES_USER = "postgres"
$env:POSTGRES_PASSWORD = "replace-with-a-local-database-password"
$env:DB_URL = "jdbc:postgresql://localhost:5432/expense_tracker"
$env:DB_USERNAME = $env:POSTGRES_USER
$env:DB_PASSWORD = $env:POSTGRES_PASSWORD
$env:JWT_SECRET = "replace-with-at-least-32-random-characters"
$env:JWT_EXPIRATION = "PT10H"

docker compose up -d
.\mvnw.cmd spring-boot:run
```

The API is available at `http://localhost:8080`. The database URL, username, password, JWT secret, and token lifetime can be provided through `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`, and `JWT_EXPIRATION`. `JWT_EXPIRATION` is a Java duration, such as `PT10H` for ten hours. The JWT secret must be at least 32 UTF-8 bytes.

To stop the database container while keeping its data:

```powershell
docker compose down
```

The Compose database uses a persistent named volume. Removing that volume deletes the local database contents.

## Build and tests

Run the full test suite:

```powershell
.\mvnw.cmd test
```

Tests use an isolated in-memory H2 database and disable Docker Compose auto-start, so a running PostgreSQL service is not needed to run them. The suite covers registration and login, request validation, expense creation and persistence, all expense update fields, listing and sorting, deletion, JWT authentication, and per-user access control. Concurrency tests are not included.

Build the application:

```powershell
.\mvnw.cmd clean package
```

## Authentication and response format

Registration and login are public. Expense endpoints require an access token from login:

```http
Authorization: Bearer <token>
```

Successful authentication and most successful operations return this envelope:

```json
{
  "response": {},
  "successful": true,
  "message": "Operation completed",
  "responseTime": "2026-10-15T04:00:00Z"
}
```

Application-level errors use the same envelope with `response: null` and `successful: false`. Framework-level request validation errors may use Spring's standard error response. The expense listing returns Spring's paginated response directly, and deletion returns a simple `message` object.

## API endpoints

Base URL: `http://localhost:8080`

| Method | Path | Authentication | Description |
| --- | --- | --- | --- |
| `POST` | `/api/v1/auth/register` | No | Register a user and account |
| `POST` | `/api/v1/auth/login` | No | Verify credentials and issue a JWT |
| `POST` | `/api/v1/expense/create` | Bearer token | Create an expense |
| `GET` | `/api/v1/expense/update` | Bearer token | Return the supported expense fields |
| `PUT` | `/api/v1/expense/update/title` | Bearer token | Change an expense title |
| `PUT` | `/api/v1/expense/update/amount` | Bearer token | Change an expense amount |
| `PUT` | `/api/v1/expense/update/expense-date` | Bearer token | Change an expense date |
| `PUT` | `/api/v1/expense/update/notes` | Bearer token | Change expense notes |
| `PUT` | `/api/v1/expense/update/category` | Bearer token | Change an expense category |
| `PUT` | `/api/v1/expense/update/payment-method` | Bearer token | Change a payment method |
| `DELETE` | `/api/v1/expense/delete` | Bearer token | Delete an owned expense |
| `GET` | `/api/v1/expense` | Bearer token | List the authenticated user's expenses |

### Register

`POST /api/v1/auth/register`

```json
{
  "username": "jane_doe",
  "role": "USER",
  "account": {
    "email": "jane@example.com",
    "password": "StrongPass1!"
  }
}
```

The username must be 5–50 characters. Email is required, must be valid, and is limited to 80 characters. Password must be 8–40 characters and contain a letter, a digit, and one of `@$!%*?&.`; whitespace is not allowed. Supported roles are `USER` and `ADMIN`. Email addresses and usernames must be unique.

### Login

`POST /api/v1/auth/login`

```json
{
  "email": "jane@example.com",
  "password": "StrongPass1!"
}
```

The successful response contains `response.token`, `response.tokenType` (`Bearer`), `response.expiration`, and the user profile. Use the token in the `Authorization` header for expense endpoints.

### Create an expense

`POST /api/v1/expense/create`

```json
{
  "title": "Groceries",
  "amount": 48.75,
  "expenseDate": "2026-10-14T18:20:00Z",
  "notes": "Weekly food shopping",
  "category": "FOOD_AND_DINING",
  "paymentMethod": "DEBIT_CARD"
}
```

The title is required and at most 100 characters. Amount is required and cannot be negative. `expenseDate` is a required ISO-8601 instant and cannot be in the future. Category and payment method are required enum values. Notes are optional; a missing or blank value is replaced with a default note. An expense title must be unique within the current user's records.

Categories:

`FOOD_AND_DINING`, `UTILITIES`, `HOUSING`, `HEALTHCARE`, `TRANSPORTATION`, `SHOPPING`, `ENTERTAINMENT`, `TRAVEL`, `EDUCATION`, `SUBSCRIPTIONS`, `MISCELLANEOUS`

Payment methods:

`CASH`, `G_CASH`, `PAY_MAYA`, `DEBIT_CARD`, `CREDIT_CARD`

### Update an expense

Each update uses `PUT` and the same JSON shape. `toApply` is typed according to the field being changed:

```json
{
  "expenseId": 42,
  "toApply": "Updated title"
}
```

Examples of `toApply` values:

| Path suffix | JSON value |
| --- | --- |
| `title` | `"Updated title"` |
| `amount` | `48.75` |
| `expense-date` | `"2026-10-14T18:20:00Z"` |
| `notes` | `"Updated note"` |
| `category` | `"UTILITIES"` |
| `payment-method` | `"CASH"` |

An authenticated user can update only their own expense.

### Delete an expense

`DELETE /api/v1/expense/delete`

```json
{
  "id": 42
}
```

### List expenses

`GET /api/v1/expense` accepts a JSON request body:

```json
{
  "pageNumber": 0,
  "pageSize": 20,
  "property": "createdAt",
  "ascending": false
}
```

`pageNumber`, `pageSize`, `property`, and `ascending` are required. Page numbering starts at zero. `property` is an entity sort property such as `createdAt`, `title`, or `amount`. `ascending: true` sorts ascending; `false` sorts descending. The response is a Spring `Page` object with fields such as `content`, `totalElements`, `totalPages`, and `number`. Only the authenticated user's records are returned.

## Project layout

```text
src/main/java/expense/tracker/v1/
  account/       Account entity, repository, and service
  auth/          Registration, login, and JWT support
  config/        Spring, JPA, security, and JWT configuration
  exception/     Domain exceptions and API exception handling
  expense/       Expense API, entity, repository, and service
  user/          User entity, repository, and service
  utils/         Shared response and service utilities
src/test/java/   Unit and API integration tests
```
## Project Link: https://roadmap.sh/projects/expense-tracker-api