# Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-jammy

ENV SPRING_PROFILES_ACTIVE=prod \
    TZ=Europe/Paris \
    SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/supplier_orders \
    SPRING_DATASOURCE_USERNAME=app_user \
    SPRING_DATASOURCE_PASSWORD=app_password \
    SPRING_JPA_HIBERNATE_DDL_AUTO=update \
    SPRING_JPA_SHOW_SQL=false \
    SERVER_PORT=8080

RUN addgroup --system --gid 1001 appuser && \
    adduser --system --uid 1001 --gid 1001 appuser

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

RUN mkdir -p /app/uploads && \
    chown -R appuser:appuser /app && \
    chmod -R 755 /app

USER 1001

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]