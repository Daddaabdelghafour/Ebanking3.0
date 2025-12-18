# Project Structure

```
ebank3.0/
├── README.md                          # Main project documentation
├── PROJECT_STRUCTURE.md                # This file
├── pom.xml                            # Parent Maven POM
├── docker-compose.yml                 # Local development setup
├── .gitignore                         # Git ignore rules
│
├── frontend-angular/                  # Angular frontend application
│   ├── package.json
│   ├── angular.json
│   ├── tsconfig.json
│   ├── README.md
│   └── src/
│       ├── main.ts
│       ├── index.html
│       ├── styles.css
│       └── app/
│           └── app.component.ts
│
├── api-gateway/                       # GraphQL API Gateway
│   ├── pom.xml
│   ├── README.md
│   └── src/
│       ├── main/java/com/ebank/gateway/
│       │   ├── ApiGatewayApplication.java
│       │   ├── config/
│       │   │   └── SecurityConfig.java
│       │   ├── resolver/
│       │   │   ├── QueryResolver.java
│       │   │   └── MutationResolver.java
│       │   └── service/
│       │       └── GatewayService.java
│       └── main/resources/
│           ├── application.yml
│           └── schema.graphqls
│
├── auth-service/                      # Authentication Service (Mouktadir)
│   ├── pom.xml
│   ├── README.md
│   └── src/
│       ├── main/java/com/ebank/auth/
│       │   ├── AuthServiceApplication.java
│       │   ├── config/
│       │   │   └── SecurityConfig.java
│       │   ├── controller/
│       │   │   └── AuthController.java
│       │   ├── dto/
│       │   │   ├── LoginRequest.java
│       │   │   ├── LoginResponse.java
│       │   │   └── RegisterRequest.java
│       │   ├── service/
│       │   │   ├── AuthService.java
│       │   │   └── JwtService.java
│       │   └── kafka/
│       │       ├── AuthEventProducer.java
│       │       └── AuthEventConsumer.java
│       └── main/resources/
│           └── application.yml
│
├── user-service/                      # Customer Service (Already Developed)
│   ├── pom.xml
│   ├── README.md
│   └── src/
│       ├── main/java/com/ebank/user/
│       │   └── UserServiceApplication.java
│       └── main/resources/
│           └── application.yml
│
├── account-service/                   # Account Service (Ishaq)
│   ├── pom.xml
│   ├── README.md
│   └── src/
│       ├── main/java/com/ebank/account/
│       │   ├── AccountServiceApplication.java
│       │   ├── controller/
│       │   │   └── AccountController.java
│       │   ├── dto/
│       │   │   ├── AccountCreateRequest.java
│       │   │   └── AccountResponse.java
│       │   ├── service/
│       │   │   └── AccountService.java
│       │   └── kafka/
│       │       ├── AccountEventProducer.java
│       │       └── AccountEventConsumer.java
│       └── main/resources/
│           └── application.yml
│
├── payment-service/                   # Payment Service (TBD)
│   ├── pom.xml
│   ├── README.md
│   └── src/
│       ├── main/java/com/ebank/payment/
│       │   ├── PaymentServiceApplication.java
│       │   ├── controller/
│       │   │   └── PaymentController.java
│       │   ├── dto/
│       │   │   ├── PaymentRequest.java
│       │   │   └── PaymentResponse.java
│       │   ├── service/
│       │   │   └── PaymentService.java
│       │   └── kafka/
│       │       └── PaymentEventProducer.java
│       └── main/resources/
│           └── application.yml
│
├── notification-service/              # Notification Service (TBD)
│   ├── pom.xml
│   ├── README.md
│   └── src/
│       ├── main/java/com/ebank/notification/
│       │   ├── NotificationServiceApplication.java
│       │   ├── controller/
│       │   │   └── NotificationController.java
│       │   ├── dto/
│       │   │   ├── NotificationRequest.java
│       │   │   └── NotificationResponse.java
│       │   ├── service/
│       │   │   └── NotificationService.java
│       │   └── kafka/
│       │       └── NotificationEventConsumer.java
│       └── main/resources/
│           └── application.yml
│
├── k8s/                               # Kubernetes manifests
│   ├── README.md
│   ├── namespace.yaml
│   ├── configmaps.yaml
│   ├── secrets.example.yaml
│   ├── auth-service/
│   │   └── deployment.yaml
│   ├── user-service/
│   │   └── deployment.yaml
│   ├── account-service/
│   │   └── deployment.yaml
│   ├── payment-service/
│   │   └── deployment.yaml
│   ├── notification-service/
│   │   └── deployment.yaml
│   └── api-gateway/
│       └── deployment.yaml
│
├── .github/                            # GitHub Actions CI/CD
│   └── workflows/
│       ├── auth-service-ci.yml
│       ├── account-service-ci.yml
│       ├── payment-service-ci.yml
│       ├── notification-service-ci.yml
│       └── api-gateway-ci.yml
│
└── Dockerfile.*                        # Dockerfiles for each service
    ├── Dockerfile.auth-service
    ├── Dockerfile.user-service
    ├── Dockerfile.account-service
    ├── Dockerfile.payment-service
    ├── Dockerfile.notification-service
    └── Dockerfile.api-gateway
```

## Service Ports

- **API Gateway**: 8080
- **Auth Service**: 8081
- **User Service**: 8082
- **Account Service**: 8083
- **Payment Service**: 8084
- **Notification Service**: 8085

## Communication Patterns

### Synchronous (REST)
- Frontend → API Gateway (GraphQL)
- API Gateway → Microservices (REST/OpenFeign)
- Microservices → Microservices (REST/OpenFeign)

### Asynchronous (Kafka)
- **Topics**:
  - `user-events`: User registration, logout events
  - `customer-events`: Account creation, updates
  - `payment-events`: Payment completed, failed events
  - `kyc-events`: KYC verification, rejection events

## Development Workflow

1. Each developer works on their assigned microservice
2. Use `dev` profile for local development (localhost)
3. Use `prod` profile for production (Kubernetes DNS)
4. CI/CD pipelines run on push to main/develop branches
5. Kubernetes deployment handled by Abdelghafour

