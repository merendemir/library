FROM openjdk:17-jdk-slim AS build

COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN chmod +x mvnw
RUN ./mvnw dependency:resolve

COPY src src
RUN ./mvnw -Dspring.profiles.active=demo package

FROM openjdk:17-jdk-slim
WORKDIR library
COPY --from=build target/*.jar library-application.jar
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=demo", "library-application.jar"]
