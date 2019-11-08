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

echo "#####SDO module preparation - begin#####"

. ./sdo_environment.sh

#Prepare main java sources and resources
mkdir -p ${SDO_DIR}/src/main/java
${SYNC_CMD} ${SDO_ORIGIN_DIR}/src/* ${SDO_DIR}/src/main/java/

#Prepare test java sources and resources
mkdir -p ${SDO_DIR}/src/test/java ${SDO_DIR}/src/test/resources/
#SDO Test classes
${SYNC_CMD} ${SDO_TEST_ORIGIN_DIR}/src/* ${SDO_DIR}/src/test/java/
#SDO Test resources
${SYNC_CMD} ${SDO_TEST_ORIGIN_DIR}/resource/* ${SDO_DIR}/src/test/resources/

#SDO Server Test module
mkdir -p ${SDO_TEST_SERVER_DIR}/src/it/java ${SDO_TEST_SERVER_DIR}/src/it/resources
${SYNC_CMD} ${SDO_TEST_SERVER_ORIGIN_DIR}/src/* ${SDO_TEST_SERVER_DIR}/src/it/java/
${SYNC_CMD} ${SDO_TEST_SERVER_ORIGIN_DIR}/resource/* ${SDO_TEST_SERVER_DIR}/src/it/resources/
${RSYNC_CMD} resources/sdo.test.server/* ${SDO_TEST_SERVER_DIR}/src/it/resources/

echo "#####SDO module preparation - end#####"
