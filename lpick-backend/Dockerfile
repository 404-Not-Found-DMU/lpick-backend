# 1단계: Build 단계
FROM gradle:8.13-jdk21 AS builder

COPY --chown=gradle:gradle . /app
WORKDIR /app

RUN gradle build -x test --no-daemon

# 2단계: 실행 단계
FROM eclipse-temurin:21-jdk-alpine

ARG JAR_FILE=build/libs/*.jar
COPY --from=builder /app/${JAR_FILE} app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app.jar"]
