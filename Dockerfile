# Use OpenJDK as the base image
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the project files
COPY . .

# Build the project using Maven
RUN apt-get update && apt-get install -y maven && mvn clean package

# Expose the port Render expects (8080)
EXPOSE 8080

# Run the application
CMD ["java", "-cp", "target/info3150-1.0-SNAPSHOT.jar", "Main"]
