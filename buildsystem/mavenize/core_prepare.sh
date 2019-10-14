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

echo "#####CORE module preparation - begin#####"

. ./core_environment.sh

#Prepare main java sources and resources
mkdir -p ${CORE_DIR}/src/main/java ${CORE_DIR}/src/main/resources/org
${SYNC_CMD} ${CORE_ORIGIN_DIR}/src/* ${CORE_DIR}/src/main/java/
${SYNC_CMD} ${CORE_ORIGIN_DIR}/resource/org/* ${CORE_DIR}/src/main/resources/org/

#Prepare test java sources and resources
mkdir -p ${CORE_TEST_DIR}/src/test/java ${CORE_TEST_DIR}/src/it/java ${CORE_TEST_DIR}/src/it/resources
#Core Test classes
${SYNC_CMD} ${CORE_TEST_ORIGIN_DIR}/src/* ${CORE_TEST_DIR}/src/it/java/
#Core Test resources
${SYNC_CMD} ${CORE_TEST_ORIGIN_DIR}/resource/* ${CORE_TEST_DIR}/src/it/resources/

#Prepare Unit tests
TEST_JUNIT_PACKAGE='org/eclipse/persistence/testing/tests/junit'
mkdir -p ${CORE_TEST_DIR}/src/test/java/${TEST_JUNIT_PACKAGE}
${MV_CMD} ${CORE_TEST_DIR}/src/it/java/${TEST_JUNIT_PACKAGE}/* ${CORE_TEST_DIR}/src/test/java/${TEST_JUNIT_PACKAGE}
rm -rfv ${CORE_TEST_DIR}/src/it/java/${TEST_JUNIT_PACKAGE}

#Prepare core test framework
mkdir -p ${CORE_TEST_FRAMEWORK_DIR}/src/main/java
#Prepare core test framework packages
TEST_FRAMEWORK_PACKAGES=('org/eclipse/persistence/testing/framework' 'org/eclipse/persistence/tools' 'org/eclipse/persistence/testing/tests/performance/emulateddb')
for TEST_FRAMEWORK_PACKAGE in ${TEST_FRAMEWORK_PACKAGES[*]}
do
    mkdir -p ${CORE_TEST_FRAMEWORK_DIR}/src/main/java/${TEST_FRAMEWORK_PACKAGE}
    ${MV_CMD} ${CORE_TEST_DIR}/src/it/java/${TEST_FRAMEWORK_PACKAGE}/* ${CORE_TEST_FRAMEWORK_DIR}/src/main/java/${TEST_FRAMEWORK_PACKAGE}
    rm -rfv ${CORE_TEST_DIR}/src/it/java/${TEST_FRAMEWORK_PACKAGE}
done
${MV_CMD} ${CORE_TEST_DIR}/src/it/java/org/eclipse/persistence/testing/tests/TestRunModel.java ${CORE_TEST_FRAMEWORK_DIR}/src/main/java/org/eclipse/persistence/testing/tests/TestRunModel.java
${MV_CMD} ${CORE_TEST_DIR}/src/it/java/org/eclipse/persistence/testing/tests/ClearDatabaseSchemaTest.java ${CORE_TEST_FRAMEWORK_DIR}/src/main/java/org/eclipse/persistence/testing/tests/ClearDatabaseSchemaTest.java
echo "#####CORE module preparation - end#####"
