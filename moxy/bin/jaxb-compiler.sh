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

. `dirname $0`/setenv.sh

# User may increase Java memory setting(s) if desired:
JVM_ARGS="-Xmx256m"

# If going through a proxy, set the proxy host and proxy port below, then uncomment the line
# JVM_ARGS="${JVM_ARGS} -DproxySet=true -Dhttp.proxyHost= -Dhttp.proxyPort="

# Please do not change any of the following lines:
CLASSPATH=`dirname $0`/../jlib/moxy/jaxb-core_2.2.11.v201407311112.jar:\
`dirname $0`/../jlib/moxy/jaxb-xjc_2.2.11.v201407311112.jar:\
`dirname $0`/../jlib/moxy/javax.json-api_1.1.2.jar:\
`dirname $0`/../jlib/moxy/javax.json_1.1.2.jar:\
`dirname $0`/../jlib/moxy/javax.validation.api_2.0.1.Final.jar:\
`dirname $0`/../jlib/eclipselink.jar
JAVA_ARGS="$@"

${JAVA_HOME}/bin/java ${JVM_ARGS} -cp ${CLASSPATH} -Djava.endorsed.dirs=../jlib/moxy/api org.eclipse.persistence.jaxb.xjc.MOXyXJC ${JAVA_ARGS}
