# --- Build stage ---
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
# Cache dependencies separately from source for faster rebuilds
RUN mvn -q -B dependency:go-offline || true
COPY src ./src
RUN mvn -q -B clean package -DskipTests

# --- Run stage ---
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /build/target/tourism-backend.jar app.jar

# Render (and most PaaS) inject PORT; application.properties already reads
# server.port=${PORT:8080}. The Derby database is written under ./data,
# i.e. /app/data inside this image - see the hosting notes about
# persistence on free tiers.
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
