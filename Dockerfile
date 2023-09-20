FROM eclipse-temurin:17-jdk-alpine
EXPOSE 8004
RUN mkdir /opt/commerceapps
ARG JAR_FILE=build/libs/orders-service-0.0.1.jar
COPY ${JAR_FILE} /opt/commerceapps/orders-service-0.0.1.jar
ENTRYPOINT ["java","-jar","/opt/commerceapps/orders-service-0.0.1.jar"]


# BUILD
# docker build -t orders-service .

# IMAGE built and in docker

# docker images
# REPOSITORY                    TAG             IMAGE ID       CREATED         SIZE
# orders-service             latest          6977a8e7c2a4   4 minutes ago   386MB

# NETWORK for MySQL and Container
# docker network create shipments-netw

# RUN the container
# docker run -p8004:8004 orders-service:latest

# INSPECT
# docker inspect message-server
# docker stop message-server
# docker rm message-server
# What's Next?
  #  View a summary of image vulnerabilities and recommendations → docker scout quickview

