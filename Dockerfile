FROM eclipse-temurin:17-jdk-alpine
VOLUME /schemes
COPY build/libs/*.jar app.jar
EXPOSE 8080
ENV BPM_SCHEME_VOLUME=/schemes
ENTRYPOINT ["java","-jar","/app.jar"]