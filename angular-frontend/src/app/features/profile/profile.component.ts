import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { UserService } from '../../core/services/user.service';
import { AuthService } from '../../core/services/auth.service';
import { NotificationService } from '../../core/services/notification.service';
import { User } from '../../core/models/user.model';
import { LoaderComponent } from '../../shared/components/loader/loader.component';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, LoaderComponent],
  template: `
    <div class="profile-page">
      <app-loader *ngIf="isLoading"></app-loader>

      <div class="container">
        <!-- Profile Header -->
        <div class="profile-header">
          <div class="profile-avatar-large">
            {{ getInitials() }}
          </div>
          <div class="profile-header-info">
            <h1>{{ user?.firstName }} {{ user?.lastName }}</h1>
            <p>{{ user?.email }}</p>
            <div class="profile-badges">
              <span class="badge verified" *ngIf="user?.emailVerified">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M12 1L3 5v6c0 5.55 3.84 10.74 9 12 5.16-1.26 9-6.45 9-12V5l-9-4zm-2 16l-4-4 1.41-1.41L10 14.17l6.59-6.59L18 9l-8 8z"/>
                </svg>
                Email vérifié
              </span>
              <span class="badge active" *ngIf="user?.active">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
                </svg>
                Compte actif
              </span>
              <span class="badge member-since">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M11.99 2C6.47 2 2 6.48 2 12s4.47 10 9.99 10C17.52 22 22 17.52 22 12S17.52 2 11.99 2zM12 20c-4.42 0-8-3.58-8-8s3.58-8 8-8 8 3.58 8 8-3.58 8-8 8zm.5-13H11v6l5.25 3.15.75-1.23-4.5-2.67z"/>
                </svg>
                Membre depuis {{ formatDate(user?.createdAt) }}
              </span>
            </div>
          </div>
        </div>

        <!-- Tabs -->
        <div class="tabs">
          <button 
            class="tab" 
            [class.active]="activeTab === 'info'"
            (click)="activeTab = 'info'"
          >
            Informations personnelles
          </button>
          <button 
            class="tab" 
            [class.active]="activeTab === 'security'"
            (click)="activeTab = 'security'"
          >
            Sécurité
          </button>
          <button 
            class="tab" 
            [class.active]="activeTab === 'preferences'"
            (click)="activeTab = 'preferences'"
          >
            Préférences
          </button>
        </div>

        <!-- Tab Content -->
        <div class="tab-content">
          <!-- Personal Information -->
          <div *ngIf="activeTab === 'info'" class="tab-panel">
            <div class="card">
              <div class="card-header">
                <h3>Informations personnelles</h3>
                <button 
                  class="btn btn-outline"
                  *ngIf="! isEditing"
                  (click)="startEditing()"
                >
                  Modifier
                </button>
              </div>

              <form [formGroup]="profileForm" (ngSubmit)="saveProfile()" *ngIf="isEditing; else viewMode">
                <div class="form-row">
                  <div class="form-group">
                    <label class="form-label">Prénom</label>
                    <input 
                      type="text" 
                      class="form-control"
                      formControlName="firstName"
                    />
                  </div>

                  <div class="form-group">
                    <label class="form-label">Nom</label>
                    <input 
                      type="text" 
                      class="form-control"
                      formControlName="lastName"
                    />
                  </div>
                </div>

                <div class="form-group">
                  <label class="form-label">Email</label>
                  <input 
                    type="email" 
                    class="form-control"
                    [value]="user?.email"
                    disabled
                  />
                  <small class="form-hint">L'email ne peut pas être modifié ici.  Utilisez l'onglet Sécurité. </small>
                </div>

                <div class="form-group">
                  <label class="form-label">Téléphone</label>
                  <input 
                    type="tel" 
                    class="form-control"
                    formControlName="phoneNumber"
                    placeholder="+212 6XX XXX XXX"
                  />
                </div>

                <div class="form-group">
                  <label class="form-label">Date de naissance</label>
                  <input 
                    type="date" 
                    class="form-control"
                    formControlName="dateOfBirth"
                  />
                </div>

                <div class="form-group">
                  <label class="form-label">Genre</label>
                  <select class="form-control" formControlName="gender">
                    <option value="">Sélectionner</option>
                    <option value="MALE">Homme</option>
                    <option value="FEMALE">Femme</option>
                    <option value="OTHER">Autre</option>
                    <option value="PREFER_NOT_TO_SAY">Préfère ne pas dire</option>
                  </select>
                </div>

                <div class="form-group">
                  <label class="form-label">Adresse</label>
                  <input 
                    type="text" 
                    class="form-control"
                    formControlName="address"
                    placeholder="Rue, numéro"
                  />
                </div>

                <div class="form-row">
                  <div class="form-group">
                    <label class="form-label">Ville</label>
                    <input 
                      type="text" 
                      class="form-control"
                      formControlName="city"
                      placeholder="Casablanca"
                    />
                  </div>

                  <div class="form-group">
                    <label class="form-label">Code postal</label>
                    <input 
                      type="text" 
                      class="form-control"
                      formControlName="postalCode"
                      placeholder="20000"
                    />
                  </div>
                </div>

                <div class="form-group">
                  <label class="form-label">Pays</label>
                  <input 
                    type="text" 
                    class="form-control"
                    formControlName="country"
                    placeholder="Maroc"
                  />
                </div>

                <div class="form-group">
                  <label class="form-label">Biographie</label>
                  <textarea 
                    class="form-control"
                    formControlName="bio"
                    rows="4"
                    placeholder="Parlez-nous de vous..."
                  ></textarea>
                </div>

                <div class="form-actions">
                  <button 
                    type="button" 
                    class="btn btn-secondary"
                    (click)="cancelEditing()"
                  >
                    Annuler
                  </button>
                  <button 
                    type="submit" 
                    class="btn btn-primary"
                    [disabled]="profileForm.invalid || isSaving"
                  >
                    <span *ngIf="! isSaving">Enregistrer</span>
                    <span *ngIf="isSaving">Enregistrement... </span>
                  </button>
                </div>
              </form>

              <ng-template #viewMode>
                <div class="info-grid">
                  <div class="info-item">
                    <span class="info-label">Prénom</span>
                    <span class="info-value">{{ user?.firstName || '-' }}</span>
                  </div>

                  <div class="info-item">
                    <span class="info-label">Nom</span>
                    <span class="info-value">{{ user?.lastName || '-' }}</span>
                  </div>

                  <div class="info-item">
                    <span class="info-label">Email</span>
                    <span class="info-value">{{ user?.email }}</span>
                  </div>

                  <div class="info-item">
                    <span class="info-label">Téléphone</span>
                    <span class="info-value">{{ user?.phoneNumber || '-' }}</span>
                  </div>

                  <div class="info-item">
                    <span class="info-label">Date de naissance</span>
                    <span class="info-value">{{ user?.dateOfBirth || '-' }}</span>
                  </div>

                  <div class="info-item">
                    <span class="info-label">Genre</span>
                    <span class="info-value">{{ getGenderLabel(user?.gender) }}</span>
                  </div>

                  <div class="info-item full-width">
                    <span class="info-label">Adresse</span>
                    <span class="info-value">{{ user?.address || '-' }}</span>
                  </div>

                  <div class="info-item">
                    <span class="info-label">Ville</span>
                    <span class="info-value">{{ user?.city || '-' }}</span>
                  </div>

                  <div class="info-item">
                    <span class="info-label">Code postal</span>
                    <span class="info-value">{{ user?.postalCode || '-' }}</span>
                  </div>

                  <div class="info-item">
                    <span class="info-label">Pays</span>
                    <span class="info-value">{{ user?.country || '-' }}</span>
                  </div>

                  <div class="info-item full-width" *ngIf="user?.bio">
                    <span class="info-label">Biographie</span>
                    <span class="info-value">{{ user?.bio }}</span>
                  </div>
                </div>
              </ng-template>
            </div>
          </div>

          <!-- Security -->
          <div *ngIf="activeTab === 'security'" class="tab-panel">
            <div class="card">
              <h3>Changer le mot de passe</h3>
              <form [formGroup]="passwordForm" (ngSubmit)="changePassword()">
                <div class="form-group">
                  <label class="form-label">Mot de passe actuel</label>
                  <input 
                    type="password" 
                    class="form-control"
                    formControlName="currentPassword"
                    placeholder="••••••••"
                  />
                </div>

                <div class="form-group">
                  <label class="form-label">Nouveau mot de passe</label>
                  <input 
                    type="password" 
                    class="form-control"
                    formControlName="newPassword"
                    placeholder="••••••••"
                  />
                  <div class="password-strength">
                    <div class="strength-bar" [class]="getPasswordStrength()"></div>
                  </div>
                </div>

                <div class="form-group">
                  <label class="form-label">Confirmer le nouveau mot de passe</label>
                  <input 
                    type="password" 
                    class="form-control"
                    formControlName="confirmPassword"
                    placeholder="••••••••"
                  />
                  <div class="form-error" *ngIf="passwordForm.errors?.['passwordMismatch'] && passwordForm.get('confirmPassword')?.touched">
                    <span>Les mots de passe ne correspondent pas</span>
                  </div>
                </div>

                <button 
                  type="submit" 
                  class="btn btn-primary"
                  [disabled]="passwordForm.invalid || isChangingPassword"
                >
                  <span *ngIf="!isChangingPassword">Changer le mot de passe</span>
                  <span *ngIf="isChangingPassword">Modification...</span>
                </button>
              </form>
            </div>

            <div class="card">
              <h3>Sessions actives</h3>
              <div class="session-item">
                <div class="session-info">
                  <div class="session-icon">
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
                      <path d="M20 18c1.1 0 1.99-.9 1.99-2L22 6c0-1.1-.9-2-2-2H4c-1.1 0-2 .9-2 2v10c0 1.1.9 2 2 2H0v2h24v-2h-4zM4 6h16v10H4V6z"/>
                    </svg>
                  </div>
                  <div>
                    <strong>Session actuelle</strong>
                    <p>Chrome sur Windows • Casablanca, Maroc</p>
                    <small>Dernière activité:  Maintenant</small>
                  </div>
                </div>
                <span class="session-badge current">Actuelle</span>
              </div>
            </div>

            <div class="card danger-zone">
              <h3>Zone dangereuse</h3>
              <p>La suppression de votre compte est irréversible.</p>
              <button class="btn btn-danger" (click)="confirmDeleteAccount()">
                Supprimer mon compte
              </button>
            </div>
          </div>

          <!-- Preferences -->
          <div *ngIf="activeTab === 'preferences'" class="tab-panel">
            <div class="card">
              <h3>Notifications</h3>
              <form [formGroup]="preferencesForm" (ngSubmit)="savePreferences()">
                <div class="preference-item">
                  <div class="preference-info">
                    <strong>Notifications par email</strong>
                    <p>Recevoir des notifications sur votre email</p>
                  </div>
                  <label class="switch">
                    <input type="checkbox" formControlName="emailNotifications" />
                    <span class="slider"></span>
                  </label>
                </div>

                <div class="preference-item">
                  <div class="preference-info">
                    <strong>Notifications SMS</strong>
                    <p>Recevoir des alertes par SMS</p>
                  </div>
                  <label class="switch">
                    <input type="checkbox" formControlName="smsNotifications" />
                    <span class="slider"></span>
                  </label>
                </div>

                <div class="form-group">
                  <label class="form-label">Langue préférée</label>
                  <select class="form-control" formControlName="preferredLanguage">
                    <option value="fr">Français</option>
                    <option value="ar">العربية</option>
                    <option value="en">English</option>
                  </select>
                </div>

                <div class="form-group">
                  <label class="form-label">Fuseau horaire</label>
                  <select class="form-control" formControlName="timezone">
                    <option value="Africa/Casablanca">Casablanca (GMT+1)</option>
                    <option value="Europe/Paris">Paris (GMT+1)</option>
                    <option value="Europe/London">Londres (GMT+0)</option>
                  </select>
                </div>

                <button 
                  type="submit" 
                  class="btn btn-primary"
                  [disabled]="preferencesForm.invalid || isSavingPreferences"
                >
                  <span *ngIf="!isSavingPreferences">Enregistrer les préférences</span>
                  <span *ngIf="isSavingPreferences">Enregistrement...</span>
                </button>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .profile-page {
      min-height: calc(100vh - 80px);
      background: #F5F5F5;
      padding: 3rem 0;
    }

    .profile-header {
      background: white;
      border-radius: 16px;
      padding: 3rem;
      margin-bottom:  2rem;
      box-shadow:  0 2px 10px rgba(0, 0, 0, 0.05);
      display: flex;
      gap: 2rem;
      align-items: center;
    }

    .profile-avatar-large {
      width: 120px;
      height: 120px;
      border-radius: 50%;
      background:  linear-gradient(135deg, #00843D 0%, #006830 100%);
      color: white;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 3rem;
      font-weight:  700;
      flex-shrink: 0;
    }

    .profile-header-info {
      flex: 1;

      h1 {
        font-size: 2rem;
        margin-bottom: 0.5rem;
        color: #212121;
      }

      p {
        color: #757575;
        margin-bottom:  1rem;
      }
    }

    .profile-badges {
      display: flex;
      gap: 1rem;
      flex-wrap: wrap;
    }

    .badge {
      display: inline-flex;
      align-items: center;
      gap: 6px;
      padding: 6px 16px;
      border-radius: 20px;
      font-size:  0.875rem;
      font-weight: 600;

      &.verified {
        background: #E8F5E9;
        color: #388E3C;
      }

      &.active {
        background: #E3F2FD;
        color: #1976D2;
      }

      &.member-since {
        background: #F5F5F5;
        color: #757575;
      }
    }

    .tabs {
      display: flex;
      gap: 1rem;
      margin-bottom: 2rem;
      background: white;
      padding: 1rem;
      border-radius: 16px;
      box-shadow:  0 2px 10px rgba(0, 0, 0, 0.05);
    }

    .tab {
      flex: 1;
      padding: 1rem 2rem;
      background: transparent;
      border: 2px solid transparent;
      border-radius: 12px;
      font-weight: 600;
      cursor:  pointer;
      transition: all 0.3s ease;
      color: #757575;

      &:hover {
        background: #F5F5F5;
      }

      &.active {
        background: linear-gradient(135deg, #E8F5E9 0%, #F5F5F5 100%);
        border-color: #00843D;
        color: #00843D;
      }
    }

    .tab-content {
      .card {
        background: white;
        border-radius: 16px;
        padding: 2rem;
        margin-bottom: 2rem;
        box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);

        h3 {
          color: #00843D;
          margin-bottom: 1.5rem;
        }
      }

      .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 1.5rem;

        h3 {
          margin-bottom: 0;
        }
      }
    }

    .info-grid {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 2rem;
    }

    .info-item {
      display: flex;
      flex-direction: column;
      gap: 0.5rem;

      &.full-width {
        grid-column: span 2;
      }

      .info-label {
        font-weight: 600;
        color: #757575;
        font-size: 0.875rem;
        text-transform: uppercase;
        letter-spacing: 0.5px;
      }

      .info-value {
        color: #212121;
        font-size: 1rem;
      }
    }

    .form-row {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 1.5rem;
    }

    .form-actions {
      display: flex;
      gap: 1rem;
      justify-content: flex-end;
      margin-top: 2rem;
      padding-top: 2rem;
      border-top: 1px solid #E0E0E0;
    }

    .form-hint {
      display: block;
      color: #757575;
      font-size: 0.875rem;
      margin-top: 0.5rem;
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

    .session-item {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 1.5rem;
      background: #F5F5F5;
      border-radius: 12px;
      margin-bottom:  1rem;
    }

    .session-info {
      display: flex;
      gap: 1rem;
      align-items: center;

      .session-icon {
        width: 48px;
        height: 48px;
        background: #00843D;
        color: white;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
      }

      strong {
        display: block;
        color: #212121;
        margin-bottom: 0.25rem;
      }

      p {
        color: #757575;
        font-size: 0.9rem;
        margin:  0;
      }

      small {
        color: #757575;
        font-size:  0.85rem;
      }
    }

    .session-badge {
      padding: 6px 16px;
      border-radius:  20px;
      font-size: 0.875rem;
      font-weight: 600;

      &.current {
        background: #E8F5E9;
        color: #388E3C;
      }
    }

    .preference-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 1.5rem;
      background: #F5F5F5;
      border-radius: 12px;
      margin-bottom: 1rem;

      .preference-info {
        strong {
          display: block;
          color: #212121;
          margin-bottom: 0.25rem;
        }

        p {
          color: #757575;
          font-size: 0.9rem;
          margin: 0;
        }
      }
    }

    .switch {
      position: relative;
      display: inline-block;
      width: 52px;
      height: 28px;

      input {
        opacity: 0;
        width: 0;
        height: 0;

        &:checked + .slider {
          background: #00843D;

          &:before {
            transform: translateX(24px);
          }
        }
      }

      .slider {
        position: absolute;
        cursor: pointer;
        top:  0;
        left: 0;
        right: 0;
        bottom: 0;
        background: #ccc;
        transition: 0.4s;
        border-radius:  28px;

        &:before {
          position: absolute;
          content: "";
          height: 20px;
          width: 20px;
          left: 4px;
          bottom: 4px;
          background: white;
          transition: 0.4s;
          border-radius: 50%;
        }
      }
    }

    .danger-zone {
      border: 2px solid #FFEBEE;
      background: #FFF5F5 ! important;

      h3 {
        color: #D32F2F !important;
      }

      p {
        color: #757575;
        margin-bottom: 1rem;
      }
    }

    .btn-danger {
      background: #D32F2F;
      color: white;
      border: none;
      padding: 12px 32px;
      border-radius:  8px;
      font-weight: 600;
      cursor:  pointer;
      transition: all 0.3s ease;

      &:hover {
        background:  #B71C1C;
        transform: translateY(-2px);
        box-shadow: 0 4px 15px rgba(211, 47, 47, 0.3);
      }
    }

    @media (max-width: 768px) {
      .profile-header {
        flex-direction:  column;
        text-align: center;
      }

      .tabs {
        flex-direction: column;
      }

      .form-row,
      .info-grid {
        grid-template-columns: 1fr;
      }

      .info-item.full-width {
        grid-column: span 1;
      }
    }
  `]
})
export class ProfileComponent implements OnInit {
  isLoading = false;
  isSaving = false;
  isChangingPassword = false;
  isSavingPreferences = false;
  isEditing = false;
  activeTab:  'info' | 'security' | 'preferences' = 'info';

  user: User | null = null;
  profileForm! : FormGroup;
  passwordForm!: FormGroup;
  preferencesForm!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private authService: AuthService,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.initForms();
    this.loadProfile();
  }

  initForms(): void {
    this.profileForm = this.fb.group({
      firstName: [''],
      lastName: [''],
      phoneNumber: [''],
      dateOfBirth: [''],
      gender: [''],
      address: [''],
      city: [''],
      country: [''],
      postalCode: [''],
      bio: ['']
    });

    this.passwordForm = this.fb.group({
      currentPassword: ['', Validators.required],
      newPassword: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', Validators.required]
    }, {
      validators: this.passwordMatchValidator
    });

    this.preferencesForm = this.fb.group({
      emailNotifications: [true],
      smsNotifications:  [false],
      preferredLanguage:  ['fr'],
      timezone: ['Africa/Casablanca']
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

  loadProfile(): void {
    this.isLoading = true;

    this.userService.getProfile().subscribe({
      next: (user) => {
        this.user = user;
        this.patchForms(user);
        this.isLoading = false;
      },
      error: (error) => {
        this.isLoading = false;
        this.notificationService.showError('Erreur lors du chargement du profil');
      }
    });
  }

  patchForms(user: User): void {
    this.profileForm.patchValue({
      firstName:  user.firstName,
      lastName: user.lastName,
      phoneNumber: user.phoneNumber,
      dateOfBirth: user.dateOfBirth,
      gender: user.gender,
      address: user.address,
      city: user.city,
      country: user.country,
      postalCode: user.postalCode,
      bio:  user.bio
    });

    this.preferencesForm.patchValue({
      emailNotifications: user.emailNotifications,
      smsNotifications: user.smsNotifications,
      preferredLanguage: user.preferredLanguage,
      timezone: user.timezone
    });
  }

  startEditing(): void {
    this.isEditing = true;
  }

  cancelEditing(): void {
    this.isEditing = false;
    if (this.user) {
      this.patchForms(this.user);
    }
  }

  saveProfile(): void {
    if (this.profileForm.invalid) {
      return;
    }

    this.isSaving = true;

    this.userService.updateProfile(this.profileForm.value).subscribe({
      next: (user) => {
        this.user = user;
        this.isSaving = false;
        this.isEditing = false;
        this.notificationService.showSuccess('Profil mis à jour avec succès');
      },
      error: (error) => {
        this.isSaving = false;
        this.notificationService.showError('Erreur lors de la mise à jour');
      }
    });
  }

  changePassword(): void {
    if (this.passwordForm.invalid) {
      return;
    }

    this.isChangingPassword = true;

    const request = {
      currentPassword: this.passwordForm.value.currentPassword,
      newPassword: this.passwordForm.value.newPassword
    };

    this.authService.changePassword(request).subscribe({
      next: (response) => {
        this.isChangingPassword = false;
        if (response.success) {
          this.notificationService.showSuccess('Mot de passe modifié avec succès');
          this.passwordForm.reset();
        }
      },
      error: (error) => {
        this.isChangingPassword = false;
        this.notificationService.showError('Erreur lors du changement de mot de passe');
      }
    });
  }

  savePreferences(): void {
    if (this.preferencesForm.invalid) {
      return;
    }

    this.isSavingPreferences = true;

    this.userService.updateProfile(this.preferencesForm.value).subscribe({
      next: (user) => {
        this.user = user;
        this.isSavingPreferences = false;
        this.notificationService.showSuccess('Préférences enregistrées');
      },
      error: (error) => {
        this.isSavingPreferences = false;
        this.notificationService.showError('Erreur lors de l\'enregistrement');
      }
    });
  }

  confirmDeleteAccount(): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer votre compte ?  Cette action est irréversible. ')) {
      this.userService.deleteAccount().subscribe({
        next: () => {
          this.notificationService.showSuccess('Compte supprimé');
          this.authService.logout();
        },
        error:  () => {
          this.notificationService.showError('Erreur lors de la suppression');
        }
      });
    }
  }

  getPasswordStrength(): string {
    const password = this.passwordForm.get('newPassword')?.value || '';
    
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

  getInitials(): string {
    if (this.user?.firstName && this.user?.lastName) {
      return `${this.user.firstName.charAt(0)}${this.user.lastName.charAt(0)}`.toUpperCase();
    }
    if (this.user?.email) {
      return this.user.email.charAt(0).toUpperCase();
    }
    return 'U';
  }

  getGenderLabel(gender: string | undefined): string {
    const labels:  { [key: string]: string } = {
      'MALE': 'Homme',
      'FEMALE': 'Femme',
      'OTHER': 'Autre',
      'PREFER_NOT_TO_SAY': 'Préfère ne pas dire'
    };
    return labels[gender || ''] || '-';
  }

  formatDate(date: string | undefined): string {
    if (!date) return '-';
    return new Date(date).toLocaleDateString('fr-FR', {
      year: 'numeric',
      month: 'long'
    });
  }
}
