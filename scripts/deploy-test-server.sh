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
        -Dcom.sun.management.jmxremote=true \
        -Djava.rmi.server.hostname=3.38.58.4 \
        -Dcom.sun.management.jmxremote.port=9082 \
        -Dcom.sun.management.jmxremote.rmi.port=9082 \
        -Dcom.sun.management.jmxremote.password.file=/home/ubuntu/zips/scripts/jmx/jmx.password \
        -Dcom.sun.management.jmxremote.access.file=/home/ubuntu/zips/scripts/jmx/jmx.access \
        -Dcom.sun.management.jmxremote.ssl=false \
        -Dcom.sun.management.jmxremote.local.only=false \
        -javaagent:${SCOUTER_AGENT_DIR}/scouter.agent.jar \
        -Dscouter.config=${SCOUTER_AGENT_DIR}/conf/scouter.conf \
        -Duser.timezone=Asia/Seoul \
        -Dspring.profiles.active=dev \
        -jar build/libs/spinlog-0.0.1-SNAPSHOT.jar \
        >> /home/ubuntu/logs/was_out.log 2> /home/ubuntu/logs/was_err.log < /dev/null &


echo "finish updating WAS version"