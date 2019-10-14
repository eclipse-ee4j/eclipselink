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

echo "#####DBWS BUILDER module preparation - begin#####"

. ./utils_dbws_builder_environment.sh

#Prepare main java sources and resources
mkdir -p ${DBWS_BUILDER_DIR}/src/main/java ${DBWS_BUILDER_DIR}/src/main/resources
${SYNC_CMD} ${DBWS_BUILDER_ORIGIN_DIR}/src/* ${DBWS_BUILDER_DIR}/src/main/java/
${SYNC_CMD} ${DBWS_BUILDER_ORIGIN_DIR}/resource/* ${DBWS_BUILDER_DIR}/src/main/resources/
${MV_CMD} ${DBWS_BUILDER_DIR}/src/main/java/META-INF ${DBWS_BUILDER_DIR}/src/main/resources

#Prepare test java sources and resources
mkdir -p ${DBWS_BUILDER_DIR}/src/it/java
#DBWS Test classes
${SYNC_CMD} ${DBWS_BUILDER_TEST_ORIGIN_DIR}/src/* ${DBWS_BUILDER_DIR}/src/it/java/

#Prepare Oracle test java sources and resources
mkdir -p ${DBWS_BUILDER_TEST_ORACLE}/src/it/java ${DBWS_BUILDER_TEST_ORACLE}/src/it/resources ${DBWS_BUILDER_TEST_ORACLE}/src/it/resources/sql
#DBWS Oracle Test classes
${SYNC_CMD} ${DBWS_BUILDER_TEST_ORACLE_ORIGIN_DIR}/src/* ${DBWS_BUILDER_TEST_ORACLE}/src/it/java/
#DBWS Oracle Test resources
${SYNC_CMD} ${DBWS_BUILDER_TEST_ORACLE_ORIGIN_DIR}/resource/* ${DBWS_BUILDER_TEST_ORACLE}/src/it/resources
#DBWS Oracle Test resources (DB setup/teardow scripts)
${SYNC_CMD} ${DBWS_BUILDER_TEST_ORACLE_ORIGIN_DIR}/etc/* ${DBWS_BUILDER_TEST_ORACLE}/src/it/resources/sql

#Prepare Oracle Server test java sources and resources
mkdir -p ${DBWS_BUILDER_TEST_ORACLE_SERVER}/src/it/java ${DBWS_BUILDER_TEST_ORACLE_SERVER}/src/it/resources
#DBWS Oracle Server Test classes
${SYNC_CMD} ${DBWS_BUILDER_TEST_ORACLE_SERVER_ORIGIN_DIR}/src/* ${DBWS_BUILDER_TEST_ORACLE_SERVER}/src/it/java/
#DBWS Oracle Server Test resources (war module)
${SYNC_CMD} ${DBWS_BUILDER_TEST_ORACLE_SERVER_ORIGIN_DIR}/etc/* ${DBWS_BUILDER_TEST_ORACLE_SERVER}/src/it/resources

echo "#####DBWS  BUILDER module preparation - end#####"
