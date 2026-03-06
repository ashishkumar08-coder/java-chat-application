# Step 1: Use a Java 17 runtime
FROM eclipse-temurin:17-jdk-alpine

# Step 2: Set the working directory inside the cloud container
WORKDIR /app

# Step 3: Copy the maven build file and the source code
COPY . .

# Step 4: Build the application (skipping tests for speed)
RUN ./mvnw clean package -DskipTests

# Step 5: Run the jar file
ENTRYPOINT ["java", "-jar", "target/chat-app-1.0-SNAPSHOT.jar"]

# Step 6: Tell the cloud to use port 8080
EXPOSE 8080