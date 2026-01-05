import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { TransactionService } from '../../../core/services/transaction.service';
import { NotificationService } from '../../../core/services/notification.service';
import { Transaction, TransactionStatus, PagedResponse } from '../../../core/models/account.model';

@Component({
  selector: 'app-transaction-history',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './transaction-history.component.html',
  styleUrls: ['./transaction-history.component.scss']
})
export class TransactionHistoryComponent implements OnInit {
  transactions: Transaction[] = [];
  loading = false;
  accountId: string | null = null;
  
  // Pagination
  currentPage = 0;
  pageSize = 10;
  totalElements = 0;
  totalPages = 0;
  
  // Filter
  statusFilter: TransactionStatus | 'ALL' = 'ALL';
  
  TransactionStatus = TransactionStatus;
  Math = Math;

  constructor(
    private transactionService: TransactionService,
    private notificationService: NotificationService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.accountId = this.route.snapshot.paramMap.get('id');
    if (this.accountId) {
      this.loadTransactions();
    }
  }

  loadTransactions(): void {
    if (!this.accountId) return;
    
    this.loading = true;
    this.transactionService.getTransactionsByAccountId(this.accountId, this.currentPage, this.pageSize)
      .subscribe({
        next: (response) => {
          if (response.success && response.data) {
            this.transactions = response.data.content;
            this.totalElements = response.data.totalElements;
            this.totalPages = response.data.totalPages;
          }
          this.loading = false;
        },
        error: (error) => {
          this.notificationService.showError('Failed to load transactions');
          this.loading = false;
        }
      });
  }

  getFilteredTransactions(): Transaction[] {
    return this.transactions.filter(transaction => {
      const matchesStatus = this.statusFilter === 'ALL' || transaction.status === this.statusFilter;
      return matchesStatus;
    });
  }

  onPageChange(page: number): void {
    this.currentPage = page;
    this.loadTransactions();
  }

  onPageSizeChange(): void {
    this.currentPage = 0;
    this.loadTransactions();
  }

  onFilterChange(): void {
    this.currentPage = 0;
    this.loadTransactions();
  }

  getStatusClass(status: TransactionStatus): string {
    switch (status) {
      case TransactionStatus.COMPLETED:
        return 'badge bg-success';
      case TransactionStatus.PENDING:
        return 'badge bg-warning';
      case TransactionStatus.OTP_SENT:
        return 'badge bg-info';
      case TransactionStatus.FAILED:
        return 'badge bg-danger';
      case TransactionStatus.CANCELLED:
        return 'badge bg-secondary';
      default:
        return 'badge bg-secondary';
    }
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
