FROM openjdk:8-jdk-alpine
#ARG JAR_FILE=build/libs/*.jar
#COPY ${JAR_FILE} app.jar
#ENTRYPOINT ["java","-jar","/app.jar"]

ARG JAR_FILE=gradlew
COPY ${JAR_FILE} gradlew
ENTRYPOINT ["/gradlew","run","-pProfile=prod"]