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
cmo.createJDBCSystemResource('%%DS_NAME%%')

cd('/JDBCSystemResources/%%DS_NAME%%/JDBCResource/%%DS_NAME%%')
cmo.setName('%%DS_NAME%%')

cd('/JDBCSystemResources/%%DS_NAME%%/JDBCResource/%%DS_NAME%%/JDBCDataSourceParams/%%DS_NAME%%')
set('JNDINames',jarray.array([String('jdbc/%%DS_NAME%%')], String))

cd('/JDBCSystemResources/%%DS_NAME%%/JDBCResource/%%DS_NAME%%/JDBCDriverParams/%%DS_NAME%%')
cmo.setUrl('%%DBURL%%')
cmo.setDriverName('%%DBDRV%%')
set('PasswordEncrypted','%%DBPWD%%')

cd('/JDBCSystemResources/%%DS_NAME%%/JDBCResource/%%DS_NAME%%/JDBCConnectionPoolParams/%%DS_NAME%%')
cmo.setTestTableName('SQL SELECT 1 FROM DUAL')

cd('/JDBCSystemResources/%%DS_NAME%%/JDBCResource/%%DS_NAME%%/JDBCDriverParams/%%DS_NAME%%/Properties/%%DS_NAME%%')
cmo.createProperty('user')

cd('/JDBCSystemResources/%%DS_NAME%%/JDBCResource/%%DS_NAME%%/JDBCDriverParams/%%DS_NAME%%/Properties/%%DS_NAME%%/Properties/user')
cmo.setValue('%%DBUSR%%')

#cd('/JDBCSystemResources/%%DS_NAME%%/JDBCResource/%%DS_NAME%%/JDBCDataSourceParams/%%DS_NAME%%')
#cmo.setGlobalTransactionsProtocol('OnePhaseCommit')

cd('/SystemResources/%%DS_NAME%%')
set('Targets',jarray.array([ObjectName('com.bea:Name=%%TARGET_SERVER%%,Type=Server')], ObjectName))

save()
activate()

#===========================================================================
# Create and configure Non-JTA Data Source and target it to the server.
#===========================================================================

edit()
startEdit()

cd('/')
cmo.createJDBCSystemResource('%%NON_JTA_DS_NAME%%')

cd('/JDBCSystemResources/%%NON_JTA_DS_NAME%%/JDBCResource/%%NON_JTA_DS_NAME%%')
cmo.setName('%%NON_JTA_DS_NAME%%')

cd('/JDBCSystemResources/%%NON_JTA_DS_NAME%%/JDBCResource/%%NON_JTA_DS_NAME%%/JDBCDataSourceParams/%%NON_JTA_DS_NAME%%')
set('JNDINames',jarray.array([String('jdbc/%%NON_JTA_DS_NAME%%')], String))

cd('/JDBCSystemResources/%%NON_JTA_DS_NAME%%/JDBCResource/%%NON_JTA_DS_NAME%%/JDBCDriverParams/%%NON_JTA_DS_NAME%%')
cmo.setUrl('%%DBURL%%')
cmo.setDriverName('%%DBDRV%%')
set('PasswordEncrypted','%%DBPWD%%')

cd('/JDBCSystemResources/%%NON_JTA_DS_NAME%%/JDBCResource/%%NON_JTA_DS_NAME%%/JDBCConnectionPoolParams/%%NON_JTA_DS_NAME%%')
cmo.setTestTableName('SQL SELECT 1 FROM DUAL')

cd('/JDBCSystemResources/%%NON_JTA_DS_NAME%%/JDBCResource/%%NON_JTA_DS_NAME%%/JDBCDriverParams/%%NON_JTA_DS_NAME%%/Properties/%%NON_JTA_DS_NAME%%')
cmo.createProperty('user')

cd('/JDBCSystemResources/%%NON_JTA_DS_NAME%%/JDBCResource/%%NON_JTA_DS_NAME%%/JDBCDriverParams/%%NON_JTA_DS_NAME%%/Properties/%%NON_JTA_DS_NAME%%/Properties/user')
cmo.setValue('%%DBUSR%%')

cd('/JDBCSystemResources/%%NON_JTA_DS_NAME%%/JDBCResource/%%NON_JTA_DS_NAME%%/JDBCDataSourceParams/%%NON_JTA_DS_NAME%%')
cmo.setGlobalTransactionsProtocol('None')

cd('/SystemResources/%%NON_JTA_DS_NAME%%')
set('Targets',jarray.array([ObjectName('com.bea:Name=%%TARGET_SERVER%%,Type=Server')], ObjectName))

save()
activate()

#===========================================================================
# Exit WLST.
#===========================================================================

exit()
