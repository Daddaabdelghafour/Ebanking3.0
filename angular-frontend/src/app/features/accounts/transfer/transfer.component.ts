import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, ActivatedRoute } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { TransactionService } from '../../../core/services/transaction.service';
import { NotificationService } from '../../../core/services/notification.service';
import { AccountService } from '../../../core/services/account.service';
import { 
  InitiateTransactionRequest, 
  ConfirmTransactionRequest, 
  Transaction,
  Beneficiary 
} from '../../../core/models/account.model';

@Component({
  selector: 'app-transfer',
  standalone: true,
  imports: [CommonModule, RouterLink, ReactiveFormsModule],
  templateUrl: './transfer.component.html',
  styleUrls: ['./transfer.component.scss']
})
export class TransferComponent implements OnInit {
  transferForm: FormGroup;
  otpForm: FormGroup;
  loading = false;
  showOtpModal = false;
  currentTransaction: Transaction | null = null;
  beneficiaries: Beneficiary[] = [];
  sourceAccountId: string | null = null;

  constructor(
    private fb: FormBuilder,
    private transactionService: TransactionService,
    private accountService: AccountService,
    private notificationService: NotificationService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.transferForm = this.fb.group({
      sourceAccountId: ['', [Validators.required]],
      destinationAccountId: ['', [Validators.required]],
      amount: [0, [Validators.required, Validators.min(0.01)]],
      description: ['']
    });

    this.otpForm = this.fb.group({
      otp: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(6)]]
    });
  }

  ngOnInit(): void {
    // Get source account ID from route params if available
    this.sourceAccountId = this.route.snapshot.paramMap.get('id');
    if (this.sourceAccountId) {
      this.transferForm.patchValue({ sourceAccountId: this.sourceAccountId });
      this.loadBeneficiaries(this.sourceAccountId);
    }
  }

  loadBeneficiaries(accountId: string): void {
    this.transactionService.getBeneficiariesByAccountId(accountId)
      .subscribe({
        next: (response) => {
          if (response.success && response.data) {
            this.beneficiaries = response.data;
          } else {
            this.beneficiaries = [];
          }
        },
        error: (error) => {
          if (error.status === 404) {
            this.beneficiaries = [];
          } else {
            console.error('Failed to load beneficiaries', error);
          }
        }
      });
  }

  onSourceAccountChange(): void {
    const accountId = this.transferForm.get('sourceAccountId')?.value;
    if (accountId) {
      this.loadBeneficiaries(accountId);
    }
  }

  selectBeneficiary(beneficiary: Beneficiary): void {
    // Extract account ID from RIB (this is a simplified example)
    // In a real scenario, you might need to look up the account by RIB
    this.transferForm.patchValue({
      destinationAccountId: beneficiary.rib
    });
  }

  onSubmit(): void {
    if (this.transferForm.invalid) {
      this.markFormGroupTouched(this.transferForm);
      return;
    }

    this.loading = true;
    const request: InitiateTransactionRequest = this.transferForm.value;

    this.transactionService.initiateTransaction(request)
      .subscribe({
        next: (response) => {
          if (response.success && response.data) {
            this.currentTransaction = response.data;
            this.notificationService.showSuccess(response.message);
            this.showOtpModal = true;
          } else {
            this.notificationService.showError(response.message);
          }
          this.loading = false;
        },
        error: (error) => {
          this.notificationService.showError('Failed to initiate transfer');
          this.loading = false;
        }
      });
  }

  confirmTransfer(): void {
    if (this.otpForm.invalid || !this.currentTransaction) {
      this.markFormGroupTouched(this.otpForm);
      return;
    }

    this.loading = true;
    const request: ConfirmTransactionRequest = {
      transactionId: this.currentTransaction.id,
      otp: this.otpForm.value.otp
    };

    this.transactionService.confirmTransaction(request)
      .subscribe({
        next: (response) => {
          if (response.success) {
            this.notificationService.showSuccess('Transfer completed successfully');
            this.closeOtpModal();
            this.router.navigate(['/transactions']);
          } else {
            this.notificationService.showError(response.message);
          }
          this.loading = false;
        },
        error: (error) => {
          this.notificationService.showError('Failed to confirm transfer');
          this.loading = false;
        }
      });
  }

  closeOtpModal(): void {
    this.showOtpModal = false;
    this.otpForm.reset();
    this.currentTransaction = null;
  }

  private markFormGroupTouched(formGroup: FormGroup): void {
    Object.keys(formGroup.controls).forEach(key => {
      const control = formGroup.get(key);
      control?.markAsTouched();
    });
  }

  isFieldInvalid(formGroup: FormGroup, fieldName: string): boolean {
    const field = formGroup.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched));
  }

  getErrorMessage(formGroup: FormGroup, fieldName: string): string {
    const field = formGroup.get(fieldName);
    if (field?.hasError('required')) {
      return `${fieldName} is required`;
    }
    if (field?.hasError('min')) {
      return `${fieldName} must be at least ${field.errors?.['min'].min}`;
    }
    if (field?.hasError('minlength')) {
      return `${fieldName} must be at least ${field.errors?.['minlength'].requiredLength} characters`;
    }
    if (field?.hasError('maxlength')) {
      return `${fieldName} must be at most ${field.errors?.['maxlength'].requiredLength} characters`;
    }
    return '';
  }

  formatCurrency(amount: number): string {
    return new Intl.NumberFormat('fr-MA', {
      style: 'currency',
      currency: 'MAD'
    }).format(amount);
  }
}
