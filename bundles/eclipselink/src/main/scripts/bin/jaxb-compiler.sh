#!/bin/sh
#
# Copyright (c) 2018, 2021 Oracle and/or its affiliates. All rights reserved.
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
MODULEPATH=`dirname $0`/../jlib/moxy:\
`dirname $0`/../jlib/eclipselink.jar:\
`dirname $0`/../jlib/jpa/jakarta.persistence-api.jar
MAIN_CLASS=org.eclipse.persistence.jaxb.xjc.MOXyXJC
JAVA_ARGS="$@"

${JAVA_HOME}/bin/java ${JVM_ARGS} --add-modules jakarta.validation -p "${MODULEPATH}" -m eclipselink/${MAIN_CLASS} ${JAVA_ARGS}
