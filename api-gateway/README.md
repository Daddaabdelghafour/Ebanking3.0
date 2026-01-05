# Étape 1 : Création de l'API Gateway

## 📁 Structure du dossier

Créez cette structure de dossiers :

```
api-gateway/
├── pom.xml
├── Dockerfile
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── example/
        │           └── gateway/
        │               ├── ApiGatewayApplication.java
        │               ├── config/
        │               │   ├── SecurityConfig.java
        │               │   └── KafkaConfig.java
        │               ├── filter/
        │               │   └── LoggingGlobalFilter.java
        │               ├── model/
        │               │   ├── GatewayEvent.java
        │               │   └── RequestLog.java
        │               └── service/
        │                   └── KafkaProducerService.java
        └── resources/
            └── application.yml
```

## 📝 Instructions

1. Créez le dossier `api-gateway`
2. Copiez chaque fichier dans le bon emplacement
3. Vérifiez que la structure correspond exactement
4. Nous compilerons après avoir créé tous les fichiers