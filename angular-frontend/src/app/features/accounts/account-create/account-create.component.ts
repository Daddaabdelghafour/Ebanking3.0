import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { AccountService } from '../../../core/services/account.service';
import { NotificationService } from '../../../core/services/notification.service';
import { CreateAccountRequest } from '../../../core/models/account.model';

@Component({
  selector: 'app-account-create',
  standalone: true,
  imports: [CommonModule, RouterLink, ReactiveFormsModule],
  templateUrl: './account-create.component.html',
  styleUrls: ['./account-create.component.scss']
})
export class AccountCreateComponent {
  accountForm: FormGroup;
  loading = false;

  constructor(
    private fb: FormBuilder,
    private accountService: AccountService,
    private notificationService: NotificationService,
    private router: Router
  ) {
    this.accountForm = this.fb.group({
      customerId: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      balance: [0, [Validators.required, Validators.min(0)]]
    });
  }

  onSubmit(): void {
    if (this.accountForm.invalid) {
      this.markFormGroupTouched(this.accountForm);
      return;
    }

    this.loading = true;
    const request: CreateAccountRequest = this.accountForm.value;

    this.accountService.createAccount(request)
      .subscribe({
        next: (response) => {
          if (response.success) {
            this.notificationService.showSuccess('Account created successfully');
            this.router.navigate(['/accounts', response.data]);
          } else {
            this.notificationService.showError(response.message);
          }
          this.loading = false;
        },
        error: (error) => {
          this.notificationService.showError('Failed to create account');
          this.loading = false;
        }
      });
  }

  private markFormGroupTouched(formGroup: FormGroup): void {
    Object.keys(formGroup.controls).forEach(key => {
      const control = formGroup.get(key);
      control?.markAsTouched();
    });
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.accountForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched));
  }

  getErrorMessage(fieldName: string): string {
    const field = this.accountForm.get(fieldName);
    if (field?.hasError('required')) {
      return `${fieldName} is required`;
    }
    if (field?.hasError('email')) {
      return 'Invalid email format';
    }
    if (field?.hasError('min')) {
      return `${fieldName} must be at least ${field.errors?.['min'].min}`;
    }
    return '';
  }
}
