FROM eclipse-temurin:17-jre
WORKDIR /app
LABEL org.opencontainers.image.title="user-service"
COPY target/argo-java-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENV APP_MESSAGE="Hello from user-service"
ENTRYPOINT ["java", "-jar", "app.jar"]
