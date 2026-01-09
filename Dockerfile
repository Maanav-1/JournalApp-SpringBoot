# Stage 1: Build the application
# Use Maven to build the project
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy the pom.xml and all source code
COPY . .

# Package the application into a JAR file, skipping tests for speed
RUN mvn clean package -DskipTests

# Stage 2: Create the final runtime image
# Use a lightweight JRE image to keep the container small
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# Copy the generated JAR from the build stage to the runtime stage
COPY --from=build /app/target/*.jar app.jar

# Expose the default Spring Boot port
EXPOSE 8080

# Run the application
# Using -Xmx512m to stay within Render's free tier memory limits
# The server.port configuration ensures the app uses the port Render assigns
ENTRYPOINT ["java", "-Xmx512m", "-jar", "app.jar", "--server.port=${PORT:-8080}"]