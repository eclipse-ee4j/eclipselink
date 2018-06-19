#!/bin/sh
#
# Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License 2.0 which is available at
# http://www.eclipse.org/legal/epl-2.0.
#
# SPDX-License-Identifier: EPL-2.0
#

. `dirname $0`../../bin/setenv.sh

# Please do not change any of the following lines:
SRC_DIR=$1
DEST_DIR=$2
JVM_ARGS=-Xmx256M
CLASSPATH=`dirname $0`package-rename.jar

${JAVA_HOME}/bin/java ${JVM_ARGS} -classpath ${CLASSPATH} org.eclipse.persistence.utils.rename.MigrateTopLinkToEclipseLink ${SRC_DIR} ${DEST_DIR} package-rename.properties
