# =========================
# 1️⃣ BUILD STAGE 🏗️
# =========================
FROM gradle:8.8-jdk21 AS build

WORKDIR /app

# Copy only build files first (for cache)
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle ./gradle

RUN gradle dependencies --no-daemon

# Copy source code
COPY . .

# Build jar
RUN gradle bootJar --no-daemon


# =========================
# 2️⃣ RUNTIME STAGE 🚀
# =========================
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expose Spring Boot port
EXPOSE 8080

# Run app
ENTRYPOINT ["java", "-jar", "app.jar"]
