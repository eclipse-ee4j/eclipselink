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

echo "#####Oracle Spatial Extension module preparation - begin#####"

. ./extension_oracle_spatial_environment.sh

#Prepare test java sources and resources
mkdir -p ${EXTENSION_ORACLE_SPATIAL_DIR}/src/it/java ${EXTENSION_ORACLE_SPATIAL_DIR}/src/it/resources ${EXTENSION_ORACLE_SPATIAL_DIR}/src/it/resources/META-INF
#Oracle Test classes
${SYNC_CMD} ${EXTENSION_ORACLE_SPATIAL_ORIGIN_DIR}/src/* ${EXTENSION_ORACLE_SPATIAL_DIR}/src/it/java/
#Oracle Test resources
${SYNC_CMD} ${EXTENSION_ORACLE_SPATIAL_ORIGIN_DIR}/resource/* ${EXTENSION_ORACLE_SPATIAL_DIR}/src/it/resources/
#Move persistence.xml and struct-converter-entity-mappings.xml to META-INF
${MV_CMD} ${EXTENSION_ORACLE_SPATIAL_DIR}/src/it/resources/structconverter/persistence.xml ${EXTENSION_ORACLE_SPATIAL_DIR}/src/it/resources/META-INF/persistence.xml
${MV_CMD} ${EXTENSION_ORACLE_SPATIAL_DIR}/src/it/resources/structconverter/struct-converter-entity-mappings.xml ${EXTENSION_ORACLE_SPATIAL_DIR}/src/it/resources/META-INF/struct-converter-entity-mappings.xml

echo "#####Oracle Spatial Extension module preparation - end#####"
