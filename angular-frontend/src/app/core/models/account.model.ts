export enum AccountStatus {
  CREATED = 'CREATED',
  ACTIVATED = 'ACTIVATED',
  SUSPENDED = 'SUSPENDED',
  CLOSED = 'CLOSED'
}

export enum OperationType {
  CREDIT = 'CREDIT',
  DEBIT = 'DEBIT'
}

export enum TransactionStatus {
  PENDING = 'PENDING',
  OTP_SENT = 'OTP_SENT',
  COMPLETED = 'COMPLETED',
  FAILED = 'FAILED',
  CANCELLED = 'CANCELLED'
}

export enum TransactionType {
  TRANSFER = 'TRANSFER',
  PAYMENT = 'PAYMENT',
  WITHDRAWAL = 'WITHDRAWAL',
  DEPOSIT = 'DEPOSIT'
}

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

export interface Operation {
  id: string;
  type: OperationType;
  amount: number;
  description: string;
  createdAt: string;
}

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
  failureReason?: string;
  createdAt: string;
}

export interface Beneficiary {
  id: string;
  beneficiaryName: string;
  beneficiaryRib: string;
  isActive: boolean;
  createdAt: string;
}

export interface PagedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}

export interface ApiResponse<T> {
  success: boolean;
  data?: T;
  message: string;
  timestamp: string;
  errors?: string[];
}

// Request DTOs
export interface CreateAccountRequest {
  customerId: string;
  email: string;
  balance: number;
}

export interface OperationRequest {
  accountId: string;
  amount: number;
  description: string;
}

export interface TransferRequest {
  sourceAccount: string;
  destinationAccount: string;
  amount: number;
}

export interface UpdateAccountStatusRequest {
  accountStatus: AccountStatus;
}

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
  beneficiaryName: string;
  beneficiaryRib: string;
}
