FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .

RUN mvn -f pom.xml -pl config-server -am -DskipTests package \
    && JAR=$(ls -1 /app/config-server/target/*.jar | grep -v original | head -n 1) \
    && cp "$JAR" /app/app.jar

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/app.jar /app/app.jar
COPY --from=build /app/config-repo /app/config-repo

EXPOSE 8888

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]

