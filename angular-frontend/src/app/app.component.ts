import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs';
import { HeaderComponent } from './shared/components/header/header.component';
import { FooterComponent } from './shared/components/footer/footer.component';
import { ToastComponent } from './shared/components/toast/toast.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    RouterOutlet,
    HeaderComponent,
    FooterComponent,
    ToastComponent
  ],
  template: `
    <div class="app-container">
      <!-- Header (sauf sur les pages auth) -->
      <app-header *ngIf="!isAuthRoute"></app-header>

      <!-- Main Content -->
      <main class="main-content" [class.full-height]="isAuthRoute">
        <router-outlet></router-outlet>
      </main>

      <!-- Footer (sauf sur les pages auth) -->
      <app-footer *ngIf="!isAuthRoute"></app-footer>

      <!-- Toast Notifications -->
      <app-toast></app-toast>
    </div>
  `,
  styles: [`
    .app-container {
      display: flex;
      flex-direction: column;
      min-height: 100vh;
    }

    .main-content {
      flex: 1;
      
      &.full-height {
        min-height: 100vh;
      }
    }
  `]
})
export class AppComponent {
  isAuthRoute = false;

  constructor(private router: Router) {
    // Détecter si on est sur une route d'authentification
    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe((event: any) => {
        this.isAuthRoute = event.url.startsWith('/auth');
      });
  }
}
