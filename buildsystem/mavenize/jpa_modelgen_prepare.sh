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

echo "#####JPA MODEL GENERATOR module preparation - begin#####"

. ./jpa_modelgen_environment.sh
. ./jpa_environment.sh
. ./jpa_test_jse_environment.sh

#It's not required (comes from git) pom.xml is there
#mkdir -p ${JPA_MODELGEN_DIR}

#Prepare main java sources and resources
mkdir -p ${JPA_MODELGEN_DIR}/src/main/java ${JPA_MODELGEN_DIR}/src/main/resources
${SYNC_CMD} ${JPA_MODELGEN_ORIGIN_DIR}/src/* ${JPA_MODELGEN_DIR}/src/main/java/
${SYNC_CMD} ${JPA_MODELGEN_ORIGIN_DIR}/resource/* ${JPA_MODELGEN_DIR}/src/main/resources/

#Prepare test java sources and resources
mkdir -p ${JPA_MODELGEN_DIR}/src/test/java

#Move model CanonicalModelProcessor test from JPA JSE TEST module to JPA MODELGEN module
#Tests class location
TEST_PACKAGE='org/eclipse/persistence/jpa/test/modelgen'
mkdir -p ${JPA_MODELGEN_DIR}/src/test/java/${TEST_PACKAGE}
${MV_CMD} ${JPA_JSE_TEST_DIR}/src/it/java/${TEST_PACKAGE}/TestProcessor.java ${JPA_MODELGEN_DIR}/src/test/java/${TEST_PACKAGE}/TestProcessor.java

echo "#####JPA MODEL GENERATOR module preparation - end#####"
