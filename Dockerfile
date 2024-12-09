# Use an official Maven image with JDK 23
FROM maven:3.8.6-openjdk-23

# Set the working directory in the container
WORKDIR /app

# Copy the pom.xml and source code into the container
COPY pom.xml /app
COPY src /app/src

# Build the project
RUN mvn clean install

# Set the entry point to run the application
ENTRYPOINT ["java", "-cp", "target/PromptEngineering-1.0-SNAPSHOT.jar", "org.Plucky.Main"]