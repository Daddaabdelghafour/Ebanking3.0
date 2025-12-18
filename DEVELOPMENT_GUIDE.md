# Development Guide

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- Node.js 18+
- Docker & Docker Compose
- PostgreSQL (or use cloud databases)
- Kafka (or use cloud Kafka)

### Local Development Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd ebank3.0
   ```

2. **Start infrastructure services** (optional for local dev)
   ```bash
   docker-compose up -d
   ```
   This starts:
   - Kafka (localhost:9092)
   - PostgreSQL databases for each service

3. **Run microservices** (each in separate terminal)

   ```bash
   # Auth Service
   cd auth-service
   mvn spring-boot:run -Dspring-boot.run.profiles=dev

   # User Service
   cd user-service
   mvn spring-boot:run -Dspring-boot.run.profiles=dev

   # Account Service
   cd account-service
   mvn spring-boot:run -Dspring-boot.run.profiles=dev

   # Payment Service
   cd payment-service
   mvn spring-boot:run -Dspring-boot.run.profiles=dev

   # Notification Service
   cd notification-service
   mvn spring-boot:run -Dspring-boot.run.profiles=dev

   # API Gateway
   cd api-gateway
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
   ```

4. **Run Angular Frontend**
   ```bash
   cd frontend-angular
   npm install
   npm start
   ```

## Spring Profiles

### Development Profile (`dev`)
- Uses `localhost` for service communication
- Local database connections
- Local Kafka instance
- Debug logging enabled

### Production Profile (`prod`)
- Uses Kubernetes DNS for service communication
- Cloud database connections (via environment variables)
- Cloud Kafka (via environment variables)
- Production logging

## Environment Variables

### For Cloud Databases (Production)

Set these in Kubernetes secrets or environment:

```bash
# Database URLs
DB_URL=jdbc:postgresql://your-cloud-db:5432/service_db
DB_USERNAME=your_username
DB_PASSWORD=your_password

# Kafka
KAFKA_BOOTSTRAP_SERVERS=your-kafka-cloud:9092

# Keycloak (API Gateway)
KEYCLOAK_REALM=ebank
KEYCLOAK_URL=https://your-keycloak-url
KEYCLOAK_CLIENT_ID=api-gateway
KEYCLOAK_CLIENT_SECRET=your-secret

# JWT Secret (Auth Service)
JWT_SECRET=your-secret-key-min-256-bits

# Mail (Notification Service)
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
```

## Service Communication

### Dev Profile (localhost)
- `http://localhost:8081` - Auth Service
- `http://localhost:8082` - User Service
- `http://localhost:8083` - Account Service
- `http://localhost:8084` - Payment Service
- `http://localhost:8085` - Notification Service
- `http://localhost:8080` - API Gateway

### Prod Profile (Kubernetes DNS)
- `http://auth-service:8081`
- `http://user-service:8082`
- `http://account-service:8083`
- `http://payment-service:8084`
- `http://notification-service:8085`
- `http://api-gateway:8080`

## Kafka Topics

- `user-events`: User registration, logout
- `customer-events`: Account creation, updates
- `payment-events`: Payment completed, failed
- `kyc-events`: KYC verification, rejection

## Testing

### Unit Tests
```bash
cd <service-name>
mvn test
```

### Integration Tests
```bash
cd <service-name>
mvn verify
```

## Building Docker Images

```bash
# Build individual service
docker build -f Dockerfile.auth-service -t ebank/auth-service:latest .

# Build all services
for service in auth-service user-service account-service payment-service notification-service api-gateway; do
  docker build -f Dockerfile.$service -t ebank/$service:latest .
done
```

## Deployment

See `k8s/README.md` for Kubernetes deployment instructions.

## Code Style

- Follow Java naming conventions
- Use Lombok for boilerplate code
- Add Javadoc for public methods
- Write unit tests for business logic
- Use DTOs for API communication

## Git Workflow

1. Create feature branch from `develop`
2. Make changes
3. Commit with descriptive messages
4. Push and create pull request
5. CI/CD runs automatically
6. After review, merge to `develop`
7. Deploy to production from `main`

## Troubleshooting

### Service won't start
- Check if port is already in use
- Verify database connection
- Check Kafka connectivity
- Review logs: `mvn spring-boot:run` shows errors

### Kafka connection issues
- Verify Kafka is running: `docker ps`
- Check bootstrap servers configuration
- Verify topic exists (auto-creation enabled in dev)

### Database connection issues
- Verify database is running
- Check connection URL
- Verify credentials
- Check network connectivity

