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
# FROM openjdk:8-jdk-alpine
FROM centos-cicd2:8082/leegreiner/11-jre-alpine

# Parametrizacion del nombre del archivo que genera spring boot
ARG JAR_FILE=laboratorio1-0.0.1-SNAPSHOT.jar
ARG BUILD_DATE
ARG BUILD_VERSION
ARG BUILD_REVISION

ENV APP_HOME="/app" \
    JAVA_OPTS="" \
	HTTP_PORT=8443

# Informacion de la persona que mantiene la imagen
LABEL org.opencontainers.image.created=$BUILD_DATE \
	  org.opencontainers.image.authors="Wilman Ortiz Navarro <$EMAIL>" \
	  org.opencontainers.image.url="https://gitlab.com/wortiz1027/laboratorio1/blob/develop/Dockerfile" \
	  org.opencontainers.image.documentation="" \
	  org.opencontainers.image.source="https://gitlab.com/wortiz1027/laboratorio1/blob/develop/Dockerfile" \
	  org.opencontainers.image.version=$BUILD_VERSION \
	  org.opencontainers.image.revision=$BUILD_REVISION \
	  org.opencontainers.image.vendor="developer.io" \
	  org.opencontainers.image.licenses="" \
	  org.opencontainers.image.title="Management Customer MicroService" \
	  org.opencontainers.image.description="Imagen docker con ubuntu como base para instalar nginx web server y configurarlo con https"

# Puerto de exposicion del servicio
EXPOSE $HTTP_PORT

# Creando directorio de la aplicacion
RUN mkdir $APP_HOME && \
    mkdir $APP_HOME/config && \
    mkdir $APP_HOME/log

RUN apk add --update bash && rm -rf /var/cache/apk/*

# Volumen para el intercambio de archivos con el sistema host
VOLUME $APP_HOME/log \
	   $APP_HOME/config

# Seteando el workspace
WORKDIR $APP_HOME

# Creando un archivo en el contenedor
ADD ./target/$JAR_FILE $APP_HOME/customer-service.jar

# Ejecutanto el microservicio en el contenedor
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=qa -jar ${APP_HOME}/customer-service.jar" ]