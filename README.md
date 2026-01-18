# Mini Authorizer

A REST API for card management and transaction authorization built with Spring Boot, implementing clean architecture principles and demonstrating best practices in concurrent transaction handling.

---

## Architecture

This project follows the **Hexagonal Architecture (Ports & Adapters)** pattern, which separates business logic from external dependencies such as databases and HTTP controllers, improving testability, maintainability, and flexibility.

The architecture is organized into three main layers:

- **Domain (Core)**: Pure business rules and entities without any framework dependencies
- **Application**: Use cases and service orchestration
- **Infrastructure**: External adapters (REST controllers, JPA repositories, configurations)

<div align="center">

![Hexagonal Architecture](https://www.arnaudlanglade.com/hexagonal-architecture-by-example/hexgonal-architecture-flow-control.svg)

![](https://img.shields.io/badge/Author-Roberto%20Gualberto%20dos%20Santos-brightgreen)
![](https://img.shields.io/badge/Language-Java%2021-brightgreen)
![](https://img.shields.io/badge/Framework-SpringBoot%204.0.1-brightgreen)
![](https://img.shields.io/badge/Architecture-Hexagonal-brightgreen)

</div>

### Project Structure

```
src/main/java/com/roberto/mini_authorizer/
├── domain/                    # Business logic layer
│   ├── model/                # Domain entities (Card, Transaction)
│   ├── validator/            # Business rule validators
│   └── exceptions/           # Domain exceptions
├── application/              # Use cases layer
│   └── service/             # Application services
├── infrastructure/           # External adapters
│   ├── api/                 # REST controllers and DTOs
│   ├── persistence/         # JPA repositories and entities
│   └── config/              # Framework configurations
└── ports/                    # Interface definitions
    ├── in/                  # Input ports (use cases)
    └── out/                 # Output ports (repositories)
```

---

## Prerequisites

- [Java 21](https://www.oracle.com/java/technologies/downloads/)
- [Docker](https://www.docker.com/) & [Docker Compose](https://docs.docker.com/compose/install/)
- [Maven 3.9+](https://maven.apache.org/) (or use the included Maven Wrapper)

---

## Quick Start

### ✅ Option 1: Using the convenience script (recommended)

At the root of the project:

```bash
./start-me.sh
```

This script will:
1. Start the MySQL database container
2. Build the application
3. Run the Spring Boot application

### ✅ Option 2: Manual steps

1. **Start the database:**
   ```bash
   cd docker
   docker-compose up mysql -d
   ```

2. **Build and run the application:**
   ```bash
   ./mvnw clean install
   ./mvnw spring-boot:run
   ```

### ✅ Option 3: Using your IDE

1. Start MySQL: `cd docker && docker-compose up mysql -d`
2. Run `MiniAuthorizerApplication.java` from your IDE

---

## 🔗 API Endpoints

The API runs on `http://localhost:8080`

### Card Management

| Method | Endpoint                  | Description           |
|--------|---------------------------|-----------------------|
| POST   | `/cartoes`                | Create a new card     |
| GET    | `/cartoes/{cardNumber}`   | Get card balance      |

### Transaction Authorization

| Method | Endpoint       | Description              |
|--------|----------------|--------------------------|
| POST   | `/transacoes`  | Authorize a transaction  |

### Example Requests

**Create a card:**
```bash
curl -X POST http://localhost:8080/cartoes \
  -H "Content-Type: application/json" \
  -u username:password \
  -d '{
    "numeroCartao": "6549873025634501",
    "senha": "1234"
  }'
```

**Check balance:**
```bash
curl -X GET http://localhost:8080/cartoes/6549873025634501 \
  -u username:password
```

**Authorize transaction:**
```bash
curl -X POST http://localhost:8080/transacoes \
  -H "Content-Type: application/json" \
  -u username:password \
  -d '{
    "numeroCartao": "6549873025634501",
    "senhaCartao": "1234",
    "valor": 10.00
  }'
```

> **Authentication:** The API uses HTTP Basic Auth with default credentials:
> - Username: `username`
> - Password: `password`

---

## ✅ Running Tests

### Unit and Integration Tests

Run all tests with:

```bash
./mvnw clean test
```

Or use the convenience script:

```bash
bash unit-tests.sh
```


### Integration Tests Only

```bash
./mvnw clean verify
```

Or use the convenience script:

```bash
bash integration-test.sh
```

### Test Coverage

The project includes comprehensive test coverage:

- **Unit Tests**: Domain logic, validators, services, controllers
- **Integration Tests**: End-to-end API flows
- **Concurrency Tests**: Pessimistic locking and race condition handling

Test structure:
```
src/test/java/
├── domain/              # Domain model and validator tests
├── application/         # Service layer tests
├── infrastructure/      # Controller, repository, and mapper tests
└── integration/         # Full integration and concurrency tests
```

---

## Design Decisions

### Why Hexagonal Architecture?

Hexagonal Architecture provides:

- **Testability**: Business logic is isolated and easy to test without external dependencies
- **Flexibility**: Infrastructure can be swapped (e.g., MySQL → PostgreSQL) with minimal impact
- **Maintainability**: Clear separation of concerns makes the codebase easier to understand and evolve
- **Domain-driven**: Business rules are first-class citizens, not buried in framework code

### Concurrency Control

**Problem:** Multiple simultaneous transactions on the same card could cause race conditions and data inconsistencies.

**Solution:** Pessimistic locking with `SELECT ... FOR UPDATE`

```java
@Query(value = "SELECT * FROM cards WHERE card_number = :cardNumber FOR UPDATE", 
       nativeQuery = true)
Optional<CardEntity> findByCardNumberWithLock(@Param("cardNumber") String cardNumber);
```

This ensures:
- Only one transaction can modify a card at a time
- Database-level consistency guarantees
- No lost updates in concurrent scenarios

### Validation Chain Pattern

Transaction validation follows the Chain of Responsibility pattern:

```java
ValidatorChain validates:
  → Password is correct
  → Sufficient balance exists
  → (easily extensible for new rules)
```

Benefits:
- Single Responsibility: each validator handles one rule
- Open/Closed: new validators can be added without modifying existing code
- Composable: validators can be reordered or combined

### Error Handling Strategy

Custom domain exceptions are mapped to appropriate HTTP status codes:

| Exception                           | HTTP Status | When                          |
|-------------------------------------|-------------|-------------------------------|
| `CardNotFoundException`             | 404         | Card doesn't exist            |
| `CardAlreadyExistsException`        | 422         | Duplicate card creation       |
| `InsufficientBalanceException`      | 422         | Not enough funds              |
| `InvalidPasswordException`          | 422         | Wrong password                |
| `MethodArgumentNotValidException`   | 400         | Invalid request payload       |

---

## Technology Stack

| Layer          | Technology                    |
|----------------|-------------------------------|
| Framework      | Spring Boot 4.0.1             |
| Language       | Java 21                       |
| Persistence    | Spring Data JPA + Hibernate   |
| Database       | MySQL 5.7 (prod), H2 (test)   |
| Security       | Spring Security (Basic Auth)  |
| Validation     | Jakarta Bean Validation       |
| Testing        | JUnit 5, Mockito, AssertJ     |
| Build Tool     | Maven 3.9.12                  |
| Containerization | Docker & Docker Compose     |

---

## Docker Setup

The project includes Docker configuration for easy database setup:

```yaml
# docker/docker-compose.yml
version: "3.7"

services:
  mysql:
    image: mysql:5.7
    hostname: mysql
    container_name: mysql
    restart: always
    ports:
      - "3306:3306"
    environment:
      MYSQL_DATABASE: miniautorizador
      MYSQL_ROOT_PASSWORD:
      MYSQL_ALLOW_EMPTY_PASSWORD: "yes"

  mini-authorizer:
    image: mini-authorizer:latest
    container_name: mini-authorizer
    depends_on:
      - mysql
    network_mode: host
```

**Start database only:**
```bash
cd docker
docker-compose up mysql -d
```

**Stop database:**
```bash
docker-compose down
```

**View logs:**
```bash
docker-compose logs -f
```

---

## API Response Examples

### Success Cases

**Card created successfully (201 Created):**
```json
{
  "numeroCartao": "6549873025634501",
  "senha": "1234"
}
```

**Balance retrieved (200 OK):**
```json
500.00
```

**Transaction authorized (201 Created):**
```json
"OK"
```

### Error Cases

**Card not found (404 Not Found):**
```
(empty body)
```

**Insufficient balance (422 Unprocessable Entity):**
```json
"SALDO_INSUFICIENTE"
```

**Invalid password (422 Unprocessable Entity):**
```json
"SENHA_INVALIDA"
```

**Card already exists (422 Unprocessable Entity):**
```json
{
  "numeroCartao": "6549873025634501",
  "senha": "1234"
}
```

**Validation error (400 Bad Request):**
```json
{
  "numeroCartao": "must match \"\\d{16}\"",
  "senha": "size must be between 4 and 6"
}
```

---

## Trade-offs and Future Improvements

### What was prioritized

✅ Clean architecture and separation of concerns  
✅ Comprehensive test coverage (unit + integration + concurrency)  
✅ Thread-safe concurrent transaction handling  
✅ Proper error handling and validation  
✅ Docker setup for easy local development

### What could be improved

- **Idempotency support** for transaction authorization using idempotency keys
- **Authentication**: Replace Basic Auth with JWT or OAuth2
- **Monitoring**: Add metrics with Micrometer and Prometheus
- **API Documentation**: Integrate Swagger/OpenAPI
- **Caching**: Add Redis for frequently accessed balances
- **Audit Trail**: Log all transactions for compliance
- **Rate Limiting**: Prevent abuse with request throttling
- **Circuit Breaker**: Add Resilience4j for resilience
- **Database Migration**: Use Flyway or Liquibase for schema versioning
---

## Idempotency (Planned Improvement)

### Why Idempotency?

In financial systems, especially transaction authorization APIs, **idempotency is critical** to prevent duplicated operations caused by:

- Network timeouts
- Client retries
- Load balancer retries
- Concurrent or repeated requests with the same intent

Without idempotency, the same transaction could be processed more than once, leading to **incorrect balances and data inconsistency**.

### Proposed Approach

A future improvement would introduce **idempotency keys** for transaction authorization requests:

1. Clients send a unique `Idempotency-Key` header for each transaction
2. The API stores processed keys and their results
3. Repeated requests with the same key return the same response, **without reprocessing** the transaction

**Example:**

```bash
POST /transacoes
Idempotency-Key: 550e8400-e29b-41d4-a716-446655440000
Content-Type: application/json

{
  "numeroCartao": "6549873025634501",
  "senhaCartao": "1234",
  "valor": 100.00
}
```

If this exact request is sent again (due to network retry, for example), the API would:
- Detect the duplicate `Idempotency-Key`
- Return the original response (e.g., `"OK"`)
- **NOT** debit the card again

### Possible Implementations

#### Option 1: Database-backed idempotency
- Store idempotency keys in a table with request hash and response
- Guarantees strong consistency but adds DB overhead

```sql
CREATE TABLE idempotency_keys (
    idempotency_key VARCHAR(255) PRIMARY KEY,
    request_hash VARCHAR(255) NOT NULL,
    response_status INT NOT NULL,
    response_body TEXT,
    created_at TIMESTAMP NOT NULL,
    INDEX idx_created_at (created_at)
);
```

#### Option 2: Redis-backed idempotency
- Faster lookup and expiration-based cleanup
- Well suited for high-throughput systems
- Keys can auto-expire after a configurable period (e.g., 24 hours)

```java
@Service
public class IdempotencyService {
    private final RedisTemplate<String, String> redisTemplate;
    
    public Optional<String> getResponse(String idempotencyKey) {
        return Optional.ofNullable(
            redisTemplate.opsForValue().get(idempotencyKey)
        );
    }
    
    public void storeResponse(String idempotencyKey, String response) {
        redisTemplate.opsForValue().set(
            idempotencyKey, 
            response, 
            Duration.ofHours(24)
        );
    }
}
```

### Benefits

✅ **Prevents duplicate charges**: Same transaction won't be processed twice  
✅ **Safe retries for clients**: Clients can safely retry failed requests  
✅ **Improves resilience**: System handles network failures gracefully  
✅ **Industry standard**: Aligns with real-world payment and authorization systems (Stripe, PayPal, etc.)

### Implementation Checklist

When implementing idempotency, consider:

- [ ] Add `Idempotency-Key` header validation
- [ ] Store key + request hash + response in database or Redis
- [ ] Handle key expiration (e.g., 24-48 hours)
- [ ] Return appropriate HTTP status for duplicate requests (e.g., 200 OK with original response)
- [ ] Add metrics for idempotency cache hits/misses
- [ ] Document idempotency behavior in API documentation

---


## Database Schema

```sql
CREATE TABLE cards (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    card_number VARCHAR(16) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    balance DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    INDEX idx_card_number (card_number)
);
```

---

## License

This project is for educational and demonstration purposes.

---

## Contact

**Roberto Gualberto dos Santos**

- GitHub: [@roberto5g](https://github.com/roberto5g)
- Repository: [mini-authorizer](https://github.com/roberto5g/mini-authorizer)

---

*Built with ❤️ using Spring Boot*