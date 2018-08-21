#
# Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License v. 2.0 which is available at
# http://www.eclipse.org/legal/epl-2.0,
# or the Eclipse Distribution License v. 1.0 which is available at
# http://www.eclipse.org/org/documents/edl-v10.php.
#
# SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
#

############################################################################
# Generic script applicable on any Operating Environments (Unix, Windows)
# ScriptName    : wls_start.py
# Properties    : weblogic.properties
# Author        : Kevin Yuan
############################################################################

#===========================================================================
# Start server using wlst command
#===========================================================================

#startServer('@TARGET_SERVER@', 'eclipselink', url='t3://@WL_HOST@:@WL_PORT@', username='@WL_USR@', password='@WL_PWD@', domainDir='@WL_DOMAIN@', jvmArgs='-Xms256m -Xmx960m -Dweblogic.Stdout=stdout.log -Dweblogic.Stderr=stderr.log')

#===========================================================================
# Using the following instead of above "jvmarg" setting when using SUN jdk
# because jrockit doesn't support PermSize when the server run on SUN jdk
#===========================================================================

startServer('@TARGET_SERVER@', 'eclipselink', url='t3://@WL_HOST@:@WL_PORT@', username='@WL_USR@', password='@WL_PWD@', domainDir='@WL_DOMAIN@', jvmArgs='-XX:PermSize=128m -Dweblogic.Stdout=stdout.log -Dweblogic.Stderr=stderr.log')

#===========================================================================
# Add the following jvmarg(s) into wlst command when you try to debug
#===========================================================================

#-Xdebug
#-Xnoagent
#-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=4000
