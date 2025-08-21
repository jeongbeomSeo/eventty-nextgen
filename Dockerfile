FROM openjdk:17-jdk-slim
EXPOSE 8080
ARG JAR_FILE=/build/libs/eventty-nextgen-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} /eventty.jar

ENTRYPOINT ["java", "-jar", "/eventty.jar"]