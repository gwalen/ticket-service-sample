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

During startup flyway plugin will create initial database structure dataset

`sbt run`


TODO:
 usage
 stack
