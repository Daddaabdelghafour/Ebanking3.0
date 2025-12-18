# Auth Service

Authentication and Authorization microservice for E-Bank 3.0.

**Responsible**: Mouktadir

## Features

- User registration
- User login with JWT tokens
- Token refresh
- Token validation
- Logout with token invalidation
- Integration with Kafka for event-driven communication

## API Endpoints

- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - Login and get JWT tokens
- `POST /api/auth/refresh` - Refresh access token
- `POST /api/auth/logout` - Logout and invalidate token
- `GET /api/auth/validate` - Validate JWT token

## Kafka Events

### Published Events
- `USER_REGISTERED` - Published when a new user registers
- `USER_LOGGED_OUT` - Published when a user logs out

### Consumed Events
- `KYC_VERIFIED` - Updates user verification status
- `KYC_REJECTED` - Updates user verification status

## Environment Variables

### Development (dev profile)
- `DB_URL`: Database URL (default: `jdbc:postgresql://localhost:5432/auth_db`)
- `DB_USERNAME`: Database username (default: `auth_user`)
- `DB_PASSWORD`: Database password (default: `auth_pass`)
- `KAFKA_BOOTSTRAP_SERVERS`: Kafka bootstrap servers (default: `localhost:9092`)
- `JWT_SECRET`: JWT secret key

### Production (prod profile)
- `DB_URL`: Cloud database URL (required)
- `DB_USERNAME`: Database username (required)
- `DB_PASSWORD`: Database password (required)
- `KAFKA_BOOTSTRAP_SERVERS`: Cloud Kafka bootstrap servers (required)
- `JWT_SECRET`: JWT secret key (required)

## Running Locally

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## TODOs

- [ ] Implement user registration
- [ ] Implement login with JWT generation
- [ ] Implement token refresh
- [ ] Implement logout with token invalidation
- [ ] Implement token validation
- [ ] Create User entity and repository
- [ ] Implement password hashing
- [ ] Publish Kafka events
- [ ] Consume KYC events
- [ ] Add unit tests
- [ ] Add integration tests

