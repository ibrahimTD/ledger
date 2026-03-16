# Transaction Ledger API

A secure, production-ready RESTful financial transaction ledger built with **Spring Boot 4**, **PostgreSQL**, **JWT authentication**, and **Flyway** migrations.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 4 |
| Security | Spring Security + JWT (jjwt 0.11.5) |
| Database | PostgreSQL 16 |
| Migrations | Flyway 11 |
| ORM | Spring Data JPA / Hibernate |
| Mapping | MapStruct 1.5.5 |
| Docs | SpringDoc OpenAPI (Swagger UI) |
| Containerisation | Docker + Docker Compose |

---

## Features

- **JWT Authentication** — register and login return a signed JWT; all transaction endpoints are protected
- **BOLA Protection** — every query is scoped to the authenticated user's ID extracted from the token; users can never see each other's data
- **Paginated Transactions** — all list endpoints return Spring `Page<T>` with configurable `page` and `size`
- **Date Range Filtering** — filter transactions between two ISO-8601 timestamps
- **Input Validation** — `@Valid` on all request bodies with field-level error messages
- **Global Exception Handling** — consistent JSON error shape across all error types

---

## Running Locally

### Prerequisites
- Java 17+
- PostgreSQL 16 running on `localhost:5432`

### Step 1 — Create the database
```sql
DROP DATABASE IF EXISTS ledger;
CREATE DATABASE ledger OWNER postgres;
```

### Step 2 — Start the app
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
| `POST` | `/auth/register` | ❌ | Create account, returns JWT |
| `POST` | `/auth/login` | ❌ | Login, returns JWT |
| `POST` | `/api/transactions` | ✅ | Create a transaction |
| `GET` | `/api/transactions` | ✅ | Get paginated transactions |
| `GET` | `/api/transactions?from=&to=` | ✅ | Filter by date range |

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

Import `postman-collection.json` into Postman. The collection:
- Auto-saves the JWT after Register / Login
- Includes validation error tests (negative amount, invalid IBAN)
- Includes a 4-step **BOLA security test** proving User B cannot see User A's data

---

## Project Structure

```
src/
├── config/          # JWT filter, JWT util, Security config, OpenAPI config
├── dto/             # Request / response DTOs with validation annotations
├── exception/       # Global exception handler
├── mapper/          # MapStruct mappers (entity ↔ DTO)
├── model/           # JPA entities (UserModel, TransactionModel)
├── repository/      # Spring Data JPA repositories
├── resources/       # REST controllers (AuthController, TransactionController)
└── service/         # Business logic (UserService, TransactionService)

src/main/resources/db/migration/
└── create/
    ├── V1_03_15_15_45__create_user_table.sql
    └── V1_03_15_15_50__create_transaction_table.sql
```

