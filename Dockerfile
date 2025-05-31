FROM openjdk:17-jdk-slim

WORKDIR /app

COPY build/libs/*.jar app.jar

COPY src/main/resources/application.yml ./application.yml

EXPOSE 8080

CMD ["java", "-jar", "app.jar", "--spring.config.location=classpath:/,file:./application.yml"]
