import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-footer',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <footer class="footer">
      <div class="container">
        <div class="footer-content">
          <div class="footer-section">
            <h4>Ettijari Bank</h4>
            <p>Votre partenaire financier de confiance</p>
          </div>
          
          <div class="footer-section">
            <h4>Liens utiles</h4>
            <ul>
              <li><a routerLink="/about">À propos</a></li>
              <li><a routerLink="/contact">Contact</a></li>
              <li><a routerLink="/faq">FAQ</a></li>
            </ul>
          </div>
          
          <div class="footer-section">
            <h4>Légal</h4>
            <ul>
              <li><a routerLink="/terms">Conditions d'utilisation</a></li>
              <li><a routerLink="/privacy">Politique de confidentialité</a></li>
              <li><a routerLink="/security">Sécurité</a></li>
            </ul>
          </div>
          
          <div class="footer-section">
            <h4>Nous contacter</h4>
            <p>📞 +212 5XX-XXXXXX</p>
            <p>✉️ contact&#64;ettijariwafabank.ma</p>
          </div>
        </div>
        
        <div class="footer-bottom">
          <p>&copy; 2025 Ettijari Bank.Tous droits réservés.</p>
        </div>
      </div>
    </footer>
  `,
  styles: [`
    .footer {
      background: #212121;
      color: white;
      padding: 3rem 0 1rem;
      margin-top: 4rem;
    }

    .footer-content {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
      gap: 2rem;
      margin-bottom: 2rem;
    }

    .footer-section h4 {
      color: #00843D;
      margin-bottom: 1rem;
    }

    .footer-section p {
      color: rgba(255, 255, 255, 0.7);
      line-height: 1.6;
    }

    .footer-section ul {
      list-style: none;
      padding: 0;
    }

    .footer-section ul li {
      margin-bottom: 0.5rem;
    }

    .footer-section ul li a {
      color: rgba(255, 255, 255, 0.7);
      text-decoration: none;
      transition: color 0.3s ease;
    }

    .footer-section ul li a:hover {
      color: #00843D;
    }

    .footer-bottom {
      border-top: 1px solid rgba(255, 255, 255, 0.1);
      padding-top: 1rem;
      text-align: center;
    }

    .footer-bottom p {
      color: rgba(255, 255, 255, 0.5);
      font-size: 0.875rem;
    }
  `]
})
export class FooterComponent {}
