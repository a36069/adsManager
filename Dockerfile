FROM eclipse-temurin:17-jdk-alpine

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

COPY target/adsManager.jar app.jar

RUN mkdir /app/reports && chown -R appuser:appgroup /app

USER appuser

EXPOSE 8093

ENTRYPOINT ["java", "-jar", "app.jar"]