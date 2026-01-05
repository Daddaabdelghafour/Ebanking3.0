# EBankingFront

This project was generated using [Angular CLI](https://github.com/angular/angular-cli) version 21.0.4.

## Development server

To start a local development server, run:

```bash
ng serve
```

Once the server is running, open your browser and navigate to `http://localhost:4200/`. The application will automatically reload whenever you modify any of the source files.

## Code scaffolding

Angular CLI includes powerful code scaffolding tools. To generate a new component, run:

```bash
ng generate component component-name
```

For a complete list of available schematics (such as `components`, `directives`, or `pipes`), run:

```bash
ng generate --help
```

## Building

To build the project run:

```bash
ng build
```

This will compile your project and store the build artifacts in the `dist/` directory. By default, the production build optimizes your application for performance and speed.

## Running unit tests

To execute unit tests with the [Vitest](https://vitest.dev/) test runner, use the following command:

```bash
ng test
```

## Running end-to-end tests

For end-to-end (e2e) testing, run:

```bash
ng e2e
```

Angular CLI does not come with an end-to-end testing framework by default. You can choose one that suits your needs.

## Additional Resources

For more information on using the Angular CLI, including detailed command references, visit the [Angular CLI Overview and Command Reference](https://angular.dev/tools/cli) page.



# 🏦 Ettijari Bank - Frontend Angular

Frontend moderne pour la plateforme bancaire Ettijari Bank.

## 🚀 Démarrage Rapide

### Prérequis

- Node.js 20+
- npm 10+
- Angular CLI 17+

### Installation

```bash
# Installer les dépendances
npm install

# Lancer en mode développement
npm start

# L'app sera disponible sur http://localhost:4200
```

### Build

```bash
# Build de développement
npm run build:dev

# Build de production
npm run build:prod
```

## 🐳 Docker

### Build l'image

```bash
# Development
npm run docker:build: dev

# Production
npm run docker:build
```

### Lancer avec Docker

```bash
# Avec docker-compose
npm run docker:compose:up

# Ou directement
npm run docker:run
```

## 📁 Structure du Projet

```
src/
├── app/
│   ├── core/              # Services, guards, interceptors
│   ├── features/          # Composants métier
│   ├── shared/            # Composants partagés
│   ├── app.component.ts   # Composant racine
│   ├── app.config.ts      # Configuration
│   └── app.routes.ts      # Routes
├── assets/                # Images, icons, etc.
├── environments/          # Configuration par environnement
└── styles. scss           # Styles globaux
```

## 🎨 Composants

### Pages d'Authentification
- Login
- Register
- Verify Email
- Forgot Password
- Reset Password

### Pages Protégées
- Dashboard
- Profile
- Transactions (à venir)

### Composants Partagés
- Header
- Footer
- Toast Notifications
- Loader

## 🔧 Configuration

### Environnements

Créez les fichiers de configuration : 

**environment.dev.ts**
```typescript
export const environment = {
  production:  false,
  apiUrl: 'http://localhost:8081/api'
};
```

**environment.prod.ts**
```typescript
export const environment = {
  production: true,
  apiUrl:  'https://api.ettijariwafabank.ma/api'
};
```

## 🧪 Tests

```bash
# Tests unitaires
npm test

# Tests avec coverage
npm run test:coverage
```

## 📦 Déploiement

### Avec Docker

```bash
# Build et push
docker build -t ettijari-frontend:1.0.0 .
docker push ettijari-frontend:1.0.0
```

### Avec Nginx

```bash
# Build l'app
npm run build: prod

# Copier dist/ vers votre serveur Nginx
scp -r dist/ettijari-bank-frontend user@server:/var/www/html/
```

## 🔐 Sécurité

- JWT tokens stockés dans localStorage
- Intercepteurs HTTP pour l'authentification
- Guards pour protéger les routes
- Headers de sécurité configurés dans Nginx

## 📝 License

Propriétaire - Ettijari Bank © 2025