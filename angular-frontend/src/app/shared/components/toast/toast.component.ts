// filepath: 
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { trigger, transition, style, animate } from '@angular/animations';
import { NotificationService, Notification } from '../../../core/services/notification.service';

@Component({
  selector: 'app-toast',
  standalone: true,
  imports: [CommonModule],
  animations: [
    trigger('slideIn', [
      transition(':enter', [
        style({ transform: 'translateX(400px)', opacity: 0 }),
        animate('300ms ease-out', style({ transform: 'translateX(0)', opacity: 1 }))
      ]),
      transition(':leave', [
        animate('300ms ease-in', style({ transform: 'translateX(400px)', opacity: 0 }))
      ])
    ])
  ],
  template: `
    <div class="toast-container">
      <div *ngFor="let notification of notifications" 
           class="toast toast-{{ notification.type }}"
           [@slideIn]>
        <div class="toast-icon">
          <svg *ngIf="notification.type === 'success'" width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
            <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
          </svg>
          <svg *ngIf="notification.type === 'error'" width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
            <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/>
          </svg>
          <svg *ngIf="notification.type === 'warning'" width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
            <path d="M1 21h22L12 2 1 21zm12-3h-2v-2h2v2zm0-4h-2v-4h2v4z"/>
          </svg>
          <svg *ngIf="notification.type === 'info'" width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
            <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-6h2v6zm0-8h-2V7h2v2z"/>
          </svg>
        </div>
        <div class="toast-message">{{ notification.message }}</div>
        <button class="toast-close" (click)="close(notification.id)">
          <svg width="16" height="16" viewBox="0 0 16 16" fill="currentColor">
            <path d="M4.646 4.646a.5.5 0 0 1 .708 0L8 7.293l2.646-2.647a.5.5 0 0 1 .708.708L8.707 8l2.647 2.646a.5.5 0 0 1-.708.708L8 8.707l-2.646 2.647a.5.5 0 0 1-.708-.708L7.293 8 4.646 5.354a.5.5 0 0 1 0-.708z"/>
          </svg>
        </button>
      </div>
    </div>
  `,
  styles: [`
    .toast-container {
      position: fixed;
      top: 80px;
      right: 20px;
      z-index: 9999;
      display: flex;
      flex-direction: column;
      gap: 12px;
    }

    .toast {
      display: flex;
      align-items: center;
      gap: 12px;
      min-width: 300px;
      padding: 16px;
      background: white;
      border-radius: 12px;
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
    }

    .toast-success {
      border-left: 4px solid #388E3C;
    }
    
    .toast-success .toast-icon {
      color: #388E3C;
    }

    .toast-error {
      border-left: 4px solid #D32F2F;
    }
    
    .toast-error .toast-icon {
      color: #D32F2F;
    }

    .toast-warning {
      border-left: 4px solid #F57C00;
    }
    
    .toast-warning .toast-icon {
      color: #F57C00;
    }

    .toast-info {
      border-left: 4px solid #1976D2;
    }
    
    .toast-info .toast-icon {
      color: #1976D2;
    }

    .toast-message {
      flex: 1;
      font-size: 0.95rem;
      color: #212121;
    }

    .toast-close {
      background: none;
      border: none;
      cursor: pointer;
      color: #757575;
      padding: 4px;
      display: flex;
      align-items: center;
      justify-content: center;
      border-radius: 4px;
      transition: all 0.3s ease;
    }

    .toast-close:hover {
      background: rgba(0, 0, 0, 0.05);
      color: #212121;
    }

    @media (max-width: 768px) {
      .toast-container {
        right: 10px;
        left: 10px;
      }

      .toast {
        min-width: auto;
      }
    }
  `]
})
export class ToastComponent implements OnInit {
  notifications: Notification[] = [];

  constructor(private notificationService: NotificationService) {}

  ngOnInit(): void {
    this.notificationService.notifications$.subscribe(notifications => {
      this.notifications = notifications;
    });
  }

  close(id: string): void {
    this.notificationService.remove(id);
  }
}