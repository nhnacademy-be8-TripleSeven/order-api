FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /app

COPY libs ./libs
RUN chmod 644 ./libs/toast-logncrash-logback-sdk-3.0.5.jar
RUN mvn install:install-file \
    -Dfile=/app/libs/toast-logncrash-logback-sdk-3.0.5.jar \
    -DgroupId=com.toast.java.logncrash \
    -DartifactId=logncrash-logback-sdk \
    -Dversion=3.0.5 \
    -Dpackaging=jar \

COPY pom.xml ./
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]