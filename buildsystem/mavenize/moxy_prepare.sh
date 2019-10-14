#!/usr/bin/env bash
#
# Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License v. 2.0 which is available at
# http://www.eclipse.org/legal/epl-2.0,
# or the Eclipse Distribution License v. 1.0 which is available at
# http://www.eclipse.org/org/documents/edl-v10.php.
#
# SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
#

echo "#####MOXY module preparation - begin#####"

. ./moxy_environment.sh

#Prepare main java sources and resources
mkdir -p ${MOXY_DIR}/src/main/java/org ${MOXY_DIR}/src/main/resources
${SYNC_CMD} ${MOXY_ORIGIN_DIR}/src/org/* ${MOXY_DIR}/src/main/java/org/
${SYNC_CMD} ${MOXY_ORIGIN_DIR}/resource/* ${MOXY_DIR}/src/main/resources/
#Insert MOXy dynamic xjc sources
${SYNC_CMD} ${MOXY_XJC_ORIGIN_DIR}/src/org/eclipse/persistence/jaxb/dynamic/metadata/* ${MOXY_DIR}/src/main/java/org/eclipse/persistence/jaxb/dynamic/metadata/
${SYNC_CMD} ${MOXY_XJC_ORIGIN_DIR}/src/org/eclipse/persistence/jaxb/javamodel/* ${MOXY_DIR}/src/main/java/org/eclipse/persistence/jaxb/javamodel/

#Prepare test java sources and resources
mkdir -p ${MOXY_DIR}/src/test/java ${MOXY_DIR}/src/test/resources
#MOXy Test classes
${SYNC_CMD} ${MOXY_TEST_ORIGIN_DIR}/src/* ${MOXY_DIR}/src/test/java/
#MOXy Test resources
${SYNC_CMD} ${MOXY_TEST_ORIGIN_DIR}/resource/* ${MOXY_DIR}/src/test/resources/

echo "#####MOXY module preparation - end#####"
