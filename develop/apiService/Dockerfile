FROM maven:3.9.5-eclipse-temurin-21 AS build
WORKDIR /app
COPY ./pom.xml ./
COPY ./apiService/pom.xml ./apiService/
COPY ./apiService/src ./apiService/src/
COPY ./dbService/pom.xml ./dbService/
COPY ./dbService/src ./dbService/src/
COPY ./common-lib/pom.xml ./common-lib/
COPY ./common-lib/src ./common-lib/src/
RUN mvn clean install -DskipTests

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/apiService/target/*.jar ./apiService.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "apiService.jar"]
