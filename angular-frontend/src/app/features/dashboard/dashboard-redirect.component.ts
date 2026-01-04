import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';
import { LoaderComponent } from '../../shared/components/loader/loader.component';

@Component({
  selector: 'app-dashboard-redirect',
  standalone: true,
  imports: [CommonModule, LoaderComponent],
  template: `
    <app-loader></app-loader>
  `
})
export class DashboardRedirectComponent implements OnInit {
  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    // Rediriger vers le dashboard approprié selon le rôle
    this.authService.redirectToDashboard();
  }
}