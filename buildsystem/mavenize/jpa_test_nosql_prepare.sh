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

echo "#####JPA TEST NOSQL module preparation - begin#####"

. ./jpa_test_nosql_environment.sh

#Prepare test java sources and resources
mkdir -p ${JPA_TEST_NOSQL_DIR}/src/it ${JPA_TEST_NOSQL_DIR}/src/it/resources/server

#JPA Test resources
${RSYNC_CMD} ${JPA_TEST_DIR}/src/it/resources/server/* ${JPA_TEST_NOSQL_DIR}/src/it/resources/server/
${SYNC_CMD} ${JPA_TEST_DIR}/src/it/resources/eclipselink-mongo-model/* ${JPA_TEST_NOSQL_DIR}/src/it/resources/eclipselink-mongo-model/
${SYNC_CMD} ${JPA_TEST_DIR}/src/it/resources/eclipselink-mongo-model-xml/* ${JPA_TEST_NOSQL_DIR}/src/it/resources/eclipselink-mongo-model-xml/
${RSYNC_CMD} resources/jpa.nosql.test/* ${JPA_TEST_NOSQL_DIR}/src/it/resources/wildfly/
#Move some java classes (modified by filtering) into Maven recommended folder
TEST_SERVER_PACKAGE='org/eclipse/persistence/testing/framework/server'
mkdir -p ${JPA_TEST_NOSQL_DIR}/src/it/java-templates/${TEST_SERVER_PACKAGE}
${RSYNC_CMD} ${JPA_TEST_DIR}/src/it/java-templates/${TEST_SERVER_PACKAGE}/TestRunnerBean.java ${JPA_TEST_NOSQL_DIR}/src/it/java-templates/${TEST_SERVER_PACKAGE}/TestRunnerBean.java
${RSYNC_CMD} ${JPA_TEST_DIR}/src/it/java-templates/${TEST_SERVER_PACKAGE}/TestRunnerBean.java_nonjtaDS ${JPA_TEST_NOSQL_DIR}/src/it/java-templates/${TEST_SERVER_PACKAGE}/TestRunnerBean.java_nonjtaDS

echo "#####JPA TEST NOSQL module preparation - end#####"
