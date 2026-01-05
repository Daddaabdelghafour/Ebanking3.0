import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { NotificationService } from '../services/notification.service';
import { UserRole } from '../models/auth.model';

/**
 * Guard générique pour vérifier les rôles
 */
export function roleGuard(allowedRoles: (UserRole | string)[]): CanActivateFn {
  return (route, state) => {
    const authService = inject(AuthService);
    const router = inject(Router);
    const notificationService = inject(NotificationService);

    // Vérifier d'abord si l'utilisateur est authentifié
    if (!authService.isAuthenticated()) {
      router.navigate(['/auth/login'], { queryParams: { returnUrl: state.url } });
      return false;
    }

    // Vérifier si l'utilisateur a un des rôles autorisés
    if (authService.hasAnyRole(allowedRoles)) {
      return true;
    }

    // L'utilisateur n'a pas les droits nécessaires
    notificationService.showError('Accès refusé. Vous n\'avez pas les permissions nécessaires.');
    
    // Rediriger vers le dashboard approprié selon son rôle
    authService.redirectToDashboard();
    return false;
  };
}

// Guards spécifiques par rôle
export const adminGuard: CanActivateFn = roleGuard([UserRole.ADMIN]);
export const customerGuard: CanActivateFn = roleGuard([UserRole.CUSTOMER]);
export const agentBankGuard: CanActivateFn = roleGuard([UserRole.AGENT_BANK]);

// Guards combinés
export const adminOrAgentGuard: CanActivateFn = roleGuard([UserRole.ADMIN, UserRole.AGENT_BANK]);