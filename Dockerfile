FROM maven:3.9.9-eclipse-temurin-21 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk
COPY --from=build /target/webSocket_app-0.0.1-SNAPSHOT.jar webSocket_app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "webSocket_app.jar"]