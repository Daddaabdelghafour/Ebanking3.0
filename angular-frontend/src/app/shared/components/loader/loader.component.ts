import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-loader',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="loader-overlay">
      <div class="loader-content">
        <div class="loader"></div>
        <p>Chargement... </p>
      </div>
    </div>
  `,
  styles: [`
    .loader-overlay {
      position:  fixed;
      top: 0;
      left: 0;
      right: 0;
      bottom:  0;
      background: rgba(0, 0, 0, 0.5);
      display: flex;
      align-items:  center;
      justify-content:  center;
      z-index:  10000;
    }

    .loader-content {
      background:  white;
      padding: 3rem;
      border-radius:  16px;
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 1.5rem;
    }

    .loader {
      width: 50px;
      height: 50px;
      border: 5px solid #E8F5E9;
      border-top: 5px solid #00843D;
      border-radius: 50%;
      animation:  spin 1s linear infinite;
    }

    @keyframes spin {
      0% { transform:  rotate(0deg); }
      100% { transform: rotate(360deg); }
    }

    p {
      color: #212121;
      font-weight: 600;
      margin:  0;
    }
  `]
})
export class LoaderComponent {}
