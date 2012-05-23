############################################################################  
# Generic script applicable on any Operating Environments (Unix, Windows)  
# ScriptName    : wls_domain_setup.py  
# Properties    : weblogic.properties test.properties 
# Author        : Kevin Yuan  
############################################################################   

#===========================================================================
# Connect to wls server
#===========================================================================

connect('%%WL_USR%%','%%WL_PWD%%','t3://%%WL_HOST%%:%%WL_PORT%%')

#===========================================================================
# Set EclipseLink JPA as DefaultJPAProvider for wls server
#===========================================================================

edit()
startEdit()

cd('/JPA/%%DOMAIN_NAME%%')
cmo.setDefaultJPAProvider('org.eclipse.persistence.jpa.PersistenceProvider')

save()
activate()
exit()