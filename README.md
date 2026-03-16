# Transaction Ledger API

RESTful financial transaction ledger built with Spring Boot , PostgreSQL, JWT authentication, and Flyway migrations.

## Features

- JWT Authentication through registering and login return a signed JWT; all transaction endpoints are protected
- BOLA Protection which every query is scoped to the authenticated user's ID extracted from the token; users can never see each other's data
- Paginated Transactions all list endpoints return Spring Page<T> with configurable page and size
- Date Range Filtering which filter transactions between two timestamps
- Input Validation which @Valid on all request bodies with field-level error messages
- Global Exception Handling consistent JSON error shape across all error types

---

## Running Locally

### Prerequisites
- Java 17+
- PostgreSQL 16 running on `localhost:5432`

### 1 Create the database
```sql
DROP DATABASE IF EXISTS ledger;
CREATE DATABASE ledger OWNER postgres;
```

### 2 Start the app
```powershell
./mvnw spring-boot:run
```
Flyway automatically applies all migrations on startup.

---

## Running with Docker (no local Postgres needed)

```bash
docker compose up --build
```

This starts **both** PostgreSQL and the Spring Boot app. The app is ready when you see:
```
Started LedgerApplication in X.XXX seconds
```

| Service | URL |
|---|---|
| API | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |

To stop: `docker compose down`  
To reset the database: `docker compose down -v`

---

## API Endpoints

| Method | Path | Auth | Description |
|---|---|---|---|
| `POST` | `/auth/register` |  Create account, returns JWT |
| `POST` | `/auth/login` |  Login, returns JWT |
| `POST` | `/api/transactions` |  Create a transaction |
| `GET` | `/api/transactions` |  Get paginated transactions |
| `GET` | `/api/transactions?from=&to=` | Filter by date range |

### Example — Register
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"userName":"ibrahim","password":"password123","email":"ibrahim@example.com"}'
```

### Example — Create Transaction
```bash
curl -X POST http://localhost:8080/api/transactions \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"amount":1500.00,"currency":"USD","description":"Payment","counterPartyIban":"GB29NWBK60161331926819"}'
```

---

## Environment Variables

| Variable | Default | Description |
|---|---|---|
| `DATABASE_URL` | `jdbc:postgresql://localhost:5432/ledger` | JDBC connection string |
| `DATABASE_USERNAME` | `postgres` | DB username |
| `DATABASE_PASSWORD` | `admin` | DB password |
| `JWT_SECRET` | *(see application.yml)* | HMAC-SHA256 signing key |
| `JWT_EXPIRATION` | `86400000` | Token TTL in milliseconds (24 h) |

---

## Testing with Postman

Import postman-collection.json into Postman. The collection:
- Auto-saves the JWT after Register / Login
- Includes validation error tests (negative amount, invalid IBAN)
- Includes a 4-step BOLA security test (proving User B cannot see User A's data)
