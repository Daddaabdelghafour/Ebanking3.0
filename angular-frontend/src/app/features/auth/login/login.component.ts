import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule, ActivatedRoute } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { NotificationService } from '../../../core/services/notification.service';
import { LoaderComponent } from '../../../shared/components/loader/loader.component';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, LoaderComponent],
  template: `
    <div class="auth-page">
      <app-loader *ngIf="isLoading"></app-loader>
      
      <div class="auth-container">
        <!-- Left Side - Branding -->
        <div class="auth-branding">
          <div class="branding-content">
            <img src="assets/images/logo-ettijari.png" alt="Ettijari Bank" class="brand-logo" />
            <h1>Bienvenue chez<br/>Ettijari Bank</h1>
            <p>Votre banque digitale de confiance</p>
            <div class="features">
              <div class="feature">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
                </svg>
                <span>Sécurité maximale</span>
              </div>
              <div class="feature">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
                </svg>
                <span>Disponible 24/7</span>
              </div>
              <div class="feature">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
                </svg>
                <span>Service client réactif</span>
              </div>
            </div>
          </div>
        </div>

        <!-- Right Side - Login Form -->
        <div class="auth-form-container">
          <div class="auth-form">
            <h2>Connexion</h2>
            <p class="subtitle">Accédez à votre espace personnel</p>

            <form [formGroup]="loginForm" (ngSubmit)="onSubmit()">
              <!-- Email -->
              <div class="form-group">
                <label class="form-label">Email</label>
                <input 
                  type="email" 
                  class="form-control"
                  [class.error]="loginForm.get('email')?.invalid && loginForm.get('email')?.touched"
                  formControlName="email"
                  placeholder="exemple@ettijaribank.ma"
                />
                <div class="form-error" *ngIf="loginForm.get('email')?.invalid && loginForm.get('email')?.touched">
                  <span *ngIf="loginForm.get('email')?.errors?.['required']">L'email est requis</span>
                  <span *ngIf="loginForm.get('email')?.errors?.['email']">Email invalide</span>
                </div>
              </div>

              <!-- Password -->
              <div class="form-group">
                <label class="form-label">Mot de passe</label>
                <div class="password-input">
                  <input 
                    [type]="showPassword ? 'text' : 'password'"
                    class="form-control"
                    [class.error]="loginForm.get('password')?.invalid && loginForm.get('password')?.touched"
                    formControlName="password"
                    placeholder="••••••••"
                  />
                  <button type="button" class="password-toggle" (click)="togglePassword()">
                    <svg *ngIf="!showPassword" width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                      <path d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z"/>
                    </svg>
                    <svg *ngIf="showPassword" width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                      <path d="M12 7c2.76 0 5 2.24 5 5 0 .65-.13 1.26-.36 1.83l2.92 2.92c1.51-1.26 2.7-2.89 3.43-4.75-1.73-4.39-6-7.5-11-7.5-1.4 0-2.74.25-3.98.7l2.16 2.16C10.74 7.13 11.35 7 12 7zM2 4.27l2.28 2.28.46.46C3.08 8.3 1.78 10.02 1 12c1.73 4.39 6 7.5 11 7.5 1.55 0 3.03-.3 4.38-.84l.42.42L19.73 22 21 20.73 3.27 3 2 4.27zM7.53 9.8l1.55 1.55c-.05.21-.08.43-.08.65 0 1.66 1.34 3 3 3 .22 0 .44-.03.65-.08l1.55 1.55c-.67.33-1.41.53-2.2.53-2.76 0-5-2.24-5-5 0-.79.2-1.53.53-2.2zm4.31-.78l3.15 3.15.02-.16c0-1.66-1.34-3-3-3l-.17.01z"/>
                    </svg>
                  </button>
                </div>
                <div class="form-error" *ngIf="loginForm.get('password')?.invalid && loginForm.get('password')?.touched">
                  <span *ngIf="loginForm.get('password')?.errors?.['required']">Le mot de passe est requis</span>
                </div>
              </div>

              <!-- Remember Me & Forgot Password -->
              <div class="form-options">
                <label class="checkbox-label">
                  <input type="checkbox" formControlName="rememberMe" />
                  <span>Se souvenir de moi</span>
                </label>
                <a routerLink="/auth/forgot-password" class="link">Mot de passe oublié ?</a>
              </div>

              <!-- Submit Button -->
              <button 
                type="submit" 
                class="btn btn-primary w-100"
                [disabled]="loginForm.invalid || isLoading"
              >
                <span *ngIf="!isLoading">Se connecter</span>
                <span *ngIf="isLoading">Connexion...</span>
              </button>
            </form>

            <!-- Register Link -->
            <div class="auth-footer">
              <p>Pas encore de compte ? <a routerLink="/auth/register" class="link">S'inscrire</a></p>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .auth-page {
      min-height: 100vh;
      display: flex;
      align-items: center;
      justify-content: center;
      background: linear-gradient(135deg, #E8F5E9 0%, #F5F5F5 100%);
      padding: 2rem;
    }

    .auth-container {
      display: grid;
      grid-template-columns: 1fr 1fr;
      max-width: 1200px;
      width: 100%;
      background: white;
      border-radius: 24px;
      overflow: hidden;
      box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
    }

    .auth-branding {
      background: linear-gradient(135deg, #00843D 0%, #006830 100%);
      color: white;
      padding: 4rem;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .branding-content {
      max-width: 400px;
    }

    .brand-logo {
      height: 60px;
      margin-bottom: 2rem;
      filter: brightness(0) invert(1);
    }

    h1 {
      font-size: 2.5rem;
      font-weight: 700;
      margin-bottom: 1rem;
      line-height: 1.2;
    }

    .subtitle {
      font-size: 1.1rem;
      opacity: 0.9;
      margin-bottom: 2rem;
    }

    .features {
      display: flex;
      flex-direction: column;
      gap: 1.5rem;
    }

    .feature {
      display: flex;
      align-items: center;
      gap: 12px;
      font-size: 1rem;

      svg {
        flex-shrink: 0;
      }
    }

    .auth-form-container {
      padding: 4rem;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .auth-form {
      width: 100%;
      max-width: 400px;
    }

    h2 {
      color: #00843D;
      margin-bottom: 0.5rem;
    }

    .form-group {
      margin-bottom: 1rem;
    }

    .form-label {
      display: block;
      margin-bottom: 0.5rem;
      color: #212121;
      font-weight: 500;
      font-size: 0.9rem;
    }

    .form-control {
      width: 100%;
      padding: 12px 16px;
      border: 2px solid #E0E0E0;
      border-radius: 8px;
      font-size: 1rem;
      transition: all 0.3s ease;
      box-sizing: border-box;

      &:focus {
        outline: none;
        border-color: #00843D;
        box-shadow: 0 0 0 3px rgba(0, 132, 61, 0.1);
      }

      &.error {
        border-color: #D32F2F;
      }
    }

    .form-error {
      color: #D32F2F;
      font-size: 0.8rem;
      margin-top: 4px;
    }

    .password-input {
      position: relative;
    }

    .password-toggle {
      position: absolute;
      right: 12px;
      top: 50%;
      transform: translateY(-50%);
      background: none;
      border: none;
      cursor: pointer;
      color: #757575;
      padding: 4px;
      display: flex;
      align-items: center;
      justify-content: center;
      transition: color 0.3s ease;

      &:hover {
        color: #00843D;
      }
    }

    .form-options {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 1.5rem;
    }

    .checkbox-label {
      display: flex;
      align-items: center;
      gap: 8px;
      cursor: pointer;
      font-size: 0.9rem;

      input[type="checkbox"] {
        cursor: pointer;
      }
    }

    .link {
      color: #00843D;
      text-decoration: none;
      font-weight: 600;
      font-size: 0.9rem;
      transition: color 0.3s ease;

      &:hover {
        color: #006830;
        text-decoration: underline;
      }
    }

    .btn {
      width: 100%;
      padding: 14px 24px;
      border-radius: 8px;
      font-size: 1rem;
      font-weight: 600;
      cursor: pointer;
      transition: all 0.3s ease;
    }

    .btn-primary {
      background: #00843D;
      color: white;
      border: none;

      &:hover:not(:disabled) {
        background: #006830;
      }

      &:disabled {
        background: #A5D6A7;
        cursor: not-allowed;
      }
    }

    .auth-footer {
      margin-top: 2rem;
      text-align: center;

      p {
        color: #757575;
        font-size: 0.95rem;
      }
    }

    @media (max-width: 968px) {
      .auth-container {
        grid-template-columns: 1fr;
      }

      .auth-branding {
        display: none;
      }

      .auth-form-container {
        padding: 2rem;
      }
    }
  `]
})
export class LoginComponent implements OnInit {
  loginForm!: FormGroup;
  isLoading = false;
  showPassword = false;
  returnUrl: string = '/dashboard';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private notificationService: NotificationService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    // Récupérer l'URL de retour
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/dashboard';

    // Initialiser le formulaire
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required]],
      rememberMe: [false]
    });
  }

  togglePassword(): void {
    this.showPassword = !this.showPassword;
  }


  onSubmit(): void {
    if (this.loginForm.invalid) {
      Object.keys(this.loginForm.controls).forEach(key => {
        this.loginForm.get(key)?.markAsTouched();
      });
      return;
    }

    this.isLoading = true;

    const loginRequest = {
      email: this.loginForm.value.email,
      password: this.loginForm.value.password
    };

    this.authService.login(loginRequest).subscribe({
      next: (response) => {
        this.isLoading = false;
        this.notificationService.showSuccess('Connexion réussie !');
        
        // Rediriger vers le dashboard approprié selon le rôle
        // Si returnUrl existe et n'est pas le dashboard générique, l'utiliser
        if (this.returnUrl && this.returnUrl !== '/dashboard' && this.returnUrl !== '/') {
          this.router.navigate([this.returnUrl]);
        } else {
          // Sinon, rediriger vers le dashboard selon le rôle
          this.authService.redirectToDashboard();
        }
      },
      error: (error) => {
        this.isLoading = false;
        
        // L'erreur EMAIL_NOT_VERIFIED est gérée par AuthService (redirection automatique)
        // Ici on gère uniquement les autres erreurs (credentials invalides)
        const errorCode = error.error?.errorCode || '';
        
        if (errorCode !== 'EMAIL_NOT_VERIFIED') {
          // C'est une erreur de credentials (email ou mot de passe invalide)
          this.notificationService.showError('Email ou mot de passe incorrect');
        }
        // Si c'est EMAIL_NOT_VERIFIED, AuthService a déjà redirigé vers verify-email
      }
    });
  }
}