#!/bin/bash

echo "Creating Kafka topic: click-logs"

# Wait for Kafka to be ready
echo "Waiting for Kafka to be ready..."
sleep 10

# Create topic
docker exec kafka kafka-topics --create \
  --topic click-logs \
  --bootstrap-server localhost:9092 \
  --partitions 2 \
  --replication-factor 1

echo "Topic 'click-logs' created successfully!"

# List topics to verify
echo "Available topics:"
docker exec kafka kafka-topics --list --bootstrap-server localhost:9092