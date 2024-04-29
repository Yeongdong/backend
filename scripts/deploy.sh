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

nohup java -jar -Dserver.port=${TARGET_PORT} -Dspring.profiles.active=dev build/libs/spinlog-0.0.1-SNAPSHOT.jar >> ../logs/was_out.log 2> ../logs/was_err.log < /dev/null &

echo "finish updating WAS version"