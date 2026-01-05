# Account Service Frontend Implementation

This document provides an overview of the frontend implementation for the Account Service, including all available features and endpoints.

## Table of Contents

1. [Overview](#overview)
2. [Architecture](#architecture)
3. [Available Features](#available-features)
4. [API Endpoints](#api-endpoints)
5. [Components](#components)
6. [Services](#services)
7. [Models](#models)
8. [Routing](#routing)
9. [Usage Examples](#usage-examples)

## Overview

The Account Service frontend provides a complete interface for managing bank accounts, including:
- Account creation and management
- Fund transfers with OTP verification
- Transaction history tracking
- Beneficiary management
- Account operations (credit/debit)
- Real-time status updates

## Architecture

The implementation follows Angular best practices with:
- **Standalone Components**: All components are standalone for better modularity
- **Reactive Forms**: Used for all user inputs with proper validation
- **Service Layer**: Dedicated services for API communication
- **Type Safety**: Full TypeScript type definitions matching backend DTOs
- **CQRS Pattern**: Separate command and query endpoints

## Available Features

### 1. Account Management
- **List Accounts**: View all accounts with pagination, search, and filters
- **Account Details**: View detailed account information with operation history
- **Create Account**: Create new bank accounts with initial balance
- **Update Status**: Activate, suspend, or close accounts
- **Delete Account**: Remove accounts from the system

### 2. Money Operations
- **Credit Account**: Add funds to an account
- **Debit Account**: Withdraw funds from an account
- **Transfer Funds**: Transfer money between accounts with OTP verification

### 3. Transaction Management
- **Initiate Transaction**: Start a new transaction (sends OTP)
- **Confirm Transaction**: Complete transaction with OTP code
- **Transaction History**: View all transactions with filters and pagination
- **Transaction Details**: View individual transaction information

### 4. Beneficiary Management
- **Add Beneficiary**: Save frequently used recipient accounts
- **List Beneficiaries**: View all saved beneficiaries
- **Delete Beneficiary**: Remove saved beneficiaries
- **Quick Transfer**: Transfer to saved beneficiaries with one click

## API Endpoints

### Command Endpoints (Write Operations)

#### Base URL: `/accounts/commands`

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/` | Create a new account |
| POST | `/{id}/credit` | Credit an account |
| POST | `/{id}/debit` | Debit an account |
| POST | `/transfer` | Transfer funds between accounts |
| PUT | `/{id}/status` | Update account status |
| DELETE | `/{id}` | Delete an account |

### Query Endpoints (Read Operations)

#### Base URL: `/accounts/queries`

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | Get all accounts (paginated) |
| GET | `/account/{id}` | Get account by ID |
| GET | `/account/customer/{id}` | Get account by customer ID |
| GET | `/operation/{id}` | Get operation by ID |
| GET | `/operations` | Get operations by account ID (paginated) |

### Transaction Endpoints

#### Base URL: `/api/transactions`

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/initiate` | Initiate a transaction (sends OTP) |
| POST | `/confirm` | Confirm transaction with OTP |
| GET | `/account/{accountId}` | Get transactions by account ID |
| GET | `/{transactionId}/account/{accountId}` | Get transaction by ID |
| POST | `/beneficiaries` | Add a beneficiary |
| GET | `/beneficiaries/account/{accountId}` | Get all beneficiaries |
| GET | `/beneficiaries/{beneficiaryId}/account/{accountId}` | Get beneficiary by ID |
| DELETE | `/beneficiaries/{beneficiaryId}/account/{accountId}` | Delete beneficiary |

## Components

### 1. AccountsListComponent
**Path**: `features/accounts/accounts-list/accounts-list.component.ts`

Displays a paginated list of all accounts with:
- Search functionality (by account number, email, IBAN)
- Status filtering
- Pagination controls
- Quick action buttons

### 2. AccountDetailsComponent
**Path**: `features/accounts/account-details/account-details.component.ts`

Shows detailed account information including:
- Account details (number, IBAN, RIB, balance, status)
- Operations history with pagination
- Status update modal
- Delete confirmation modal

### 3. AccountCreateComponent
**Path**: `features/accounts/account-create/account-create.component.ts`

Form for creating new accounts with:
- Customer ID input
- Email validation
- Initial balance input
- Form validation

### 4. TransferComponent
**Path**: `features/accounts/transfer/transfer.component.ts`

Handles fund transfers with:
- Source and destination account selection
- Beneficiary quick select
- Amount input
- OTP confirmation modal
- Transfer summary

### 5. BeneficiariesComponent
**Path**: `features/accounts/beneficiaries/beneficiaries.component.ts`

Manages beneficiaries with:
- Card-based beneficiary list
- Add beneficiary modal
- Delete confirmation
- Quick transfer to beneficiaries

### 6. TransactionHistoryComponent
**Path**: `features/accounts/transaction-history/transaction-history.component.ts`

Displays transaction history with:
- Status filtering
- Pagination
- Transaction details (reference, type, amount, status)
- Failure reason display

## Services

### AccountService
**Path**: `core/services/account.service.ts`

Provides methods for:
- Account CRUD operations
- Credit/Debit operations
- Status updates
- Queries for accounts and operations

### TransactionService
**Path**: `core/services/transaction.service.ts`

Provides methods for:
- Transaction initiation and confirmation
- Transaction history queries
- Beneficiary management

## Models

**Path**: `core/models/account.model.ts`

Includes TypeScript interfaces for:
- `Account`: Account entity
- `Operation`: Operation entity
- `Transaction`: Transaction entity
- `Beneficiary`: Beneficiary entity
- `PagedResponse<T>`: Generic paginated response
- `ApiResponse<T>`: Generic API response wrapper
- Various request DTOs

### Enums
- `AccountStatus`: CREATED, ACTIVATED, SUSPENDED, CLOSED
- `OperationType`: CREDIT, DEBIT
- `TransactionStatus`: PENDING, OTP_SENT, COMPLETED, FAILED, CANCELLED
- `TransactionType`: TRANSFER, PAYMENT, WITHDRAWAL, DEPOSIT

## Routing

All account-related routes are under the `/accounts` path:

```typescript
/accounts                           // List all accounts
/accounts/create                    // Create new account
/accounts/:id                       // Account details
/accounts/:id/transfer              // Transfer from account
/accounts/:id/beneficiaries         // Manage beneficiaries
/accounts/:id/transactions          // Transaction history
```

## Usage Examples

### Creating a New Account

```typescript
const request: CreateAccountRequest = {
  customerId: '123e4567-e89b-12d3-a456-426614174000',
  email: 'customer@example.com',
  balance: 1000.00
};

accountService.createAccount(request).subscribe({
  next: (response) => {
    if (response.success) {
      console.log('Account created:', response.data);
    }
  }
});
```

### Initiating a Transfer

```typescript
const request: InitiateTransactionRequest = {
  sourceAccountId: 'source-account-uuid',
  destinationAccountId: 'destination-account-uuid',
  amount: 500.00,
  description: 'Payment for services'
};

transactionService.initiateTransaction(request).subscribe({
  next: (response) => {
    if (response.success) {
      // Show OTP modal
      console.log('Transaction initiated:', response.data);
    }
  }
});
```

### Confirming a Transfer with OTP

```typescript
const request: ConfirmTransactionRequest = {
  transactionId: 'transaction-uuid',
  otp: '123456'
};

transactionService.confirmTransaction(request).subscribe({
  next: (response) => {
    if (response.success) {
      console.log('Transfer completed:', response.data);
    }
  }
});
```

### Getting Account Operations

```typescript
accountService.getOperationsByAccountId(accountId, 0, 10).subscribe({
  next: (response) => {
    if (response.success && response.data) {
      console.log('Operations:', response.data.content);
      console.log('Total:', response.data.totalElements);
    }
  }
});
```

## Security Features

1. **OTP Verification**: All transfers require OTP confirmation sent to registered email
2. **Route Guards**: All routes are protected with authentication guards
3. **Form Validation**: Client-side validation for all inputs
4. **Error Handling**: Comprehensive error handling with user-friendly messages

## Styling

- Uses Bootstrap 5 for responsive design
- Custom SCSS for component-specific styling
- Bootstrap Icons for icons
- Responsive tables and cards
- Modal dialogs for confirmations

## Best Practices Implemented

1. **Separation of Concerns**: Clear separation between presentation and business logic
2. **Type Safety**: Full TypeScript types for all data structures
3. **Error Handling**: Centralized error handling with notifications
4. **Loading States**: Loading indicators for all async operations
5. **Pagination**: Server-side pagination for large datasets
6. **Validation**: Both client and server-side validation
7. **Accessibility**: Proper ARIA labels and semantic HTML
8. **Responsive Design**: Mobile-first responsive layouts

## Testing Recommendations

1. **Unit Tests**: Test services and component logic
2. **Integration Tests**: Test component-service interactions
3. **E2E Tests**: Test complete user flows (create account → transfer → confirm)
4. **API Tests**: Test backend endpoints independently

## Future Enhancements

- [ ] Add account statement download (PDF/CSV)
- [ ] Implement real-time balance updates with WebSockets
- [ ] Add scheduled transfers
- [ ] Implement recurring payments
- [ ] Add transaction categories and tags
- [ ] Implement budget tracking
- [ ] Add multi-currency support
- [ ] Implement transaction search and advanced filtering
- [ ] Add transaction export functionality
- [ ] Implement account freeze/unfreeze feature

## Support

For issues or questions, please contact the development team or refer to the main project documentation.
