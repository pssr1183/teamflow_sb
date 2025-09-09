FROM openjdk:21-jdk-slim

WORKDIR /app

RUN apt-get update && apt-get install -y redis-tools  # Add this

COPY target/demo-0.0.1-SNAPSHOT.jar /app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app.jar"]