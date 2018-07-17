#!/usr/bin/env bash
java -version
source ~/.profile
echo $DB_UPDATER_HOME
cd ${DB_UPDATER_HOME}
SPRING_DATA_MONGODB_PASSWORD=${SZOLNOK_DB_PASSWORD} SPRING_PROFILES_ACTIVE=$1 gradle bootRun
