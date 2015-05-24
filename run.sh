#!/bin/sh
DB_JAR=./lib/sqlite-jdbc-3.7.2.jar
java -classpath ./raportgen-0.0.1-SNAPSHOT.jar:$DB_JAR prv.k.reportgen.App test.yml
