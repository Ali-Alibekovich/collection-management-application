# syntax=docker/dockerfile:1
# Build from the repository root: docker compose --profile full up --build

FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY common/pom.xml common/
COPY server/pom.xml server/
COPY client/pom.xml client/
COPY stress/pom.xml stress/
COPY common/src common/src
COPY server/src server/src
RUN mvn -q -pl server -am package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/server/target/collection-server-*.jar server.jar
EXPOSE 5555/udp
USER 1000
ENTRYPOINT ["java", "-jar", "server.jar"]
