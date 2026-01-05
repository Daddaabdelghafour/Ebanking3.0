import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { TransactionService } from '../../../core/services/transaction.service';
import { NotificationService } from '../../../core/services/notification.service';
import { Beneficiary, AddBeneficiaryRequest } from '../../../core/models/account.model';

@Component({
  selector: 'app-beneficiaries',
  standalone: true,
  imports: [CommonModule, RouterLink, ReactiveFormsModule],
  templateUrl: './beneficiaries.component.html',
  styleUrls: ['./beneficiaries.component.scss']
})
export class BeneficiariesComponent implements OnInit {
  beneficiaries: Beneficiary[] = [];
  loading = false;
  showAddModal = false;
  showDeleteModal = false;
  beneficiaryForm: FormGroup;
  selectedBeneficiary: Beneficiary | null = null;
  accountId: string | null = null;

  constructor(
    private fb: FormBuilder,
    private transactionService: TransactionService,
    private notificationService: NotificationService,
    private route: ActivatedRoute
  ) {
    this.beneficiaryForm = this.fb.group({
      beneficiaryName: ['', [Validators.required]],
      beneficiaryRib: ['', [Validators.required, Validators.minLength(24)]]
    });
  }

  ngOnInit(): void {
    this.accountId = this.route.snapshot.paramMap.get('id');
    if (this.accountId) {
      this.loadBeneficiaries();
    }
  }

  loadBeneficiaries(): void {
    if (!this.accountId) return;
    
    this.loading = true;
    this.transactionService.getBeneficiariesByAccountId(this.accountId)
      .subscribe({
        next: (response) => {
          if (response.success && response.data) {
            this.beneficiaries = response.data;
          }
          this.loading = false;
        },
        error: (error) => {
          this.notificationService.showError('Failed to load beneficiaries');
          this.loading = false;
        }
      });
  }

  openAddModal(): void {
    this.beneficiaryForm.reset();
    this.showAddModal = true;
  }

  closeAddModal(): void {
    this.showAddModal = false;
  }

  addBeneficiary(): void {
    if (this.beneficiaryForm.invalid || !this.accountId) {
      this.markFormGroupTouched(this.beneficiaryForm);
      return;
    }

    const request: AddBeneficiaryRequest = {
      accountId: this.accountId,
      ...this.beneficiaryForm.value
    };

    this.transactionService.addBeneficiary(request)
      .subscribe({
        next: (response) => {
          if (response.success) {
            this.notificationService.showSuccess('Beneficiary added successfully');
            this.loadBeneficiaries();
            this.closeAddModal();
          } else {
            this.notificationService.showError(response.message);
          }
        },
        error: (error) => {
          this.notificationService.showError('Failed to add beneficiary');
        }
      });
  }

  openDeleteModal(beneficiary: Beneficiary): void {
    this.selectedBeneficiary = beneficiary;
    this.showDeleteModal = true;
  }

  closeDeleteModal(): void {
    this.showDeleteModal = false;
    this.selectedBeneficiary = null;
  }

  deleteBeneficiary(): void {
    if (!this.selectedBeneficiary || !this.accountId) return;

    this.transactionService.deleteBeneficiary(this.selectedBeneficiary.id, this.accountId)
      .subscribe({
        next: (response) => {
          if (response.success) {
            this.notificationService.showSuccess('Beneficiary deleted successfully');
            this.loadBeneficiaries();
          } else {
            this.notificationService.showError(response.message);
          }
          this.closeDeleteModal();
        },
        error: (error) => {
          this.notificationService.showError('Failed to delete beneficiary');
          this.closeDeleteModal();
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
    const field = this.beneficiaryForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched));
  }

  getErrorMessage(fieldName: string): string {
    const field = this.beneficiaryForm.get(fieldName);
    if (field?.hasError('required')) {
      return `${fieldName} is required`;
    }
    if (field?.hasError('minlength')) {
      return `${fieldName} must be at least ${field.errors?.['minlength'].requiredLength} characters`;
    }
    return '';
  }
}
