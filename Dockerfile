FROM openjdk:17

WORKDIR /app

ARG ARTIFACT=target/*.jar

COPY ${ARTIFACT} app.jar

ENTRYPOINT ["java","-jar", "app.jar"]