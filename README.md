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
```
db_jdbcUrl=jdbc:postgresql://sofi-chall-postgres:5432/postgres
db_user=postgres
db_password=postgres
scalatra_environment=local
db_flyway_table=schema_version
```
1. Run docker-compose-up script from root folder
```sh
$ docker-compose-up.sh
```
## Service connecting to local postgres
1. Create database first
1. Create override/docker_desktop.env with values
```
db_jdbcUrl=jdbc:postgresql://docker.for.mac.localhost:${PORT}/${DATABAE}
db_user=${USER}
db_password=${PASSWORD}
scalatra_environment=local
db_flyway_table=schema_version
```
1. Start docker as
```sh
bin/docker-local-start.sh
```
1. Stop docker as
```bash
bin/docker-local-stop.sh
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
* Create transaction - POST http://localhost:8888/v1/transactions
    * Will take request in the following format
        ```json
        {
            "merchant": "Bartell Drugs",
            "merchant-id": 1,
            "price": 5.78,
            "purchase-date": "2019-01-02T06:17:38",
            "tx-id": 1,
            "user-id": 1
        }
        ```
    * Response will be 
        ```json
        {
            "merchant": "Bartell Drugs",
            "merchant-id": 1,
            "price": 5.78,
            "purchase-date": "2019-01-02T06:17:38",
            "tx-id": 1,
            "user-id": 1,
            "void": false
        }
        ```
    * Will return a 4xx response if we try to insert another transaction with the same id     
* Void transaction - PUT http://localhost:8888/v1/transactions/:id/void
    * Will return the voided transaction
        ```json
        {
            "merchant": "Bartell Drugs",
            "merchant-id": 1,
            "price": 5.78,
            "purchase-date": "2019-01-02T06:17:38",
            "tx-id": 1,
            "user-id": 1,
            "void": true
        }
        ```
* Get merchants - GET http://localhost:8888/v1/transactions/merchants?user-id=${USER_ID_1}&user-id=${USER_ID_2}&limit=${LIMIT}&sort-type=asc or desc
    * Params
        * user-id: can be multiple - user-id=${USER_ID_1}&user-id=${USER_ID_2}
        * limit: number
        * sort-type: "asc" or "desc"
    * Will return an array of merchants as response
        ```json
        {
            "merchants": [
                "Arbys",
                "Lola",
                "Flying Pie Pizza"
            ]
        }
        ```
* Get transactions - GET http://localhost:8888/v1/transactions?query-params
    * Params
        * user-id: can only be 1 user-id
        * limit: number
        * offset-date: offset that is of type date
        * offset-int: offset that is int type
        * offset-price: offset that is of type big decimal
        * sort-key: key using which results will be sorted - merchant-id or tx-id or purchase-date or price
        * pagination-direction: "previous" or "next"
    * Example requests
        * http://localhost:8888/v1/transactions?user-id=1&sort-key=purchase-date&pagination-direction=previous
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
                        },
                        ...
                   ]
                }
                ```
        * http://localhost:8888/v1/transactions?user-id=1&sort-key=purchase-date&pagination-direction=next&offset-date=xx
        * http://localhost:8888/v1/transactions?user-id=1&sort-key=tx-id&pagination-direction=next
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
                    },
                    ...
               ]
            }
            ```
        * http://localhost:8888/v1/transactions?user-id=1&sort-key=tx-id&pagination-direction=next&offset-int=xx
* API calls will return a well defined json body in case of validation errors
    * Validation error response
        ```json
        {
            "validationFailures": [
                {
                    "displayString": "No transactions for provided user ids - List(4)",
                    "failureCode": "invalid",
                    "fieldName": "user-ids"
                }
            ]
        }
        ```        
# Development #
1. You need a running postgres for development
1. Create override/local.properties file with values
```
scalatra_environment=local
db_jdbcUrl=jdbc:postgresql://localhost:${DB_PORT}/${DB_NAME}
db_host_url=jdbc:postgresql://localhost:{DB_PORT}
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
db_host_url=jdbc:postgresql://localhost:${DB_PORT}
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

# Metrics #
1. [metrics-scala](https://github.com/erikvanoosten/metrics-scala) is used for metrics. Basic support is added but needs more work

# Discussion #
1. When testing with the provided data User 2 does return an output because there are 5 transactions for User 2. 
```
count | user_id | merchant_id |  merchant_name
 4    |       2 |           1 | Bartell Drugs
 1    |       2 |           2 | Safeway
```

1. Transaction is the only domain object in this system for now. In a full fledged system user and merchant should be 
first class concepts
1. Currently there is no authorization or authentication on any of the calls. In a production system we would add them
1. We are not recording history currently. We should add that to make the system more robust. We can leverage db 
triggers for that. 
1. Similarly we should have audit support to record who made the request and what the request was