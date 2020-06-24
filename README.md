# Ticket app

## Run application

### Start docker database 
Application uses PostgreSQL database, it can be setup anywhere, easy way is to use docker image.
Example with docker:

download and run PostgreSQL 9.6:

`docker run --name postgis96 -e POSTGRES_PASSWORD=postgres -p 5432:5432 -d mdillon/postgis:9.6`
 

After initial download:  
stop  : `docker stop postgis96`  
start : `docker start postgis96`  

### Create schema for application

create schema :

`docker exec -it -u postgres postgis96 sh -c "psql -c 'create database eventworld'"`

### Start app

During startup flyway plugin will create initial database structure and example dataset

`sbt run`

### API docs 

Yaml rest-api docs are exposed on `http://localhost:8080/api/docs/docs.yaml`

To read it using swagger-ui, go to `http://localhost:8080/api/docs/index.html#/` and in the top input type local api-rest api address 
(`http://localhost:8080/api/docs/docs.yaml`)

### Usage 

* add
```
curl --location --request POST 'http://localhost:8080/api/reservations' \
--header 'Content-Type: application/json' \
--data-raw '{
    "reservationDto" : {
        "clientId": 105,
        "eventId": 1000,
        "ticketCount": 3
    }
}'
```

* get 
```
curl --location --request GET 'http://localhost:8080/api/reservations'
```

* delete
```
curl --location --request DELETE 'http://localhost:8080/api/reservations/6'
```

* extend expiry
```
curl --location --request PATCH 'http://localhost:8080/api/reservations' \
--header 'Content-Type: application/json' \
--data-raw '{
    "reservationId": 8, 
    "newExpiryDate": "2021-07-29T11:00:00Z"
}'
```

