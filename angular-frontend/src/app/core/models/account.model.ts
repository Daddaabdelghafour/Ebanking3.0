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
  type: OperationType;
  amount: number;
  description: string;
  createdAt: string;
}

export enum OperationType {
    CREDIT = 'CREDIT',
    DEBIT = 'DEBIT'
}

/**
 * Transaction Model - Corresponds to TransactionResponseDTO
 */
export interface Transaction {
  id: string;
  sourceAccountId: string;
  destinationAccountId: string;
  sourceAccountNumber: string;
  destinationAccountNumber: string;
  amount: number;
  status: TransactionStatus;
  type: TransactionType;
  reference: string;
  description?: string;
  failureReason?: string;
  createdAt: string;
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

export enum TransactionType {
  TRANSFER = 'TRANSFER',
  PAYMENT = 'PAYMENT',
  WITHDRAWAL = 'WITHDRAWAL',
  DEPOSIT = 'DEPOSIT'
}

/**
 * Beneficiary Model - Corresponds to BeneficiaryResponseDTO
 */
export interface Beneficiary {
  id: string;
  beneficiaryName: string;
  beneficiaryRib: string;
  isActive: boolean;
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
  beneficiaryId: string;
  amount: number;
  reference: string;
}

export interface ConfirmTransactionRequest {
  transactionId: string;
  otpCode: string;
}

export interface AddBeneficiaryRequest {
  accountId: string;
  beneficiaryName: string;
  beneficiaryRib: string;
}

// Request DTOs for account service
export interface CreateAccountRequest {
  customerId: string;
  email: string;
  initialBalance?: number;
}

export interface OperationRequest {
  accountId: string;
  amount: number;
  description: string;
}

export interface UpdateAccountStatusRequest {
  accountStatus: AccountStatus;
}
