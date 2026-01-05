import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { 
  Account, 
  AccountApiResponse, 
  Operation, 
  PagedResponse,
  Beneficiary,
  Transaction
} from '../models/account.model';

@Injectable({
  providedIn: 'root'
})
export class AccountService {
  private apiUrl = `${environment.apiUrl}/accounts/queries`;

  constructor(private http: HttpClient) {}

  /**
   * Get account by ID
   */
  getAccountById(id: string): Observable<Account> {
    return this.http.get<AccountApiResponse<Account>>(`${this.apiUrl}/account/${id}`)
      .pipe(map(response => response.data));
  }

  /**
   * Get account by customer ID (keycloakUserId)
   */
  getAccountByCustomerId(customerId: string): Observable<Account> {
    return this.http.get<AccountApiResponse<Account>>(`${this.apiUrl}/account/customer/${customerId}`)
      .pipe(map(response => response.data));
  }

  /**
   * Get current user's account (uses keycloakUserId from stored user)
   */
  getMyAccount(): Observable<Account> {
    const currentUser = localStorage.getItem('currentUser');
    if (!currentUser) {
      throw new Error('No user logged in');
    }
    const user = JSON.parse(currentUser);
    const customerId = user.keycloakUserId;
    return this.getAccountByCustomerId(customerId);
  }

  /**
   * Get all accounts (paginated) - Admin only
   */
  getAllAccounts(page: number = 0, size: number = 10): Observable<PagedResponse<Account>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    return this.http.get<AccountApiResponse<PagedResponse<Account>>>(this.apiUrl, { params })
      .pipe(map(response => response.data));
  }

  /**
   * Get operations for an account (paginated)
   */
  getAccountOperations(accountId: string, page: number = 0, size: number = 10): Observable<PagedResponse<Operation>> {
    const params = new HttpParams()
      .set('accountId', accountId)
      .set('page', page.toString())
      .set('size', size.toString());
    
    return this.http.get<AccountApiResponse<PagedResponse<Operation>>>(`${this.apiUrl}/operations`, { params })
      .pipe(map(response => response.data));
  }

  /**
   * Get operation by ID
   */
  getOperationById(id: string): Observable<Operation> {
    return this.http.get<AccountApiResponse<Operation>>(`${this.apiUrl}/operation/${id}`)
      .pipe(map(response => response.data));
  }

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
