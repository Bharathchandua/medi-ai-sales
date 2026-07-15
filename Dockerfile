# 1. Start with a base image containing Java 17 (The environment)
FROM eclipse-temurin:17-jdk-alpine

# 2. Set the working directory inside the container
WORKDIR /app

# 3. Copy our compiled JAR file into the container and rename it to 'app.jar'
COPY target/medi-ai-sales-0.0.1-SNAPSHOT.jar app.jar

# 4. Expose the port our app runs on (8080)
EXPOSE 8080

# 5. The command to run when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]