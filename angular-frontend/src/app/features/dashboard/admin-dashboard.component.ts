import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { LoginResponse } from '../../core/models/auth.model';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="dashboard-page">
      <!-- Header Banner -->
      <div class="admin-banner">
        <div class="container">
          <div class="banner-content">
            <div class="banner-text">
              <span class="badge">Administration</span>
              <h1>Tableau de bord Admin</h1>
              <p>Bienvenue, {{ currentUser?.email }}</p>
            </div>
            <div class="banner-actions">
              <button class="btn btn-light" (click)="logout()">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M17 7l-1.41 1.41L18.17 11H8v2h10.17l-2.58 2.58L17 17l5-5zM4 5h8V3H4c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h8v-2H4V5z"/>
                </svg>
                Déconnexion
              </button>
            </div>
          </div>
        </div>
      </div>

      <div class="container">
        <!-- Stats Cards -->
        <div class="stats-grid">
          <div class="stat-card">
            <div class="stat-icon users">
              <svg width="32" height="32" viewBox="0 0 24 24" fill="currentColor">
                <path d="M16 11c1.66 0 2.99-1.34 2.99-3S17.66 5 16 5c-1.66 0-3 1.34-3 3s1.34 3 3 3zm-8 0c1.66 0 2.99-1.34 2.99-3S9.66 5 8 5C6.34 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5c0-2.33-4.67-3.5-7-3.5zm8 0c-.29 0-.62.02-.97.05 1.16.84 1.97 1.97 1.97 3.45V19h6v-2.5c0-2.33-4.67-3.5-7-3.5z"/>
              </svg>
            </div>
            <div class="stat-info">
              <span class="stat-value">1,234</span>
              <span class="stat-label">Utilisateurs</span>
            </div>
          </div>

          <div class="stat-card">
            <div class="stat-icon accounts">
              <svg width="32" height="32" viewBox="0 0 24 24" fill="currentColor">
                <path d="M21 18v1c0 1.1-.9 2-2 2H5c-1.11 0-2-.9-2-2V5c0-1.1.89-2 2-2h14c1.1 0 2 .9 2 2v1h-9c-1.11 0-2 .9-2 2v8c0 1.1.89 2 2 2h9zm-9-2h10V8H12v8zm4-2.5c-.83 0-1.5-.67-1.5-1.5s.67-1.5 1.5-1.5 1.5.67 1.5 1.5-.67 1.5-1.5 1.5z"/>
              </svg>
            </div>
            <div class="stat-info">
              <span class="stat-value">856</span>
              <span class="stat-label">Comptes actifs</span>
            </div>
          </div>

          <div class="stat-card">
            <div class="stat-icon transactions">
              <svg width="32" height="32" viewBox="0 0 24 24" fill="currentColor">
                <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-7 14c-1.66 0-3-1.34-3-3s1.34-3 3-3 3 1.34 3 3-1.34 3-3 3z"/>
              </svg>
            </div>
            <div class="stat-info">
              <span class="stat-value">12,456</span>
              <span class="stat-label">Transactions</span>
            </div>
          </div>

          <div class="stat-card">
            <div class="stat-icon revenue">
              <svg width="32" height="32" viewBox="0 0 24 24" fill="currentColor">
                <path d="M11.8 10.9c-2.27-.59-3-1.2-3-2.15 0-1.09 1.01-1.85 2.7-1.85 1.78 0 2.44.85 2.5 2.1h2.21c-.07-1.72-1.12-3.3-3.21-3.81V3h-3v2.16c-1.94.42-3.5 1.68-3.5 3.61 0 2.31 1.91 3.46 4.7 4.13 2.5.6 3 1.48 3 2.41 0 .69-.49 1.79-2.7 1.79-2.06 0-2.87-.92-2.98-2.1h-2.2c.12 2.19 1.76 3.42 3.68 3.83V21h3v-2.15c1.95-.37 3.5-1.5 3.5-3.55 0-2.84-2.43-3.81-4.7-4.4z"/>
              </svg>
            </div>
            <div class="stat-info">
              <span class="stat-value">2.5M DH</span>
              <span class="stat-label">Volume total</span>
            </div>
          </div>
        </div>

        <!-- Admin Actions -->
        <div class="section">
          <h3>Gestion</h3>
          <div class="actions-grid">
            <a routerLink="/admin/users" class="action-card">
              <div class="action-icon">
                <svg width="32" height="32" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/>
                </svg>
              </div>
              <span class="action-title">Utilisateurs</span>
              <span class="action-desc">Gérer les utilisateurs</span>
            </a>

            <a routerLink="/admin/accounts" class="action-card">
              <div class="action-icon">
                <svg width="32" height="32" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M21 18v1c0 1.1-.9 2-2 2H5c-1.11 0-2-.9-2-2V5c0-1.1.89-2 2-2h14c1.1 0 2 .9 2 2v1h-9c-1.11 0-2 .9-2 2v8c0 1.1.89 2 2 2h9z"/>
                </svg>
              </div>
              <span class="action-title">Comptes</span>
              <span class="action-desc">Gérer les comptes bancaires</span>
            </a>

            <a routerLink="/admin/transactions" class="action-card">
              <div class="action-icon">
                <svg width="32" height="32" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M4 6h16v2H4zm0 5h16v2H4zm0 5h16v2H4z"/>
                </svg>
              </div>
              <span class="action-title">Transactions</span>
              <span class="action-desc">Historique des opérations</span>
            </a>

            <a routerLink="/admin/settings" class="action-card">
              <div class="action-icon">
                <svg width="32" height="32" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M19.14,12.94c0.04-0.31,0.06-0.63,0.06-0.94c0-0.32-0.02-0.64-0.07-0.94l2.03-1.58c0.18-0.14,0.23-0.41,0.12-0.61 l-1.92-3.32c-0.12-0.22-0.37-0.29-0.59-0.22l-2.39,0.96c-0.5-0.38-1.03-0.7-1.62-0.94L14.4,2.81c-0.04-0.24-0.24-0.41-0.48-0.41 h-3.84c-0.24,0-0.43,0.17-0.47,0.41L9.25,5.35C8.66,5.59,8.12,5.92,7.63,6.29L5.24,5.33c-0.22-0.08-0.47,0-0.59,0.22L2.74,8.87 C2.62,9.08,2.66,9.34,2.86,9.48l2.03,1.58C4.84,11.36,4.8,11.69,4.8,12s0.02,0.64,0.07,0.94l-2.03,1.58 c-0.18,0.14-0.23,0.41-0.12,0.61l1.92,3.32c0.12,0.22,0.37,0.29,0.59,0.22l2.39-0.96c0.5,0.38,1.03,0.7,1.62,0.94l0.36,2.54 c0.05,0.24,0.24,0.41,0.48,0.41h3.84c0.24,0,0.44-0.17,0.47-0.41l0.36-2.54c0.59-0.24,1.13-0.56,1.62-0.94l2.39,0.96 c0.22,0.08,0.47,0,0.59-0.22l1.92-3.32c0.12-0.22,0.07-0.47-0.12-0.61L19.14,12.94z M12,15.6c-1.98,0-3.6-1.62-3.6-3.6 s1.62-3.6,3.6-3.6s3.6,1.62,3.6,3.6S13.98,15.6,12,15.6z"/>
                </svg>
              </div>
              <span class="action-title">Paramètres</span>
              <span class="action-desc">Configuration système</span>
            </a>
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

    .admin-banner {
      background: linear-gradient(135deg, #1a237e 0%, #283593 100%);
      padding: 3rem 0;
      color: white;
    }

    .container {
      max-width: 1200px;
      margin: 0 auto;
      padding: 0 1.5rem;
    }

    .banner-content {
      display: flex;
      justify-content: space-between;
      align-items: center;
      flex-wrap: wrap;
      gap: 1rem;
    }

    .badge {
      display: inline-block;
      background: rgba(255, 255, 255, 0.2);
      padding: 0.25rem 0.75rem;
      border-radius: 20px;
      font-size: 0.875rem;
      margin-bottom: 0.5rem;
    }

    .banner-text h1 {
      font-size: 2rem;
      margin-bottom: 0.5rem;
    }

    .banner-text p {
      opacity: 0.9;
    }

    .btn-light {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      background: white;
      color: #1a237e;
      padding: 0.75rem 1.5rem;
      border: none;
      border-radius: 8px;
      font-weight: 600;
      cursor: pointer;
      transition: all 0.3s ease;

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
      }
    }

    .stats-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
      gap: 1.5rem;
      margin: 2rem 0;
    }

    .stat-card {
      background: white;
      border-radius: 16px;
      padding: 1.5rem;
      display: flex;
      align-items: center;
      gap: 1rem;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
    }

    .stat-icon {
      width: 60px;
      height: 60px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;

      &.users { background: #E3F2FD; color: #1976D2; }
      &.accounts { background: #E8F5E9; color: #388E3C; }
      &.transactions { background: #FFF3E0; color: #F57C00; }
      &.revenue { background: #F3E5F5; color: #7B1FA2; }
    }

    .stat-info {
      display: flex;
      flex-direction: column;
    }

    .stat-value {
      font-size: 1.75rem;
      font-weight: 700;
      color: #212121;
    }

    .stat-label {
      color: #757575;
      font-size: 0.875rem;
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
      border-radius: 16px;
      padding: 1.5rem;
      text-align: center;
      text-decoration: none;
      color: inherit;
      transition: all 0.3s ease;
      border: 2px solid transparent;

      &:hover {
        border-color: #1a237e;
        transform: translateY(-4px);
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
      }
    }

    .action-icon {
      width: 64px;
      height: 64px;
      background: linear-gradient(135deg, #E8EAF6, #C5CAE9);
      border-radius: 16px;
      display: flex;
      align-items: center;
      justify-content: center;
      margin: 0 auto 1rem;
      color: #1a237e;
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

    @media (max-width: 768px) {
      .banner-content {
        flex-direction: column;
        text-align: center;
      }
    }
  `]
})
export class AdminDashboardComponent implements OnInit {
  currentUser: LoginResponse | null = null;

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.currentUser = this.authService.getCurrentUserValue();
  }

  logout(): void {
    this.authService.logout().subscribe();
  }
}