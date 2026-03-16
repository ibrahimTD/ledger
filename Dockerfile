FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

COPY mvnw ./
COPY .mvn .mvn

RUN chmod +x ./mvnw

COPY pom.xml ./

RUN ./mvnw dependency:go-offline -B --no-transfer-progress

COPY src ./src

RUN ./mvnw package -B --no-transfer-progress -DskipTests


FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

