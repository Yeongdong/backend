#!/usr/bin/bash

source ~/zips/mysql_secret.sh

TARGET_PORT=8080
TARGET_PID=$(sudo lsof -ti :${TARGET_PORT})

echo "current running WAS's pid is ${TARGET_PID}"
echo "start updating WAS version"

if [ -z "${TARGET_PID}" ]; then
    echo "current WAS is not running"
fi

sudo kill ${TARGET_PID}

nohup java -Xms256m -Xmx512m \
        -XX:+HeapDumpOnOutOfMemoryError \
        -jar -Dserver.port=${TARGET_PORT} -Dspring.profiles.active=dev build/libs/spinlog-0.0.1-SNAPSHOT.jar \
        >> ../logs/was_out.log 2> ../logs/was_err.log < /dev/null &

pid=$!

echo $pid | sudo tee /sys/fs/cgroup/example/tasks/cgroup.procs

echo "Program is running with PID $pid and has been added to cgroup 'tasks'"