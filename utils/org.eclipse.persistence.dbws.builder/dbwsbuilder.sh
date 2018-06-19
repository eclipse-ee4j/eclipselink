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

. `dirname $0`/../../bin/setenv.sh

# User may increase Java memory setting(s) if desired:
JVM_ARGS=-Xmx256m

# Please do not change any of the following lines:
CLASSPATH=`dirname $0`/javax.servlet_2.4.0.v200806031604.jar:\
`dirname $0`/javax.wsdl_1.6.2.v201012040545.jar:\
`dirname $0`/org.eclipse.persistence.oracleddlparser_1.0.0.v20150306.jar:\
`dirname $0`/../../jlib/eclipselink.jar:\
`dirname $0`/eclipselink-dbwsutils.jar:\
${DRIVER_CLASSPATH}:\
${JAVA_HOME}/lib/tools.jar

DBWSBUILDER_ARGS="$@"

${JAVA_HOME}/bin/java ${JVM_ARGS} -cp ${CLASSPATH} \
    org.eclipse.persistence.tools.dbws.DBWSBuilder ${DBWSBUILDER_ARGS}
