# Account Service

Account Management microservice for E-Bank 3.0.

**Responsible**: Ishaq

## Features

- Account CRUD operations
- Account balance management
- Deposit and withdrawal operations
- Integration with user-service and payment-service
- Kafka event publishing and consumption

## API Endpoints

- `GET /api/accounts` - Get all accounts
- `GET /api/accounts/{id}` - Get account by ID
- `GET /api/accounts/customer/{customerId}` - Get accounts by customer ID
- `POST /api/accounts` - Create new account
- `PUT /api/accounts/{id}` - Update account
- `POST /api/accounts/{id}/deposit` - Deposit money
- `POST /api/accounts/{id}/withdraw` - Withdraw money
- `GET /api/accounts/{id}/balance` - Get account balance

## Kafka Events

### Published Events
- `ACCOUNT_CREATED` - Published to `customer-events` topic when account is created
- `ACCOUNT_UPDATED` - Published to `customer-events` topic when account balance changes

### Consumed Events
- `USER_REGISTERED` / `CUSTOMER_CREATED` - Optionally create default account
- `PAYMENT_COMPLETED` - Update account balance
- `PAYMENT_FAILED` - Rollback account balance if needed

## Environment Variables

### Development (dev profile)
- `DB_URL`: Database URL (default: `jdbc:postgresql://localhost:5434/account_db`)
- `DB_USERNAME`: Database username (default: `account_user`)
- `DB_PASSWORD`: Database password (default: `account_pass`)
- `KAFKA_BOOTSTRAP_SERVERS`: Kafka bootstrap servers (default: `localhost:9092`)

### Production (prod profile)
- `DB_URL`: Cloud database URL (required)
- `DB_USERNAME`: Database username (required)
- `DB_PASSWORD`: Database password (required)
- `KAFKA_BOOTSTRAP_SERVERS`: Cloud Kafka bootstrap servers (required)

## Running Locally

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## TODOs

- [ ] Create Account entity and repository
- [ ] Create Operation entity for transaction history
- [ ] Implement account CRUD operations
- [ ] Implement deposit/withdraw operations
- [ ] Integrate with user-service for customer validation
- [ ] Publish Kafka events
- [ ] Consume payment and user events
- [ ] Add unit tests
- [ ] Add integration tests

