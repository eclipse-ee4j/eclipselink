#!/bin/sh
#
# Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License v. 2.0 which is available at
# http://www.eclipse.org/legal/epl-2.0,
# or the Eclipse Distribution License v. 1.0 which is available at
# http://www.eclipse.org/org/documents/edl-v10.php.
#
# SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
#

. `dirname $0`../../bin/setenv.sh

# Please do not change any of the following lines:
SRC_DIR=$1
DEST_DIR=$2
JVM_ARGS=-Xmx256M
CLASSPATH=`dirname $0`package-rename.jar

${JAVA_HOME}/bin/java ${JVM_ARGS} -classpath ${CLASSPATH} org.eclipse.persistence.utils.rename.MigrateTopLinkToEclipseLink ${SRC_DIR} ${DEST_DIR} package-rename.properties
