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


############################################################################
# Generic script applicable on any Operating Environments (Unix, Windows)  
# ScriptName    : wls_install.py  
# Properties    : weblogic.properties test.properties 
# Author        : Kevin Yuan  
############################################################################   

#===========================================================================
# Open a domain template.
#===========================================================================

try:
   readTemplate(System.getProperty("weblogic.installdir") + "/common/templates/wls/wls.jar")
except:
   readTemplate(System.getProperty("weblogic.installdir") + "/common/templates/domains/wls.jar")

#===========================================================================
# Configure the Administration Server and SSL port.
#===========================================================================

cd('Servers/AdminServer')
set('Name', System.getProperty("target.server"))
set('ListenAddress','')
set('ListenPort', Integer.getInteger("weblogic.port"))

create('AdminServer','SSL')
cd('SSL/AdminServer')
set('Enabled', 'True')
set('ListenPort', 7002)

#===========================================================================
# Define the user password for weblogic.
#===========================================================================

cd('/')
cd('Security/base_domain/User/weblogic')
set('Name',System.getProperty("server.user"))
cmo.setPassword(System.getProperty("server.pwd"))

#===========================================================================
# Write the domain and close the domain template.
#===========================================================================

setOption('OverwriteDomain', 'true')
writeDomain(System.getProperty("weblogic.domain"))
closeTemplate()

#===========================================================================
# Exit WLST.
#===========================================================================

exit()


