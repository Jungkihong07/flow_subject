# Multi-stage build for Spring Boot application
FROM gradle:8.10.2-jdk21 AS build

# Set working directory
WORKDIR /app

# Copy all files
COPY . .

# Make gradlew executable
RUN chmod +x ./gradlew

# Build the application (skip tests for faster build)
RUN ./gradlew clean build -x test

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

# Set working directory
WORKDIR /app

# Copy the built jar from build stage
COPY --from=build /app/build/libs/extension-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8080

# Set JVM options for Cloud Run
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
