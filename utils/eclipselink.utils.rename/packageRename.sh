#!/bin/sh
. `dirname $0`../../bin/setenv.sh

# Please do not change any of the following lines:
SRC_DIR=$1
DEST_DIR=$2
JVM_ARGS=-Xmx256M
CLASSPATH=`dirname $0`package-rename.jar

${JAVA_HOME}/bin/java ${JVM_ARGS} -classpath ${CLASSPATH} org.eclipse.persistence.utils.rename.MigrateTopLinkToEclipseLink ${SRC_DIR} ${DEST_DIR} package-rename.properties
