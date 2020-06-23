FROM openjdk:11.0.2-jre-slim-stretch

RUN apt-get update && \
  apt-get install -y gnupg2 apt-transport-https && \
  echo "deb https://dl.bintray.com/sbt/debian /" > /etc/apt/sources.list.d/sbt.list && \
  apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 2EE0EA64E40A89B84B2DF73499E82A75642AC823 && \
  apt-get update && \
  apt-get install -y sbt && \
  sbt version

COPY . /app

WORKDIR /app

RUN sbt stage && \
  chmod +x /app/target/universal/stage/bin/base-app

FROM openjdk:11.0.2-jre-slim-stretch

ENV APP_DIR /app

EXPOSE 8080

USER daemon

WORKDIR ${APP_DIR}

COPY --from=0 /app/target/universal/stage ${APP_DIR}/target/universal/stage
COPY --from=0 /app/src/main/resources/db ${APP_DIR}/src/main/resources/db

CMD ["${APP_DIR}/target/universal/stage/bin/base-app"]