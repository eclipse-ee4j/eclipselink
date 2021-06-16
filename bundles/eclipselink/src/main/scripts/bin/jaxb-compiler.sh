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

. `dirname $0`/setenv.sh

# User may increase Java memory setting(s) if desired:
JVM_ARGS="-Xmx256m -Djakarta.xml.bind.JAXBContextFactory=org.eclipse.persistence.jaxb.JAXBContextFactory"

# If going through a proxy, set the proxy host and proxy port below, then uncomment the line
# JVM_ARGS="${JVM_ARGS} -DproxySet=true -Dhttp.proxyHost= -Dhttp.proxyPort="

# Please do not change any of the following lines:
MODULEPATH=`dirname $0`/../jlib/moxy:\
`dirname $0`/../jlib/eclipselink.jar:\
`dirname $0`/../jlib/jpa/jakarta.persistence-api.jar
MAIN_CLASS=org.eclipse.persistence.jaxb.xjc.MOXyXJC
JAVA_ARGS="$@"

${JAVA_HOME}/bin/java ${JVM_ARGS} --add-modules jakarta.validation -p "${MODULEPATH}" -m eclipselink/${MAIN_CLASS} ${JAVA_ARGS}
