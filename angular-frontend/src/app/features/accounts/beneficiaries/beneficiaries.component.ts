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
  beneficiaryForm: FormGroup;
  accountId: string | null = null;
  errorMessage: string | null = null;
  isEditing = false;
  deletingId: string | null = null;

  constructor(
    private fb: FormBuilder,
    private transactionService: TransactionService,
    private notificationService: NotificationService,
    private route: ActivatedRoute
  ) {
    this.beneficiaryForm = this.fb.group({
      beneficiaryName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
      beneficiaryRib: ['', [
        Validators.required,
        Validators.pattern(/^[A-Z0-9]{24,28}$/)
      ]]
    });
  }

  ngOnInit(): void {
    this.accountId = this.route.snapshot.paramMap.get('id');
    if (this.accountId) {
      this.loadBeneficiaries();
    }
    
    // Auto-format RIB to uppercase
    this.beneficiaryForm.get('beneficiaryRib')?.valueChanges.subscribe(value => {
      if (value && typeof value === 'string') {
        const upperValue = value.toUpperCase().replace(/[^A-Z0-9]/g, '');
        if (upperValue !== value) {
          this.beneficiaryForm.get('beneficiaryRib')?.setValue(upperValue, { emitEvent: false });
        }
      }
    });
  }

  loadBeneficiaries(): void {
    if (!this.accountId) return;

    this.loading = true;
    this.errorMessage = null;
    this.transactionService.getBeneficiariesByAccountId(this.accountId)
      .subscribe({
        next: (response) => {
          if (response.success && response.data) {
            this.beneficiaries = response.data;
            this.errorMessage = null;
          } else {
            this.beneficiaries = [];
            this.errorMessage = response.message || 'Failed to load beneficiaries';
          }
          this.loading = false;
        },
        error: (error) => {
          console.error('Error loading beneficiaries:', error);
          this.beneficiaries = [];
          this.loading = false;
          
          if (error.status === 404) {
            // 404 means no beneficiaries found yet - this is okay
            this.errorMessage = null;
          } else if (error.status === 0) {
            this.errorMessage = 'Cannot connect to server. Please check if the backend is running.';
          } else {
            this.errorMessage = error.error?.message || 'Failed to load beneficiaries. Please try again.';
          }
        }
      });
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
            this.notificationService.showSuccess('Bénéficiaire ajouté avec succès');
            this.loadBeneficiaries();
            this.beneficiaryForm.reset();
          } else {
            this.notificationService.showError(response.message);
          }
        },
        error: (error) => {
          this.notificationService.showError(error.error?.message || 'Échec de l\'ajout du bénéficiaire');
        }
      });
  }

  confirmDelete(beneficiary: Beneficiary): void {
    if (confirm(`Êtes-vous sûr de vouloir supprimer ${beneficiary.beneficiaryName}?\n\nCette action est irréversible.`)) {
      this.deleteBeneficiary(beneficiary);
    }
  }

  deleteBeneficiary(beneficiary: Beneficiary): void {
    if (!this.accountId) return;

    this.deletingId = beneficiary.id;
    
    this.transactionService.deleteBeneficiary(beneficiary.id, this.accountId)
      .subscribe({
        next: (response) => {
          if (response.success) {
            this.notificationService.showSuccess('Bénéficiaire supprimé avec succès');
            this.loadBeneficiaries();
          } else {
            this.notificationService.showError(response.message);
          }
          this.deletingId = null;
        },
        error: (error) => {
          this.notificationService.showError(error.error?.message || 'Échec de la suppression');
          this.deletingId = null;
        }
      });
  }

  cancelEdit(): void {
    this.isEditing = false;
    this.beneficiaryForm.reset();
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
      if (fieldName === 'beneficiaryName') {
        return 'Beneficiary name is required';
      }
      if (fieldName === 'beneficiaryRib') {
        return 'RIB/IBAN is required';
      }
      return `${fieldName} is required`;
    }
    if (field?.hasError('minlength')) {
      return `Must be at least ${field.errors?.['minlength'].requiredLength} characters`;
    }
    if (field?.hasError('maxlength')) {
      return `Cannot exceed ${field.errors?.['maxlength'].requiredLength} characters`;
    }
    if (field?.hasError('pattern')) {
      if (fieldName === 'beneficiaryRib') {
        return 'RIB must be 24 digits or valid IBAN format (uppercase letters and numbers only)';
      }
    }
    return '';
  }

  formatRib(rib: string): string {
    if (!rib) return '';
    // Format RIB with spaces every 4 characters for better readability
    return rib.match(/.{1,4}/g)?.join(' ') || rib;
  }
}
