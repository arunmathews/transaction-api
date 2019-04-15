#!/bin/bash
sbt "project flyway" test:setupTestDb && sbt -mem 1562 "project service" test
