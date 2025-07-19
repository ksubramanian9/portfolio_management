# Stage 0: build and test the application
FROM hseeberger/scala-sbt:11.0.18_1.9.6_2.13.12 as builder
WORKDIR /build
COPY . .
RUN sbt "project portfolioManagement" test && \
    sbt "project portfolioManagement" package

# Stage 1: run the packaged artifact
FROM openjdk:11-jre-slim as runtime
WORKDIR /app
COPY --from=builder /build/src/portfolio-management-service/target/scala-2.13/*.jar app.jar
CMD ["java", "-jar", "app.jar"]

