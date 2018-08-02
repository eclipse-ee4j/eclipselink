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
# ScriptName    : wls_setup.py  
# Properties    : weblogic.properties test.properties 
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
# Create and configure JTA and non-JTA Data Sources and target it to the server.
#===========================================================================

datasources = [System.getProperty("wls.ds.jta.name"), System.getProperty("wls.ds.nonjta.name")]
if not System.getProperty("db2.url").startswith("${") :
    datasources.append(System.getProperty("wls.ds2.jta.name"))
    datasources.append(System.getProperty("wls.ds2.nonjta.name"))
if not System.getProperty("db3.url").startswith("${") :
    datasources.append(System.getProperty("wls.ds3.jta.name"))
    datasources.append(System.getProperty("wls.ds3.nonjta.name"))

targetServer = System.getProperty("target.server")


for x in range(len(datasources)):
    ds = datasources[x]
    y = x / 2 + 1
    if y < 2 :
        dbUrl = System.getProperty("db.url")
        dbDriver = System.getProperty("db.driver")
        dbPwd = System.getProperty("db.pwd")
        dbUser = System.getProperty("db.user")
    else :
        dbUrl = System.getProperty("db" + str(y) + ".url")
        dbDriver = System.getProperty("db" + str(y) + ".driver")
        dbPwd = System.getProperty("db" + str(y) + ".pwd")
        dbUser = System.getProperty("db" + str(y) + ".user")

    print 'Setting up ' + ds + ' datasource (url: ' + dbUrl + ', driver: ' + dbDriver + ')'

    try:
        edit()
        startEdit()
        cd('/')
        cmo.createJDBCSystemResource(ds)

        cd('/JDBCSystemResources/' + ds + '/JDBCResource/' + ds)
        set('Name',ds)

        cd('/JDBCSystemResources/' + ds + '/JDBCResource/' + ds + '/JDBCDataSourceParams/' + ds)
        set('JNDINames',jarray.array([String('jdbc/' + ds)], String))

        #cd('/JDBCSystemResources/' + ds + '/JDBCResource/' + ds + '/JDBCDataSourceParams/' + ds)
        #cmo.setGlobalTransactionsProtocol('OnePhaseCommit')

        if ds.find('Non') > -1:
            #non-JTA datasource
            #cd('/JDBCSystemResources/' + ds + '/JDBCResource/' + ds + '/JDBCDataSourceParams/' + ds)
            cmo.setGlobalTransactionsProtocol('None')


        cd('/JDBCSystemResources/' + ds + '/JDBCResource/' + ds + '/JDBCDriverParams/' + ds)
        set('DriverName',dbDriver)
        set('Url',dbUrl)
        set('Password',dbPwd)

        cd('/JDBCSystemResources/' + ds + '/JDBCResource/' + ds + '/JDBCDriverParams/' + ds + '/Properties/' + ds)
        cmo.createProperty('user')

        cd('/JDBCSystemResources/' + ds + '/JDBCResource/' + ds + '/JDBCDriverParams/' + ds + '/Properties/' + ds + '/Properties/user')
        set('Value',dbUser)

        cd('/JDBCSystemResources/' + ds + '/JDBCResource/' + ds + '/JDBCConnectionPoolParams/' + ds)
        cmo.setTestTableName('SQL SELECT 1 FROM DUAL')

        cd('/JDBCSystemResources/' + ds)
        set('Targets',jarray.array([ObjectName('com.bea:Name=' + targetServer + ',Type=Server')], ObjectName))
        save()
        activate()
    except Exception, x:
        print 'Failed to create ' + ds + ': ', x

#===========================================================================
# Enable Exalogic Optimization
#===========================================================================
if Boolean.getBoolean("is.exalogic"):
    try:
        edit()
        startEdit()
        cd('/')
        cmo.setExalogicOptimizationsEnabled(true)
        save()
        activate()
    except Exception, x:
        print 'Cannot enable ExaLogic Optimization', x

#===========================================================================
# Exit WLST.
#===========================================================================

disconnect()
exit()


