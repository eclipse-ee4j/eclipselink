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

echo "#####ASM module preparation - begin#####"

. ./asm_environment.sh

mkdir -p ${ASM_DIR}/${ASM_SRC_DIR}/${ASM_PACKAGE_DIR}
${SYNC_CMD} ${ASM_ORIGIN_DIR}/src/${ASM_PACKAGE_DIR}/EclipseLinkClassReader.java ${ASM_DIR}/${ASM_SRC_DIR}/${ASM_PACKAGE_DIR}/EclipseLinkClassReader.java

echo "#####ASM module preparation - end#####"
