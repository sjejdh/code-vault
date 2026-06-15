FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY target/code-vault-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
