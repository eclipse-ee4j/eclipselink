############################################################################  
# Generic script applicable on any Operating Environments (Unix, Windows)  
# ScriptName    : wls_derby_setup.py - only for Derby DB when using Network Server
#                                      not for Enbedded Server
# Properties    : weblogic.properties test.properties 
# Author        : Kevin Yuan  
############################################################################   

#===========================================================================
# Connect to wls server
#===========================================================================

connect('%%WL_USR%%','%%WL_PWD%%','t3://%%WL_HOST%%:%%WL_PORT%%')

#===========================================================================
# Create and configure JTA Data Source and target it to the server.
#===========================================================================
edit()
startEdit()

cd('/')
cmo.createJDBCSystemResource('EclipseLinkDS')

cd('/JDBCSystemResources/EclipseLinkDS/JDBCResource/EclipseLinkDS')
cmo.setName('EclipseLinkDS')

cd('/JDBCSystemResources/EclipseLinkDS/JDBCResource/EclipseLinkDS/JDBCDataSourceParams/EclipseLinkDS')
set('JNDINames',jarray.array([String('jdbc/EclipseLinkDS')], String))

cd('/JDBCSystemResources/EclipseLinkDS/JDBCResource/EclipseLinkDS/JDBCDriverParams/EclipseLinkDS')
cmo.setUrl('jdbc:derby://localhost:1527/ECLIPSELINK;create=true;ServerName=localhost;databaseName=ECLIPSELINK')
cmo.setDriverName('org.apache.derby.jdbc.ClientXADataSource')
set('PasswordEncrypted','password')

cd('/JDBCSystemResources/EclipseLinkDS/JDBCResource/EclipseLinkDS/JDBCConnectionPoolParams/EclipseLinkDS')
cmo.setTestTableName('SQL SELECT 1 FROM SYS.SYSTABLES\r\n\r\n')

cd('/JDBCSystemResources/EclipseLinkDS/JDBCResource/EclipseLinkDS/JDBCDriverParams/EclipseLinkDS/Properties/EclipseLinkDS')
cmo.createProperty('user')

cd('/JDBCSystemResources/EclipseLinkDS/JDBCResource/EclipseLinkDS/JDBCDriverParams/EclipseLinkDS/Properties/EclipseLinkDS/Properties/user')
cmo.setValue('user')

cd('/JDBCSystemResources/EclipseLinkDS/JDBCResource/EclipseLinkDS/JDBCDriverParams/EclipseLinkDS/Properties/EclipseLinkDS')
cmo.createProperty('portNumber')

cd('/JDBCSystemResources/EclipseLinkDS/JDBCResource/EclipseLinkDS/JDBCDriverParams/EclipseLinkDS/Properties/EclipseLinkDS/Properties/portNumber')
cmo.setValue('1527')

cd('/JDBCSystemResources/EclipseLinkDS/JDBCResource/EclipseLinkDS/JDBCDriverParams/EclipseLinkDS/Properties/EclipseLinkDS')
cmo.createProperty('databaseName')

cd('/JDBCSystemResources/EclipseLinkDS/JDBCResource/EclipseLinkDS/JDBCDriverParams/EclipseLinkDS/Properties/EclipseLinkDS/Properties/databaseName')
cmo.setValue('ECLIPSELINK;create=true')

cd('/JDBCSystemResources/EclipseLinkDS/JDBCResource/EclipseLinkDS/JDBCDriverParams/EclipseLinkDS/Properties/EclipseLinkDS')
cmo.createProperty('serverName')

cd('/JDBCSystemResources/EclipseLinkDS/JDBCResource/EclipseLinkDS/JDBCDriverParams/EclipseLinkDS/Properties/EclipseLinkDS/Properties/serverName')
cmo.setValue('localhost')

cd('/JDBCSystemResources/EclipseLinkDS/JDBCResource/EclipseLinkDS/JDBCDataSourceParams/EclipseLinkDS')
cmo.setGlobalTransactionsProtocol('TwoPhaseCommit')

cd('/SystemResources/EclipseLinkDS')
set('Targets',jarray.array([ObjectName('com.bea:Name=myserver,Type=Server')], ObjectName))

save()
activate()

#===========================================================================
# Create and configure Non-JTA Data Source and target it to the server.
#===========================================================================

edit()
startEdit()

cd('/')
cmo.createJDBCSystemResource('ELNonJTADS')

cd('/JDBCSystemResources/ELNonJTADS/JDBCResource/ELNonJTADS')
cmo.setName('ELNonJTADS')

cd('/JDBCSystemResources/ELNonJTADS/JDBCResource/ELNonJTADS/JDBCDataSourceParams/ELNonJTADS')
set('JNDINames',jarray.array([String('jdbc/ELNonJTADS')], String))

cd('/JDBCSystemResources/ELNonJTADS/JDBCResource/ELNonJTADS/JDBCDriverParams/ELNonJTADS')
cmo.setUrl('jdbc:derby://localhost:1527/ECLIPSELINK;create=true;ServerName=localhost;databaseName=ECLIPSELINK')
cmo.setDriverName('org.apache.derby.jdbc.ClientDataSource')
set('PasswordEncrypted','password')

cd('/JDBCSystemResources/ELNonJTADS/JDBCResource/ELNonJTADS/JDBCConnectionPoolParams/ELNonJTADS')
cmo.setTestTableName('SQL SELECT 1 FROM SYS.SYSTABLES\r\n\r\n')

cd('/JDBCSystemResources/ELNonJTADS/JDBCResource/ELNonJTADS/JDBCDriverParams/ELNonJTADS/Properties/ELNonJTADS')
cmo.createProperty('user')

cd('/JDBCSystemResources/ELNonJTADS/JDBCResource/ELNonJTADS/JDBCDriverParams/ELNonJTADS/Properties/ELNonJTADS/Properties/user')
cmo.setValue('user')

cd('/JDBCSystemResources/ELNonJTADS/JDBCResource/ELNonJTADS/JDBCDriverParams/ELNonJTADS/Properties/ELNonJTADS')
cmo.createProperty('portNumber')

cd('/JDBCSystemResources/ELNonJTADS/JDBCResource/ELNonJTADS/JDBCDriverParams/ELNonJTADS/Properties/ELNonJTADS/Properties/portNumber')
cmo.setValue('1527')

cd('/JDBCSystemResources/ELNonJTADS/JDBCResource/ELNonJTADS/JDBCDriverParams/ELNonJTADS/Properties/ELNonJTADS')
cmo.createProperty('databaseName')

cd('/JDBCSystemResources/ELNonJTADS/JDBCResource/ELNonJTADS/JDBCDriverParams/ELNonJTADS/Properties/ELNonJTADS/Properties/databaseName')
cmo.setValue('ECLIPSELINK;create=true')

cd('/JDBCSystemResources/ELNonJTADS/JDBCResource/ELNonJTADS/JDBCDriverParams/ELNonJTADS/Properties/ELNonJTADS')
cmo.createProperty('serverName')

cd('/JDBCSystemResources/ELNonJTADS/JDBCResource/ELNonJTADS/JDBCDriverParams/ELNonJTADS/Properties/ELNonJTADS/Properties/serverName')
cmo.setValue('localhost')

cd('/JDBCSystemResources/ELNonJTADS/JDBCResource/ELNonJTADS/JDBCDataSourceParams/ELNonJTADS')
cmo.setGlobalTransactionsProtocol('None')

cd('/SystemResources/ELNonJTADS')
set('Targets',jarray.array([ObjectName('com.bea:Name=myserver,Type=Server')], ObjectName))

save()
activate()

#===========================================================================
# Exit WLST.
#===========================================================================

exit()
