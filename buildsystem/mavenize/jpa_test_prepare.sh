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

echo "#####JPA TEST module preparation - begin#####"

. ./jpa_test_environment.sh

#Prepare test java sources and resources
mkdir -p ${JPA_TEST_DIR}/src/it/java ${JPA_TEST_DIR}/src/it/resources
#JPA Test classes
${SYNC_CMD} ${JPA_TEST_ORIGIN_DIR}/src/* ${JPA_TEST_DIR}/src/it/java/
#JPA Test resources
${SYNC_CMD} ${JPA_TEST_ORIGIN_DIR}/resource/* ${JPA_TEST_DIR}/src/it/resources/
${RSYNC_CMD} resources/jpa.test/* ${JPA_TEST_DIR}/src/it/resources/wildfly/
#Move some java classes (modified by filtering) into Maven recommended folder
TEST_PACKAGE='org/eclipse/persistence/testing/framework/server'
mkdir -p ${JPA_TEST_DIR}/src/it/java-templates/${TEST_PACKAGE}
${MV_CMD} ${JPA_TEST_DIR}/src/it/java/${TEST_PACKAGE}/TestRunnerBean.java ${JPA_TEST_DIR}/src/it/java-templates/${TEST_PACKAGE}/TestRunnerBean.java
${MV_CMD} ${JPA_TEST_DIR}/src/it/java/${TEST_PACKAGE}/TestRunnerBean.java_nonjtaDS ${JPA_TEST_DIR}/src/it/java-templates/${TEST_PACKAGE}/TestRunnerBean.java_nonjtaDS

#Prepare core test framework
mkdir -p ${JPA_TEST_FRAMEWORK_DIR}/src/main/java
#Prepare jpa test framework packages
TEST_FRAMEWORK_PACKAGES=('org/eclipse/persistence/testing/framework')
for TEST_FRAMEWORK_PACKAGE in ${TEST_FRAMEWORK_PACKAGES[*]}
do
    mkdir -p ${JPA_TEST_FRAMEWORK_DIR}/src/main/java/${TEST_FRAMEWORK_PACKAGE}
    ${MV_CMD} ${JPA_TEST_DIR}/src/it/java/${TEST_FRAMEWORK_PACKAGE}/* ${JPA_TEST_FRAMEWORK_DIR}/src/main/java/${TEST_FRAMEWORK_PACKAGE}
    rm -rfv ${JPA_TEST_DIR}/src/it/java/${TEST_FRAMEWORK_PACKAGE}
done

echo "#####JPA TEST module preparation - end#####"
