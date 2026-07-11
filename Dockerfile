# Stage 1: Runtime
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the built jar file from the target directory
COPY target/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
