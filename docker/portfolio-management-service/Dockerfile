# Stage 0: build and test the application
FROM maven:3.9.6-eclipse-temurin-17 as builder
WORKDIR /build
COPY pom.xml .
RUN mvn -q dependency:resolve
COPY src src
RUN mvn -q test package

# Stage 1: run the packaged artifact
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /build/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
