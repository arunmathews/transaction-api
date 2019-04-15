#!/bin/bash
set -e

flyway -url=$db_jdbcUrl -user=$db_user -password=$db_password -jarDirs=$FLYWAY_PATH/jars/ -locations=classpath:com.sofichallenge.transactionapi.database.migration migrate

export CATALINA_OPTS="$CATALINA_OPTS -Djava.security.egd=file:/dev/./urandom -Dlogback.configurationFile=conf/logback.xml  -Djava.rmi.server.hostname=localhost -Djava.net.preferIPv4Stack=true"
catalina.sh run
