# Auth Service

Authentication and Authorization microservice for **E-Bank 3.0**, based on **Keycloak**.

**Responsible**: Mouktadir

---

## Overview

This service delegates authentication and authorization to **Keycloak** (OIDC/OAuth2). It acts as a thin integration layer between E-Bank services, Keycloak, and Kafka for event-driven workflows.

---

## Features

* User authentication and authorization via **Keycloak**
* JWT access & refresh token management (issued by Keycloak)
* Token validation via Keycloak introspection / JWKS
* Logout via Keycloak session invalidation
* Role- and scope-based access control
* Kafka integration for auth-related domain events

> **Note**: Password management, hashing, MFA, and session management are handled by Keycloak.

---

## Architecture

* **Auth Service**: Spring Boot service acting as a gateway to Keycloak and Kafka
* **Keycloak**: Identity Provider (IdP)
* **Kafka**: Event bus for user lifecycle and compliance events

---

## API Endpoints

### Auth (Keycloak-backed)

* `POST /api/auth/login`

  * Delegates login to Keycloak (Resource Owner Password or Authorization Code flow)
  * Returns Keycloak-issued JWT tokens

* `POST /api/auth/refresh`

  * Refresh access token via Keycloak

* `POST /api/auth/logout`

  * Invalidates user session in Keycloak

* `GET /api/auth/validate`

  * Validates token using Keycloak (JWKS or introspection)

### User Management

> User registration and profile updates are handled **directly by Keycloak** (Admin API or Keycloak UI).

---

## Kafka Events

### Published Events

* `USER_REGISTERED`

  * Published after successful user creation in Keycloak

* `USER_LOGGED_OUT`

  * Published after logout/session invalidation

### Consumed Events

* `KYC_VERIFIED`

  * Updates user attributes or roles in Keycloak (e.g., `kyc_verified=true`)

* `KYC_REJECTED`

  * Updates user attributes or roles in Keycloak

---

## Keycloak Configuration

### Realm

* Realm name: `ebank`

### Clients

* Client ID: `auth-service`
* Client Type: Confidential
* Enabled flows:

  * Authorization Code
  * Refresh Token

### Roles

* `USER`
* `ADMIN`
* `KYC_VERIFIED`

### Token Settings

* Access token: JWT
* Public key exposed via JWKS

---

## Configuration via `application.yml`

All environment variables **must be declared and mapped in the `application.yml` file**. The service reads configuration from Spring profiles (`dev`, `prod`).

Example structure:

```yaml
spring:
  profiles:
    active: dev

keycloak:
  server-url: ${KEYCLOAK_SERVER_URL}
  realm: ${KEYCLOAK_REALM}
  client-id: ${KEYCLOAK_CLIENT_ID}
  client-secret: ${KEYCLOAK_CLIENT_SECRET}

spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: ${KEYCLOAK_SERVER_URL}/realms/${KEYCLOAK_REALM}

kafka:
  bootstrap-servers: ${KAFKA_BOOTSTRAP_SERVERS}
```

### Development (dev profile)

The following variables must be defined (e.g. `.env`, IDE, or Docker):

* `KEYCLOAK_SERVER_URL`: `http://localhost:8080`
* `KEYCLOAK_REALM`: `ebank`
* `KEYCLOAK_CLIENT_ID`: `auth-service`
* `KEYCLOAK_CLIENT_SECRET`: `dev-secret`
* `KAFKA_BOOTSTRAP_SERVERS`: `localhost:9092`

### Production (prod profile)

All variables are **required**:

* `KEYCLOAK_SERVER_URL`
* `KEYCLOAK_REALM`
* `KEYCLOAK_CLIENT_ID`
* `KEYCLOAK_CLIENT_SECRET`
* `KAFKA_BOOTSTRAP_SERVERS`

---

## Running Locally

```bash
# Start Keycloak (example with Docker)
docker run -p 8080:8080 \
  -e KEYCLOAK_ADMIN=admin \
  -e KEYCLOAK_ADMIN_PASSWORD=admin \
  quay.io/keycloak/keycloak:latest start-dev

# Run Auth Service
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

---

## TODOs (For the Responsible Developer)

### Keycloak & Security

* [ ] Create `ebank` realm in Keycloak
* [ ] Create `auth-service` confidential client
* [ ] Configure client secret and redirect URIs
* [ ] Define realm roles: `USER`, `ADMIN`, `KYC_VERIFIED`
* [ ] Configure token lifespan (access & refresh)

### Spring Boot Integration

* [ ] Configure Spring Security OAuth2 Resource Server (JWT)
* [ ] Map Keycloak roles to Spring Security authorities
* [ ] Implement `/login`, `/refresh`, `/logout` delegation to Keycloak
* [ ] Implement `/validate` using JWT verification

### Kafka Integration

* [ ] Publish `USER_REGISTERED` event after Keycloak user creation
* [ ] Publish `USER_LOGGED_OUT` event after logout
* [ ] Consume `KYC_VERIFIED` event and update user roles/attributes in Keycloak
* [ ] Consume `KYC_REJECTED` event and update Keycloak user state

### Configuration & DevOps

* [ ] Add all required variables to `application.yml`
* [ ] Support `dev` and `prod` Spring profiles
* [ ] Secure secrets using Vault / environment injection

### Testing & Docs

* [ ] Unit tests for security configuration
* [ ] Integration tests with Testcontainers (Keycloak + Kafka)
* [ ] Add OpenAPI / Swagger documentation
* [ ] Add README section explaining Keycloak flows
