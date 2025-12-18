# Project TODOs

This document tracks all TODOs across the monorepo.

## Auth Service (Mouktadir)

### Implementation
- [ ] Create User entity and repository
- [ ] Implement user registration logic
- [ ] Implement login with JWT generation
- [ ] Implement token refresh
- [ ] Implement logout with token invalidation (Redis/DB)
- [ ] Implement token validation
- [ ] Add password hashing (BCrypt)
- [ ] Publish USER_REGISTERED event to Kafka
- [ ] Publish USER_LOGGED_OUT event to Kafka
- [ ] Consume KYC events and update user verification status

### Testing
- [ ] Add unit tests for AuthService
- [ ] Add unit tests for JwtService
- [ ] Add integration tests for AuthController
- [ ] Add security tests

## User Service (Already Developed)

### Integration
- [ ] Verify Kafka integration
- [ ] Verify service communication
- [ ] Update documentation if needed

## Account Service (Ishaq)

### Implementation
- [ ] Create Account entity and repository
- [ ] Create Operation entity for transaction history
- [ ] Implement account CRUD operations
- [ ] Implement deposit operation
- [ ] Implement withdraw operation with balance check
- [ ] Integrate with user-service for customer validation (Feign client)
- [ ] Publish ACCOUNT_CREATED event to Kafka
- [ ] Publish ACCOUNT_UPDATED event to Kafka
- [ ] Consume payment events (PAYMENT_COMPLETED, PAYMENT_FAILED)
- [ ] Consume user events (USER_REGISTERED, CUSTOMER_CREATED)

### Testing
- [ ] Add unit tests for AccountService
- [ ] Add integration tests for AccountController
- [ ] Add tests for Kafka event handling

## Payment Service (TBD)

### Implementation
- [ ] Create Transaction entity and repository
- [ ] Implement payment processing logic
- [ ] Integrate with account-service for balance checks (Feign client)
- [ ] Integrate with account-service for balance updates (Feign client)
- [ ] Implement payment cancellation
- [ ] Add payment validation
- [ ] Add fraud detection (optional)
- [ ] Publish PAYMENT_COMPLETED event to Kafka
- [ ] Publish PAYMENT_FAILED event to Kafka
- [ ] Publish PAYMENT_CANCELLED event to Kafka

### Testing
- [ ] Add unit tests for PaymentService
- [ ] Add integration tests for PaymentController
- [ ] Add tests for Kafka event handling
- [ ] Add tests for payment validation

## Notification Service (TBD)

### Implementation
- [ ] Create Notification entity and repository
- [ ] Implement email sending (JavaMailSender)
- [ ] Implement SMS sending (Twilio integration)
- [ ] Implement push notifications
- [ ] Create notification templates
- [ ] Consume customer-events and send notifications
- [ ] Consume payment-events and send notifications
- [ ] Consume user-events and send notifications
- [ ] Consume kyc-events and send notifications

### Testing
- [ ] Add unit tests for NotificationService
- [ ] Add integration tests for NotificationController
- [ ] Add tests for Kafka event handling
- [ ] Add tests for email/SMS sending

## API Gateway

### Implementation
- [ ] Define complete GraphQL schema
- [ ] Implement Query resolvers (customer, account, payment queries)
- [ ] Implement Mutation resolvers (create account, process payment, etc.)
- [ ] Create Feign clients for each microservice:
  - [ ] AuthServiceClient
  - [ ] UserServiceClient
  - [ ] AccountServiceClient
  - [ ] PaymentServiceClient
  - [ ] NotificationServiceClient
- [ ] Configure Keycloak adapter properly
- [ ] Add error handling and error responses
- [ ] Add request/response logging
- [ ] Add rate limiting
- [ ] Add caching for frequently accessed data

### Testing
- [ ] Add GraphQL query tests
- [ ] Add GraphQL mutation tests
- [ ] Add integration tests
- [ ] Add security tests

## Angular Frontend (Khalid)

### Implementation
- [ ] Set up routing
- [ ] Create authentication module:
  - [ ] Login component
  - [ ] Register component
  - [ ] Auth guard
  - [ ] Auth service
- [ ] Create account management module:
  - [ ] Account list component
  - [ ] Account details component
  - [ ] Create account component
- [ ] Create payment module:
  - [ ] Payment form component
  - [ ] Payment history component
- [ ] Create notification module:
  - [ ] Notification center component
  - [ ] Notification list component
- [ ] Integrate with GraphQL API Gateway
- [ ] Add error handling
- [ ] Add loading states
- [ ] Add form validation
- [ ] Add responsive design

### Testing
- [ ] Add unit tests for components
- [ ] Add unit tests for services
- [ ] Add e2e tests

## Infrastructure

### Cloud Setup
- [ ] Create databases on cloud (PostgreSQL)
- [ ] Set up Kafka cluster on cloud
- [ ] Set up Keycloak instance
- [ ] Update ConfigMaps with actual URLs
- [ ] Create Kubernetes secrets (DO NOT commit)

### Kubernetes
- [ ] Configure ingress for external access
- [ ] Set up monitoring (Prometheus/Grafana)
- [ ] Set up logging (ELK stack or similar)
- [ ] Configure resource limits
- [ ] Set up horizontal pod autoscaling
- [ ] Configure network policies

### CI/CD
- [ ] Complete CI pipelines (Mouktadir)
- [ ] Complete CD pipelines (Abdelghafour)
- [ ] Set up Docker registry
- [ ] Configure Kubernetes deployment automation
- [ ] Add deployment notifications

## Documentation

- [ ] Complete API documentation (Swagger/OpenAPI)
- [ ] Add architecture diagrams
- [ ] Add sequence diagrams for key flows
- [ ] Add database schema documentation
- [ ] Add deployment runbooks

## Security

- [ ] Security audit
- [ ] Add input validation everywhere
- [ ] Add rate limiting
- [ ] Add CORS configuration
- [ ] Add security headers
- [ ] Set up SSL/TLS certificates
- [ ] Add secrets management (Vault or similar)

