#
# Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License 2.0 which is available at
# http://www.eclipse.org/legal/epl-2.0.
#
# SPDX-License-Identifier: EPL-2.0
#

############################################################################
# Generic script applicable on any Operating Environments (Unix, Windows)
# ScriptName    : wls_domain_reset.py
# Properties    : weblogic.properties test.properties
# Author        : Kevin Yuan
############################################################################

#===========================================================================
# Connect to wls server
#===========================================================================

connect('@WL_USR@','@WL_PWD@','t3://@WL_HOST@:@WL_PORT@')

#===========================================================================
# Set back openjpa as DefaultJPAProvider for wls server
#===========================================================================

edit()
startEdit()

cd('/JPA/@DOMAIN_NAME@')
cmo.setDefaultJPAProvider('org.apache.openjpa.persistence.PersistenceProviderImpl')

save()
activate()
exit()
