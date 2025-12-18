# E-Bank 3.0 Monorepo

Microservices-based banking application with Angular frontend and Spring Boot backend.

## Architecture Overview

- **Frontend**: Angular application
- **API Gateway**: GraphQL Gateway (Spring Boot)
- **Microservices**:
  - `auth-service`: Authentication and authorization
  - `user-service`: Customer management (already developed)
  - `account-service`: Account management (Responsible: Ishaq)
  - `payment-service`: Payment processing (Responsible: TBD)
  - `notification-service`: Notifications (Responsible: TBD)
- **Message Broker**: Kafka (Cloud)
- **Databases**: Cloud databases (URLs in environment files)

## Project Structure

```
monorepo/
├── frontend-angular/          # Angular frontend application
├── api-gateway/               # GraphQL API Gateway (Spring Boot)
├── auth-service/              # Authentication service (Spring Boot)
├── user-service/              # Customer service (Spring Boot)
├── account-service/           # Account service (Spring Boot)
├── payment-service/           # Payment service (Spring Boot)
├── notification-service/      # Notification service (Spring Boot)
├── docker-compose.yml         # Local development setup
├── .github/                   # GitHub Actions CI/CD
└── k8s/                       # Kubernetes manifests
```

## Spring Profiles

Each microservice supports two profiles:

- **dev**: Communication via `localhost` for local development
- **prod**: Communication via Kubernetes DNS for production

Configuration files:
- `application-dev.yml`: Development profile
- `application-prod.yml`: Production profile

## Getting Started

### Prerequisites

- Java 17+
- Node.js 18+
- Docker & Docker Compose (for local Kafka)
- Maven 3.8+

### Local Development

1. Start Kafka (if running locally):
   ```bash
   docker-compose up -d kafka
   ```

2. Run each microservice with dev profile:
   ```bash
   cd auth-service && mvn spring-boot:run -Dspring-boot.run.profiles=dev
   ```

3. Start API Gateway:
   ```bash
   cd api-gateway && mvn spring-boot:run -Dspring-boot.run.profiles=dev
   ```

4. Start Angular frontend:
   ```bash
   cd frontend-angular && npm install && npm start
   ```

## Environment Variables

Each service requires environment variables for:
- Database URLs (Cloud)
- Kafka bootstrap servers (Cloud)
- Service discovery URLs (Kubernetes DNS for prod)

See individual service README files for details.

## CI/CD

- **CI**: GitHub Actions (Responsible: Mouktadir)
- **CD**: Kubernetes deployment (Responsible: Abdelghafour)

## Team Responsibilities

- **KYC**: Included in project scope
- **Customer Service**: Already developed
- **Frontend**: Khalid
- **Account Service**: Ishaq
- **Auth Service**: Mouktadir
- **Notification Service**: TBD
- **Payment Service**: TBD

## Documentation

- [Project Structure](PROJECT_STRUCTURE.md) - Detailed project structure and organization
- [Development Guide](DEVELOPMENT_GUIDE.md) - Comprehensive development setup and guidelines
- [Kubernetes Deployment](k8s/README.md) - Kubernetes deployment instructions

## Development Guidelines

1. Each microservice is developed independently
2. Use Spring Profiles for environment-specific configuration
3. Use Kafka for asynchronous event-driven communication
4. Follow RESTful principles for synchronous communication
5. Use GraphQL API Gateway for frontend communication

## Next Steps

1. **Set up cloud databases** - Create databases on cloud and update environment variables
2. **Set up cloud Kafka** - Configure Kafka cluster and update bootstrap servers
3. **Configure Keycloak** - Set up Keycloak instance and update API Gateway configuration
4. **Implement business logic** - Each service has TODO comments for implementation
5. **Add tests** - Write unit and integration tests for each service
6. **Deploy to Kubernetes** - Use provided Kubernetes manifests for deployment

