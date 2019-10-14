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

echo "#####DBWS module preparation - begin#####"

. ./dbws_environment.sh

#Prepare main java sources and resources
mkdir -p ${DBWS_DIR}/src/main/java ${DBWS_DIR}/src/main/resources
${SYNC_CMD} ${DBWS_ORIGIN_DIR}/src/* ${DBWS_DIR}/src/main/java/
${SYNC_CMD} ${DBWS_ORIGIN_DIR}/resource/* ${DBWS_DIR}/src/main/resources/

#Prepare test java sources and resources
mkdir -p ${DBWS_DIR}/src/it/java ${DBWS_DIR}/src/it/resources ${DBWS_DIR}/src/it/resources/sql
#DBWS Test classes
${SYNC_CMD} ${DBWS_TEST_ORIGIN_DIR}/src/* ${DBWS_DIR}/src/it/java/
#DBWS Test resources
${SYNC_CMD} ${DBWS_TEST_ORIGIN_DIR}/etc/* ${DBWS_DIR}/src/it/resources/sql/

#DBWS Oracle Test module
mkdir -p ${DBWS_ORACLE_DIR}/src/it/java ${DBWS_ORACLE_DIR}/src/it/resources ${DBWS_ORACLE_DIR}/src/it/resources/sql
#DBWS Oracle Test classes
${SYNC_CMD} ${DBWS_TEST_ORACLE_ORIGIN_DIR}/src/* ${DBWS_ORACLE_DIR}/src/it/java/
#DBWS Test resources
${SYNC_CMD} ${DBWS_TEST_ORACLE_ORIGIN_DIR}/etc/* ${DBWS_ORACLE_DIR}/src/it/resources/sql

echo "#####DBWS module preparation - end#####"
