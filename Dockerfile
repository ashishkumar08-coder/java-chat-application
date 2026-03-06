# Step 1: Use a Java 17 runtime with Maven pre-installed
FROM maven:3.8.4-openjdk-17-slim AS build

# Step 2: Set the working directory
WORKDIR /app

# Step 3: Copy only the pom.xml first (this makes builds faster)
COPY pom.xml .

# Step 4: Copy the source code
COPY src ./src

# Step 5: Build the application using standard maven
RUN mvn clean package -DskipTests

# Step 6: Create the final lightweight runtime image
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/chat-app-1.0-SNAPSHOT.jar app.jar

# Step 7: Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
EXPOSE 8080