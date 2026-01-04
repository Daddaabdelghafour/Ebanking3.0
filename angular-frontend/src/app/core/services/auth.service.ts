import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { BehaviorSubject, Observable, tap, catchError, throwError } from 'rxjs';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import { NotificationService } from './notification.service';
import {
  LoginRequest,
  LoginResponse,
  RegisterRequest,
  AuthResponse,
  VerifyEmailRequest,
  ForgotPasswordRequest,
  ResetPasswordRequest,
  ChangePasswordRequest,
  UserRole
} from '../models/auth.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/auth`;
  private currentUserSubject = new BehaviorSubject<LoginResponse | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(
    private http: HttpClient,
    private router: Router,
    private notificationService: NotificationService
  ) {
    const storedUser = localStorage.getItem('currentUser');
    if (storedUser) {
      this.currentUserSubject.next(JSON.parse(storedUser));
    }
  }

  /**
   * Inscription
   */
  register(request: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/public/register`, request);
  }

  /**
   * Connexion
   */
  login(request: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/public/login`, request)
      .pipe(
        tap(response => {
          localStorage.setItem('accessToken', response.accessToken);
          localStorage.setItem('refreshToken', response.refreshToken);
          localStorage.setItem('currentUser', JSON.stringify(response));
          this.currentUserSubject.next(response);
        }),
        catchError((error: HttpErrorResponse) => {
          // Gérer le cas où l'email n'est pas vérifié
          if (this.isEmailNotVerifiedError(error)) {
            const email = request.email;
            localStorage.setItem('pendingVerificationEmail', email);
            this.notificationService.showWarning('Votre email n\'est pas encore vérifié. Veuillez le vérifier.');
            this.router.navigate(['/auth/verify-email'], { queryParams: { email } });
          }
          return throwError(() => error);
        })
      );
  }

  /**
   * Vérifier si c'est une erreur d'email non vérifié
   */
  private isEmailNotVerifiedError(error: HttpErrorResponse): boolean {
    if (error.status === 401 || error.status === 403 || error.status === 400) {
      const errorBody = error.error;
      if (!errorBody) return false;
      
      const message = String(errorBody.message || '').toLowerCase();
      return message.includes('emailnotverifiedexception') ||
             message.includes('email not verified') ||
             message.includes('email non vérifié');
    }
    return false;
  }

  /**
   * Vérifier l'email
   */
  verifyEmail(request: VerifyEmailRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/public/verify-email`, request)
      .pipe(
        tap(() => {
          localStorage.removeItem('pendingVerificationEmail');
        })
      );
  }

  /**
   * Renvoyer le code de vérification
   */
  resendVerificationCode(email: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/public/resend-verification-code`, { email });
  }

  /**
   * Récupérer l'email en attente de vérification
   */
  getPendingVerificationEmail(): string | null {
    return localStorage.getItem('pendingVerificationEmail');
  }

  /**
   * Mot de passe oublié
   */
  forgotPassword(request: ForgotPasswordRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/public/forgot-password`, request);
  }

  /**
   * Réinitialiser le mot de passe
   */
  resetPassword(request: ResetPasswordRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/public/reset-password`, request);
  }

  /**
   * Changer le mot de passe (connecté)
   */
  changePassword(request: ChangePasswordRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/change-password`, request);
  }

  /**
   * Rafraîchir le token
   */
  refreshToken(): Observable<LoginResponse> {
    const refreshToken = localStorage.getItem('refreshToken');
    return this.http.post<LoginResponse>(`${this.apiUrl}/public/refresh`, { refresh_token: refreshToken })
      .pipe(
        tap(response => {
          localStorage.setItem('accessToken', response.accessToken);
          localStorage.setItem('refreshToken', response.refreshToken);
          localStorage.setItem('currentUser', JSON.stringify(response));
          this.currentUserSubject.next(response);
        })
      );
  }

  /**
   * Déconnexion
   */
  logout(): Observable<any> {
    const refreshToken = localStorage.getItem('refreshToken');
    return this.http.post(`${this.apiUrl}/logout`, { refresh_token: refreshToken })
      .pipe(
        tap(() => {
          this.clearAuth();
          this.router.navigate(['/auth/login']);
        })
      );
  }

  getCurrentUser(): Observable<any> {
    return this.http.get(`${this.apiUrl}/me`);
  }

  isAuthenticated(): boolean {
    return !!localStorage.getItem('accessToken');
  }

  getToken(): string | null {
    return localStorage.getItem('accessToken');
  }

  getCurrentUserValue(): LoginResponse | null {
    return this.currentUserSubject.value;
  }

  /**
   * Récupérer les rôles de l'utilisateur connecté
   */
  getUserRoles(): string[] {
    const user = this.getCurrentUserValue();
    return user?.roles || [];
  }

  /**
   * Vérifier si l'utilisateur a un rôle spécifique
   */
  hasRole(role: UserRole | string): boolean {
    const roles = this.getUserRoles();
    return roles.some(r => r.toUpperCase() === role.toUpperCase());
  }

  /**
   * Vérifier si l'utilisateur a au moins un des rôles spécifiés
   */
  hasAnyRole(roles: (UserRole | string)[]): boolean {
    return roles.some(role => this.hasRole(role));
  }

  /**
   * Vérifier si l'utilisateur est Admin
   */
  isAdmin(): boolean {
    return this.hasRole(UserRole.ADMIN);
  }

  /**
   * Vérifier si l'utilisateur est Customer
   */
  isCustomer(): boolean {
    return this.hasRole(UserRole.CUSTOMER);
  }

  /**
   * Vérifier si l'utilisateur est Agent Bank
   */
  isAgentBank(): boolean {
    return this.hasRole(UserRole.AGENT_BANK);
  }

  /**
   * Obtenir le dashboard URL selon le rôle de l'utilisateur
   */
  getDashboardUrlByRole(): string {
    if (this.isAdmin()) {
      return '/admin/dashboard';
    } else if (this.isAgentBank()) {
      return '/agent/dashboard';
    } else {
      // Par défaut: Customer
      return '/customer/dashboard';
    }
  }

  /**
   * Rediriger vers le dashboard approprié selon le rôle
   */
  redirectToDashboard(): void {
    const dashboardUrl = this.getDashboardUrlByRole();
    this.router.navigate([dashboardUrl]);
  }

  private clearAuth(): void {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('currentUser');
    localStorage.removeItem('pendingVerificationEmail');
    this.currentUserSubject.next(null);
  }
}