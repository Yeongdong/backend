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

nohup java -Xmx512m \
        --add-opens java.base/java.lang=ALL-UNNAMED \
        -Dserver.port=${TARGET_PORT} \
        -javaagent:${SCOUTER_AGENT_DIR}/scouter.agent.jar \
        -Dscouter.config=${SCOUTER_AGENT_DIR}/conf/scouter.conf \
        -Duser.timezone=Asia/Seoul \
        -Dspring.profiles.active=dev \
        -jar build/libs/spinlog-0.0.1-SNAPSHOT.jar \
        >> ../logs/was_out.log 2> ../logs/was_err.log < /dev/null &


echo "finish updating WAS version"