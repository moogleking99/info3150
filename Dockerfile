FROM maven:3.8.5-openjdk-17-slim AS build

# Set working directory
WORKDIR /app

# Copy the Maven project files
COPY pom.xml .
COPY src ./src

# Build the Uber JAR inside the container
RUN mvn clean package -DskipTests

# ---- Production Stage ----
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the built JAR from the previous build stage
COPY --from=build /app/target/info3150-1.0-SNAPSHOT-shaded.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]

