############################################################################  
# Generic script applicable on any Operating Environments (Unix, Windows)  
# ScriptName    : wls_setup.py  
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
cmo.setUrl('%%DBURL%%')
cmo.setDriverName('%%DBDRV%%')
set('PasswordEncrypted','%%DBPWD%%')

cd('/JDBCSystemResources/EclipseLinkDS/JDBCResource/EclipseLinkDS/JDBCConnectionPoolParams/EclipseLinkDS')
cmo.setTestTableName('SQL SELECT 1 FROM DUAL')

cd('/JDBCSystemResources/EclipseLinkDS/JDBCResource/EclipseLinkDS/JDBCDriverParams/EclipseLinkDS/Properties/EclipseLinkDS')
cmo.createProperty('user')

cd('/JDBCSystemResources/EclipseLinkDS/JDBCResource/EclipseLinkDS/JDBCDriverParams/EclipseLinkDS/Properties/EclipseLinkDS/Properties/user')
cmo.setValue('%%DBUSR%%')

#cd('/JDBCSystemResources/EclipseLinkDS/JDBCResource/EclipseLinkDS/JDBCDataSourceParams/EclipseLinkDS')
#cmo.setGlobalTransactionsProtocol('OnePhaseCommit')

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
cmo.setUrl('%%DBURL%%')
cmo.setDriverName('%%DBDRV%%')
set('PasswordEncrypted','%%DBPWD%%')

cd('/JDBCSystemResources/ELNonJTADS/JDBCResource/ELNonJTADS/JDBCConnectionPoolParams/ELNonJTADS')
cmo.setTestTableName('SQL SELECT 1 FROM DUAL')

cd('/JDBCSystemResources/ELNonJTADS/JDBCResource/ELNonJTADS/JDBCDriverParams/ELNonJTADS/Properties/ELNonJTADS')
cmo.createProperty('user')

cd('/JDBCSystemResources/ELNonJTADS/JDBCResource/ELNonJTADS/JDBCDriverParams/ELNonJTADS/Properties/ELNonJTADS/Properties/user')
cmo.setValue('%%DBUSR%%')

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


