import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { 
  Account, 
  AccountApiResponse, 
  Operation, 
  PagedResponse,
  Beneficiary,
  Transaction,
  CreateAccountRequest,
  OperationRequest,
  UpdateAccountStatusRequest,
  ApiResponse
} from '../models/account.model';

@Injectable({
  providedIn: 'root'
})
export class AccountService {
  private queryUrl = `${environment.apiUrl}/accounts/queries`;
  private commandUrl = `${environment.apiUrl}/accounts/commands`;

  constructor(private http: HttpClient) {}

  // ==================== QUERY ENDPOINTS ====================

  /**
   * Get account by ID
   */
  getAccountById(id: string): Observable<Account> {
    return this.http.get<AccountApiResponse<Account>>(`${this.queryUrl}/account/${id}`)
      .pipe(map(response => response.data));
  }

  /**
   * Get account by customer ID (keycloakUserId)
   */
  getAccountByCustomerId(customerId: string): Observable<Account> {
    return this.http.get<AccountApiResponse<Account>>(`${this.queryUrl}/account/customer/${customerId}`)
      .pipe(map(response => response.data));
  }

  /**
   * Get current user's account (uses keycloakUserId from stored user)
   */
  getMyAccount(): Observable<Account> {
    const currentUser = localStorage.getItem('currentUser');
    if (!currentUser) {
      console.error('[AccountService] No user logged in');
      return throwError(() => new Error('No user logged in'));
    }
    
    try {
      const user = JSON.parse(currentUser);
      const customerId = user.keycloakUserId;
      
      if (!customerId) {
        console.error('[AccountService] No keycloakUserId found in user data:', user);
        return throwError(() => new Error('User ID not found'));
      }
      
      console.log('[AccountService] Fetching account for customer ID:', customerId);
      console.log('[AccountService] Full API URL:', `${this.queryUrl}/account/customer/${customerId}`);
      
      return this.getAccountByCustomerId(customerId);
    } catch (error) {
      console.error('[AccountService] Error parsing user data:', error);
      return throwError(() => new Error('Invalid user data'));
    }
  }

  /**
   * Get operations for an account (paginated)
   */
  getAccountOperations(accountId: string, page: number = 0, size: number = 10): Observable<PagedResponse<Operation>> {
    const params = new HttpParams()
      .set('accountId', accountId)
      .set('page', page.toString())
      .set('size', size.toString());
    
    return this.http.get<AccountApiResponse<PagedResponse<Operation>>>(`${this.queryUrl}/operations`, { params })
      .pipe(map(response => response.data));
  }

  /**
   * Get operation by ID
   */
  getOperationById(id: string): Observable<Operation> {
    return this.http.get<AccountApiResponse<Operation>>(`${this.queryUrl}/operation/${id}`)
      .pipe(map(response => response.data));
  }

  // ==================== COMMAND ENDPOINTS ====================

  /**
   * Create a new account
   */
  createAccount(request: CreateAccountRequest): Observable<ApiResponse<string>> {
    return this.http.post<ApiResponse<string>>(this.commandUrl, request);
  }

  /**
   * Credit an account
   */
  creditAccount(accountId: string, request: OperationRequest): Observable<ApiResponse<string>> {
    return this.http.post<ApiResponse<string>>(`${this.commandUrl}/${accountId}/credit`, request);
  }

  /**
   * Debit an account
   */
  debitAccount(accountId: string, request: OperationRequest): Observable<ApiResponse<string>> {
    return this.http.post<ApiResponse<string>>(`${this.commandUrl}/${accountId}/debit`, request);
  }

  /**
   * Update account status (activate or suspend)
   */
  updateAccountStatus(accountId: string, request: UpdateAccountStatusRequest): Observable<ApiResponse<string>> {
    return this.http.put<ApiResponse<string>>(`${this.commandUrl}/${accountId}/status`, request);
  }

  /**
   * Delete an account
   */
  deleteAccount(accountId: string): Observable<ApiResponse<string>> {
    return this.http.delete<ApiResponse<string>>(`${this.commandUrl}/${accountId}`);
  }

  // ==================== UTILITY METHODS ====================

  /**
   * Format balance to display currency
   */
  formatBalance(balance: number, currency: string = 'MAD'): string {
    return new Intl.NumberFormat('fr-MA', {
      style: 'currency',
      currency: currency,
      minimumFractionDigits: 2
    }).format(balance);
  }

  /**
   * Format IBAN for display (with spaces)
   */
  formatIban(iban: string): string {
    if (!iban) return '';
    return iban.replace(/(.{4})/g, '$1 ').trim();
  }

  /**
   * Mask account number for display
   */
  maskAccountNumber(accountNumber: string): string {
    if (!accountNumber) return '';
    const length = accountNumber.length;
    if (length <= 4) return accountNumber;
    return '•••• ' + accountNumber.slice(-4);
  }
}
