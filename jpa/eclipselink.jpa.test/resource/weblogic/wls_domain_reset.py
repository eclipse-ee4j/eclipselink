############################################################################  
# Generic script applicable on any Operating Environments (Unix, Windows)  
# ScriptName    : wls_domain_reset.py  
# Properties    : weblogic.properties test.properties 
# Author        : Kevin Yuan  
############################################################################   

#===========================================================================
# Connect to wls server
#===========================================================================

connect('%%WL_USR%%','%%WL_PWD%%','t3://%%WL_HOST%%:%%WL_PORT%%')

#===========================================================================
# Set back openjpa as DefaultJPAProvider for wls server
#===========================================================================

edit()
startEdit()

cd('/JPA/mydomain')
cmo.setDefaultJPAProvider('org.apache.openjpa.persistence.PersistenceProviderImpl')

save()
activate()
exit()