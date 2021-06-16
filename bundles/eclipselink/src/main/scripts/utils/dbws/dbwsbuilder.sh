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

# User may increase Java memory setting(s) if desired:
JVM_ARGS=-Xmx256m

# Please do not change any of the following lines:
CLASSPATH=`dirname $0`/jakarta.servlet-api.jar:\
`dirname $0`/wsdl4j.jar:\
`dirname $0`/org.eclipse.persistence.oracleddlparser.jar:\
`dirname $0`/../../jlib/eclipselink.jar:\
`dirname $0`/org.eclipse.persistence.dbws.builder.jar:\
${DRIVER_CLASSPATH}:\
${JAVA_HOME}/lib/tools.jar

DBWSBUILDER_ARGS="$@"

${JAVA_HOME}/bin/java ${JVM_ARGS} -cp ${CLASSPATH} \
    org.eclipse.persistence.tools.dbws.DBWSBuilder ${DBWSBUILDER_ARGS}
