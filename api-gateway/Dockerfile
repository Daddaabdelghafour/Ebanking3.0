# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy root POM for dependency resolution
COPY ../pom.xml ./pom.xml
# Copy only the api-gateway module
COPY pom.xml ./api-gateway/pom.xml
COPY src ./api-gateway/src
RUN mvn -pl api-gateway -am clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/api-gateway/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
