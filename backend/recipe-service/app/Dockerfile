FROM openjdk:22-jdk-slim

WORKDIR /app

COPY pom.xml .
COPY src ./src

COPY target/fridgemate-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]
