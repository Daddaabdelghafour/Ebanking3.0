# Stage 1: Build
FROM eclipse-temurin:17-jdk-alpine as builder
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN apk add --no-cache maven
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8086

ENV JAVA_OPTS="-Xms256m -Xmx512m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]