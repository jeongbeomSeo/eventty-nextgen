FROM openjdk:17-jdk-slim
EXPOSE 8080
ARG JAR_FILE=/build/libs/eventty-nextgen-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} /eventty.jar

ARG PROFILE
ENV PROFILE_ENV=${PROFILE:-dev}

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=${PROFILE_ENV}", "/eventty.jar"]