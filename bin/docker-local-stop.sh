#!/bin/bash

set -e


echo "Stopping transactionapi"

docker stop transactionapi
docker rm transactionapi
