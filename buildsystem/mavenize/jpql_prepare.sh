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

echo "#####JPQL module preparation - begin#####"

. ./jpql_environment.sh

#It's not required (comes from git) pom.xml is there
#mkdir -p ${JPQL_DIR}

#Prepare main java sources and resources
mkdir -p ${JPQL_DIR}/src/main/java ${JPQL_DIR}/src/main/resources
#JPQL Main classes
${SYNC_CMD} ${JPQL_ORIGIN_DIR}/src/* ${JPQL_DIR}/src/main/java/
#JPQL Main resources
${SYNC_CMD} ${JPQL_ORIGIN_DIR}/resource/* ${JPQL_DIR}/src/main/resources/


#Prepare test java sources and resources
mkdir -p ${JPQL_DIR}/src/test/java ${JPQL_DIR}/src/test/resources
#JPQL Test classes
${SYNC_CMD} ${JPQL_TEST_ORIGIN_DIR}/src/* ${JPQL_DIR}/src/test/java/
#JPQL Test entity classes
${SYNC_CMD} ${JPQL_TEST_ORIGIN_DIR}/resource/source/jpql ${JPQL_DIR}/src/test/java/
#JPQL Test resources
${SYNC_CMD} ${JPQL_TEST_ORIGIN_DIR}/resource/source/META-INF ${JPQL_DIR}/src/test/resources/

echo "#####JPQL module preparation - end#####"
