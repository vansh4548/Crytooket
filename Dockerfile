# Use OpenJDK 24 base image
FROM openjdk:24-jdk-slim

# Set working directory
WORKDIR /app

# Copy Maven wrapper & project files
COPY . .

# Ensure mvnw is executable
RUN chmod +x mvnw

# Build the Spring Boot JAR
RUN ./mvnw clean package -DskipTests

# Expose port 8080 (default Spring Boot port)
EXPOSE 8080

# Run the JAR
CMD ["java", "-jar", "target/crytoket-backend-0.0.1-SNAPSHOT.jar"]
