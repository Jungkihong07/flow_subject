# Multi-stage build for Spring Boot application
FROM gradle:8.10.2-jdk21 AS build

# Set working directory
WORKDIR /app

# Copy gradle wrapper files first for better caching
COPY gradle gradle
COPY gradlew gradlew.bat
COPY build.gradle settings.gradle

# Copy source code
COPY src src

# Build the application (skip tests for faster build)
RUN ./gradlew clean build -x test

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

# Set working directory
WORKDIR /app

# Copy the built jar from build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expose port
EXPOSE 8080

# Set JVM options for Cloud Run
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
