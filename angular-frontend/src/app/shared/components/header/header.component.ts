import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { LoginResponse } from '../../../core/models/auth.model';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <header class="header">
      <div class="container">
        <div class="header-content">
          <!-- Logo -->
          <div class="logo" routerLink="/">
            <img src="assets/images/logo-ettijari.png" alt="Ettijari Bank" />
            <span class="logo-text">Ettijari Bank</span>
          </div>

          <!-- Navigation -->
          <nav class="nav" *ngIf="currentUser">
            <a routerLink="/dashboard" routerLinkActive="active">Tableau de bord</a>
            <a routerLink="/profile" routerLinkActive="active">Mon profil</a>
            <a routerLink="/transactions" routerLinkActive="active">Transactions</a>
          </nav>

          <!-- User Menu -->
          <div class="user-menu">
            <div *ngIf="currentUser; else loginBtn" class="user-info">
              <div class="user-avatar">
                {{ getInitials() }}
              </div>
              <div class="user-details">
                <span class="user-name">{{ currentUser.email }}</span>
                <button class="btn-logout" (click)="logout()">
                  <svg width="16" height="16" viewBox="0 0 16 16" fill="currentColor">
                    <path d="M10 3v2h4v10H2V5h4V3H0v14h16V3z"/>
                    <path d="M8 0L6.586 1.414 9.172 4H0v2h9.172l-2.586 2.586L8 10l5-5z"/>
                  </svg>
                  Déconnexion
                </button>
              </div>
            </div>
            <ng-template #loginBtn>
              <a routerLink="/auth/login" class="btn btn-primary">Se connecter</a>
            </ng-template>
          </div>
        </div>
      </div>
    </header>
  `,
  styles: [`
    .header {
      background: linear-gradient(135deg, #00843D 0%, #006830 100%);
      color: white;
      box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
      position: sticky;
      top: 0;
      z-index: 1000;
    }

    .header-content {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 1rem 0;
      gap: 2rem;
    }

    .logo {
      display: flex;
      align-items: center;
      gap: 12px;
      cursor: pointer;
      transition: transform 0.3s ease;

      &:hover {
        transform: scale(1.05);
      }

      img {
        height: 40px;
        width: auto;
      }

      .logo-text {
        font-size: 1.5rem;
        font-weight:  700;
        color: white;
      }
    }

    .nav {
      display: flex;
      gap: 2rem;

      a {
        color: rgba(255, 255, 255, 0.9);
        text-decoration: none;
        font-weight: 500;
        padding: 0.5rem 1rem;
        border-radius:  8px;
        transition: all 0.3s ease;

        &:hover, &.active {
          background: rgba(255, 255, 255, 0.2);
          color: white;
        }
      }
    }

    .user-menu {
      display: flex;
      align-items: center;
    }

    .user-info {
      display: flex;
      align-items: center;
      gap: 12px;
    }

    .user-avatar {
      width: 40px;
      height: 40px;
      border-radius: 50%;
      background: white;
      color: #00843D;
      display: flex;
      align-items: center;
      justify-content: center;
      font-weight: 700;
      font-size: 1rem;
    }

    .user-details {
      display: flex;
      flex-direction: column;
      gap: 4px;
    }

    .user-name {
      font-weight: 600;
      font-size: 0.9rem;
    }

    .btn-logout {
      background: none;
      border:  none;
      color: rgba(255, 255, 255, 0.8);
      cursor: pointer;
      font-size: 0.85rem;
      display: flex;
      align-items: center;
      gap: 6px;
      padding: 4px 8px;
      border-radius: 4px;
      transition: all 0.3s ease;

      &:hover {
        background: rgba(255, 255, 255, 0.1);
        color: white;
      }
    }

    @media (max-width: 768px) {
      .nav {
        display: none;
      }

      .logo-text {
        display: none;
      }
    }
  `]
})
export class HeaderComponent implements OnInit {
  currentUser:  LoginResponse | null = null;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
    });
  }

  getInitials(): string {
    if (this.currentUser?.email) {
      return this.currentUser.email.charAt(0).toUpperCase();
    }
    return 'U';
  }

  logout(): void {
    this.authService.logout().subscribe({
      next: () => {
        this.router.navigate(['/auth/login']);
      }
    });
  }
}
