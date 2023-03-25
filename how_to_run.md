# how to run the application

## Software/versions used

1. Java 17
2. Maven
3. Spring boot 3
4. Postgresql database
5. Test containers for testing
6. Docker for containers


Both the java application and database is containerized so that you only need to install \
[docker](https://docs.docker.com/get-docker/) and [docker-compose](https://docs.docker.com/compose/install/)

Once you have installed these softwares, you can start the application using 

```bash
docker-compose up --build
```
This will start the java app on port 8080. 

This will run containers in foreground. If you want them to run in background, then run

```bash
docker-compose up -d --build
```


If you dont want to run them in containers then you can run below commands

1. Run jave app on machine with postgres db as docker

Start db container as

```bash
docker-compose -f postgres.yml up
```

and then run java app as

```bash
mvn clean package
java -jar target/omdb-application.jar
```

Note that the java version has to be 17
