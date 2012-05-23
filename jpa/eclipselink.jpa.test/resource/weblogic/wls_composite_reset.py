############################################################################  
# Generic script applicable on any Operating Environments (Unix, Windows)  
# ScriptName    : wls_reset.py  
# Properties    : weblogic.properties  
# Author        : Kevin Yuan  
############################################################################   

#===========================================================================
# Connect to wls server
#===========================================================================

connect('%%WL_USR%%','%%WL_PWD%%','t3://%%WL_HOST%%:%%WL_PORT%%')

#===========================================================================
# Remove Data Sources using wlst on-line commonds for three composite models
#===========================================================================

edit()
startEdit()
delete('EclipseLinkDS','JDBCSystemResource')
delete('EclipseLinkDS2','JDBCSystemResource')
delete('EclipseLinkDS3','JDBCSystemResource')
save()
activate()
exit()