############################################################################  
# Generic script applicable on any Operating Environments (Unix, Windows)  
# ScriptName    : wls_deploy.py  
# Properties    : weblogic.properties test.properties 
# Author        : Kevin Yuan  
############################################################################   

#===========================================================================
# Connect to wls server
#===========================================================================

connect('%%WL_USR%%','%%WL_PWD%%','t3://%%WL_HOST%%:%%WL_PORT%%')

#===========================================================================
# Deploy applications to wls server
#===========================================================================

edit()
startEdit()
deploy(appName='%%appName%%', path='%%testDir%%/%%earName%%', targets='%%TARGET_SERVER%%')
#startApplication('%%appName%%')
#dumpStack()
save()
activate()
exit()