#!/bin/bash
set -e

echo "🔹 Running integration tests..."
./mvnw failsafe:integration-test failsafe:verify
