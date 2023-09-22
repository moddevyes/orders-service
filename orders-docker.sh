#!/bin/bash

# DATABASE
echo ""
echo "Creating ORDERS database."
echo ""

docker run --name orders_mysql_container -d -p 3304:3306 -e MYSQL_ROOT_PASSWORD_FILE=/run/secrets/mysql-root-password \
-e MYSQL_DATABASE=ecommerce_orders_db \
-e MYSQL_USER=davidking \
-e MYSQL_PASSWORD=davidking!! \
--env-file config/.env.dev -v ./secrets:/run/secrets --network commerce-net --restart unless-stopped mysql:8.0.1

# DOCKER IMAGE
echo ""
echo "Building DOCKER image for orders-service."
echo ""
docker build -t orders-service .

echo ""
echo "Deploying/Running DOCKER image for orders-service."
echo ""
docker run -d --env-file config/.env.dev --name=orders_service_container --net=commerce-net -p 8004:8004 --restart unless-stopped orders-service

# VERIFY
echo ""
echo "VERIFY deployment"
echo ""
docker ps
echo ""
echo ""
docker images
