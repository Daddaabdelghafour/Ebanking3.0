import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { UserService } from '../../core/services/user.service';
import { AccountService } from '../../core/services/account.service';
import { NotificationService } from '../../core/services/notification.service';
import { User } from '../../core/models/user.model';
import { Account, AccountStatus } from '../../core/models/account.model';
import { LoginResponse } from '../../core/models/auth.model';
import { LoaderComponent } from '../../shared/components/loader/loader.component';

@Component({
  selector: 'app-customer-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, LoaderComponent],
  template: `
    <div class="dashboard-page">
      <app-loader *ngIf="isLoading"></app-loader>

      <!-- Welcome Banner -->
      <div class="welcome-banner">
        <div class="container">
          <div class="welcome-content">
            <div class="welcome-text">
              <span class="badge">Client</span>
              <h1>Bienvenue, {{ userProfile?.firstName || 'Client' }} !</h1>
              <p>Gérez vos comptes et services bancaires en toute sécurité</p>
            </div>
            <div class="welcome-stats">
              <div class="stat-card">
                <div class="stat-icon">
                  <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M11.8 10.9c-2.27-.59-3-1.2-3-2.15 0-1.09 1.01-1.85 2.7-1.85 1.78 0 2.44.85 2.5 2.1h2.21c-.07-1.72-1.12-3.3-3.21-3.81V3h-3v2.16c-1.94.42-3.5 1.68-3.5 3.61 0 2.31 1.91 3.46 4.7 4.13 2.5.6 3 1.48 3 2.41 0 .69-.49 1.79-2.7 1.79-2.06 0-2.87-.92-2.98-2.1h-2.2c.12 2.19 1.76 3.42 3.68 3.83V21h3v-2.15c1.95-.37 3.5-1.5 3.5-3.55 0-2.84-2.43-3.81-4.7-4.4z"/>
                  </svg>
                </div>
                <div class="stat-info">
                  <span class="stat-label">Solde total</span>
                  <span class="stat-value">{{ account ? accountService.formatBalance(account.balance) : '-- DH' }}</span>
                </div>
              </div>
              <div class="stat-card">
                <div class="stat-icon">
                  <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M20 4H4c-1.11 0-1.99.89-1.99 2L2 18c0 1.11.89 2 2 2h16c1.11 0 2-.89 2-2V6c0-1.11-.89-2-2-2zm0 14H4v-6h16v6zm0-10H4V6h16v2z"/>
                  </svg>
                </div>
                <div class="stat-info">
                  <span class="stat-label">N° Compte</span>
                  <span class="stat-value">{{ account ? accountService.maskAccountNumber(account.accountNumber) : '•••• ----' }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Account Info Section -->
      <div class="container" *ngIf="account">
        <div class="section account-info">
          <h3>Informations du compte</h3>
          <div class="account-details-grid">
            <div class="detail-card">
              <span class="detail-label">RIB</span>
              <span class="detail-value">{{ account.rib }}</span>
            </div>
            <div class="detail-card">
              <span class="detail-label">IBAN</span>
              <span class="detail-value">{{ accountService.formatIban(account.iban) }}</span>
            </div>
            <div class="detail-card">
              <span class="detail-label">Statut</span>
              <span class="detail-value status-badge" [ngClass]="getStatusClass(account.status)">
                {{ getStatusLabel(account.status) }}
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- Account Loading Error -->
      <div class="container" *ngIf="!isLoading && !account && accountError">
        <div class="section account-error">
          <div class="error-card">
            <svg width="48" height="48" viewBox="0 0 24 24" fill="currentColor">
              <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"/>
            </svg>
            <h4>Compte non trouvé</h4>
            <p>{{ accountError }}</p>
          </div>
        </div>
      </div>

      <div class="container">
        <div class="dashboard-grid">
          <!-- Quick Actions -->
          <div class="section quick-actions">
            <h3>Actions rapides</h3>
            <div class="actions-grid">
              <button class="action-card" routerLink="/customer/transfer">
                <div class="action-icon">
                  <svg width="32" height="32" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M7 11h10v2H7zm0-4h10v2H7zm0 8h7v2H7zm13-9v14c0 1.1-.9 2-2 2H6c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2h12c1.1 0 2 .9 2 2z"/>
                  </svg>
                </div>
                <span class="action-title">Virement</span>
                <span class="action-desc">Transférer de l'argent</span>
              </button>

              <button class="action-card" routerLink="/customer/recharge">
                <div class="action-icon">
                  <svg width="32" height="32" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M17 1.01L7 1c-1.1 0-2 .9-2 2v18c0 1.1.9 2 2 2h10c1.1 0 2-.9 2-2V3c0-1.1-.9-1.99-2-1.99zM17 19H7V5h10v14z"/>
                  </svg>
                </div>
                <span class="action-title">Recharge</span>
                <span class="action-desc">Crédit mobile</span>
              </button>

              <button class="action-card" routerLink="/customer/bills">
                <div class="action-icon">
                  <svg width="32" height="32" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-7 14c-1.66 0-3-1.34-3-3s1.34-3 3-3 3 1.34 3 3-1.34 3-3 3z"/>
                  </svg>
                </div>
                <span class="action-title">Factures</span>
                <span class="action-desc">Payer vos factures</span>
              </button>

              <button class="action-card" routerLink="/customer/history">
                <div class="action-icon">
                  <svg width="32" height="32" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M13 3c-4.97 0-9 4.03-9 9H1l3.89 3.89.07.14L9 12H6c0-3.87 3.13-7 7-7s7 3.13 7 7-3.13 7-7 7c-1.93 0-3.68-.79-4.94-2.06l-1.42 1.42C8.27 19.99 10.51 21 13 21c4.97 0 9-4.03 9-9s-4.03-9-9-9zm-1 5v5l4.28 2.54.72-1.21-3.5-2.08V8H12z"/>
                  </svg>
                </div>
                <span class="action-title">Historique</span>
                <span class="action-desc">Consulter l'historique</span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .dashboard-page {
      min-height: 100vh;
      background: #F5F5F5;
    }

    .welcome-banner {
      background: linear-gradient(135deg, #00843D 0%, #006830 100%);
      padding: 3rem 0;
      color: white;
    }

    .container {
      max-width: 1200px;
      margin: 0 auto;
      padding: 0 1.5rem;
    }

    .welcome-content {
      display: flex;
      justify-content: space-between;
      align-items: center;
      flex-wrap: wrap;
      gap: 2rem;
    }

    .badge {
      display: inline-block;
      background: rgba(255, 255, 255, 0.2);
      padding: 0.25rem 0.75rem;
      border-radius: 20px;
      font-size: 0.875rem;
      margin-bottom: 0.5rem;
    }

    .welcome-text h1 {
      font-size: 2rem;
      margin-bottom: 0.5rem;
    }

    .welcome-text p {
      opacity: 0.9;
    }

    .welcome-stats {
      display: flex;
      gap: 1rem;
    }

    .stat-card {
      background: rgba(255, 255, 255, 0.15);
      backdrop-filter: blur(10px);
      border-radius: 16px;
      padding: 1rem 1.5rem;
      display: flex;
      align-items: center;
      gap: 1rem;
    }

    .stat-icon {
      width: 48px;
      height: 48px;
      background: rgba(255, 255, 255, 0.2);
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .stat-info {
      display: flex;
      flex-direction: column;
    }

    .stat-label {
      font-size: 0.875rem;
      opacity: 0.9;
    }

    .stat-value {
      font-size: 1.25rem;
      font-weight: 700;
    }

    .section {
      margin: 2rem 0;

      h3 {
        color: #212121;
        margin-bottom: 1rem;
      }
    }

    .actions-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      gap: 1rem;
    }

    .action-card {
      background: white;
      border: none;
      border-radius: 16px;
      padding: 1.5rem;
      text-align: center;
      cursor: pointer;
      transition: all 0.3s ease;
      border: 2px solid transparent;

      &:hover {
        border-color: #00843D;
        transform: translateY(-4px);
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
      }
    }

    .action-icon {
      width: 64px;
      height: 64px;
      background: linear-gradient(135deg, #E8F5E9, #C8E6C9);
      border-radius: 16px;
      display: flex;
      align-items: center;
      justify-content: center;
      margin: 0 auto 1rem;
      color: #00843D;
    }

    .action-title {
      display: block;
      font-weight: 600;
      color: #212121;
      margin-bottom: 0.25rem;
    }

    .action-desc {
      font-size: 0.875rem;
      color: #757575;
    }

    /* Account Info Section */
    .account-details-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
      gap: 1rem;
    }

    .detail-card {
      background: white;
      border-radius: 12px;
      padding: 1.25rem;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    }

    .detail-label {
      display: block;
      font-size: 0.75rem;
      color: #757575;
      text-transform: uppercase;
      letter-spacing: 0.5px;
      margin-bottom: 0.5rem;
    }

    .detail-value {
      display: block;
      font-size: 1rem;
      color: #212121;
      font-weight: 500;
      word-break: break-all;
    }

    .status-badge {
      display: inline-block;
      padding: 0.25rem 0.75rem;
      border-radius: 20px;
      font-size: 0.875rem;
      font-weight: 600;
    }

    .status-active {
      background: #E8F5E9;
      color: #2E7D32;
    }

    .status-inactive {
      background: #EEEEEE;
      color: #616161;
    }

    .status-suspended {
      background: #FFF3E0;
      color: #EF6C00;
    }

    .status-closed {
      background: #FFEBEE;
      color: #C62828;
    }

    .status-pending {
      background: #E3F2FD;
      color: #1565C0;
    }

    /* Error State */
    .account-error {
      margin: 2rem 0;
    }

    .error-card {
      background: #FFF3E0;
      border: 1px solid #FFE0B2;
      border-radius: 16px;
      padding: 2rem;
      text-align: center;
      color: #E65100;
    }

    .error-card svg {
      margin-bottom: 1rem;
      opacity: 0.7;
    }

    .error-card h4 {
      margin: 0 0 0.5rem;
      font-size: 1.25rem;
    }

    .error-card p {
      margin: 0;
      opacity: 0.8;
    }

    @media (max-width: 768px) {
      .welcome-content {
        flex-direction: column;
        text-align: center;
      }

      .welcome-stats {
        flex-direction: column;
        width: 100%;
      }
    }
  `]
})
export class CustomerDashboardComponent implements OnInit {
  isLoading = false;
  userProfile: User | null = null;
  currentUser: LoginResponse | null = null;
  account: Account | null = null;
  accountError: string | null = null;

  constructor(
    private authService: AuthService,
    private userService: UserService,
    public accountService: AccountService,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.currentUser = this.authService.getCurrentUserValue();
    this.loadUserProfile();
    this.loadAccount();
  }

  loadUserProfile(): void {
    this.isLoading = true;
    this.userService.getProfile().subscribe({
      next: (user) => {
        this.userProfile = user;
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
      }
    });
  }

  loadAccount(): void {
    this.accountService.getMyAccount().subscribe({
      next: (account) => {
        this.account = account;
        this.accountError = null;
      },
      error: (error) => {
        this.accountError = 'Impossible de charger les informations du compte. Veuillez réessayer plus tard.';
        console.error('Error loading account:', error);
      }
    });
  }

  getStatusClass(status: AccountStatus): string {
    const statusClasses: Record<AccountStatus, string> = {
      [AccountStatus.ACTIVE]: 'status-active',
      [AccountStatus.INACTIVE]: 'status-inactive',
      [AccountStatus.SUSPENDED]: 'status-suspended',
      [AccountStatus.CLOSED]: 'status-closed',
      [AccountStatus.PENDING]: 'status-pending'
    };
    return statusClasses[status] || 'status-pending';
  }

  getStatusLabel(status: AccountStatus): string {
    const statusLabels: Record<AccountStatus, string> = {
      [AccountStatus.ACTIVE]: 'Actif',
      [AccountStatus.INACTIVE]: 'Inactif',
      [AccountStatus.SUSPENDED]: 'Suspendu',
      [AccountStatus.CLOSED]: 'Fermé',
      [AccountStatus.PENDING]: 'En attente'
    };
    return statusLabels[status] || status;
  }
}