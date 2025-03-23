# Start with a base image containing Java runtime
FROM amazoncorretto:20-alpine

# Create a directory
WORKDIR /app

# Copy the JAR file from the local machine to the image
COPY build/libs/library-Kotlin-0.0.1-SNAPSHOT.jar .

# Expose port 7777
EXPOSE 7777

# Run the jar file
CMD ["java", "-jar", "library-Kotlin-0.0.1-SNAPSHOT.jar"]
