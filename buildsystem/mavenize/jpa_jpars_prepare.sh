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

echo "#####JPA TEST JPA-RS module preparation - begin#####"

. ./jpa_jpars_environment.sh

#Prepare main java sources and resources
mkdir -p ${JPA_JPARS_DIR}/src/main/java ${JPA_JPARS_DIR}/src/main/resources/META-INF
#JPA-RS classes
${SYNC_CMD} ${JPA_JPARS_ORIGIN_DIR}/src/org ${JPA_JPARS_DIR}/src/main/java/
#JPA-RS resources
${SYNC_CMD} ${JPA_JPARS_ORIGIN_DIR}/src/META-INF/* ${JPA_JPARS_DIR}/src/main/resources/META-INF

#Prepare test java sources and resources
mkdir -p ${JPA_JPARS_DIR}/src/it/java ${JPA_JPARS_DIR}/src/it/resources ${JPA_JPARS_DIR}/src/it/resources/META-INF
#JPA-RS Test classes
${SYNC_CMD} ${JPA_JPARS_TEST_ORIGIN_DIR}/src/org ${JPA_JPARS_DIR}/src/it/java/
#JPA-RS Test resources
${SYNC_CMD} ${JPA_JPARS_TEST_ORIGIN_DIR}/resource/org ${JPA_JPARS_DIR}/src/it/resources/
${SYNC_CMD} ${JPA_JPARS_TEST_ORIGIN_DIR}/src/META-INF/* ${JPA_JPARS_DIR}/src/it/resources/META-INF

echo "#####JPA TEST JPA-RS module preparation - end#####"
