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

echo "#####NoSQL Extension module preparation - begin#####"

. ./extension_nosql_environment.sh

#Prepare main java sources and resources
mkdir -p ${EXTENSION_NOSQL_DIR}/src/main/java
${SYNC_CMD} ${EXTENSION_NOSQL_ORIGIN_DIR}/src/* ${EXTENSION_NOSQL_DIR}/src/main/java/

#Prepare test java sources and resources
mkdir -p ${EXTENSION_NOSQL_DIR}/src/it/java ${EXTENSION_NOSQL_DIR}/src/it/resources
#NoSQL Extension Test classes
${SYNC_CMD} ${EXTENSION_NOSQL_TEST_ORIGIN_DIR}/src/* ${EXTENSION_NOSQL_DIR}/src/it/java/
#NoSQL Extension Test resources
${SYNC_CMD} ${EXTENSION_NOSQL_TEST_ORIGIN_DIR}/resource/* ${EXTENSION_NOSQL_DIR}/src/it/resources/

echo "#####NoSQL Extension module preparation - end#####"
