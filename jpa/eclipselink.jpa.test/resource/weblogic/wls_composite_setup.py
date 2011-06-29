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

# Create EclipseLinkDS for server-test-lrg

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
set('Targets',jarray.array([ObjectName('com.bea:Name=%%TARGET_SERVER%%,Type=Server')], ObjectName))

# Create EclipseLinkDS2 for server-test-composite-advanced (DB2)

cd('/')
cmo.createJDBCSystemResource('EclipseLinkDS2')

cd('/JDBCSystemResources/EclipseLinkDS2/JDBCResource/EclipseLinkDS2')
cmo.setName('EclipseLinkDS2')

cd('/JDBCSystemResources/EclipseLinkDS2/JDBCResource/EclipseLinkDS2/JDBCDataSourceParams/EclipseLinkDS2')
set('JNDINames',jarray.array([String('jdbc/EclipseLinkDS2')], String))

cd('/JDBCSystemResources/EclipseLinkDS2/JDBCResource/EclipseLinkDS2/JDBCDriverParams/EclipseLinkDS2')
cmo.setUrl('%%DB2URL%%')
cmo.setDriverName('%%DB2DRV%%')
set('PasswordEncrypted','%%DB2PWD%%')

cd('/JDBCSystemResources/EclipseLinkDS2/JDBCResource/EclipseLinkDS2/JDBCConnectionPoolParams/EclipseLinkDS2')
cmo.setTestTableName('SQL SELECT 1 FROM DUAL')

cd('/JDBCSystemResources/EclipseLinkDS2/JDBCResource/EclipseLinkDS2/JDBCDriverParams/EclipseLinkDS2/Properties/EclipseLinkDS2')
cmo.createProperty('user')

cd('/JDBCSystemResources/EclipseLinkDS2/JDBCResource/EclipseLinkDS2/JDBCDriverParams/EclipseLinkDS2/Properties/EclipseLinkDS2/Properties/user')
cmo.setValue('%%DB2USR%%')

cd('/JDBCSystemResources/EclipseLinkDS2/JDBCResource/EclipseLinkDS2/JDBCDriverParams/EclipseLinkDS2/Properties/EclipseLinkDS2')
cmo.createProperty('portNumber')

cd('/JDBCSystemResources/EclipseLinkDS2/JDBCResource/EclipseLinkDS2/JDBCDriverParams/EclipseLinkDS2/Properties/EclipseLinkDS2/Properties/portNumber')
cmo.setValue('50000')

cd('/JDBCSystemResources/EclipseLinkDS2/JDBCResource/EclipseLinkDS2/JDBCDriverParams/EclipseLinkDS2/Properties/EclipseLinkDS2')
cmo.createProperty('databaseName')

cd('/JDBCSystemResources/EclipseLinkDS2/JDBCResource/EclipseLinkDS2/JDBCDriverParams/EclipseLinkDS2/Properties/EclipseLinkDS2/Properties/databaseName')
cmo.setValue('TOPLINK2')

cd('/JDBCSystemResources/EclipseLinkDS2/JDBCResource/EclipseLinkDS2/JDBCDriverParams/EclipseLinkDS2/Properties/EclipseLinkDS2')
cmo.createProperty('serverName')

cd('/JDBCSystemResources/EclipseLinkDS2/JDBCResource/EclipseLinkDS2/JDBCDriverParams/EclipseLinkDS2/Properties/EclipseLinkDS2/Properties/serverName')
cmo.setValue('ottvm046.ca.oracle.com')

cd('/JDBCSystemResources/EclipseLinkDS2/JDBCResource/EclipseLinkDS2/JDBCDataSourceParams/EclipseLinkDS2')
cmo.setGlobalTransactionsProtocol('TwoPhaseCommit')

cd('/SystemResources/EclipseLinkDS2')
set('Targets',jarray.array([ObjectName('com.bea:Name=%%TARGET_SERVER%%,Type=Server')], ObjectName))

# Create EclipseLinkDS3 for server-test-composite-advanced (MySQL)

cd('/')
cmo.createJDBCSystemResource('EclipseLinkDS3')

cd('/JDBCSystemResources/EclipseLinkDS3/JDBCResource/EclipseLinkDS3')
cmo.setName('EclipseLinkDS3')

cd('/JDBCSystemResources/EclipseLinkDS3/JDBCResource/EclipseLinkDS3/JDBCDataSourceParams/EclipseLinkDS3')
set('JNDINames',jarray.array([String('jdbc/EclipseLinkDS3')], String))

cd('/JDBCSystemResources/EclipseLinkDS3/JDBCResource/EclipseLinkDS3/JDBCDriverParams/EclipseLinkDS3')
cmo.setUrl('%%DB3URL%%')
cmo.setDriverName('%%DB3DRV%%')
set('PasswordEncrypted','%%DB3PWD%%')

cd('/JDBCSystemResources/EclipseLinkDS3/JDBCResource/EclipseLinkDS3/JDBCConnectionPoolParams/EclipseLinkDS3')
cmo.setTestTableName('SQL SELECT 1 FROM DUAL')

cd('/JDBCSystemResources/EclipseLinkDS3/JDBCResource/EclipseLinkDS3/JDBCDriverParams/EclipseLinkDS3/Properties/EclipseLinkDS3')
cmo.createProperty('user')

cd('/JDBCSystemResources/EclipseLinkDS3/JDBCResource/EclipseLinkDS3/JDBCDriverParams/EclipseLinkDS3/Properties/EclipseLinkDS3/Properties/user')
cmo.setValue('%%DB3USR%%')

cd('/JDBCSystemResources/EclipseLinkDS3/JDBCResource/EclipseLinkDS3/JDBCDataSourceParams/EclipseLinkDS3')
cmo.setGlobalTransactionsProtocol('EmulateTwoPhaseCommit')

cd('/SystemResources/EclipseLinkDS3')
set('Targets',jarray.array([ObjectName('com.bea:Name=%%TARGET_SERVER%%,Type=Server')], ObjectName))

save()
activate()

#===========================================================================
# Exit WLST.
#===========================================================================

exit()


