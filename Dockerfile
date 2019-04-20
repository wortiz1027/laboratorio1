# -----------------------------------------------------
# - DOCKERFILE
# - AUTOR: Wilman Alberto Ortiz Navarro
# - FECHA: 15-Abril-2019
# - DESCRIPCION: Dockerfile que permite la creacion del
# -              contenedor con nuestro microservicio
# -              basico sobre docker
# -----------------------------------------------------

# escape=\ (backslash)
# Imagen base del Docker Registry para compilar nuestro microservicio
# FROM java:8
FROM openjdk:8-jdk-alpine

# Parametrizacion del nombre del archivo que genera spring boot
ARG JAR_FILE=laboratorio1-0.0.1-SNAPSHOT.jar
ARG BUILD_DATE
ARG BUILD_VERSION

ENV APP_HOME="/app"
# Asignar JVM options (https://www.oracle.com/technetwork/java/javase/tech/vmoptions-jsp-140102.html)
ENV JAVA_OPTS=""

# Informacion de la persona que mantiene la imagen
LABEL maintainer="willman.ortiz@gmail.com" \
      version=$BUILD_VERSION \
      build-date=$BUILD_DATE

# Puerto de exposicion del servicio
EXPOSE 8081

# Creando directorio de la aplicacion
RUN mkdir $APP_HOME && \
    mkdir $APP_HOME/config && \
    mkdir $APP_HOME/log

RUN apk add --update bash && rm -rf /var/cache/apk/*

# Volumen para el intercambio de archivos con el sistema host
VOLUME $APP_HOME/log
VOLUME $APP_HOME/config

# Seteando el workspace
WORKDIR $APP_HOME

# Creando un archivo en el contenedor
#ADD wait-for-it.sh $APP_HOME/config
ADD ./target/$JAR_FILE $APP_HOME/customer-service.jar

#RUN echo "direct $APP_HOME/customer-service.jar"

# Ejecutanto el microservicio en el contenedor
#ENTRYPOINT [ "java","-Djava.security.egd=file:/dev/./urandom","-jar","$APP_HOME/customer-service.jar" ]
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=qa -jar ${APP_HOME}/customer-service.jar" ]