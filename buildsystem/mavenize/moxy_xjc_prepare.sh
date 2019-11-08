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

echo "#####MOXY XJC module preparation - begin#####"

. ./moxy_xjc_environment.sh

#Prepare main java sources and resources
mkdir -p ${MOXY_UTILS_XJC_DIR}/src/main/java/org
${SYNC_CMD} ${MOXY_UTILS_XJC_ORIGIN_DIR}/src/org/* ${MOXY_UTILS_XJC_DIR}/src/main/java/org/

echo "#####MOXY XJC module preparation - end#####"
