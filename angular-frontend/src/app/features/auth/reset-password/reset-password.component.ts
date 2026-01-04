import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule, ActivatedRoute } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { NotificationService } from '../../../core/services/notification.service';
import { LoaderComponent } from '../../../shared/components/loader/loader.component';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, LoaderComponent],
  template: `
    <div class="auth-page">
      <app-loader *ngIf="isLoading"></app-loader>
      
      <div class="reset-container">
        <div class="reset-card">
          <div class="reset-icon">
            <svg width="80" height="80" viewBox="0 0 24 24" fill="currentColor">
              <path d="M9 11h6v2H9v-2zm0 4h6v2H9v-2zm5-14H6c-1.1 0-2 .9-2 2v16c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V7l-6-6zm4 18H6V3h7v5h5v11z"/>
            </svg>
          </div>

          <h2>Réinitialiser le mot de passe</h2>
          <p class="subtitle">
            Entrez le code reçu par email et votre nouveau mot de passe
          </p>

          <form [formGroup]="resetForm" (ngSubmit)="onSubmit()">
            <!-- Code -->
            <div class="form-group">
              <label class="form-label">Code de vérification</label>
              <input 
                type="text" 
                class="form-control"
                [class.error]="resetForm.get('code')?.invalid && resetForm.get('code')?.touched"
                formControlName="code"
                placeholder="123456"
                maxlength="6"
              />
              <div class="form-error" *ngIf="resetForm.get('code')?.invalid && resetForm.get('code')?.touched">
                <span *ngIf="resetForm.get('code')?.errors?.['required']">Le code est requis</span>
                <span *ngIf="resetForm.get('code')?.errors?.['minlength']">Le code doit contenir 6 chiffres</span>
              </div>
            </div>

            <!-- New Password -->
            <div class="form-group">
              <label class="form-label">Nouveau mot de passe</label>
              <div class="password-input">
                <input 
                  [type]="showPassword ? 'text' : 'password'"
                  class="form-control"
                  [class.error]="resetForm.get('newPassword')?.invalid && resetForm.get('newPassword')?.touched"
                  formControlName="newPassword"
                  placeholder="••••••••"
                />
                <button type="button" class="password-toggle" (click)="togglePassword()">
                  <svg *ngIf="! showPassword" width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z"/>
                  </svg>
                  <svg *ngIf="showPassword" width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M12 7c2.76 0 5 2.24 5 5 0 .65-.13 1.26-.36 1.83l2.92 2.92c1.51-1.26 2.7-2.89 3.43-4.75-1.73-4.39-6-7.5-11-7.5-1.4 0-2.74.25-3.98.7l2.16 2.16C10.74 7.13 11.35 7 12 7zM2 4.27l2.28 2.28.46.46C3.08 8.3 1.78 10.02 1 12c1.73 4.39 6 7.5 11 7.5 1.55 0 3.03-.3 4.38-.84l.42.42L19.73 22 21 20.73 3.27 3 2 4.27zM7.53 9.8l1.55 1.55c-.05.21-.08.43-.08.65 0 1.66 1.34 3 3 3 .22 0 .44-.03.65-.08l1.55 1.55c-.67.33-1.41.53-2.2.53-2.76 0-5-2.24-5-5 0-.79.2-1.53.53-2.2zm4.31-.78l3.15 3.15.02-.16c0-1.66-1.34-3-3-3l-.17.01z"/>
                  </svg>
                </button>
              </div>
              <div class="password-strength">
                <div class="strength-bar" [class]="getPasswordStrength()"></div>
              </div>
              <div class="form-error" *ngIf="resetForm.get('newPassword')?.invalid && resetForm.get('newPassword')?.touched">
                <span *ngIf="resetForm.get('newPassword')?.errors?.['required']">Le mot de passe est requis</span>
                <span *ngIf="resetForm.get('newPassword')?.errors?.['minlength']">Minimum 8 caractères</span>
              </div>
            </div>

            <!-- Confirm Password -->
            <div class="form-group">
              <label class="form-label">Confirmer le mot de passe</label>
              <input 
                [type]="showConfirmPassword ?  'text' : 'password'"
                class="form-control"
                [class.error]="resetForm.get('confirmPassword')?.invalid && resetForm.get('confirmPassword')?.touched"
                formControlName="confirmPassword"
                placeholder="••••••••"
              />
              <div class="form-error" *ngIf="resetForm.errors?.['passwordMismatch'] && resetForm.get('confirmPassword')?.touched">
                <span>Les mots de passe ne correspondent pas</span>
              </div>
            </div>

            <button 
              type="submit" 
              class="btn btn-primary w-100"
              [disabled]="resetForm.invalid || isLoading"
            >
              <span *ngIf="!isLoading">Réinitialiser</span>
              <span *ngIf="isLoading">Réinitialisation... </span>
            </button>
          </form>

          <div class="auth-footer">
            <a routerLink="/auth/login" class="link">← Retour à la connexion</a>
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

    .reset-container {
      max-width: 500px;
      width: 100%;
    }

    .reset-card {
      background: white;
      border-radius: 24px;
      padding: 3rem;
      box-shadow:  0 20px 60px rgba(0, 0, 0, 0.15);
      text-align: center;
    }

    .reset-icon {
      margin-bottom: 2rem;
      color: #00843D;
    }

    h2 {
      color: #00843D;
      margin-bottom: 1rem;
    }

    .subtitle {
      color: #757575;
      margin-bottom: 2rem;
      line-height:  1.6;
    }

    .form-group {
      text-align: left;
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

    .password-strength {
      height: 4px;
      background: #E0E0E0;
      border-radius: 2px;
      margin-top: 8px;
      overflow: hidden;
    }

    .strength-bar {
      height: 100%;
      transition: all 0.3s ease;

      &.weak {
        width: 33%;
        background: #D32F2F;
      }

      &.medium {
        width: 66%;
        background: #F57C00;
      }

      &.strong {
        width: 100%;
        background: #388E3C;
      }
    }

    .auth-footer {
      margin-top: 2rem;

      .link {
        color: #00843D;
        text-decoration: none;
        font-weight: 600;
        transition:  color 0.3s ease;

        &:hover {
          color: #006830;
        }
      }
    }

    @media (max-width:  768px) {
      .reset-card {
        padding:  2rem;
      }
    }
  `]
})
export class ResetPasswordComponent implements OnInit {
  resetForm! : FormGroup;
  isLoading = false;
  showPassword = false;
  showConfirmPassword = false;
  email: string = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private notificationService:  NotificationService,
    private router: Router,
    private route:  ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.email = this.route.snapshot.queryParams['email'] || '';
    
    if (! this.email) {
      this.router.navigate(['/auth/forgot-password']);
      return;
    }

    this.resetForm = this.fb.group({
      code: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(6)]],
      newPassword: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', [Validators.required]]
    }, {
      validators: this.passwordMatchValidator
    });
  }

  passwordMatchValidator(formGroup: FormGroup) {
    const password = formGroup.get('newPassword');
    const confirmPassword = formGroup.get('confirmPassword');

    if (password && confirmPassword && password.value !== confirmPassword.value) {
      return { passwordMismatch: true };
    }

    return null;
  }

  togglePassword(): void {
    this.showPassword = !this.showPassword;
  }

  getPasswordStrength(): string {
    const password = this.resetForm.get('newPassword')?.value || '';
    
    if (password.length === 0) return '';
    if (password.length < 8) return 'weak';
    
    let strength = 0;
    if (/[a-z]/.test(password)) strength++;
    if (/[A-Z]/.test(password)) strength++;
    if (/[0-9]/.test(password)) strength++;
    if (/[^a-zA-Z0-9]/.test(password)) strength++;

    if (strength <= 2) return 'weak';
    if (strength === 3) return 'medium';
    return 'strong';
  }

  onSubmit(): void {
    if (this.resetForm.invalid) {
      Object.keys(this.resetForm.controls).forEach(key => {
        this.resetForm.get(key)?.markAsTouched();
      });
      return;
    }

    this.isLoading = true;

    const resetRequest = {
      email: this.email,
      code: this.resetForm.value.code,
      newPassword: this.resetForm.value.newPassword
    };

    this.authService.resetPassword(resetRequest).subscribe({
      next: (response) => {
        this.isLoading = false;
        if (response.success) {
          this.notificationService.showSuccess('Mot de passe réinitialisé avec succès ! ');
          this.router.navigate(['/auth/login']);
        }
      },
      error:  (error) => {
        this.isLoading = false;
        this.notificationService.showError(
          error.error?.message || 'Erreur lors de la réinitialisation'
        );
      }
    });
  }
}
