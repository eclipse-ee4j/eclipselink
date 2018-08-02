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
