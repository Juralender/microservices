# ---- deps ----
FROM maven:3.9-eclipse-temurin-21 AS deps
WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:go-offline

# ---- dev ----
FROM deps AS dev
WORKDIR /app
COPY . .
CMD ["mvn", "-B", "spring-boot:run"]

# ---- build ----
FROM deps AS build
WORKDIR /app
COPY . .
RUN mvn -B package -DskipTests

# ---- runtime ----
FROM eclipse-temurin:21-jre-alpine AS runtime
RUN addgroup -S app && adduser -S app -G app
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
USER app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
