import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  Account,
  Operation,
  PagedResponse,
  ApiResponse,
  CreateAccountRequest,
  OperationRequest,
  TransferRequest,
  UpdateAccountStatusRequest
} from '../models/account.model';

@Injectable({
  providedIn: 'root'
})
export class AccountService {
  private readonly commandsUrl = `${environment.apiUrl}/accounts/commands`;
  private readonly queriesUrl = `${environment.apiUrl}/accounts/queries`;

  constructor(private http: HttpClient) {}

  // ==================== COMMAND ENDPOINTS ====================

  /**
   * Create a new account
   */
  createAccount(request: CreateAccountRequest): Observable<ApiResponse<string>> {
    return this.http.post<ApiResponse<string>>(this.commandsUrl, request);
  }

  /**
   * Credit an account (add funds)
   */
  creditAccount(accountId: string, request: OperationRequest): Observable<ApiResponse<string>> {
    return this.http.post<ApiResponse<string>>(
      `${this.commandsUrl}/${accountId}/credit`,
      request
    );
  }

  /**
   * Debit an account (withdraw funds)
   */
  debitAccount(accountId: string, request: OperationRequest): Observable<ApiResponse<string>> {
    return this.http.post<ApiResponse<string>>(
      `${this.commandsUrl}/${accountId}/debit`,
      request
    );
  }

  /**
   * Transfer funds between accounts
   */
  transferFunds(request: TransferRequest): Observable<ApiResponse<string>> {
    return this.http.post<ApiResponse<string>>(
      `${this.commandsUrl}/transfer`,
      request
    );
  }

  /**
   * Update account status (activate/suspend)
   */
  updateAccountStatus(
    accountId: string,
    request: UpdateAccountStatusRequest
  ): Observable<ApiResponse<string>> {
    return this.http.put<ApiResponse<string>>(
      `${this.commandsUrl}/${accountId}/status`,
      request
    );
  }

  /**
   * Delete an account
   */
  deleteAccount(accountId: string): Observable<ApiResponse<string>> {
    return this.http.delete<ApiResponse<string>>(
      `${this.commandsUrl}/${accountId}`
    );
  }

  // ==================== QUERY ENDPOINTS ====================

  /**
   * Get account by ID
   */
  getAccountById(accountId: string): Observable<ApiResponse<Account>> {
    return this.http.get<ApiResponse<Account>>(
      `${this.queriesUrl}/account/${accountId}`
    );
  }

  /**
   * Get account by customer ID
   */
  getAccountByCustomerId(customerId: string): Observable<ApiResponse<Account>> {
    return this.http.get<ApiResponse<Account>>(
      `${this.queriesUrl}/account/customer/${customerId}`
    );
  }

  /**
   * Get all accounts with pagination
   */
  getAllAccounts(page: number = 0, size: number = 10): Observable<ApiResponse<PagedResponse<Account>>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<ApiResponse<PagedResponse<Account>>>(
      this.queriesUrl,
      { params }
    );
  }

  /**
   * Get operation by ID
   */
  getOperationById(operationId: string): Observable<ApiResponse<Operation>> {
    return this.http.get<ApiResponse<Operation>>(
      `${this.queriesUrl}/operation/${operationId}`
    );
  }

  /**
   * Get operations by account ID with pagination
   */
  getOperationsByAccountId(
    accountId: string,
    page: number = 0,
    size: number = 10
  ): Observable<ApiResponse<PagedResponse<Operation>>> {
    const params = new HttpParams()
      .set('accountId', accountId)
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<ApiResponse<PagedResponse<Operation>>>(
      `${this.queriesUrl}/operations`,
      { params }
    );
  }
}
