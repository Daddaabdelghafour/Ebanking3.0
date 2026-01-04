import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { LoginResponse } from '../../core/models/auth.model';

@Component({
  selector: 'app-agent-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="dashboard-page">
      <!-- Header Banner -->
      <div class="agent-banner">
        <div class="container">
          <div class="banner-content">
            <div class="banner-text">
              <span class="badge">Agent Bancaire</span>
              <h1>Espace Agent</h1>
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
            <div class="stat-icon pending">
              <svg width="32" height="32" viewBox="0 0 24 24" fill="currentColor">
                <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
              </svg>
            </div>
            <div class="stat-info">
              <span class="stat-value">24</span>
              <span class="stat-label">Demandes en attente</span>
            </div>
          </div>

          <div class="stat-card">
            <div class="stat-icon clients">
              <svg width="32" height="32" viewBox="0 0 24 24" fill="currentColor">
                <path d="M16 11c1.66 0 2.99-1.34 2.99-3S17.66 5 16 5c-1.66 0-3 1.34-3 3s1.34 3 3 3zm-8 0c1.66 0 2.99-1.34 2.99-3S9.66 5 8 5C6.34 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5c0-2.33-4.67-3.5-7-3.5z"/>
              </svg>
            </div>
            <div class="stat-info">
              <span class="stat-value">156</span>
              <span class="stat-label">Clients gérés</span>
            </div>
          </div>

          <div class="stat-card">
            <div class="stat-icon today">
              <svg width="32" height="32" viewBox="0 0 24 24" fill="currentColor">
                <path d="M19 3h-1V1h-2v2H8V1H6v2H5c-1.11 0-1.99.9-1.99 2L3 19c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm0 16H5V8h14v11zM9 10H7v2h2v-2zm4 0h-2v2h2v-2zm4 0h-2v2h2v-2z"/>
              </svg>
            </div>
            <div class="stat-info">
              <span class="stat-value">12</span>
              <span class="stat-label">Opérations aujourd'hui</span>
            </div>
          </div>

          <div class="stat-card">
            <div class="stat-icon approved">
              <svg width="32" height="32" viewBox="0 0 24 24" fill="currentColor">
                <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
              </svg>
            </div>
            <div class="stat-info">
              <span class="stat-value">89</span>
              <span class="stat-label">Validées ce mois</span>
            </div>
          </div>
        </div>

        <!-- Agent Actions -->
        <div class="section">
          <h3>Actions rapides</h3>
          <div class="actions-grid">
            <a routerLink="/agent/requests" class="action-card">
              <div class="action-icon">
                <svg width="32" height="32" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-5 14H7v-2h7v2zm3-4H7v-2h10v2zm0-4H7V7h10v2z"/>
                </svg>
              </div>
              <span class="action-title">Demandes</span>
              <span class="action-desc">Traiter les demandes clients</span>
            </a>

            <a routerLink="/agent/new-account" class="action-card">
              <div class="action-icon">
                <svg width="32" height="32" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"/>
                </svg>
              </div>
              <span class="action-title">Nouveau compte</span>
              <span class="action-desc">Ouvrir un compte client</span>
            </a>

            <a routerLink="/agent/clients" class="action-card">
              <div class="action-icon">
                <svg width="32" height="32" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/>
                </svg>
              </div>
              <span class="action-title">Clients</span>
              <span class="action-desc">Consulter les dossiers</span>
            </a>

            <a routerLink="/agent/operations" class="action-card">
              <div class="action-icon">
                <svg width="32" height="32" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-7 14c-1.66 0-3-1.34-3-3s1.34-3 3-3 3 1.34 3 3-1.34 3-3 3z"/>
                </svg>
              </div>
              <span class="action-title">Opérations</span>
              <span class="action-desc">Effectuer des opérations</span>
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

    .agent-banner {
      background: linear-gradient(135deg, #00695c 0%, #00897b 100%);
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
      color: #00695c;
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

      &.pending { background: #FFF3E0; color: #F57C00; }
      &.clients { background: #E3F2FD; color: #1976D2; }
      &.today { background: #F3E5F5; color: #7B1FA2; }
      &.approved { background: #E8F5E9; color: #388E3C; }
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
        border-color: #00695c;
        transform: translateY(-4px);
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
      }
    }

    .action-icon {
      width: 64px;
      height: 64px;
      background: linear-gradient(135deg, #E0F2F1, #B2DFDB);
      border-radius: 16px;
      display: flex;
      align-items: center;
      justify-content: center;
      margin: 0 auto 1rem;
      color: #00695c;
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
export class AgentDashboardComponent implements OnInit {
  currentUser: LoginResponse | null = null;

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.currentUser = this.authService.getCurrentUserValue();
  }

  logout(): void {
    this.authService.logout().subscribe();
  }
}