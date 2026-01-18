#!/bin/bash
set -e

APP_CONTAINER="mini-authorizer"

echo "🔹 Checking if application container is running..."

if docker ps -a --format '{{.Names}}' | grep -q "^${APP_CONTAINER}$"; then
  echo "⚠️ Container ${APP_CONTAINER} already exists. Stopping and removing..."
  docker stop ${APP_CONTAINER}
  docker rm ${APP_CONTAINER}
else
  echo "✅ No existing application container found."
fi

echo "🔹 Building application..."
./mvnw clean package -DskipTests

echo "🔹 Building Docker image..."
docker build -t mini-authorizer:latest .

echo "🔹 Starting application (MySQL will remain running)..."
cd docker
docker compose up -d mini-authorizer
cd ..

echo "✅ Application is running"
echo "➡️ App: http://localhost:8080"
