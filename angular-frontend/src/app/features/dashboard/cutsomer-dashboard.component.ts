import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { UserService } from '../../core/services/user.service';
import { AccountService } from '../../core/services/account.service';
import { TransactionService } from '../../core/services/transaction.service';
import { NotificationService } from '../../core/services/notification.service';
import { User } from '../../core/models/user.model';
import { Account, AccountStatus, Transaction, TransactionStatus } from '../../core/models/account.model';
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
              <button class="action-card" *ngIf="account" [routerLink]="['/accounts', account.id, 'transfer']">
                <div class="action-icon">
                  <svg width="32" height="32" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M7 11h10v2H7zm0-4h10v2H7zm0 8h7v2H7zm13-9v14c0 1.1-.9 2-2 2H6c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2h12c1.1 0 2 .9 2 2z"/>
                  </svg>
                </div>
                <span class="action-title">Virement</span>
                <span class="action-desc">Transférer de l'argent</span>
              </button>

              <button class="action-card" *ngIf="account" [routerLink]="['/accounts', account.id, 'operations', 'credit']">
                <div class="action-icon">
                  <svg width="32" height="32" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"/>
                  </svg>
                </div>
                <span class="action-title">Crédit</span>
                <span class="action-desc">Ajouter des fonds</span>
              </button>

              <button class="action-card" *ngIf="account" [routerLink]="['/accounts', account.id, 'operations', 'debit']">
                <div class="action-icon">
                  <svg width="32" height="32" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M19 13H5v-2h14v2z"/>
                  </svg>
                </div>
                <span class="action-title">Débit</span>
                <span class="action-desc">Retirer des fonds</span>
              </button>

              <button class="action-card" *ngIf="account" [routerLink]="['/accounts', account.id, 'beneficiaries']">
                <div class="action-icon">
                  <svg width="32" height="32" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M16 11c1.66 0 2.99-1.34 2.99-3S17.66 5 16 5c-1.66 0-3 1.34-3 3s1.34 3 3 3zm-8 0c1.66 0 2.99-1.34 2.99-3S9.66 5 8 5C6.34 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5c0-2.33-4.67-3.5-7-3.5zm8 0c-.29 0-.62.02-.97.05 1.16.84 1.97 1.97 1.97 3.45V19h6v-2.5c0-2.33-4.67-3.5-7-3.5z"/>
                  </svg>
                </div>
                <span class="action-title">Bénéficiaires</span>
                <span class="action-desc">Gérer les bénéficiaires</span>
              </button>

              <button class="action-card" *ngIf="account" [routerLink]="['/accounts', account.id, 'transactions']">
                <div class="action-icon">
                  <svg width="32" height="32" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M13 3c-4.97 0-9 4.03-9 9H1l3.89 3.89.07.14L9 12H6c0-3.87 3.13-7 7-7s7 3.13 7 7-3.13 7-7 7c-1.93 0-3.68-.79-4.94-2.06l-1.42 1.42C8.27 19.99 10.51 21 13 21c4.97 0 9-4.03 9-9s-4.03-9-9-9zm-1 5v5l4.28 2.54.72-1.21-3.5-2.08V8H12z"/>
                  </svg>
                </div>
                <span class="action-title">Historique</span>
                <span class="action-desc">Consulter l'historique</span>
              </button>

              <button class="action-card" *ngIf="account" [routerLink]="['/accounts', account.id]">
                <div class="action-icon">
                  <svg width="32" height="32" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-7 3c1.93 0 3.5 1.57 3.5 3.5S13.93 13 12 13s-3.5-1.57-3.5-3.5S10.07 6 12 6zm7 13H5v-.23c0-.62.28-1.2.76-1.58C7.47 15.82 9.64 15 12 15s4.53.82 6.24 2.19c.48.38.76.97.76 1.58V19z"/>
                  </svg>
                </div>
                <span class="action-title">Mon Compte</span>
                <span class="action-desc">Détails du compte</span>
              </button>
            </div>
          </div>

          <!-- Recent Transactions -->
          <div class="section recent-transactions" *ngIf="account">
            <div class="section-header">
              <h3>Transactions récentes</h3>
              <button class="btn-link" [routerLink]="['/accounts', account.id, 'transactions']">
                Voir tout
                <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M8.59 16.59L13.17 12 8.59 7.41 10 6l6 6-6 6-1.41-1.41z"/>
                </svg>
              </button>
            </div>
            
            <app-loader *ngIf="isLoadingTransactions"></app-loader>
            
            <div class="transactions-container" *ngIf="!isLoadingTransactions">
              <div class="transaction-card" *ngFor="let transaction of recentTransactions">
                <div class="transaction-info">
                  <div class="transaction-icon" [ngClass]="getTransactionIconClass(transaction)">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                      <path *ngIf="transaction.sourceAccountId === account?.id" d="M16.01 11H4v2h12.01v3L20 12l-3.99-4z"/>
                      <path *ngIf="transaction.destinationAccountId === account?.id" d="M20 12l-3.99 4v-3H4v-2h12.01V8z"/>
                    </svg>
                  </div>
                  <div class="transaction-details">
                    <span class="transaction-description">{{ transaction.description || 'Transaction' }}</span>
                    <span class="transaction-reference">Réf: {{ transaction.reference }}</span>
                    <span class="transaction-date">{{ formatDate(transaction.createdAt) }}</span>
                  </div>
                </div>
                <div class="transaction-amount-status">
                  <span class="transaction-amount" [ngClass]="getAmountClass(transaction)">
                    {{ formatTransactionAmount(transaction) }}
                  </span>
                  <span class="transaction-status" [ngClass]="getStatusClass(transaction.status)">
                    {{ getStatusLabel(transaction.status) }}
                  </span>
                </div>
              </div>
              
              <div class="no-transactions" *ngIf="recentTransactions.length === 0 && !transactionsError">
                <svg width="64" height="64" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M13 3c-4.97 0-9 4.03-9 9H1l3.89 3.89.07.14L9 12H6c0-3.87 3.13-7 7-7s7 3.13 7 7-3.13 7-7 7c-1.93 0-3.68-.79-4.94-2.06l-1.42 1.42C8.27 19.99 10.51 21 13 21c4.97 0 9-4.03 9-9s-4.03-9-9-9zm-1 5v5l4.28 2.54.72-1.21-3.5-2.08V8H12z"/>
                </svg>
                <p>Aucune transaction récente</p>
              </div>
              
              <div class="transactions-error" *ngIf="transactionsError">
                <svg width="48" height="48" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"/>
                </svg>
                <p>{{ transactionsError }}</p>
              </div>
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

    /* Recent Transactions Section */
    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 1rem;
    }

    .btn-link {
      background: none;
      border: none;
      color: #00843D;
      font-weight: 600;
      cursor: pointer;
      display: flex;
      align-items: center;
      gap: 0.25rem;
      transition: color 0.2s ease;

      &:hover {
        color: #006830;
      }
    }

    .transactions-container {
      background: white;
      border-radius: 16px;
      padding: 1.5rem;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    }

    .transaction-card {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 1rem 0;
      border-bottom: 1px solid #F5F5F5;

      &:last-child {
        border-bottom: none;
      }
    }

    .transaction-info {
      display: flex;
      align-items: center;
      gap: 1rem;
    }

    .transaction-icon {
      width: 40px;
      height: 40px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      flex-shrink: 0;

      &.outgoing {
        background: #FFEBEE;
        color: #C62828;
      }

      &.incoming {
        background: #E8F5E9;
        color: #2E7D32;
      }
    }

    .transaction-details {
      display: flex;
      flex-direction: column;
      gap: 0.25rem;
    }

    .transaction-description {
      font-weight: 600;
      color: #212121;
    }

    .transaction-reference {
      font-size: 0.75rem;
      color: #757575;
    }

    .transaction-date {
      font-size: 0.75rem;
      color: #9E9E9E;
    }

    .transaction-amount-status {
      display: flex;
      flex-direction: column;
      align-items: flex-end;
      gap: 0.5rem;
    }

    .transaction-amount {
      font-weight: 700;
      font-size: 1.125rem;

      &.positive {
        color: #2E7D32;
      }

      &.negative {
        color: #C62828;
      }
    }

    .transaction-status {
      padding: 0.25rem 0.75rem;
      border-radius: 12px;
      font-size: 0.75rem;
      font-weight: 600;

      &.status-completed {
        background: #E8F5E9;
        color: #2E7D32;
      }

      &.status-pending {
        background: #FFF3E0;
        color: #EF6C00;
      }

      &.status-failed {
        background: #FFEBEE;
        color: #C62828;
      }

      &.status-processing {
        background: #E3F2FD;
        color: #1565C0;
      }
    }

    .no-transactions {
      text-align: center;
      padding: 3rem 1rem;
      color: #9E9E9E;

      svg {
        opacity: 0.3;
        margin-bottom: 1rem;
      }

      p {
        margin: 0;
        font-size: 1rem;
      }
    }

    .transactions-error {
      text-align: center;
      padding: 2rem 1rem;
      color: #E65100;
      background: #FFF3E0;
      border-radius: 12px;

      svg {
        opacity: 0.7;
        margin-bottom: 1rem;
      }

      p {
        margin: 0;
      }
    }

    .dashboard-grid {
      display: grid;
      gap: 2rem;
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
  isLoadingTransactions = false;
  userProfile: User | null = null;
  currentUser: LoginResponse | null = null;
  account: Account | null = null;
  accountError: string | null = null;
  recentTransactions: Transaction[] = [];
  transactionsError: string | null = null;

  constructor(
    private authService: AuthService,
    private userService: UserService,
    public accountService: AccountService,
    private transactionService: TransactionService,
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
    console.log('[Dashboard] Loading account...');
    this.accountService.getMyAccount().subscribe({
      next: (account) => {
        console.log('[Dashboard] Account loaded successfully:', account);
        this.account = account;
        this.accountError = null;
        // Load recent transactions after account is loaded
        this.loadRecentTransactions();
      },
      error: (error) => {
        console.error('[Dashboard] Error loading account:', error);
        console.error('[Dashboard] Error status:', error.status);
        console.error('[Dashboard] Error message:', error.message);
        console.error('[Dashboard] Full error:', error);
        
        if (error.status === 404) {
          this.accountError = 'Compte non trouvé. Veuillez contacter le support pour créer votre compte.';
        } else if (error.status === 0) {
          this.accountError = 'Impossible de se connecter au serveur. Vérifiez que les services sont démarrés.';
        } else {
          this.accountError = 'Impossible de charger les informations du compte. Veuillez réessayer plus tard.';
        }
      }
    });
  }

  loadRecentTransactions(): void {
    if (!this.account?.id) {
      console.log('[Dashboard] No account ID available for loading transactions');
      return;
    }

    console.log('[Dashboard] Loading recent transactions for account:', this.account.id);
    this.isLoadingTransactions = true;
    this.transactionsError = null;

    // Get the 5 most recent transactions
    this.transactionService.getTransactionsByAccountId(this.account.id, 0, 5).subscribe({
      next: (response) => {
        console.log('[Dashboard] Transactions loaded successfully:', response);
        if (response.success && response.data) {
          this.recentTransactions = response.data.content;
        }
        this.isLoadingTransactions = false;
      },
      error: (error) => {
        console.error('[Dashboard] Error loading transactions:', error);
        if (error.status === 404) {
          // No transactions found is not really an error
          this.recentTransactions = [];
        } else if (error.status === 0) {
          this.transactionsError = 'Impossible de se connecter au serveur.';
        } else {
          this.transactionsError = 'Impossible de charger les transactions.';
        }
        this.isLoadingTransactions = false;
      }
    });
  }

  getStatusClass(status: AccountStatus | TransactionStatus): string {
    // Account status classes
    const accountStatusClasses: Record<AccountStatus, string> = {
      [AccountStatus.CREATED]: 'status-info',
      [AccountStatus.ACTIVATED]: 'status-active',
      [AccountStatus.SUSPENDED]: 'status-suspended',
      [AccountStatus.DELETED]: 'status-closed'
    };
    
    // Transaction status classes
    const transactionStatusClasses: Record<TransactionStatus, string> = {
      [TransactionStatus.PENDING]: 'status-pending',
      [TransactionStatus.OTP_SENT]: 'status-pending',
      [TransactionStatus.OTP_VERIFIED]: 'status-processing',
      [TransactionStatus.PROCESSING]: 'status-processing',
      [TransactionStatus.COMPLETED]: 'status-completed',
      [TransactionStatus.FAILED]: 'status-failed',
      [TransactionStatus.CANCELLED]: 'status-failed',
      [TransactionStatus.EXPIRED]: 'status-failed'
    };

    // Check if it's a transaction status
    if (Object.values(TransactionStatus).includes(status as TransactionStatus)) {
      return transactionStatusClasses[status as TransactionStatus] || 'status-pending';
    }
    
    return accountStatusClasses[status as AccountStatus] || 'status-pending';
  }

  getStatusLabel(status: AccountStatus | TransactionStatus): string {
    // Account status labels
    const accountStatusLabels: Record<AccountStatus, string> = {
      [AccountStatus.CREATED]: 'Créé',
      [AccountStatus.ACTIVATED]: 'Activé',
      [AccountStatus.SUSPENDED]: 'Suspendu',
      [AccountStatus.DELETED]: 'Supprimé'
    };
    
    // Transaction status labels
    const transactionStatusLabels: Record<TransactionStatus, string> = {
      [TransactionStatus.PENDING]: 'En attente',
      [TransactionStatus.OTP_SENT]: 'OTP envoyé',
      [TransactionStatus.OTP_VERIFIED]: 'OTP vérifié',
      [TransactionStatus.PROCESSING]: 'En cours',
      [TransactionStatus.COMPLETED]: 'Complété',
      [TransactionStatus.FAILED]: 'Échoué',
      [TransactionStatus.CANCELLED]: 'Annulé',
      [TransactionStatus.EXPIRED]: 'Expiré'
    };

    // Check if it's a transaction status
    if (Object.values(TransactionStatus).includes(status as TransactionStatus)) {
      return transactionStatusLabels[status as TransactionStatus] || status;
    }
    
    return accountStatusLabels[status as AccountStatus] || status;
  }

  getTransactionIconClass(transaction: Transaction): string {
    if (!this.account) return '';
    return transaction.sourceAccountId === this.account.id ? 'outgoing' : 'incoming';
  }

  getAmountClass(transaction: Transaction): string {
    if (!this.account) return '';
    return transaction.sourceAccountId === this.account.id ? 'negative' : 'positive';
  }

  formatTransactionAmount(transaction: Transaction): string {
    if (!this.account) return '';
    const isOutgoing = transaction.sourceAccountId === this.account.id;
    const sign = isOutgoing ? '-' : '+';
    return `${sign} ${this.formatCurrency(transaction.amount)}`;
  }

  formatCurrency(amount: number): string {
    return new Intl.NumberFormat('fr-MA', {
      style: 'currency',
      currency: 'MAD'
    }).format(amount);
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    const now = new Date();
    const diffInMs = now.getTime() - date.getTime();
    const diffInDays = Math.floor(diffInMs / (1000 * 60 * 60 * 24));

    if (diffInDays === 0) {
      return 'Aujourd\'hui à ' + date.toLocaleTimeString('fr-FR', { hour: '2-digit', minute: '2-digit' });
    } else if (diffInDays === 1) {
      return 'Hier à ' + date.toLocaleTimeString('fr-FR', { hour: '2-digit', minute: '2-digit' });
    } else if (diffInDays < 7) {
      return `Il y a ${diffInDays} jours`;
    } else {
      return date.toLocaleDateString('fr-FR', { day: '2-digit', month: '2-digit', year: 'numeric' });
    }
  }
}