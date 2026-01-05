/**
 * Account Model - Corresponds to AccountResponseDTO from account-service
 */
export interface Account {
  id: string;
  customerId: string;
  email: string;
  balance: number;
  accountNumber: string;
  ribKey: string;
  rib: string;
  iban: string;
  status: AccountStatus;
  createdAt: string;
  updatedAt: string;
  deletedAt?: string;
}

export enum AccountStatus {
    CREATED = 'CREATED',
    ACTIVATED = 'ACTIVATED',
    SUSPENDED = 'SUSPENDED',
    DELETED = 'DELETED'
}

/**
 * Operation Model - Corresponds to OperationResponseDTO
 */
export interface Operation {
  id: string;
  accountId: string;
  type: OperationType;
  amount: number;
  description: string;
  reference: string;
  status: OperationStatus;
  createdAt: string;
  executedAt?: string;
}

export enum OperationType {
    CREDIT = 'CREDIT',
    DEBIT = 'DEBIT'
}

export enum OperationStatus {
  PENDING = 'PENDING',
  COMPLETED = 'COMPLETED',
  FAILED = 'FAILED',
  CANCELLED = 'CANCELLED'
}

/**
 * Transaction Model
 */
export interface Transaction {
  id: string;
  sourceAccountId: string;
  destinationAccountId: string;
  amount: number;
  description: string;
  reference: string;
  status: TransactionStatus;
  createdAt: string;
  executedAt?: string;
}

export enum TransactionStatus {
PENDING = 'PENDING',
    OTP_SENT = 'OTP_SENT',
    OTP_VERIFIED = 'OTP_VERIFIED',
    PROCESSING = 'PROCESSING',
    COMPLETED = 'COMPLETED',
    FAILED = 'FAILED',
    CANCELLED = 'CANCELLED',
    EXPIRED = 'EXPIRED'
}

/**
 * Beneficiary Model
 */
export interface Beneficiary {
  id: string;
  accountId: string;
  name: string;
  rib: string;
  iban: string;
  bankName: string;
  createdAt: string;
}

/**
 * API Response wrapper from account-service
 */
export interface AccountApiResponse<T> {
  success: boolean;
  data: T;
  message: string;
  timestamp: string;
}

// Alias for consistency with transaction service
export type ApiResponse<T> = AccountApiResponse<T>;

/**
 * Paged Response for lists
 */
export interface PagedResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
  first: boolean;
}

// Request DTOs for transaction service
export interface InitiateTransactionRequest {
  sourceAccountId: string;
  destinationAccountId: string;
  amount: number;
  description?: string;
}

export interface ConfirmTransactionRequest {
  transactionId: string;
  otp: string;
}

export interface AddBeneficiaryRequest {
  accountId: string;
  name: string;
  rib: string;
  iban?: string;
  bankName?: string;
}

// Request DTOs for account service  
export interface CreateAccountRequest {
  customerId: string;
  email: string;
  initialBalance?: number;
}
