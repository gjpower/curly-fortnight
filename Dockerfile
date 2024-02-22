ARG BASE_IMAGE=amazoncorretto:17.0.10-al2023-headless
ARG ARCH=
FROM ${ARCH}${BASE_IMAGE}
WORKDIR /app
RUN mkdir data
COPY config config
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]

