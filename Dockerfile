# =========================
# 🏗️ Stage 1: Build JAR
# =========================
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml terlebih dahulu agar dependency bisa di-cache
COPY pom.xml .

# Resolve dependencies (lebih aman daripada go-offline)
RUN mvn dependency:resolve -B || true

# Copy source code
COPY src ./src

# Build project tanpa test
RUN mvn clean package -DskipTests

# =========================
# 🚀 Stage 2: Run the app
# =========================
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
