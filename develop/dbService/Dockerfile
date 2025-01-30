FROM maven:3.9.5-eclipse-temurin-21 AS build
WORKDIR /app
COPY ./pom.xml ./
COPY ./dbService/pom.xml ./dbService/
COPY ./dbService/src ./dbService/src/
COPY ./apiService/pom.xml ./apiService/
COPY ./apiService/src ./apiService/src/
COPY ./common-lib/pom.xml ./common-lib/
COPY ./common-lib/src ./common-lib/src/
RUN mvn clean install -DskipTests

FROM openjdk:21-jdk
WORKDIR /app
COPY --from=build /app/dbService/target/*.jar ./dbService.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "dbService.jar"]
