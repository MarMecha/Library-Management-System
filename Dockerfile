# Stage 1: Build the Spring Boot application
FROM maven:3.9.16-eclipse-temurin-17-alpine AS build

WORKDIR /app

COPY pom.xml .

RUN mvn --batch-mode --no-transfer-progress dependency:go-offline

COPY src ./src

RUN mvn --batch-mode --no-transfer-progress clean package -DskipTests


# Stage 2: Run the packaged application
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring

COPY --from=build --chown=spring:spring /app/target/*.jar app.jar

USER spring:spring

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]