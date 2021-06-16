#!/bin/sh
#
# Copyright (c) 2018, 2021 Oracle and/or its affiliates. All rights reserved.
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License v. 2.0, which is available at
# http://www.eclipse.org/legal/epl-2.0.
#
# This Source Code may also be made available under the following Secondary
# Licenses when the conditions for such availability set forth in the
# Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
# version 2 with the GNU Classpath Exception, which is available at
# https://www.gnu.org/software/classpath/license.html.
#
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
#

. `dirname $0`/../../bin/setenv.sh

# Please do not change any of the following lines:
SRC_DIR=$1
DEST_DIR=$2
JVM_ARGS=-Xmx256M
CLASSPATH=`dirname $0`/package-rename.jar

${JAVA_HOME}/bin/java ${JVM_ARGS} -classpath ${CLASSPATH} org.eclipse.persistence.utils.rename.MigrateTopLinkToEclipseLink ${SRC_DIR} ${DEST_DIR} package-rename.properties
