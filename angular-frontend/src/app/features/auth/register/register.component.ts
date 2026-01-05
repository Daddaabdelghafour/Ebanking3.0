import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, AbstractControl, ValidationErrors } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { NotificationService } from '../../../core/services/notification.service';
import { LoaderComponent } from '../../../shared/components/loader/loader.component';

@Component({
  selector: 'app-register',
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
            <h1>Rejoignez<br/>Ettijari Bank</h1>
            <p>Créez votre compte en quelques minutes</p>
            <div class="features">
              <div class="feature">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
                </svg>
                <span>Inscription rapide</span>
              </div>
              <div class="feature">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
                </svg>
                <span>Données sécurisées</span>
              </div>
              <div class="feature">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
                </svg>
                <span>Activation immédiate</span>
              </div>
            </div>
          </div>
        </div>

        <!-- Right Side - Register Form -->
        <div class="auth-form-container">
          <div class="auth-form">
            <h2>Inscription</h2>
            <p class="subtitle">Créez votre compte Ettijari Bank</p>

            <!-- Steps indicator -->
            <div class="steps-indicator">
              <div class="step" [class.active]="currentStep === 1" [class.completed]="currentStep > 1">
                <span class="step-number">1</span>
                <span class="step-label">Identité</span>
              </div>
              <div class="step-line" [class.completed]="currentStep > 1"></div>
              <div class="step" [class.active]="currentStep === 2" [class.completed]="currentStep > 2">
                <span class="step-number">2</span>
                <span class="step-label">Contact</span>
              </div>
              <div class="step-line" [class.completed]="currentStep > 2"></div>
              <div class="step" [class.active]="currentStep === 3">
                <span class="step-number">3</span>
                <span class="step-label">Sécurité</span>
              </div>
            </div>

            <form [formGroup]="registerForm" (ngSubmit)="onSubmit()">
              
              <!-- Step 1: Identité -->
              <div class="form-step" *ngIf="currentStep === 1">
                <div class="form-row">
                  <!-- First Name -->
                  <div class="form-group">
                    <label class="form-label">Prénom *</label>
                    <input 
                      type="text" 
                      class="form-control"
                      [class.error]="registerForm.get('firstName')?.invalid && registerForm.get('firstName')?.touched"
                      formControlName="firstName"
                      placeholder="Votre prénom"
                    />
                    <div class="form-error" *ngIf="registerForm.get('firstName')?.invalid && registerForm.get('firstName')?.touched">
                      <span *ngIf="registerForm.get('firstName')?.errors?.['required']">Le prénom est requis</span>
                    </div>
                  </div>

                  <!-- Last Name -->
                  <div class="form-group">
                    <label class="form-label">Nom *</label>
                    <input 
                      type="text" 
                      class="form-control"
                      [class.error]="registerForm.get('lastName')?.invalid && registerForm.get('lastName')?.touched"
                      formControlName="lastName"
                      placeholder="Votre nom"
                    />
                    <div class="form-error" *ngIf="registerForm.get('lastName')?.invalid && registerForm.get('lastName')?.touched">
                      <span *ngIf="registerForm.get('lastName')?.errors?.['required']">Le nom est requis</span>
                    </div>
                  </div>
                </div>

                <!-- Username -->
                <div class="form-group">
                  <label class="form-label">Nom d'utilisateur *</label>
                  <input 
                    type="text" 
                    class="form-control"
                    [class.error]="registerForm.get('username')?.invalid && registerForm.get('username')?.touched"
                    formControlName="username"
                    placeholder="Votre nom d'utilisateur"
                  />
                  <div class="form-error" *ngIf="registerForm.get('username')?.invalid && registerForm.get('username')?.touched">
                    <span *ngIf="registerForm.get('username')?.errors?.['required']">Le nom d'utilisateur est requis</span>
                    <span *ngIf="registerForm.get('username')?.errors?.['minlength']">Minimum 3 caractères</span>
                    <span *ngIf="registerForm.get('username')?.errors?.['maxlength']">Maximum 50 caractères</span>
                  </div>
                </div>

                <!-- CIN or Passport -->
                <div class="form-group">
                  <label class="form-label">CIN ou Passeport *</label>
                  <input 
                    type="text" 
                    class="form-control"
                    [class.error]="registerForm.get('cinOrPassport')?.invalid && registerForm.get('cinOrPassport')?.touched"
                    formControlName="cinOrPassport"
                    placeholder="Ex: AB123456"
                    style="text-transform: uppercase;"
                  />
                  <div class="form-error" *ngIf="registerForm.get('cinOrPassport')?.invalid && registerForm.get('cinOrPassport')?.touched">
                    <span *ngIf="registerForm.get('cinOrPassport')?.errors?.['required']">Le CIN ou Passeport est requis</span>
                    <span *ngIf="registerForm.get('cinOrPassport')?.errors?.['pattern']">Format invalide (5-20 caractères alphanumériques majuscules)</span>
                  </div>
                </div>

                <div class="form-row">
                  <!-- Gender -->
                  <div class="form-group">
                    <label class="form-label">Genre *</label>
                    <select 
                      class="form-control"
                      [class.error]="registerForm.get('gender')?.invalid && registerForm.get('gender')?.touched"
                      formControlName="gender"
                    >
                      <option value="">Sélectionnez</option>
                      <option value="M">Homme</option>
                      <option value="F">Femme</option>
                    </select>
                    <div class="form-error" *ngIf="registerForm.get('gender')?.invalid && registerForm.get('gender')?.touched">
                      <span *ngIf="registerForm.get('gender')?.errors?.['required']">Le genre est requis</span>
                    </div>
                  </div>

                  <!-- Date of Birth -->
                  <div class="form-group">
                    <label class="form-label">Date de naissance *</label>
                    <input 
                      type="date" 
                      class="form-control"
                      [class.error]="(registerForm.get('dateOfBirth')?.invalid && registerForm.get('dateOfBirth')?.touched) || (registerForm.errors?.['underage'] && registerForm.get('dateOfBirth')?.touched)"
                      formControlName="dateOfBirth"
                      [max]="maxDate"
                    />
                    <div class="form-error" *ngIf="registerForm.get('dateOfBirth')?.invalid && registerForm.get('dateOfBirth')?.touched">
                      <span *ngIf="registerForm.get('dateOfBirth')?.errors?.['required']">La date de naissance est requise</span>
                    </div>
                    <div class="form-error" *ngIf="registerForm.errors?.['underage'] && registerForm.get('dateOfBirth')?.touched">
                      <span>Vous devez avoir au moins 18 ans pour créer un compte</span>
                    </div>
                  </div>
                </div>

                <!-- Nationality -->
                <div class="form-group">
                  <label class="form-label">Nationalité *</label>
                  <input 
                    type="text" 
                    class="form-control"
                    [class.error]="registerForm.get('nationality')?.invalid && registerForm.get('nationality')?.touched"
                    formControlName="nationality"
                    placeholder="Ex: Marocaine"
                  />
                  <div class="form-error" *ngIf="registerForm.get('nationality')?.invalid && registerForm.get('nationality')?.touched">
                    <span *ngIf="registerForm.get('nationality')?.errors?.['required']">La nationalité est requise</span>
                    <span *ngIf="registerForm.get('nationality')?.errors?.['maxlength']">Maximum 50 caractères</span>
                  </div>
                </div>
              </div>

              <!-- Step 2: Contact -->
              <div class="form-step" *ngIf="currentStep === 2">
                <!-- Email -->
                <div class="form-group">
                  <label class="form-label">Email *</label>
                  <input 
                    type="email" 
                    class="form-control"
                    [class.error]="registerForm.get('email')?.invalid && registerForm.get('email')?.touched"
                    formControlName="email"
                    placeholder="exemple@email.com"
                  />
                  <div class="form-error" *ngIf="registerForm.get('email')?.invalid && registerForm.get('email')?.touched">
                    <span *ngIf="registerForm.get('email')?.errors?.['required']">L'email est requis</span>
                    <span *ngIf="registerForm.get('email')?.errors?.['email']">Email invalide</span>
                  </div>
                </div>

                <!-- Phone -->
                <div class="form-group">
                  <label class="form-label">Téléphone *</label>
                  <input 
                    type="tel" 
                    class="form-control"
                    [class.error]="registerForm.get('phone')?.invalid && registerForm.get('phone')?.touched"
                    formControlName="phone"
                    placeholder="+212 6XX XXX XXX"
                  />
                  <div class="form-error" *ngIf="registerForm.get('phone')?.invalid && registerForm.get('phone')?.touched">
                    <span *ngIf="registerForm.get('phone')?.errors?.['required']">Le téléphone est requis</span>
                    <span *ngIf="registerForm.get('phone')?.errors?.['pattern']">Numéro invalide (10-15 chiffres)</span>
                  </div>
                </div>

                <!-- Address -->
                <div class="form-group">
                  <label class="form-label">Adresse</label>
                  <input 
                    type="text" 
                    class="form-control"
                    formControlName="address"
                    placeholder="Votre adresse"
                  />
                  <div class="form-error" *ngIf="registerForm.get('address')?.errors?.['maxlength']">
                    <span>Maximum 100 caractères</span>
                  </div>
                </div>

                <div class="form-row">
                  <!-- City -->
                  <div class="form-group">
                    <label class="form-label">Ville</label>
                    <input 
                      type="text" 
                      class="form-control"
                      formControlName="city"
                      placeholder="Votre ville"
                    />
                    <div class="form-error" *ngIf="registerForm.get('city')?.errors?.['maxlength']">
                      <span>Maximum 50 caractères</span>
                    </div>
                  </div>

                  <!-- Country -->
                  <div class="form-group">
                    <label class="form-label">Pays</label>
                    <input 
                      type="text" 
                      class="form-control"
                      formControlName="country"
                      placeholder="Votre pays"
                    />
                    <div class="form-error" *ngIf="registerForm.get('country')?.errors?.['maxlength']">
                      <span>Maximum 50 caractères</span>
                    </div>
                  </div>
                </div>

                <!-- Profession -->
                <div class="form-group">
                  <label class="form-label">Profession</label>
                  <input 
                    type="text" 
                    class="form-control"
                    formControlName="profession"
                    placeholder="Votre profession"
                  />
                  <div class="form-error" *ngIf="registerForm.get('profession')?.errors?.['maxlength']">
                    <span>Maximum 100 caractères</span>
                  </div>
                </div>
              </div>

              <!-- Step 3: Sécurité -->
              <div class="form-step" *ngIf="currentStep === 3">
                <!-- Password -->
                <div class="form-group">
                  <label class="form-label">Mot de passe *</label>
                  <div class="password-input">
                    <input 
                      [type]="showPassword ? 'text' : 'password'"
                      class="form-control"
                      [class.error]="registerForm.get('password')?.invalid && registerForm.get('password')?.touched"
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
                  <div class="password-strength">
                    <div class="strength-bar" [class]="getPasswordStrength()"></div>
                  </div>
                  <div class="password-hints">
                    <small>Le mot de passe doit contenir au moins 6 caractères</small>
                  </div>
                  <div class="form-error" *ngIf="registerForm.get('password')?.invalid && registerForm.get('password')?.touched">
                    <span *ngIf="registerForm.get('password')?.errors?.['required']">Le mot de passe est requis</span>
                    <span *ngIf="registerForm.get('password')?.errors?.['minlength']">Minimum 6 caractères</span>
                  </div>
                </div>

                <!-- Confirm Password -->
                <div class="form-group">
                  <label class="form-label">Confirmer le mot de passe *</label>
                  <div class="password-input">
                    <input 
                      [type]="showConfirmPassword ? 'text' : 'password'"
                      class="form-control"
                      [class.error]="registerForm.get('confirmPassword')?.invalid && registerForm.get('confirmPassword')?.touched"
                      formControlName="confirmPassword"
                      placeholder="••••••••"
                    />
                    <button type="button" class="password-toggle" (click)="showConfirmPassword = !showConfirmPassword">
                      <svg *ngIf="!showConfirmPassword" width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                        <path d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z"/>
                      </svg>
                      <svg *ngIf="showConfirmPassword" width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                        <path d="M12 7c2.76 0 5 2.24 5 5 0 .65-.13 1.26-.36 1.83l2.92 2.92c1.51-1.26 2.7-2.89 3.43-4.75-1.73-4.39-6-7.5-11-7.5-1.4 0-2.74.25-3.98.7l2.16 2.16C10.74 7.13 11.35 7 12 7zM2 4.27l2.28 2.28.46.46C3.08 8.3 1.78 10.02 1 12c1.73 4.39 6 7.5 11 7.5 1.55 0 3.03-.3 4.38-.84l.42.42L19.73 22 21 20.73 3.27 3 2 4.27zM7.53 9.8l1.55 1.55c-.05.21-.08.43-.08.65 0 1.66 1.34 3 3 3 .22 0 .44-.03.65-.08l1.55 1.55c-.67.33-1.41.53-2.2.53-2.76 0-5-2.24-5-5 0-.79.2-1.53.53-2.2zm4.31-.78l3.15 3.15.02-.16c0-1.66-1.34-3-3-3l-.17.01z"/>
                      </svg>
                    </button>
                  </div>
                  <div class="form-error" *ngIf="registerForm.get('confirmPassword')?.invalid && registerForm.get('confirmPassword')?.touched">
                    <span *ngIf="registerForm.get('confirmPassword')?.errors?.['required']">Veuillez confirmer le mot de passe</span>
                  </div>
                  <div class="form-error" *ngIf="registerForm.errors?.['passwordMismatch'] && registerForm.get('confirmPassword')?.touched">
                    <span>Les mots de passe ne correspondent pas</span>
                  </div>
                </div>

                <!-- Terms & Conditions -->
                <div class="form-group">
                  <label class="checkbox-label">
                    <input type="checkbox" formControlName="acceptTerms" />
                    <span>J'accepte les <a href="#" class="link">conditions d'utilisation</a> et la <a href="#" class="link">politique de confidentialité</a></span>
                  </label>
                  <div class="form-error" *ngIf="registerForm.get('acceptTerms')?.invalid && registerForm.get('acceptTerms')?.touched">
                    <span>Vous devez accepter les conditions</span>
                  </div>
                </div>
              </div>

              <!-- Navigation Buttons -->
              <div class="form-navigation">
                <button 
                  type="button" 
                  class="btn btn-outline"
                  *ngIf="currentStep > 1"
                  (click)="previousStep()"
                >
                  Précédent
                </button>
                
                <button 
                  type="button" 
                  class="btn btn-primary"
                  *ngIf="currentStep < 3"
                  (click)="nextStep()"
                  [disabled]="!isCurrentStepValid()"
                >
                  Suivant
                </button>

                <button 
                  type="submit" 
                  class="btn btn-primary"
                  *ngIf="currentStep === 3"
                  [disabled]="registerForm.invalid || isLoading"
                >
                  <span *ngIf="!isLoading">Créer mon compte</span>
                  <span *ngIf="isLoading">Inscription en cours...</span>
                </button>
              </div>
            </form>

            <!-- Login Link -->
            <div class="auth-footer">
              <p>Vous avez déjà un compte ? <a routerLink="/auth/login" class="link">Se connecter</a></p>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
// Dans la section styles, remplacer les styles suivants :

styles: [`
    .auth-page {
      min-height: 100vh;
      display: flex;
      align-items: center;
      justify-content: center;
      background: linear-gradient(135deg, #E8F5E9 0%, #F5F5F5 100%);
      padding: 1rem;
    }

    .auth-container {
      display: grid;
      grid-template-columns: 1fr 1.2fr;
      max-width: 1100px;
      width: 100%;
      background: white;
      border-radius: 24px;
      overflow: hidden;
      box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
      max-height: 95vh;
    }

    .auth-branding {
      background: linear-gradient(135deg, #00843D 0%, #006830 100%);
      color: white;
      padding: 3rem;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .branding-content {
      max-width: 350px;
    }

    .brand-logo {
      height: 50px;
      margin-bottom: 1.5rem;
      filter: brightness(0) invert(1);
    }

    h1 {
      font-size: 2rem;
      font-weight: 700;
      margin-bottom: 0.75rem;
      line-height: 1.2;
    }

    .branding-content > p {
      font-size: 1rem;
      opacity: 0.9;
      margin-bottom: 2rem;
    }

    .features {
      display: flex;
      flex-direction: column;
      gap: 1rem;
    }

    .feature {
      display: flex;
      align-items: center;
      gap: 10px;
      font-size: 0.95rem;

      svg {
        flex-shrink: 0;
        width: 20px;
        height: 20px;
      }
    }

    .auth-form-container {
      padding: 2rem 2.5rem;
      display: flex;
      flex-direction: column;
      justify-content: flex-start;
      overflow-y: auto;
      max-height: 95vh;
    }

    .auth-form {
      width: 100%;
      max-width: 420px;
      margin: 0 auto;
    }

    h2 {
      color: #00843D;
      margin-bottom: 0.25rem;
      font-size: 1.5rem;
    }

    .subtitle {
      color: #757575;
      margin-bottom: 1.25rem;
      font-size: 0.95rem;
    }

    /* Steps indicator - Amélioré */
    .steps-indicator {
      display: flex;
      align-items: center;
      justify-content: center;
      margin-bottom: 1.5rem;
      padding: 1rem;
      background: #F8F9FA;
      border-radius: 12px;
    }

    .step {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 6px;
    }

    .step-number {
      width: 36px;
      height: 36px;
      border-radius: 50%;
      background: #E0E0E0;
      color: #757575;
      display: flex;
      align-items: center;
      justify-content: center;
      font-weight: 600;
      font-size: 0.95rem;
      transition: all 0.3s ease;
    }

    .step.active .step-number {
      background: #00843D;
      color: white;
      box-shadow: 0 4px 12px rgba(0, 132, 61, 0.3);
    }

    .step.completed .step-number {
      background: #388E3C;
      color: white;
    }

    .step-label {
      font-size: 0.8rem;
      color: #757575;
      font-weight: 500;
    }

    .step.active .step-label {
      color: #00843D;
      font-weight: 600;
    }

    .step.completed .step-label {
      color: #388E3C;
    }

    .step-line {
      width: 50px;
      height: 3px;
      background: #E0E0E0;
      margin: 0 12px;
      margin-bottom: 22px;
      border-radius: 2px;
      transition: background 0.3s ease;
    }

    .step-line.completed {
      background: #388E3C;
    }

    /* Form styles */
    .form-step {
      animation: fadeIn 0.3s ease;
    }

    @keyframes fadeIn {
      from { opacity: 0; transform: translateX(10px); }
      to { opacity: 1; transform: translateX(0); }
    }

    .form-row {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 0.75rem;
    }

    .form-group {
      margin-bottom: 0.85rem;
    }

    .form-label {
      display: block;
      margin-bottom: 0.4rem;
      color: #212121;
      font-weight: 500;
      font-size: 0.85rem;
    }

    .form-control {
      width: 100%;
      padding: 10px 14px;
      border: 2px solid #E0E0E0;
      border-radius: 8px;
      font-size: 0.95rem;
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

    select.form-control {
      cursor: pointer;
      background: white;
    }

    .form-error {
      color: #D32F2F;
      font-size: 0.75rem;
      margin-top: 3px;
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
      margin-top: 6px;
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

    .password-hints {
      margin-top: 4px;
      
      small {
        color: #757575;
        font-size: 0.7rem;
      }
    }

    .checkbox-label {
      display: flex;
      align-items: flex-start;
      gap: 8px;
      cursor: pointer;
      font-size: 0.85rem;

      input[type="checkbox"] {
        margin-top: 3px;
        cursor: pointer;
        width: 16px;
        height: 16px;
      }
    }

    .link {
      color: #00843D;
      text-decoration: none;
      font-weight: 600;
      transition: color 0.3s ease;

      &:hover {
        color: #006830;
        text-decoration: underline;
      }
    }

    /* Navigation buttons */
    .form-navigation {
      display: flex;
      gap: 0.75rem;
      margin-top: 1.25rem;
    }

    .btn {
      flex: 1;
      padding: 12px 20px;
      border-radius: 8px;
      font-size: 0.95rem;
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

    .btn-outline {
      background: white;
      color: #00843D;
      border: 2px solid #00843D;

      &:hover {
        background: #E8F5E9;
      }
    }

    .auth-footer {
      margin-top: 1.25rem;
      text-align: center;

      p {
        color: #757575;
        font-size: 0.9rem;
      }
    }

    @media (max-width: 968px) {
      .auth-container {
        grid-template-columns: 1fr;
        max-height: none;
      }

      .auth-branding {
        display: none;
      }

      .auth-form-container {
        padding: 1.5rem;
        max-height: none;
      }

      .form-row {
        grid-template-columns: 1fr;
      }

      .steps-indicator {
        padding: 0.75rem;
      }

      .step-line {
        width: 30px;
      }
    }

    /* Scrollbar personnalisée */
    .auth-form-container::-webkit-scrollbar {
      width: 6px;
    }

    .auth-form-container::-webkit-scrollbar-track {
      background: #f1f1f1;
      border-radius: 3px;
    }

    .auth-form-container::-webkit-scrollbar-thumb {
      background: #c1c1c1;
      border-radius: 3px;
    }

    .auth-form-container::-webkit-scrollbar-thumb:hover {
      background: #a1a1a1;
    }
  `]
})
export class RegisterComponent implements OnInit {
  registerForm!: FormGroup;
  isLoading = false;
  showPassword = false;
  showConfirmPassword = false;
  currentStep = 1;
  maxDate: string;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private notificationService: NotificationService,
    private router: Router
  ) {
    // Date max = aujourd'hui - 18 ans (majorité)
    const today = new Date();
    today.setFullYear(today.getFullYear() - 18);
    this.maxDate = today.toISOString().split('T')[0];
  }

  ngOnInit(): void {
    this.registerForm = this.fb.group({
      // Step 1: Identité (champs obligatoires)
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]],
      username: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(50)]],
      cinOrPassport: ['', [Validators.required, Validators.pattern(/^[A-Z0-9]{5,20}$/)]],
      gender: ['', [Validators.required]],
      dateOfBirth: ['', [Validators.required]],
      nationality: ['', [Validators.required, Validators.maxLength(50)]],
      
      // Step 2: Contact
      email: ['', [Validators.required, Validators.email]],
      phone: ['', [Validators.required, Validators.pattern(/^\+?[0-9]{10,15}$/)]],
      address: ['', [Validators.maxLength(100)]],
      city: ['', [Validators.maxLength(50)]],
      country: ['', [Validators.maxLength(50)]],
      profession: ['', [Validators.maxLength(100)]],
      
      // Step 3: Sécurité
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required]],
      acceptTerms: [false, [Validators.requiredTrue]]
    }, {
      validators: [this.passwordMatchValidator, this.ageValidator]
    });
  }

  passwordMatchValidator(formGroup: FormGroup): ValidationErrors | null {
    const password = formGroup.get('password');
    const confirmPassword = formGroup.get('confirmPassword');

    if (password && confirmPassword && password.value !== confirmPassword.value) {
      return { passwordMismatch: true };
    }
    return null;
  }

  ageValidator(formGroup: FormGroup): ValidationErrors | null {
    const dateOfBirth = formGroup.get('dateOfBirth')?.value;
    
    if (!dateOfBirth) return null;
    
    const today = new Date();
    const birthDate = new Date(dateOfBirth);
    let age = today.getFullYear() - birthDate.getFullYear();
    const monthDiff = today.getMonth() - birthDate.getMonth();
    
    if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
      age--;
    }
    
    if (age < 18) {
      return { underage: true };
    }
    
    return null;
  }

  // Validation par étape
  isCurrentStepValid(): boolean {
    switch (this.currentStep) {
      case 1:
        const step1Valid = this.registerForm.get('firstName')?.valid &&
               this.registerForm.get('lastName')?.valid &&
               this.registerForm.get('username')?.valid &&
               this.registerForm.get('cinOrPassport')?.valid &&
               this.registerForm.get('gender')?.valid &&
               this.registerForm.get('dateOfBirth')?.valid &&
               this.registerForm.get('nationality')?.valid &&
               !this.registerForm.errors?.['underage'];
        return step1Valid || false;
      case 2:
        return this.registerForm.get('email')?.valid &&
               this.registerForm.get('phone')?.valid || false;
      case 3:
        return this.registerForm.get('password')?.valid &&
               this.registerForm.get('confirmPassword')?.valid &&
               this.registerForm.get('acceptTerms')?.valid &&
               !this.registerForm.errors?.['passwordMismatch'] || false;
      default:
        return false;
    }
  }

  nextStep(): void {
    if (this.isCurrentStepValid() && this.currentStep < 3) {
      this.currentStep++;
    } else {
      this.markCurrentStepAsTouched();
    }
  }

  previousStep(): void {
    if (this.currentStep > 1) {
      this.currentStep--;
    }
  }

  markCurrentStepAsTouched(): void {
    const stepFields: { [key: number]: string[] } = {
      1: ['firstName', 'lastName', 'username', 'cinOrPassport', 'gender', 'dateOfBirth', 'nationality'],
      2: ['email', 'phone'],
      3: ['password', 'confirmPassword', 'acceptTerms']
    };

    stepFields[this.currentStep].forEach(field => {
      this.registerForm.get(field)?.markAsTouched();
    });
  }

  togglePassword(): void {
    this.showPassword = !this.showPassword;
  }

  getPasswordStrength(): string {
    const password = this.registerForm.get('password')?.value || '';
    
    if (password.length === 0) return '';
    if (password.length < 6) return 'weak';
    
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
    if (this.registerForm.invalid) {
      Object.keys(this.registerForm.controls).forEach(key => {
        this.registerForm.get(key)?.markAsTouched();
      });
      return;
    }

    this.isLoading = true;

    const formValue = this.registerForm.value;
    
    const registerRequest = {
      username: formValue.username,
      email: formValue.email,
      password: formValue.password,
      firstName: formValue.firstName,
      lastName: formValue.lastName,
      cinOrPassport: formValue.cinOrPassport.toUpperCase(),
      nationality: formValue.nationality,
      gender: formValue.gender,
      phone: formValue.phone,
      dateOfBirth: formValue.dateOfBirth,
      address: formValue.address || undefined,
      city: formValue.city || undefined,
      country: formValue.country || undefined,
      profession: formValue.profession || undefined
    };

    this.authService.register(registerRequest).subscribe({
      next: (response) => {
        this.isLoading = false;
        if (response.success) {
          this.notificationService.showSuccess(
            'Inscription réussie ! Vérifiez votre email pour activer votre compte.'
          );
          this.router.navigate(['/auth/verify-email'], { 
            queryParams: { email: registerRequest.email } 
          });
        }
      },
      error: (error) => {
        this.isLoading = false;
        this.notificationService.showError(
          error.error?.message || 'Une erreur est survenue lors de l\'inscription'
        );
      }
    });
  }
}