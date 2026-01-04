import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule, ActivatedRoute } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { NotificationService } from '../../../core/services/notification.service';
import { LoaderComponent } from '../../../shared/components/loader/loader.component';

@Component({
  selector: 'app-verify-email',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, LoaderComponent],
  template: `
    <div class="auth-page">
      <app-loader *ngIf="isLoading"></app-loader>
      
      <div class="verify-container">
        <div class="verify-card">
          <!-- Alert si redirection depuis login -->
          <div class="alert alert-warning" *ngIf="fromLogin">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
              <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"/>
            </svg>
            <div>
              <strong>Vérification requise</strong>
              <p>Votre email n'est pas encore vérifié. Veuillez entrer le code reçu par email.</p>
            </div>
          </div>

          <div class="verify-icon">
            <svg width="80" height="80" viewBox="0 0 24 24" fill="currentColor">
              <path d="M12 1L3 5v6c0 5.55 3.84 10.74 9 12 5.16-1.26 9-6.45 9-12V5l-9-4zm-2 16l-4-4 1.41-1.41L10 14.17l6.59-6.59L18 9l-8 8z"/>
            </svg>
          </div>

          <h2>Vérification de votre email</h2>
          <p class="subtitle">
            Nous avons envoyé un code de vérification à<br/>
            <strong>{{ email }}</strong>
          </p>

          <form [formGroup]="verifyForm" (ngSubmit)="onSubmit()">
            <div class="form-group">
              <label class="form-label">Code de vérification</label>
              <div class="code-input-group">
                <input 
                  *ngFor="let input of codeInputs; let i = index"
                  #codeInput
                  type="text" 
                  maxlength="1"
                  class="code-input"
                  [class.error]="verifyForm.get('code')?.invalid && verifyForm.get('code')?.touched"
                  (input)="onCodeInput($event, i)"
                  (keydown)="onKeyDown($event, i)"
                  [value]="getCodeDigit(i)"
                />
              </div>
              <div class="form-error" *ngIf="verifyForm.get('code')?.invalid && verifyForm.get('code')?.touched">
                <span>Le code doit contenir 6 chiffres</span>
              </div>
            </div>

            <button 
              type="submit" 
              class="btn btn-primary w-100"
              [disabled]="verifyForm.invalid || isLoading"
            >
              <span *ngIf="!isLoading">Vérifier</span>
              <span *ngIf="isLoading">Vérification...</span>
            </button>
          </form>

          <div class="resend-section">
            <p>Vous n'avez pas reçu le code ?</p>
            <button 
              class="btn-link" 
              (click)="resendCode()"
              [disabled]="isResending || countdown > 0"
            >
              <span *ngIf="countdown === 0 && !isResending">Renvoyer le code</span>
              <span *ngIf="countdown > 0">Renvoyer dans {{ countdown }}s</span>
              <span *ngIf="isResending">Envoi en cours...</span>
            </button>
          </div>

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

    .verify-container {
      max-width: 500px;
      width: 100%;
    }

    .verify-card {
      background: white;
      border-radius: 24px;
      padding: 3rem;
      box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
      text-align: center;
    }

    .alert {
      display: flex;
      align-items: flex-start;
      gap: 12px;
      padding: 1rem;
      border-radius: 12px;
      margin-bottom: 1.5rem;
      text-align: left;

      svg {
        flex-shrink: 0;
        margin-top: 2px;
      }

      strong {
        display: block;
        margin-bottom: 4px;
      }

      p {
        margin: 0;
        font-size: 0.875rem;
        color: inherit;
        opacity: 0.9;
      }
    }

    .alert-warning {
      background: #FFF3E0;
      border: 1px solid #FFB74D;
      color: #E65100;

      svg {
        color: #FF9800;
      }
    }

    .verify-icon {
      margin-bottom: 2rem;
      color: #00843D;
      
      svg {
        animation: pulse 2s infinite;
      }
    }

    @keyframes pulse {
      0%, 100% {
        transform: scale(1);
      }
      50% {
        transform: scale(1.05);
      }
    }

    h2 {
      color: #00843D;
      margin-bottom: 1rem;
    }

    .subtitle {
      color: #757575;
      margin-bottom: 2rem;
      line-height: 1.6;

      strong {
        color: #212121;
      }
    }

    .code-input-group {
      display: flex;
      gap: 12px;
      justify-content: center;
      margin-bottom: 0.5rem;
    }

    .code-input {
      width: 50px;
      height: 60px;
      font-size: 1.5rem;
      font-weight: 700;
      text-align: center;
      border: 2px solid #E0E0E0;
      border-radius: 12px;
      transition: all 0.3s ease;

      &:focus {
        outline: none;
        border-color: #00843D;
        box-shadow: 0 0 0 3px rgba(0, 132, 61, 0.1);
      }

      &.error {
        border-color: #D32F2F;
      }
    }

    .resend-section {
      margin-top: 2rem;
      padding-top: 2rem;
      border-top: 1px solid #E0E0E0;

      p {
        color: #757575;
        margin-bottom: 0.5rem;
      }
    }

    .btn-link {
      background: none;
      border: none;
      color: #00843D;
      font-weight: 600;
      cursor: pointer;
      font-size: 1rem;
      padding: 8px 16px;
      border-radius: 8px;
      transition: all 0.3s ease;

      &:hover:not(:disabled) {
        background: rgba(0, 132, 61, 0.1);
      }

      &:disabled {
        opacity: 0.6;
        cursor: not-allowed;
      }
    }

    .auth-footer {
      margin-top: 2rem;

      .link {
        color: #00843D;
        text-decoration: none;
        font-weight: 600;
        transition: color 0.3s ease;

        &:hover {
          color: #006830;
        }
      }
    }

    .w-100 {
      width: 100%;
    }

    .btn {
      padding: 1rem 2rem;
      font-size: 1rem;
      font-weight: 600;
      border: none;
      border-radius: 12px;
      cursor: pointer;
      transition: all 0.3s ease;
    }

    .btn-primary {
      background: linear-gradient(135deg, #00843D, #006830);
      color: white;

      &:hover:not(:disabled) {
        transform: translateY(-2px);
        box-shadow: 0 8px 20px rgba(0, 132, 61, 0.3);
      }

      &:disabled {
        opacity: 0.6;
        cursor: not-allowed;
      }
    }

    .form-group {
      margin-bottom: 1.5rem;
    }

    .form-label {
      display: block;
      margin-bottom: 0.5rem;
      color: #212121;
      font-weight: 600;
    }

    .form-error {
      color: #D32F2F;
      font-size: 0.875rem;
      margin-top: 0.5rem;
    }

    @media (max-width: 768px) {
      .verify-card {
        padding: 2rem;
      }

      .code-input {
        width: 45px;
        height: 55px;
        font-size: 1.25rem;
      }

      .code-input-group {
        gap: 8px;
      }
    }
  `]
})
export class VerifyEmailComponent implements OnInit, OnDestroy {
  verifyForm!: FormGroup;
  isLoading = false;
  isResending = false;
  email: string = '';
  fromLogin = false; // Indique si l'utilisateur vient de la page login
  codeInputs = Array(6).fill('');
  countdown = 0;
  countdownInterval: any;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private notificationService: NotificationService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    // Récupérer l'email depuis les query params ou localStorage
    this.email = this.route.snapshot.queryParams['email'] || 
                 this.authService.getPendingVerificationEmail() || 
                 '';
    
    // Vérifier si l'utilisateur vient de la page login (email dans localStorage)
    this.fromLogin = !!this.authService.getPendingVerificationEmail();
    
    if (!this.email) {
      this.notificationService.showError('Email non spécifié');
      this.router.navigate(['/auth/login']);
      return;
    }

    this.verifyForm = this.fb.group({
      code: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(6)]]
    });

    // Si l'utilisateur vient du login, renvoyer automatiquement un code
    if (this.fromLogin) {
      this.resendCode();
    }
  }

  getCodeDigit(index: number): string {
    const code = this.verifyForm.get('code')?.value || '';
    return code[index] || '';
  }

  onCodeInput(event: any, index: number): void {
    const input = event.target;
    const value = input.value;

    if (value.length === 1 && /^\d$/.test(value)) {
      // Mettre à jour le formulaire
      const currentCode = this.verifyForm.get('code')?.value || '';
      const codeArray = currentCode.split('');
      codeArray[index] = value;
      const newCode = codeArray.join('');
      this.verifyForm.patchValue({ code: newCode });

      // Passer à l'input suivant
      if (index < 5) {
        const nextInput = input.parentElement.children[index + 1] as HTMLInputElement;
        nextInput?.focus();
      }

      // Si tous les chiffres sont saisis, soumettre automatiquement
      if (newCode.length === 6) {
        this.onSubmit();
      }
    } else {
      input.value = '';
    }
  }

  onKeyDown(event: KeyboardEvent, index: number): void {
    const input = event.target as HTMLInputElement;

    // Gérer la touche Backspace
    if (event.key === 'Backspace') {
      event.preventDefault();
      
      const currentCode = this.verifyForm.get('code')?.value || '';
      const codeArray = currentCode.split('');
      codeArray[index] = '';
      const newCode = codeArray.join('');
      this.verifyForm.patchValue({ code: newCode });

      if (index > 0 && !input.value) {
        const prevInput = input.parentElement?.children[index - 1] as HTMLInputElement;
        prevInput?.focus();
      }
    }

    // Gérer les flèches
    if (event.key === 'ArrowLeft' && index > 0) {
      const prevInput = input.parentElement?.children[index - 1] as HTMLInputElement;
      prevInput?.focus();
    }

    if (event.key === 'ArrowRight' && index < 5) {
      const nextInput = input.parentElement?.children[index + 1] as HTMLInputElement;
      nextInput?.focus();
    }
  }

  onSubmit(): void {
    if (this.verifyForm.invalid) {
      this.verifyForm.get('code')?.markAsTouched();
      return;
    }

    this.isLoading = true;

    const verifyRequest = {
      email: this.email,
      verificationCode: this.verifyForm.value.code
    };

    this.authService.verifyEmail(verifyRequest).subscribe({
      next: (response) => {
        this.isLoading = false;
        if (response.success) {
          this.notificationService.showSuccess('Email vérifié avec succès ! Vous pouvez maintenant vous connecter.');
          this.router.navigate(['/auth/login']);
        } else {
          this.notificationService.showError(response.message || 'Code invalide');
        }
      },
      error: (error) => {
        this.isLoading = false;
        this.notificationService.showError(
          error.error?.message || 'Code invalide ou expiré'
        );
      }
    });
  }

  resendCode(): void {
    if (this.countdown > 0 || this.isResending) return;

    this.isResending = true;

    this.authService.resendVerificationCode(this.email).subscribe({
      next: (response) => {
        this.isResending = false;
        this.notificationService.showSuccess('Un nouveau code a été envoyé à votre email !');
        this.startCountdown();
      },
      error: (error) => {
        this.isResending = false;
        this.notificationService.showError(
          error.error?.message || 'Erreur lors de l\'envoi du code'
        );
      }
    });
  }

  startCountdown(): void {
    this.countdown = 60;
    this.countdownInterval = setInterval(() => {
      this.countdown--;
      if (this.countdown === 0) {
        clearInterval(this.countdownInterval);
      }
    }, 1000);
  }

  ngOnDestroy(): void {
    if (this.countdownInterval) {
      clearInterval(this.countdownInterval);
    }
  }
}