#!/bin/bash

set -e

port=8888

echo "Building service"
sbt "project flyway" assembly
sbt "project service" package

if [ "$?" != "0" ]; then
  exit 1
fi
docker build -f dockerfiles/Dockerfile -t transactionapi .
if [ "$?" != "0" ]; then
  exit 1
fi
docker run --name transactionapi --env-file override/docker_desktop.env -p $port:8080 -i -t transactionapi | tee /tmp/test_transactionapi.log
