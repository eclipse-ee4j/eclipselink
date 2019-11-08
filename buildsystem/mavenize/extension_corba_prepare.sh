#!/usr/bin/env bash
#
# Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License v. 2.0 which is available at
# http://www.eclipse.org/legal/epl-2.0,
# or the Eclipse Distribution License v. 1.0 which is available at
# http://www.eclipse.org/org/documents/edl-v10.php.
#
# SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
#

echo "#####CORBA Extension module preparation - begin#####"

. ./extension_corba_environment.sh

#Prepare main java sources and resources
mkdir -p ${EXTENSION_CORBA_DIR}/src/main/java
#Corba classes
${SYNC_CMD} ${EXTENSION_CORBA_ORIGIN_DIR}/src/main/java/* ${EXTENSION_CORBA_DIR}/src/main/java/

#Prepare test java sources and resources
mkdir -p ${EXTENSION_CORBA_DIR}/src/it/java ${EXTENSION_CORBA_DIR}/src/it/resources
#Corba Test classes
${SYNC_CMD} ${EXTENSION_CORBA_TEST_ORIGIN_DIR}/src/* ${EXTENSION_CORBA_DIR}/src/it/java/
#Corba Test resources
${SYNC_CMD} ${EXTENSION_CORBA_TEST_ORIGIN_DIR}/resource/* ${EXTENSION_CORBA_DIR}/src/it/resources/
${RSYNC_CMD} ${CORE_TEST_DIR}/src/it/resources/java.policy.allpermissions ${EXTENSION_CORBA_DIR}/src/it/resources/java.policy.allpermissions

echo "#####CORBA Extension module preparation - end#####"
