FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the compiled Uber JAR from the 'target' folder into the container
COPY target/info3150-1.0-SNAPSHOT-shaded.jar app.jar

# Expose the application port (Render usually expects 8080)
EXPOSE 8080

# Run the Uber JAR
CMD ["java", "-jar", "app.jar"]
