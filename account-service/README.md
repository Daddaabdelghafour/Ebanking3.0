# Account Service

Account Management microservice for E-Bank 3.0 using **CQRS** and **Event Sourcing** with Axon Framework.

**Responsible**: Ishaq

## Features

- Event Sourcing with PostgreSQL as event store
- CQRS pattern with separate Command and Query sides
- Account lifecycle management (create, activate, suspend, delete)
- Balance operations (credit, debit, transfer)
- Automatic RIB/IBAN generation for Moroccan banking
- Notification integration for account events
- Full audit trail through event store
- **Kafka integration** for event-driven microservices communication

## API Endpoints

### Command Endpoints
- `POST /accounts/commands` - Create new account
- `POST /accounts/commands/{id}/credit` - Credit account (deposit)
- `POST /accounts/commands/{id}/debit` - Debit account (withdraw)
- `POST /accounts/commands/transfer` - Transfer between accounts
- `PUT /accounts/commands/{id}/status` - Activate/suspend account
- `DELETE /accounts/commands/{id}` - Delete account (balance must be 0)

### Query Endpoints
- `GET /accounts/queries` - Get all accounts (paginated)
- `GET /accounts/queries/account/{id}` - Get account by ID
- `GET /accounts/queries/account/customer/{customerId}` - Get account by customer ID
- `GET /accounts/queries/operation/{id}` - Get operation by ID
- `GET /accounts/queries/operations?accountId={id}&page={n}&size={s}` - Get account operations (paginated)

## Event Sourcing Architecture

### Events Published
- `AccountCreationEvent` - When account is created
- `AccountActivatedEvent` - When account is activated
- `AccountSuspendedEvent` - When account is suspended
- `AccountCreditedEvent` - When account receives credit
- `AccountDebitedEvent` - When account is debited
- `AccountDeletedEvent` - When account is deleted

### Event Store
- PostgreSQL tables: `domain_event_entry`, `snapshot_event_entry`, `association_value_entry`, `token_entry`
- Events are immutable and provide complete audit trail

## Kafka Integration

### Published Events (Producer)
The service publishes account events to Kafka topics after processing Axon events:

| Topic | Event Type | Description |
|-------|------------|-------------|
| `account.created` | ACCOUNT_CREATED | New account created with RIB/IBAN |
| `account.activated` | ACCOUNT_ACTIVATED | Account activated and ready |
| `account.suspended` | ACCOUNT_SUSPENDED | Account suspended (security/compliance) |
| `account.deleted` | ACCOUNT_DELETED | Account soft-deleted |
| `account.credited` | ACCOUNT_CREDITED | Funds added to account |
| `account.debited` | ACCOUNT_DEBITED | Funds withdrawn from account |
| `account.transferred` | ACCOUNT_TRANSFERRED | Transfer between accounts |

**Event Schema Example:**
```json
{
  "accountId": "uuid",
  "customerId": "uuid",
  "email": "customer@example.com",
  "accountNumber": "string",
  "iban": "string",
  "balance": 1000.00,
  "status": "ACTIVATED",
  "amount": 100.00,
  "description": "Credit operation",
  "timestamp": "2026-01-02T10:30:00",
  "eventType": "ACCOUNT_CREDITED"
}
```

### Consumed Events (Consumer)
The service listens to external events and executes commands:

| Topic | Event Type | Action |
|-------|------------|--------|
| `customer.created` | CUSTOMER_CREATED | Auto-create account for new customer |
| `customer.deleted` | CUSTOMER_DELETED | Log warning (manual intervention) |
| `payment.completed` | PAYMENT_COMPLETED | Credit account with payment amount |
| `payment.failed` | PAYMENT_FAILED | Log warning for manual review |
| `fraud.detected` | FRAUD_DETECTED | Immediately suspend account |

**Consumer Group:** `account-service-group`

## Environment Variables

### Required
- `DB_URL`: PostgreSQL database URL
- `DB_USERNAME`: Database username
- `DB_PASSWORD`: Database password

### Optional
- `KAFKA_BOOTSTRAP_SERVERS`: Kafka broker address (default: `localhost:9092`)
- `notification-service.url`: Notification service URL (default: `http://localhost:8082/api/v1/notifications`)
- `bank.bank-code`: Moroccan bank code (default: `026`)
- `bank.city-code`: City code for RIB (default: `001`)
- `bank.currency`: Currency (default: `MAD`)

## Running Locally

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## Technical Stack

- **Framework**: Spring Boot 3.5.7
- **Java Version**: 17
- **Message Broker**: Apache Kafka (Spring Kafka)
- **Event Sourcing**: Axon Framework 4.12.1
- **Database**: PostgreSQL (for event store and projections)
- **Object Mapping**: MapStruct
- **API Communication**: OpenFeign
- **Build Tool**: Maven

## Architecture

### Command Side
- `AccountAggregate`: Domain aggregate with business logic
- `Commands`: Immutable command objects
- `CommandFactory`: Factory for creating commands
- `CommandController`: REST endpoints for commands
- `CommandGateway`: Axon command dispatcher

### Query Side
- `EventHandlers`: Process events and update read models
- `Entities`: JPA entities for read models (Account, Operation)
- `QueryHandlers`: Handle queries and return DTOs
- `QueryController`: REST endpoints for queries
- `QueryGateway`: Axon query dispatcher

