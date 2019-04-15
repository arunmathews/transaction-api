FROM tomcat:9-jre8-alpine

ENV TERM=xterm

RUN echo "http://dl-5.alpinelinux.org/alpine/v3.7/main" >> /etc/apk/repositories

RUN apk update && apk upgrade && apk add less vim curl socat net-tools \
postgresql java-postgresql-jdbc htop

ENV FLYWAY_VERSION 5.2.4
ENV FLYWAY_HOME=/flyway
ENV FLYWAY_PATH=$FLYWAY_HOME/flyway-$FLYWAY_VERSION
ENV PATH=$FLYWAY_PATH:$PATH

WORKDIR $FLYWAY_HOME

RUN curl -O http://repo1.maven.org/maven2/org/flywaydb/flyway-commandline/$FLYWAY_VERSION/flyway-commandline-$FLYWAY_VERSION.tar.gz && \
        tar -zxf flyway-commandline-$FLYWAY_VERSION.tar.gz -C $FLYWAY_HOME && \
        cp /usr/share/java/postgresql*.jar $FLYWAY_PATH/jars/ && \
        rm flyway*.tar.gz

WORKDIR $CATALINA_HOME

RUN rm -rf webapps/ROOT webapps/examples webapps/manager webapps/host-manager webapps/docs

ADD flyway/target/scala-2.12/flyway-assembly.jar $FLYWAY_PATH/jars/

ADD service/target/scala-2.12/service_2.12-0.1.0.war webapps/ROOT.war

ADD dockerfiles/docker-entrypoint.sh docker-entrypoint.sh

ADD service/conf/logback.xml conf/logback.xml

RUN chmod 700 docker-entrypoint.sh

EXPOSE 8000

CMD ./docker-entrypoint.sh