# ---------- BUILD STAGE ----------
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /build

# pom'ları önce al (cache)
COPY pom.xml .
COPY app-domain-model/pom.xml app-domain-model/pom.xml
COPY app-client-management/pom.xml app-client-management/pom.xml
COPY user-service/pom.xml user-service/pom.xml
COPY project-service/pom.xml project-service/pom.xml
COPY task-service/pom.xml task-service/pom.xml
COPY discovery-service/pom.xml discovery-service/pom.xml
COPY gateway-service/pom.xml gateway-service/pom.xml

RUN mvn -B -q dependency:go-offline

# source
COPY . .

# sadece istenen modülü build et
ARG MODULE
RUN mvn -pl ${MODULE} -am -DskipTests package

# ---------- RUNTIME STAGE ----------
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Install curl for healthcheck
RUN apk add --no-cache curl

ARG MODULE
COPY --from=build /build/${MODULE}/target/*.jar app.jar

ENV JAVA_OPTS="-Xms128m -Xmx512m"

EXPOSE 8080

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]

