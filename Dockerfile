FROM gradle:8.8-jdk17 AS build
WORKDIR /app
COPY build.gradle settings.gradle ./
COPY src ./src
RUN gradle bootJar --no-daemon -x test

FROM amazoncorretto:17-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]