# Kubernetes Deployment

Kubernetes manifests for deploying E-Bank 3.0 microservices.

**Responsible**: Abdelghafour

## Prerequisites

- Kubernetes cluster
- kubectl configured
- Docker images pushed to registry

## Setup

1. Create namespace:
   ```bash
   kubectl apply -f namespace.yaml
   ```

2. Create ConfigMaps:
   ```bash
   kubectl apply -f configmaps.yaml
   ```

3. Create Secrets (use secrets.example.yaml as reference):
   ```bash
   # Create secrets using kubectl commands (DO NOT commit actual secrets)
   kubectl create secret generic auth-service-secrets \
     --from-literal=db-url='...' \
     --from-literal=db-username='...' \
     --from-literal=db-password='...' \
     --from-literal=jwt-secret='...' \
     -n ebank
   # Repeat for other services
   ```

4. Deploy services:
   ```bash
   kubectl apply -f auth-service/deployment.yaml
   kubectl apply -f user-service/deployment.yaml
   kubectl apply -f account-service/deployment.yaml
   kubectl apply -f payment-service/deployment.yaml
   kubectl apply -f notification-service/deployment.yaml
   kubectl apply -f api-gateway/deployment.yaml
   ```

## Service URLs

Services communicate via Kubernetes DNS:
- `auth-service:8081`
- `user-service:8082`
- `account-service:8083`
- `payment-service:8084`
- `notification-service:8085`
- `api-gateway:8080` (LoadBalancer - external access)

## TODOs

- [ ] Update ConfigMaps with actual cloud URLs
- [ ] Create actual secrets (DO NOT commit)
- [ ] Configure ingress for external access
- [ ] Set up monitoring and logging
- [ ] Configure resource limits
- [ ] Set up horizontal pod autoscaling
- [ ] Configure network policies

