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

echo "#####JPA TEST JPA-RS test server module preparation - begin#####"

. ./jpa_test_jpars_server_environment.sh

#Prepare java sources and resources for web module
mkdir -p ${JPA_JPARS_TEST_DIR}/src/main/java ${JPA_JPARS_TEST_DIR}/src/main/resources/META-INF ${JPA_JPARS_TEST_DIR}/src/main/webapp/WEB-INF
#JPA JPARS Web module classes
${RSYNC_CMD} ${JPA_JPARS_DIR}/src/main/java/ ${JPA_JPARS_TEST_DIR}/src/main/java/
#JPA JPARS Web module resources
${RSYNC_CMD} ${JPA_JPARS_DIR}/src/main/resources/META-INF/* ${JPA_JPARS_TEST_DIR}/src/main/resources/META-INF
#JPA JPARS Web module web content
${SYNC_CMD} ${JPA_JPARS_TEST_ORIGIN_DIR}/WebContent/WEB-INF/*  ${JPA_JPARS_TEST_DIR}/src/main/webapp/WEB-INF

#Prepare test java sources and resources for integration-test
mkdir -p ${JPA_JPARS_TEST_DIR}/src/test/java ${JPA_JPARS_TEST_DIR}/src/test/resources ${JPA_JPARS_TEST_DIR}/src/test/resources/META-INF
#JPA JPARS integration-test module classes
${RSYNC_CMD} ${JPA_JPARS_DIR}/src/it/java/* ${JPA_JPARS_TEST_DIR}/src/test/java/
#JPA JPARS integration-test module resources
${RSYNC_CMD} ${JPA_JPARS_DIR}/src/it/resources/META-INF/* ${JPA_JPARS_TEST_DIR}/src/test/resources/META-INF

echo "#####JPA TEST JPA-RS test server module preparation - end#####"
