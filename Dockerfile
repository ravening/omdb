FROM openjdk:17-jdk-slim
ARG JAR_FILE=target/omdb-application.jar
COPY ${JAR_FILE} application.jar
ENTRYPOINT ["java", "-jar", "application.jar"]
