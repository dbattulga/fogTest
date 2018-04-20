#!/bin/bash

nohup bin/start-local.sh > /dev/null 2>&1 &
sleep 2

nohup bin/taskmanager.sh start > /dev/null 2>&1 &
sleep 2 

nohup bin/flink run examples/mypress/FlinkConsTest-0.0.1-SNAPSHOT-jar-with-dependencies.jar > /dev/null 2>&1 &
sleep 2

nohup bin/flink run examples/mypress/FlinkProdTest-0.0.1-SNAPSHOT-jar-with-dependencies.jar > /dev/null 2>&1 &
sleep 2
