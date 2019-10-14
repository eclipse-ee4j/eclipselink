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

echo "#####JPA TEST JAXRS module preparation - begin#####"

. ./jpa_test_jaxrs_environment.sh

#It's not required (comes from git) pom.xml is there
#mkdir -p ${JPA_JAXRS_TEST_DIR}

#Prepare main java sources and resources
mkdir -p ${JPA_JAXRS_TEST_DIR}/src/main/java ${JPA_JAXRS_TEST_DIR}/src/main/resources ${JPA_JAXRS_TEST_DIR}/src/main/webapp
${SYNC_CMD} ${JPA_JAXRS_TEST_ORIGIN_DIR}/src/main/java/* ${JPA_JAXRS_TEST_DIR}/src/main/java/
${SYNC_CMD} ${JPA_JAXRS_TEST_ORIGIN_DIR}/src/main/resources/* ${JPA_JAXRS_TEST_DIR}/src/main/resources/
${MV_CMD} ${JPA_JAXRS_TEST_DIR}/src/main/resources/WEB-INF ${JPA_JAXRS_TEST_DIR}/src/main/webapp

#TODO should be removed after origin project modifications
${MV_CMD} ${JPA_JAXRS_TEST_DIR}/src/main/resources/binding-address.xml ${JPA_JAXRS_TEST_DIR}/src/main/resources/META-INF/binding-address.xml
${MV_CMD} ${JPA_JAXRS_TEST_DIR}/src/main/resources/binding-phonenumber.xml ${JPA_JAXRS_TEST_DIR}/src/main/resources/META-INF/binding-phonenumber.xml
mkdir -p ${JPA_JAXRS_TEST_DIR}/src/main/resources/org/eclipse/persistence/testing/jaxrs/model
${MV_CMD} ${JPA_JAXRS_TEST_DIR}/src/main/resources/jaxb.properties ${JPA_JAXRS_TEST_DIR}/src/main/resources/org/eclipse/persistence/testing/jaxrs/model/jaxb.properties

#Prepare test java sources and resources
mkdir -p ${JPA_JAXRS_TEST_DIR}/src/it/java ${JPA_JAXRS_TEST_DIR}/src/it/resources
#JPA JAXRS Test classes
${SYNC_CMD} ${JPA_JAXRS_TEST_ORIGIN_DIR}/src/it/java/* ${JPA_JAXRS_TEST_DIR}/src/it/java/
#JPA JAXRS Test resources
${SYNC_CMD} ${JPA_JAXRS_TEST_ORIGIN_DIR}/src/it/resources/* ${JPA_JAXRS_TEST_DIR}/src/it/resources/

echo "#####JPA TEST JAXRS module preparation - end#####"
