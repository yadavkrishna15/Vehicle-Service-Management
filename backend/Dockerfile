# Multi-stage Docker build for Java 17 Spring Boot Backend
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Copy pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build JAR package skipping unit tests
RUN mvn clean package -DskipTests

# Run stage using lightweight JRE
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy executable jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose default Spring Boot port
EXPOSE 8080

# Run the backend
ENTRYPOINT ["java", "-jar", "app.jar"]
