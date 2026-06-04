# Build stage
FROM gradle:8.10.2-jdk21 AS build
WORKDIR /home/gradle/src

# Copy ALL configuration files from root to satisfy version catalog and project structure
COPY gradle /home/gradle/src/gradle
COPY settings.gradle.kts /home/gradle/src/
COPY build.gradle.kts /home/gradle/src/
COPY gradle.properties /home/gradle/src/

# Copy the backend source
COPY backend /home/gradle/src/backend

# Build the backend specifically
RUN gradle :backend:bootJar --no-daemon

# Run stage
FROM eclipse-temurin:21-jre-alpine
EXPOSE 8080
COPY --from=build /home/gradle/src/backend/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
