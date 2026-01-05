import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AccountService } from '../../../core/services/account.service';
import { NotificationService } from '../../../core/services/notification.service';
import { Account, Operation, AccountStatus, PagedResponse } from '../../../core/models/account.model';

@Component({
  selector: 'app-account-details',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './account-details.component.html',
  styleUrls: ['./account-details.component.scss']
})
export class AccountDetailsComponent implements OnInit {
  account: Account | null = null;
  operations: Operation[] = [];
  loading = false;
  loadingOperations = false;
  
  // Pagination for operations
  currentPage = 0;
  pageSize = 10;
  totalElements = 0;
  totalPages = 0;
  
  // Modal state
  showStatusModal = false;
  showDeleteModal = false;
  selectedStatus: AccountStatus = AccountStatus.ACTIVATED;
  
  AccountStatus = AccountStatus;

  constructor(
    private accountService: AccountService,
    private notificationService: NotificationService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    const accountId = this.route.snapshot.paramMap.get('id');
    if (accountId) {
      this.loadAccountDetails(accountId);
      this.loadOperations(accountId);
    }
  }

  loadAccountDetails(accountId: string): void {
    this.loading = true;
    this.accountService.getAccountById(accountId)
      .subscribe({
        next: (account) => {
          this.account = account;
          this.selectedStatus = account.status;
          this.loading = false;
        },
        error: (error) => {
          this.notificationService.showError('Failed to load account details');
          this.loading = false;
        }
      });
  }

  loadOperations(accountId: string): void {
    this.loadingOperations = true;
    this.accountService.getAccountOperations(accountId, this.currentPage, this.pageSize)
      .subscribe({
        next: (response) => {
          this.operations = response.content;
          this.totalElements = response.totalElements;
          this.totalPages = response.totalPages;
          this.loadingOperations = false;
        },
        error: (error) => {
          this.notificationService.showError('Failed to load operations');
          this.loadingOperations = false;
        }
      });
  }

  onPageChange(page: number): void {
    if (!this.account) return;
    this.currentPage = page;
    this.loadOperations(this.account.id);
  }

  openStatusModal(): void {
    this.showStatusModal = true;
  }

  closeStatusModal(): void {
    this.showStatusModal = false;
  }

  updateStatus(): void {
    if (!this.account) return;
    
    this.accountService.updateAccountStatus(this.account.id, {
      accountStatus: this.selectedStatus
    }).subscribe({
      next: (response) => {
        if (response.success) {
          this.notificationService.showSuccess('Status updated successfully');
          this.loadAccountDetails(this.account!.id);
          this.closeStatusModal();
        } else {
          this.notificationService.showError(response.message || 'Failed to update status');
        }
      },
      error: (error) => {
        this.notificationService.showError('Failed to update account status: ' + (error.message || 'Unknown error'));
      }
    });
  }

  openDeleteModal(): void {
    this.showDeleteModal = true;
  }

  closeDeleteModal(): void {
    this.showDeleteModal = false;
  }

  deleteAccount(): void {
    if (!this.account) return;
    
    this.accountService.deleteAccount(this.account.id)
      .subscribe({
        next: (response) => {
          if (response.success) {
            this.notificationService.showSuccess('Account deleted successfully');
            this.router.navigate(['/accounts']);
          } else {
            this.notificationService.showError(response.message || 'Failed to delete account');
          }
          this.closeDeleteModal();
        },
        error: (error) => {
          this.notificationService.showError('Failed to delete account: ' + (error.message || 'Unknown error'));
          this.closeDeleteModal();
        }
      });
  }

  getStatusClass(status: AccountStatus): string {
    switch (status) {
      case AccountStatus.ACTIVATED:
        return 'badge bg-success';
      case AccountStatus.CREATED:
        return 'badge bg-info';
      case AccountStatus.SUSPENDED:
        return 'badge bg-warning';
      case AccountStatus.DELETED:
        return 'badge bg-danger';
      default:
        return 'badge bg-secondary';
    }
  }

  getOperationClass(type: string): string {
    return type === 'CREDIT' ? 'text-success' : 'text-danger';
  }

  formatCurrency(amount: number): string {
    return new Intl.NumberFormat('fr-MA', {
      style: 'currency',
      currency: 'MAD'
    }).format(amount);
  }

  get pageNumbers(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i);
  }
}
