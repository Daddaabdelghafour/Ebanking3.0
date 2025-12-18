# Notification Service

Notification microservice for E-Bank 3.0.

**Responsible**: TBD

## Features

- Email notifications
- SMS notifications
- Push notifications
- Notification history
- Event-driven notifications via Kafka

## API Endpoints

- `POST /api/notifications` - Send notification manually
- `GET /api/notifications/user/{userId}` - Get notifications by user ID
- `GET /api/notifications/{id}` - Get notification by ID
- `PUT /api/notifications/{id}/read` - Mark notification as read

## Kafka Events

### Consumed Events
- `customer-events`: ACCOUNT_CREATED, ACCOUNT_UPDATED, CUSTOMER_CREATED
- `payment-events`: PAYMENT_COMPLETED, PAYMENT_FAILED
- `user-events`: USER_REGISTERED, USER_LOGGED_OUT
- `kyc-events`: KYC_VERIFIED, KYC_REJECTED

## Environment Variables

### Development (dev profile)
- `DB_URL`: Database URL (default: `jdbc:postgresql://localhost:5436/notification_db`)
- `DB_USERNAME`: Database username (default: `notification_user`)
- `DB_PASSWORD`: Database password (default: `notification_pass`)
- `KAFKA_BOOTSTRAP_SERVERS`: Kafka bootstrap servers (default: `localhost:9092`)
- `MAIL_HOST`: SMTP host (default: `smtp.gmail.com`)
- `MAIL_PORT`: SMTP port (default: `587`)
- `MAIL_USERNAME`: Email username
- `MAIL_PASSWORD`: Email password

### Production (prod profile)
- `DB_URL`: Cloud database URL (required)
- `DB_USERNAME`: Database username (required)
- `DB_PASSWORD`: Database password (required)
- `KAFKA_BOOTSTRAP_SERVERS`: Cloud Kafka bootstrap servers (required)
- `MAIL_HOST`: SMTP host (required)
- `MAIL_PORT`: SMTP port (required)
- `MAIL_USERNAME`: Email username (required)
- `MAIL_PASSWORD`: Email password (required)

## Running Locally

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## TODOs

- [ ] Create Notification entity and repository
- [ ] Implement email sending
- [ ] Implement SMS sending (Twilio integration)
- [ ] Implement push notifications
- [ ] Consume Kafka events and send notifications
- [ ] Add notification templates
- [ ] Add unit tests
- [ ] Add integration tests

