FROM amazoncorretto:17-alpine-jdk
COPY target/*.jar statsapp.jar
ENTRYPOINT ["java","-jar","statsapp.jar"]