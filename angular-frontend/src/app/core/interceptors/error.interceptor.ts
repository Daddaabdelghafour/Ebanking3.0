import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { NotificationService } from '../services/notification.service';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const notificationService = inject(NotificationService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      let errorMessage = 'Une erreur est survenue';
      let shouldShowNotification = true;

      // Ne pas traiter les erreurs des routes publiques d'authentification
      // Ces erreurs sont gérées par les composants eux-mêmes
      if (req.url.includes('/auth/public/')) {
        return throwError(() => error);
      }

      if (error.error instanceof ErrorEvent) {
        // Erreur côté client
        errorMessage = `Erreur: ${error.error.message}`;
      } else {
        // Erreur côté serveur
        const serverMessage = error.error?.message || '';
        
        switch (error.status) {
          case 401:
            errorMessage = 'Session expirée. Veuillez vous reconnecter.';
            localStorage.clear();
            router.navigate(['/auth/login']);
            break;
          case 403:
            errorMessage = serverMessage || 'Accès refusé.';
            break;
          case 404:
            errorMessage = 'Ressource non trouvée.';
            break;
          case 400:
            errorMessage = serverMessage || 'Requête invalide.';
            break;
          case 500:
            errorMessage = 'Erreur serveur. Veuillez réessayer plus tard.';
            break;
          default:
            errorMessage = serverMessage || 'Une erreur est survenue';
        }
      }

      if (shouldShowNotification) {
        notificationService.showError(errorMessage);
      }
      
      return throwError(() => error);
    })
  );
};