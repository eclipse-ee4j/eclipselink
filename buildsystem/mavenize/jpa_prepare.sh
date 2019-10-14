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

echo "#####JPA module preparation - begin#####"

. ./jpa_environment.sh

#It's not required (comes from git) pom.xml is there
#mkdir -p ${JPA_DIR}

#Prepare main java sources and resources
mkdir -p ${JPA_DIR}/src/main/java ${JPA_DIR}/src/main/resources
#JPA Main classes
${SYNC_CMD} ${JPA_ORIGIN_DIR}/src/* ${JPA_DIR}/src/main/java/
#JPA Main resources
${SYNC_CMD} ${JPA_ORIGIN_DIR}/resource/* ${JPA_DIR}/src/main/resources/

echo "#####JPA module preparation - end#####"
