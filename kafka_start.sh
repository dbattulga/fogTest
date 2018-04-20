#!/bin/bash

nohup bin/zookeeper-server-start.sh config/zookeeper.properties > /dev/null 2>&1 &
sleep 2
nohup bin/kafka-server-start.sh config/server.properties > /dev/null 2>&1 &
sleep 2

nohup bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic testone > /dev/null 2>&1 &
sleep 2
