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

echo "#####UTILS RENAME module preparation - begin#####"

. ./utils_rename_environment.sh

#Prepare main java sources and resources
mkdir -p ${UTILS_RENAME_DIR}/src/main/java ${UTILS_RENAME_DIR}/src/main/resources
${SYNC_CMD} ${UTILS_RENAME_ORIGIN_DIR}/src/* ${UTILS_RENAME_DIR}/src/main/java/
${SYNC_CMD} ${UTILS_RENAME_ORIGIN_DIR}/resource/* ${UTILS_RENAME_DIR}/src/main/resources/

echo "#####UTILS RENAME module preparation - end#####"
