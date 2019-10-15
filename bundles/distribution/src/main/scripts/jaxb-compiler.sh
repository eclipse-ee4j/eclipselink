#!/bin/sh
#
# Copyright (c) 2018, 2019 Oracle and/or its affiliates. All rights reserved.
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License v. 2.0 which is available at
# http://www.eclipse.org/legal/epl-2.0,
# or the Eclipse Distribution License v. 1.0 which is available at
# http://www.eclipse.org/org/documents/edl-v10.php.
#
# SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
#

. `dirname $0`/setenv.sh

# User may increase Java memory setting(s) if desired:
JVM_ARGS="-Xmx256m"

# If going through a proxy, set the proxy host and proxy port below, then uncomment the line
# JVM_ARGS="${JVM_ARGS} -DproxySet=true -Dhttp.proxyHost= -Dhttp.proxyPort="

# Please do not change any of the following lines:
CLASSPATH=`dirname $0`/../jlib/moxy/jaxb-osgi.jar:\
`dirname $0`/../jlib/moxy/jakarta.activation.jar:\
`dirname $0`/../jlib/moxy/jakarta.json.jar:\
`dirname $0`/../jlib/moxy/jakarta.validation-api.jar:\
`dirname $0`/../jlib/eclipselink.jar
JAXB_API=`dirname $0`/../jlib/moxy/api/jakarta.xml.bind-api.jar
ENDORSED_DIR=../jlib/moxy/api
MAIN_CLASS=org.eclipse.persistence.jaxb.xjc.MOXyXJC
JAVA_ARGS="$@"

JAVA_VERSION=`${JAVA_HOME}/bin/java -version 2>&1 | head -n 1 | cut -d'"' -f2 | sed -E 's/^(1\.)?([0-9]+).+$/\2/'`
echo "Java major version: ${JAVA_VERSION}"

# Check if supports module path
if [ ${JAVA_VERSION} -lt 9 ];
then
    #Java 8
    ${JAVA_HOME}/bin/java ${JVM_ARGS} -cp ${CLASSPATH} -Djava.endorsed.dirs=${ENDORSED_DIR} ${MAIN_CLASS} ${JAVA_ARGS}
else
    #Java >8
    ${JAVA_HOME}/bin/java ${JVM_ARGS} -cp ${CLASSPATH}:${JAXB_API} ${MAIN_CLASS} ${JAVA_ARGS}
fi