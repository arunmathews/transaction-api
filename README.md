# transaction-api #

This service provides apis to create and query transactions

# Design #

There are three main layers. Service -> Handler -> API.

Service - Uses a web framework to accept requests. The framework could be
Scalatra, Finch etc. We are currently using Scalatra.

Handler - Business logic layer. Self contained with no external
dependencies.

API - Specifies the external dependencies that is required by the
handler. We could swap out different implementations of the API as
needed. These dependencies could be databases, other services etc.

# Scalatra #
[Scalatra home](http://scalatra.org)

# Prerequisites #
1. Install Java8
1. Install sbt
1. Install docker

# Running Service #
## Postgres + Service
1. Create override/docker_dev.env file with values:
```$xslt
db_jdbcUrl=jdbc:postgresql://sofi-chall-postgres:5432/postgres
db_user=postgres
db_password=postgres
scalatra_environment=local
db_flyway_table=schema_version
```
1. Run docker-compose-up
```sh
$ docker-compose-up.sh
```
## Service connecting to local postgres
1. Create database first
1. Create override/docker_desktop.env with values
```$xslt
db_jdbcUrl=jdbc:postgresql://docker.for.mac.localhost:${PORT}/${DATABAE}
db_user=${USER}
db_password=${PASSWORD}
scalatra_environment=local
db_flyway_table=schema_version
```
1. Run docker
```sh
bin/docker-local-start.sh
```
1. Ping service
```bash
curl -I http://localhost:8888/v1/ping
```
1. Use script to add data to service
```bash
ruby service/src/main/scripts/call_service.rb ${PATH_TO_DATA_CSV} http://localhost:8888
```
# Exposed routes #
* Create transaction - POST prefix/v1/transactions
* Void transaction - PUT prefix/v1/transactions/:id/void
* Get merchants - GET prefix/v1/transactions/merchants?user-id=${USER_ID_1}&user-id=${USER_ID_2}&limit=${LIMIT}&sort-type=asc or desc
    * Params
        * user-id: can be multiple - user-id=${USER_ID_1}&user-id=${USER_ID_2}
        * limit: number
        * sort-type: "asc" or "desc"
* Get transactions - GET prefix/v1/transactions?query-params
    * Params
        * user-id: one user-id
        * limit: number
        * offset-date: offset that is of type date
        * offset-int: offset that is int type
        * offset-price: offset that is big decimal
        * sort-key: key using which results will be sorted - merchant-id or tx-id or purchase-date or price
        * pagination-direction: "previous" or "next"
    * Example requests
        * prefix/v1/transactions?user-id=1&sort-key=purchase-date&pagination-direction=previous
            * Sample response
                ```json
                {
                    "count": 10,
                    "nextOffset": "2019-01-02T06:17:56",
                    "transactions": [
                        {
                            "merchant": "Bartell Drugs",
                            "merchant-id": 1,
                            "price": 5.78,
                            "purchase-date": "2019-01-02T06:45:55",
                            "tx-id": 49,
                            "user-id": 1,
                            "void": false
                        },...
                   ]
                }
                ```
        * prefix/v1/transactions?user-id=1&sort-key=purchase-date&pagination-direction=next&offset-date=xx
        * prefix/v1/transactions?user-id=1&sort-key=tx-id&pagination-direction=next
            * Sample response
            ```json
            {
                "count": 3,
                "nextOffset": "24",
                "transactions": [
                    {
                        "merchant": "Bartell Drugs",
                        "merchant-id": 1,
                        "price": 5.78,
                        "purchase-date": "2019-01-02T06:45:55",
                        "tx-id": 49,
                        "user-id": 1,
                        "void": false
                    },...
               ]
            }
            ```
        * prefix/v1/transactions?user-id=1&sort-key=tx-id&pagination-direction=next&offset-int=xx
# Development #
1. You need a running postgres for development
1. Create local.properties file with values
```
scalatra_environment=local
db_jdbcUrl=jdbc:postgresql://${DB_PATH}
db_host_url=jdbc:postgresql://localhost
db_default_user=postgres
default_db=postgres
common_db=transaction_api
db_user=${USER}
db_password=${PASSWORD}
db_flyway_table=schema_version
```
## FLyway ##
[Flyway](http://flywaydb.org/) is a tool that helps with db migrations.

If you want to make schema changes:

Add new sql files for table changes under flyway/src/main/resources/com/sofichallenge/transactionapi/database/migration/V{n+1}__{name}          

Update tables:
```sh
$ sbt
> project flyway
> migrateFlywayDbTables
```

## Slick ##
[Slick](http://slick.lightbend.com/) is a library for relational mapping in Scala

If you want to generate Scala code from latest version of db tables
```sh
$ sbt
> project service
> genScalaTables
```

## Running service locally #
```bash
$ sbt
> jetty:start
```

# Testing #
1. Unit tests have an external dependency on postgres.
1. Create test.properties file with values
```
db_host_url=jdbc:postgresql://localhost
default_db=postgres
test_db=${TEST_DB_NAME}
db_default_user=${USER}
``` 
1. default_db is used by sbt test task to connect to db and create database with name given by test_db
1. Run tests as
```bash
$ sbt
> project flyway
> test:setupTestDb              # Create test db - useful for running one off tests
> project service
> test
> project flyway
> test:teardownTestDb           # Delete test db
```

