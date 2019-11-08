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

echo "#####JPA TEST WDF module preparation - begin#####"

. ./jpa_test_wdf_environment.sh

#It's not required (comes from git) pom.xml is there
#mkdir -p ${JPA_WDF_TEST_DIR}

#Prepare test java sources and resources
mkdir -p ${JPA_WDF_TEST_DIR}/src/it/java ${JPA_WDF_TEST_DIR}/src/it/resources
#JPA WDF Test sources
${SYNC_CMD} ${JPA_WDF_TEST_ORIGIN_DIR}/src/* ${JPA_WDF_TEST_DIR}/src/it/java
#JPA WDF Test resources
${SYNC_CMD} ${JPA_WDF_TEST_ORIGIN_DIR}/resource/* ${JPA_WDF_TEST_DIR}/src/it/resources

echo "#####JPA TEST WDF module preparation - end#####"
