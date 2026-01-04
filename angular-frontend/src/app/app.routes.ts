import { Routes } from '@angular/router';
import { authGuard, noAuthGuard } from './core/guards/auth.guard';
import { adminGuard, customerGuard, agentBankGuard } from './core/guards/role.guard';

export const routes: Routes = [
  // Redirect root - sera géré par un composant de redirection
  {
    path: '',
    loadComponent: () => import('./features/dashboard/dashboard-redirect.component').then(m => m.DashboardRedirectComponent),
    canActivate: [authGuard]
  },

  // ==========================================
  // Auth Routes (Public)
  // ==========================================
  {
    path: 'auth',
    children: [
      {
        path: 'login',
        loadComponent: () => import('./features/auth/login/login.component').then(m => m.LoginComponent),
        canActivate: [noAuthGuard]
      },
      {
        path: 'register',
        loadComponent: () => import('./features/auth/register/register.component').then(m => m.RegisterComponent),
        canActivate: [noAuthGuard]
      },
      {
        path: 'verify-email',
        loadComponent: () => import('./features/auth/verify-email/verify-email.component').then(m => m.VerifyEmailComponent)
      },
      {
        path: 'forgot-password',
        loadComponent: () => import('./features/auth/forgot-password/forgot-password.component').then(m => m.ForgotPasswordComponent),
        canActivate: [noAuthGuard]
      },
      {
        path: 'reset-password',
        loadComponent: () => import('./features/auth/reset-password/reset-password.component').then(m => m.ResetPasswordComponent)
      }
    ]
  },

  // ==========================================
  // Customer Routes (CUSTOMER role)
  // ==========================================
  {
    path: 'customer',
    canActivate: [authGuard, customerGuard],
    children: [
      {
        path: 'dashboard',
        loadComponent: () => import('./features/dashboard/cutsomer-dashboard.component').then(m => m.CustomerDashboardComponent)
      },
      // {
      //   path: 'accounts',
      //   loadComponent: () => import('./features/customer/accounts/accounts.component').then(m => m.AccountsComponent)
      // },
      // {
      //   path: 'transfers',
      //   loadComponent: () => import('./features/customer/transfers/transfers.component').then(m => m.TransfersComponent)
      // },
      // {
      //   path: 'cards',
      //   loadComponent: () => import('./features/customer/cards/cards.component').then(m => m.CardsComponent)
      // },
      // {
      //   path: 'history',
      //   loadComponent: () => import('./features/customer/history/history.component').then(m => m.HistoryComponent)
      // },
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
      }
    ]
  },

  // ==========================================
  // Admin Routes (ADMIN role)
  // ==========================================
  {
    path: 'admin',
    canActivate: [authGuard, adminGuard],
    children: [
      {
        path: 'dashboard',
        loadComponent: () => import('./features/dashboard/admin-dashboard.component').then(m => m.AdminDashboardComponent)
      },
      // {
      //   path: 'users',
      //   loadComponent: () => import('./features/admin/users/users.component').then(m => m.UsersComponent)
      // },
      // {
      //   path: 'agents',
      //   loadComponent: () => import('./features/admin/agents/agents.component').then(m => m.AgentsComponent)
      // },
      // {
      //   path: 'transactions',
      //   loadComponent: () => import('./features/admin/transactions/transactions.component').then(m => m.TransactionsComponent)
      // },
      // {
      //   path: 'reports',
      //   loadComponent: () => import('./features/admin/reports/reports.component').then(m => m.ReportsComponent)
      // },
      // {
      //   path: 'settings',
      //   loadComponent: () => import('./features/admin/settings/settings.component').then(m => m.SettingsComponent)
      // },
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
      }
    ]
  },

  // ==========================================
  // Agent Bank Routes (AGENT-BANK role)
  // ==========================================
  {
    path: 'agent',
    canActivate: [authGuard, agentBankGuard],
    children: [
      {
        path: 'dashboard',
        loadComponent: () => import('./features/dashboard/agent-dashboard.component').then(m => m.AgentDashboardComponent)
      },
      // {
      //   path: 'customers',
      //   loadComponent: () => import('./features/agent/customers/customers.component').then(m => m.CustomersComponent)
      // },
      // {
      //   path: 'requests',
      //   loadComponent: () => import('./features/agent/requests/requests.component').then(m => m.RequestsComponent)
      // },
      // {
      //   path: 'operations',
      //   loadComponent: () => import('./features/agent/operations/operations.component').then(m => m.OperationsComponent)
      // },
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
      }
    ]
  },

  // ==========================================
  // Shared Protected Routes
  // ==========================================
  {
    path: 'profile',
    loadComponent: () => import('./features/profile/profile.component').then(m => m.ProfileComponent),
    canActivate: [authGuard]
  },

  // ==========================================
  // Legacy redirect (ancien dashboard)
  // ==========================================
  {
    path: 'dashboard',
    loadComponent: () => import('./features/dashboard/dashboard-redirect.component').then(m => m.DashboardRedirectComponent),
    canActivate: [authGuard]
  },

  // ==========================================
  // 404 - Wildcard route
  // ==========================================
  {
    path: '**',
    loadComponent: () => import('./features/not-found/not-found.component').then(m => m.NotFoundComponent)
  }
];