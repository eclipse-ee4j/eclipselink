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
# ScriptName    : wls_reset.py  
# Properties    : weblogic.properties  
# Author        : Kevin Yuan  
############################################################################   

#===========================================================================
# Connect to wls server
#===========================================================================

wlUser = System.getProperty("server.user")
wlPwd = System.getProperty("server.pwd")
wlHost = System.getProperty("weblogic.host")
wlPort = System.getProperty("weblogic.port")


connect(wlUser, wlPwd,'t3://' + wlHost + ':' + wlPort)

#===========================================================================
# Remove Data Sources using wlst on-line commonds
#===========================================================================

datasources = [System.getProperty("wls.ds.jta.name"), System.getProperty("wls.ds.nonjta.name")]
datasources.append(System.getProperty("wls.ds2.jta.name"))
datasources.append(System.getProperty("wls.ds2.nonjta.name"))
datasources.append(System.getProperty("wls.ds3.jta.name"))
datasources.append(System.getProperty("wls.ds3.nonjta.name"))

for ds in datasources:
    try:
        print 'Removing ' + ds + ' datasource ...'
        edit()
        startEdit()
        delete(ds,'JDBCSystemResource')
        save()
        activate()
    except Exception, x:
        print 'Failed to remove ' + ds + ': ', x

disconnect()
exit()
