import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { AccountService } from '../../../core/services/account.service';
import { NotificationService } from '../../../core/services/notification.service';
import { Account, OperationRequest } from '../../../core/models/account.model';

@Component({
  selector: 'app-account-operations',
  standalone: true,
  imports: [CommonModule, RouterLink, ReactiveFormsModule],
  templateUrl: './account-operations.component.html',
  styleUrls: ['./account-operations.component.scss']
})
export class AccountOperationsComponent implements OnInit {
  account: Account | null = null;
  operationForm: FormGroup;
  loading = false;
  operationType: 'credit' | 'debit' = 'credit';
  accountId: string | null = null;

  constructor(
    private fb: FormBuilder,
    public accountService: AccountService,
    private notificationService: NotificationService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.operationForm = this.fb.group({
      amount: [0, [Validators.required, Validators.min(0.01)]],
      description: ['', [Validators.required]]
    });
  }

  ngOnInit(): void {
    this.accountId = this.route.snapshot.paramMap.get('id');
    const type = this.route.snapshot.paramMap.get('type');
    
    if (type === 'credit' || type === 'debit') {
      this.operationType = type;
    }

    if (this.accountId) {
      this.loadAccountDetails(this.accountId);
    }
  }

  loadAccountDetails(accountId: string): void {
    this.loading = true;
    this.accountService.getAccountById(accountId)
      .subscribe({
        next: (account) => {
          this.account = account;
          this.loading = false;
        },
        error: (error) => {
          this.notificationService.showError('Failed to load account details');
          this.loading = false;
        }
      });
  }

  setOperationType(type: 'credit' | 'debit'): void {
    this.operationType = type;
  }

  onSubmit(): void {
    if (this.operationForm.invalid || !this.accountId) {
      this.markFormGroupTouched(this.operationForm);
      return;
    }

    this.loading = true;
    const request: OperationRequest = {
      accountId: this.accountId,
      amount: this.operationForm.value.amount,
      description: this.operationForm.value.description
    };

    const operation$ = this.operationType === 'credit'
      ? this.accountService.creditAccount(this.accountId, request)
      : this.accountService.debitAccount(this.accountId, request);

    operation$.subscribe({
      next: (response) => {
        if (response.success) {
          this.notificationService.showSuccess(
            `Account ${this.operationType === 'credit' ? 'credited' : 'debited'} successfully`
          );
          this.router.navigate(['/accounts', this.accountId]);
        } else {
          this.notificationService.showError(response.message || 'Operation failed');
        }
        this.loading = false;
      },
      error: (error) => {
        this.notificationService.showError(
          `Failed to ${this.operationType} account: ` + (error.message || 'Unknown error')
        );
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
    const field = this.operationForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched));
  }

  getErrorMessage(fieldName: string): string {
    const field = this.operationForm.get(fieldName);
    if (field?.hasError('required')) {
      return `${fieldName} is required`;
    }
    if (field?.hasError('min')) {
      return `${fieldName} must be at least ${field.errors?.['min'].min}`;
    }
    return '';
  }
}
