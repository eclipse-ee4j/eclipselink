#!/bin/sh
. `dirname $0`/setenv.sh

# Please do not change any of the following lines:
SRC_DIR=$1
DEST_DIR=$2
JVM_ARGS=-Xmx256M
CLASSPATH=`dirname $0`/../jlib/package-rename.jar

${JAVA_HOME}/bin/java ${JVM_ARGS} -classpath ${CLASSPATH} utilities.MigrateTopLinkToEclipseLink ${SRC_DIR} ${DEST_DIR} ../config/package-rename.properties
