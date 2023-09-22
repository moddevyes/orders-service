FROM eclipse-temurin:17-jdk-alpine
EXPOSE 8004
RUN mkdir /opt/commerceapps
ARG JAR_FILE=build/libs/orders-service-0.0.1.jar
COPY ${JAR_FILE} /opt/commerceapps/orders-service-0.0.1.jar
ENTRYPOINT ["java","-jar","/opt/commerceapps/orders-service-0.0.1.jar"]
