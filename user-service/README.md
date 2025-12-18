# User Service

Customer Management microservice for E-Bank 3.0.

**Status**: Already Developed

## Features

- Customer CRUD operations
- Customer profile management
- Integration with KYC service
- Kafka event publishing and consumption

## API Endpoints

- `GET /api/customers` - Get all customers
- `GET /api/customers/{id}` - Get customer by ID
- `POST /api/customers` - Create new customer
- `PUT /api/customers/{id}` - Update customer
- `DELETE /api/customers/{id}` - Delete customer

## Kafka Events

### Published Events
- `CUSTOMER_CREATED` - Published when a new customer is created
- `CUSTOMER_UPDATED` - Published when customer is updated

### Consumed Events
- `USER_REGISTERED` - Creates customer profile when user registers
- `KYC_VERIFIED` - Updates customer verification status
- `KYC_REJECTED` - Updates customer verification status

## Environment Variables

See `application.yml` for configuration details.

