# ---------- BUILD STAGE ----------
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /build

# Pre-cache dependencies
COPY pom.xml .
COPY app-domain-model/pom.xml app-domain-model/pom.xml
COPY app-client-management/pom.xml app-client-management/pom.xml
COPY user-service/pom.xml user-service/pom.xml
COPY project-service/pom.xml project-service/pom.xml
COPY task-service/pom.xml task-service/pom.xml
COPY discovery-service/pom.xml discovery-service/pom.xml
COPY gateway-service/pom.xml gateway-service/pom.xml

RUN mvn -B -q dependency:go-offline

# Source copy
COPY . .

# Build only the requested module
ARG MODULE
RUN mvn -pl ${MODULE} -am -DskipTests package

# ---------- RUNTIME STAGE ----------
# Changed from 'alpine' to standard 'jre' to support Mac M1/M2/M3 & Windows
FROM eclipse-temurin:17-jre

WORKDIR /app

# Changed 'apk' to 'apt-get' because we are no longer on Alpine
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

ARG MODULE
COPY --from=build /build/${MODULE}/target/*.jar app.jar

ENV JAVA_OPTS="-Xms128m -Xmx512m"

EXPOSE 8080

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]