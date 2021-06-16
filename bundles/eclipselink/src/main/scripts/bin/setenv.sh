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

# User MUST set DRIVER_CLASSPATH to point to their desired driver jar(s), such as JDBC and J2C.  For example:
# DRIVER_CLASSPATH=/some_dir/jdbc/ojdbc14.jar:/some_other_dir/j2c/aqapi.jar; export DRIVER_CLASSPATH
#
# Note: DRIVER_CLASSPATH should NOT contain any classes for your persistent business objects - these are
# configured in the Mapping Workbench project.

# User MUST set INSTALL_JAVA_HOME to point a supported JRE. If none
# is provided for INSTALL_JAVA_HOME then the system JAVA_HOME
# value will be used
INSTALL_JAVA_HOME=%s_jreDirectory%;
if  [ "${JAVA_HOME}" = '' ]; then
    JAVA_HOME=$INSTALL_JAVA_HOME; export JAVA_HOME
fi
