import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AccountService } from '../../../core/services/account.service';
import { NotificationService } from '../../../core/services/notification.service';
import { Account, AccountStatus, PagedResponse } from '../../../core/models/account.model';

@Component({
  selector: 'app-accounts-list',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './accounts-list.component.html',
  styleUrls: ['./accounts-list.component.scss']
})
export class AccountsListComponent implements OnInit {
  accounts: Account[] = [];
  loading = false;
  
  // Pagination
  currentPage = 0;
  pageSize = 10;
  totalElements = 0;
  totalPages = 0;
  
  // Filter
  searchTerm = '';
  statusFilter: AccountStatus | 'ALL' = 'ALL';
  
  AccountStatus = AccountStatus;
  Math = Math;

  constructor(
    private accountService: AccountService,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.loadAccounts();
  }

  loadAccounts(): void {
    this.loading = true;
    this.accountService.getAllAccounts(this.currentPage, this.pageSize)
      .subscribe({
        next: (response) => {
          if (response.success && response.data) {
            this.accounts = response.data.content;
            this.totalElements = response.data.totalElements;
            this.totalPages = response.data.totalPages;
          }
          this.loading = false;
        },
        error: (error) => {
          this.notificationService.showError('Failed to load accounts');
          this.loading = false;
        }
      });
  }

  getFilteredAccounts(): Account[] {
    return this.accounts.filter(account => {
      const matchesSearch = !this.searchTerm || 
        account.accountNumber.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        account.email.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        account.iban.toLowerCase().includes(this.searchTerm.toLowerCase());
      
      const matchesStatus = this.statusFilter === 'ALL' || account.status === this.statusFilter;
      
      return matchesSearch && matchesStatus;
    });
  }

  onPageChange(page: number): void {
    this.currentPage = page;
    this.loadAccounts();
  }

  onPageSizeChange(): void {
    this.currentPage = 0;
    this.loadAccounts();
  }

  onFilterChange(): void {
    this.currentPage = 0;
    this.loadAccounts();
  }

  getStatusClass(status: AccountStatus): string {
    switch (status) {
      case AccountStatus.ACTIVATED:
        return 'badge bg-success';
      case AccountStatus.CREATED:
        return 'badge bg-info';
      case AccountStatus.SUSPENDED:
        return 'badge bg-warning';
      case AccountStatus.CLOSED:
        return 'badge bg-danger';
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
