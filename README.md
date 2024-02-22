## Java version

If using sdkman you can set the correct java sdk for this project using
`sdk env install` to install the correct version if necessary
`sdk env use` to activate it.

## Run locally

The application's default configuration will store the json and event log
in the `./data` folder.
If this folder doesn't exist be sure to create it so these files can be
written.

```shell
./gradlew bootRun
```

## Build docker image

```shell
# create the runnable bootjar
./gradlew bootJar

# build the docker image
docker build -t local/assignment:latest .
```

## Run the docker image

Run the image exposing internal port 8080 onto host at 58080

```shell
docker run -p 58080:8080 local/assignment:latest
```

Mount local dir data to container data directory for persistence
and destroy image when finished

```shell
docker run --rm -p 58080:8080 -v $PWD/data:/app/data local/assignment:latest
```

## Run with docker compose

```shell
docker compose up
```
