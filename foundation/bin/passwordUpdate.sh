#!/bin/sh
#
# Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License v. 2.0 which is available at
# http://www.eclipse.org/legal/epl-2.0,
# or the Eclipse Distribution License v. 1.0 which is available at
# http://www.eclipse.org/org/documents/edl-v10.php.
#
# SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
#

#Usage is `passwordUpdate.sh|.cmd -ip <old encrypted password>`
#This application internally decrypt old encrypted password used by some previous version EclipseLink and encrypt it by latest algorithm.

. `dirname $0`/../../bin/setenv.sh

# User may increase Java memory setting(s) if desired:
JVM_ARGS=-Xmx256m

# Please do not change any of the following lines:
CLASSPATH=`dirname $0`/../../jlib/eclipselink.jar:

PASSWORD_UPDATE_ARGS="$@"

${JAVA_HOME}/bin/java ${JVM_ARGS} -cp ${CLASSPATH} \
    org.eclipse.persistence.tools.security.JCEEncryptorCmd ${PASSWORD_UPDATE_ARGS}
