import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { NotificationService } from '../../../core/services/notification.service';
import { LoaderComponent } from '../../../shared/components/loader/loader.component';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, LoaderComponent],
  template: `
    <div class="auth-page">
      <app-loader *ngIf="isLoading"></app-loader>
      
      <div class="forgot-container">
        <div class="forgot-card">
          <div class="forgot-icon">
            <svg width="80" height="80" viewBox="0 0 24 24" fill="currentColor">
              <path d="M18 8h-1V6c0-2.76-2.24-5-5-5S7 3.24 7 6v2H6c-1.1 0-2 .9-2 2v10c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V10c0-1.1-.9-2-2-2zm-6 9c-1.1 0-2-.9-2-2s.9-2 2-2 2 .9 2 2-.9 2-2 2zm3.1-9H8.9V6c0-1.71 1.39-3.1 3.1-3.1 1.71 0 3.1 1.39 3.1 3.1v2z"/>
            </svg>
          </div>

          <h2>Mot de passe oublié ? </h2>
          <p class="subtitle">
            Entrez votre adresse email et nous vous enverrons un code pour réinitialiser votre mot de passe.
          </p>

          <form [formGroup]="forgotForm" (ngSubmit)="onSubmit()">
            <div class="form-group">
              <label class="form-label">Email</label>
              <input 
                type="email" 
                class="form-control"
                [class.error]="forgotForm.get('email')?.invalid && forgotForm.get('email')?.touched"
                formControlName="email"
                placeholder="exemple@ettijaribank.ma"
              />
              <div class="form-error" *ngIf="forgotForm.get('email')?.invalid && forgotForm.get('email')?.touched">
                <span *ngIf="forgotForm.get('email')?.errors?.['required']">L'email est requis</span>
                <span *ngIf="forgotForm.get('email')?.errors?.['email']">Email invalide</span>
              </div>
            </div>

            <button 
              type="submit" 
              class="btn btn-primary w-100"
              [disabled]="forgotForm.invalid || isLoading"
            >
              <span *ngIf="!isLoading">Envoyer le code</span>
              <span *ngIf="isLoading">Envoi en cours...</span>
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

    .forgot-container {
      max-width: 500px;
      width: 100%;
    }

    .forgot-card {
      background: white;
      border-radius: 24px;
      padding: 3rem;
      box-shadow:  0 20px 60px rgba(0, 0, 0, 0.15);
      text-align: center;
    }

    .forgot-icon {
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
      line-height: 1.6;
    }

    .form-group {
      text-align: left;
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
      .forgot-card {
        padding:  2rem;
      }
    }
  `]
})
export class ForgotPasswordComponent implements OnInit {
  forgotForm!: FormGroup;
  isLoading = false;

  constructor(
    private fb:  FormBuilder,
    private authService: AuthService,
    private notificationService: NotificationService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.forgotForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]]
    });
  }

  onSubmit(): void {
    if (this.forgotForm.invalid) {
      this.forgotForm.get('email')?.markAsTouched();
      return;
    }

    this.isLoading = true;

    const request = {
      email: this.forgotForm.value.email
    };

    this.authService.forgotPassword(request).subscribe({
      next: (response) => {
        this.isLoading = false;
        if (response.success) {
          this.notificationService.showSuccess(
            'Un code de réinitialisation a été envoyé à votre email'
          );
          this.router.navigate(['/auth/reset-password'], {
            queryParams: { email:  request.email }
          });
        }
      },
      error: (error) => {
        this.isLoading = false;
        this.notificationService.showError(
          error.error?.message || 'Erreur lors de l\'envoi du code'
        );
      }
    });
  }
}
