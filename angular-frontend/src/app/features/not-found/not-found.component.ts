import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-not-found',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="not-found-page">
      <div class="not-found-container">
        <div class="not-found-icon">
          <svg width="120" height="120" viewBox="0 0 24 24" fill="currentColor">
            <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"/>
          </svg>
        </div>
        
        <h1>404</h1>
        <h2>Page non trouvée</h2>
        <p>La page que vous recherchez n'existe pas ou a été déplacée.</p>
        
        <div class="actions">
          <button class="btn btn-primary" (click)="goToDashboard()">
            Retour au tableau de bord
          </button>
          <a routerLink="/auth/login" class="btn btn-outline" *ngIf="!isAuthenticated">
            Se connecter
          </a>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .not-found-page {
      min-height: 100vh;
      display: flex;
      align-items: center;
      justify-content: center;
      background: linear-gradient(135deg, #E8F5E9 0%, #F5F5F5 100%);
      padding: 2rem;
    }

    .not-found-container {
      text-align: center;
      max-width: 500px;
    }

    .not-found-icon {
      color: #00843D;
      margin-bottom: 2rem;
      opacity: 0.8;
    }

    h1 {
      font-size: 6rem;
      color: #00843D;
      margin: 0;
      line-height: 1;
    }

    h2 {
      color: #212121;
      margin: 1rem 0;
      font-size: 1.5rem;
    }

    p {
      color: #757575;
      margin-bottom: 2rem;
    }

    .actions {
      display: flex;
      gap: 1rem;
      justify-content: center;
      flex-wrap: wrap;
    }

    .btn {
      padding: 1rem 2rem;
      font-size: 1rem;
      font-weight: 600;
      border: none;
      border-radius: 12px;
      cursor: pointer;
      transition: all 0.3s ease;
      text-decoration: none;
    }

    .btn-primary {
      background: linear-gradient(135deg, #00843D, #006830);
      color: white;

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 8px 20px rgba(0, 132, 61, 0.3);
      }
    }

    .btn-outline {
      background: transparent;
      border: 2px solid #00843D;
      color: #00843D;

      &:hover {
        background: #00843D;
        color: white;
      }
    }
  `]
})
export class NotFoundComponent {
  isAuthenticated = false;

  constructor(private authService: AuthService) {
    this.isAuthenticated = this.authService.isAuthenticated();
  }

  goToDashboard(): void {
    if (this.isAuthenticated) {
      this.authService.redirectToDashboard();
    } else {
      window.location.href = '/auth/login';
    }
  }
}