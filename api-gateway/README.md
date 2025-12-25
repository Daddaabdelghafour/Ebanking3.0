# API Gateway

GraphQL API Gateway for E-Bank 3.0.

## Features

- GraphQL endpoint for frontend
- Keycloak integration for authentication
- Service aggregation and routing
- Single entry point for all microservices

## Endpoints

- `POST /graphql` - GraphQL endpoint
- `GET /graphiql` - GraphQL playground (dev only)

## Service Communication

The gateway communicates with backend microservices using:
- Service URLs configured per profile (dev/prod)

## Environment Variables

### Development (dev profile)
- `KEYCLOAK_REALM`: Keycloak realm (default: `ebank`)
- `KEYCLOAK_URL`: Keycloak server URL (default: `http://localhost:8180`)
- `KEYCLOAK_CLIENT_ID`: Keycloak client ID (default: `api-gateway`)
- `KEYCLOAK_CLIENT_SECRET`: Keycloak client secret

### Production (prod profile)
- `KEYCLOAK_REALM`: Keycloak realm (required)
- `KEYCLOAK_URL`: Keycloak server URL (required)
- `KEYCLOAK_CLIENT_ID`: Keycloak client ID (required)
- `KEYCLOAK_CLIENT_SECRET`: Keycloak client secret (required)

## Running Locally

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## TODOs

- [ ] Define GraphQL schema
- [ ] Implement Query resolvers
- [ ] Implement Mutation resolvers
- [ ] Create Feign clients for each microservice
- [ ] Configure Keycloak adapter
- [ ] Add error handling
- [ ] Add request/response logging
- [ ] Add rate limiting
- [ ] Add caching


