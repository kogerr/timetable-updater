#!/usr/bin/env bash
java -version
source ~/.profile
echo $DB_UPDATER_HOME
cd ${DB_UPDATER_HOME}
mvn spring-boot:run -Dspring.data.mongodb.password=${SZOLNOK_DB_PASSWORD}
