#!/bin/bash

set -e

echo "Building service"
sbt "project flyway" assembly
sbt "project service" package

if [ "$?" != "0" ]; then
  exit 1
fi

docker-compose up