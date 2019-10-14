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

echo "#####Performance Tests module preparation - begin#####"

. ./performance_tests_environment.sh

#Prepare test java sources and resources
mkdir -p ${PERFORMANCE_TESTS_DIR}/src/test/java ${PERFORMANCE_TESTS_DIR}/src/test/resources
#Performance Test classes
${SYNC_CMD} ${PERFORMANCE_TESTS_ORIGIN_DIR}/src/* ${PERFORMANCE_TESTS_DIR}/src/test/java/
#Performance Test resources
${SYNC_CMD} ${PERFORMANCE_TESTS_ORIGIN_DIR}/resource/* ${PERFORMANCE_TESTS_DIR}/src/test/resources/

#TODO FIX for wrong location of jaxb.properties in src instead of resources
mkdir -p ${PERFORMANCE_TESTS_DIR}/src/test/resources/org/eclipse/persistence/testing/perf/moxy/referenceresolver
${MV_CMD} ${PERFORMANCE_TESTS_DIR}/src/test/java/org/eclipse/persistence/testing/perf/moxy/referenceresolver/jaxb.properties ${PERFORMANCE_TESTS_DIR}/src/test/resources/org/eclipse/persistence/testing/perf/moxy/referenceresolver/jaxb.properties
mkdir -p ${PERFORMANCE_TESTS_DIR}/src/test/resources/org/eclipse/persistence/testing/perf/largexml/bigpo
${MV_CMD} ${PERFORMANCE_TESTS_DIR}/src/test/java/org/eclipse/persistence/testing/perf/largexml/bigpo/jaxb.properties ${PERFORMANCE_TESTS_DIR}/src/test/resources/org/eclipse/persistence/testing/perf/largexml/bigpo/jaxb.properties
mkdir -p ${PERFORMANCE_TESTS_DIR}/src/test/resources/org/eclipse/persistence/testing/perf/json/model
${MV_CMD} ${PERFORMANCE_TESTS_DIR}/src/test/java/org/eclipse/persistence/testing/perf/json/model/jaxb.properties ${PERFORMANCE_TESTS_DIR}/src/test/resources/org/eclipse/persistence/testing/perf/json/model/jaxb.properties
mkdir -p ${PERFORMANCE_TESTS_DIR}/src/test/resources/org/eclipse/persistence/testing/perf/smallxml/workorder
${MV_CMD} ${PERFORMANCE_TESTS_DIR}/src/test/java/org/eclipse/persistence/testing/perf/smallxml/workorder/jaxb.properties ${PERFORMANCE_TESTS_DIR}/src/test/resources/org/eclipse/persistence/testing/perf/smallxml/workorder/jaxb.properties


echo "#####Performance Tests module preparation - end#####"
