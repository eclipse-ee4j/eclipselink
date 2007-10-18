#!/bin/sh
. `dirname $0`/setenv.sh

# Please do not change any of the following lines:
SRC_DIR=$1
DEST_DIR=$2
LOG_DIR=$3
JVM_ARGS=-Xmx256M
CLASSPATH=`dirname $0`/../lib/java/internal/packageRename.jar
JAVA_ARGS=`dirname $0`/../config/packageRename.properties \
${SRC_DIR} ${DEST_DIR} ${LOG_DIR}
${JAVA_HOME}/bin/java ${JVM_ARGS} -classpath ${CLASSPATH} \
    org.eclipse.persistence.tools.PackageRenamer ${JAVA_ARGS}
