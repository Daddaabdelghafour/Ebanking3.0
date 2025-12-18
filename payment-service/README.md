# Payment Service

Payment Processing microservice for E-Bank 3.0.

**Responsible**: TBD

## Features

- Payment processing between accounts
- Payment history tracking
- Payment cancellation
- Integration with account-service
- Kafka event publishing

## API Endpoints

- `POST /api/payments` - Process a payment
- `GET /api/payments/{id}` - Get payment by ID
- `GET /api/payments/account/{accountId}` - Get payments by account ID
- `POST /api/payments/{id}/cancel` - Cancel a payment

## Kafka Events

### Published Events
- `PAYMENT_COMPLETED` - Published to `payment-events` topic when payment succeeds
- `PAYMENT_FAILED` - Published to `payment-events` topic when payment fails
- `PAYMENT_CANCELLED` - Published when payment is cancelled

## Environment Variables

### Development (dev profile)
- `DB_URL`: Database URL (default: `jdbc:postgresql://localhost:5435/payment_db`)
- `DB_USERNAME`: Database username (default: `payment_user`)
- `DB_PASSWORD`: Database password (default: `payment_pass`)
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

- [ ] Create Transaction entity and repository
- [ ] Implement payment processing logic
- [ ] Integrate with account-service for balance checks and updates
- [ ] Implement payment cancellation
- [ ] Publish Kafka events
- [ ] Add unit tests
- [ ] Add integration tests
- [ ] Add payment validation and fraud detection

