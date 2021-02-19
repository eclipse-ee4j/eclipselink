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

# User MUST set INSTALL_JAVA_HOME to point a supported JRE. If none
# is provided for INSTALL_JAVA_HOME then the system JAVA_HOME
# value will be used
INSTALL_JAVA_HOME=%s_jreDirectory%;
if  [ "${JAVA_HOME}" = '' ]; then
    JAVA_HOME=$INSTALL_JAVA_HOME; export JAVA_HOME
fi
