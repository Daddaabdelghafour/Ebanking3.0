import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  Transaction,
  Beneficiary,
  PagedResponse,
  ApiResponse,
  InitiateTransactionRequest,
  ConfirmTransactionRequest,
  AddBeneficiaryRequest
} from '../models/account.model';

@Injectable({
  providedIn: 'root'
})
export class TransactionService {
  private readonly apiUrl = `${environment.apiUrl}/transactions`;

  constructor(private http: HttpClient) {}

  // ==================== TRANSACTION ENDPOINTS ====================

  /**
   * Initiate a new transaction (sends OTP)
   */
  initiateTransaction(request: InitiateTransactionRequest): Observable<ApiResponse<Transaction>> {
    return this.http.post<ApiResponse<Transaction>>(
      `${this.apiUrl}/initiate`,
      request
    );
  }

  /**
   * Confirm transaction with OTP
   */
  confirmTransaction(request: ConfirmTransactionRequest): Observable<ApiResponse<Transaction>> {
    return this.http.post<ApiResponse<Transaction>>(
      `${this.apiUrl}/confirm`,
      request
    );
  }

  /**
   * Get transactions by account ID with pagination
   */
  getTransactionsByAccountId(
    accountId: string,
    page: number = 0,
    size: number = 10
  ): Observable<ApiResponse<PagedResponse<Transaction>>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<ApiResponse<PagedResponse<Transaction>>>(
      `${this.apiUrl}/account/${accountId}`,
      { params }
    );
  }

  /**
   * Get transaction by ID
   */
  getTransactionById(
    transactionId: string,
    accountId: string
  ): Observable<ApiResponse<Transaction>> {
    return this.http.get<ApiResponse<Transaction>>(
      `${this.apiUrl}/${transactionId}/account/${accountId}`
    );
  }

  // ==================== BENEFICIARY ENDPOINTS ====================

  /**
   * Add a new beneficiary
   */
  addBeneficiary(request: AddBeneficiaryRequest): Observable<ApiResponse<Beneficiary>> {
    return this.http.post<ApiResponse<Beneficiary>>(
      `${this.apiUrl}/beneficiaries`,
      request
    );
  }

  /**
   * Get all beneficiaries for an account
   */
  getBeneficiariesByAccountId(accountId: string): Observable<ApiResponse<Beneficiary[]>> {
    return this.http.get<ApiResponse<Beneficiary[]>>(
      `${this.apiUrl}/beneficiaries/account/${accountId}`
    );
  }

  /**
   * Get beneficiary by ID
   */
  getBeneficiaryById(
    beneficiaryId: string,
    accountId: string
  ): Observable<ApiResponse<Beneficiary>> {
    return this.http.get<ApiResponse<Beneficiary>>(
      `${this.apiUrl}/beneficiaries/${beneficiaryId}/account/${accountId}`
    );
  }

  /**
   * Delete a beneficiary
   */
  deleteBeneficiary(
    beneficiaryId: string,
    accountId: string
  ): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(
      `${this.apiUrl}/beneficiaries/${beneficiaryId}/account/${accountId}`
    );
  }
}
