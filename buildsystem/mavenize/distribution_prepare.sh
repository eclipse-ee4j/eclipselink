#!/usr/bin/env bash
#
# Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License v. 2.0 which is available at
# http://www.eclipse.org/legal/epl-2.0,
# or the Eclipse Distribution License v. 1.0 which is available at
# http://www.eclipse.org/org/documents/edl-v10.php.
#
# SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
#

echo "#####DISTRIBUTION module preparation - begin#####"

. ./distribution_environment.sh

#Prepare main resources
mkdir -p ${BUNDLES_DISTRIBUTION_DIR}/src/main/scripts ${BUNDLES_DISTRIBUTION_DIR}/src/main/scripts/dbws ${BUNDLES_DISTRIBUTION_DIR}/src/main/scripts/rename
${SYNC_CMD} ${FOUNDATION_PARENT_ORIGIN_DIR}/bin/* ${BUNDLES_DISTRIBUTION_DIR}/src/main/scripts
${SYNC_CMD} ${MOXY_PARENT_ORIGIN_DIR}/bin/* ${BUNDLES_DISTRIBUTION_DIR}/src/main/scripts
${SYNC_CMD} ${SDO_PARENT_ORIGIN_DIR}/bin/* ${BUNDLES_DISTRIBUTION_DIR}/src/main/scripts
${SYNC_CMD} ${DBWS_BUILDER_ORIGIN_DIR}/dbwsbuilder.* ${BUNDLES_DISTRIBUTION_DIR}/src/main/scripts/dbws
${SYNC_CMD} ${UTILS_RENAME_ORIGIN_DIR}/packageRename.* ${BUNDLES_DISTRIBUTION_DIR}/src/main/scripts/rename
${SYNC_CMD} ${UTILS_RENAME_ORIGIN_DIR}/readme.html ${BUNDLES_DISTRIBUTION_DIR}/src/main/scripts/rename/readme.html

#Prepare installer test sources/resources
TEST_INSTALLER_PACKAGE='org/eclipse/persistence/testing/jaxb/installer'
mkdir -p ${BUNDLES_DISTRIBUTION_DIR}/src/test/java/${TEST_INSTALLER_PACKAGE} ${BUNDLES_DISTRIBUTION_DIR}/src/test/resources/${TEST_INSTALLER_PACKAGE}
${MV_CMD} ${MOXY_DIR}/src/test/java/${TEST_INSTALLER_PACKAGE}/* ${BUNDLES_DISTRIBUTION_DIR}/src/test/java/${TEST_INSTALLER_PACKAGE}
${MV_CMD} ${MOXY_DIR}/src/test/resources/${TEST_INSTALLER_PACKAGE}/* ${BUNDLES_DISTRIBUTION_DIR}/src/test/resources/${TEST_INSTALLER_PACKAGE}
rm -rfv ${MOXY_DIR}/src/test/java/${TEST_INSTALLER_PACKAGE}
rm -rfv ${MOXY_DIR}/src/test/resources/${TEST_INSTALLER_PACKAGE}

#Prepare OSGI test sources
TEST_OSGI_PACKAGE='org/eclipse/persistence/testing/osgi'
mkdir -p ${BUNDLES_DISTRIBUTION_DIR}/src/test/java/${TEST_OSGI_PACKAGE}
${MV_CMD} ${MOXY_DIR}/src/test/java/${TEST_OSGI_PACKAGE}/* ${BUNDLES_DISTRIBUTION_DIR}/src/test/java/${TEST_OSGI_PACKAGE}
rm -rfv ${MOXY_DIR}/src/test/java/${TEST_OSGI_PACKAGE}

#Prepare eclipselink-test-src.zip resources
mkdir -p ${BUNDLES_OTHERS_DIR}/src/main/resources/nonfiltered/eclipselink-test-src.zip
mkdir -p ${BUNDLES_OTHERS_DIR}/src/main/resources/nonfiltered/eclipselink-test-src.zip/buildsystem
mkdir -p ${BUNDLES_OTHERS_DIR}/src/main/resources/nonfiltered/eclipselink-test-src.zip/foundation/eclipselink.core.test
mkdir -p ${BUNDLES_OTHERS_DIR}/src/main/resources/nonfiltered/eclipselink-test-src.zip/foundation/eclipselink.extension.nosql.test
mkdir -p ${BUNDLES_OTHERS_DIR}/src/main/resources/nonfiltered/eclipselink-test-src.zip/foundation/eclipselink.extension.oracle.nosql.test
mkdir -p ${BUNDLES_OTHERS_DIR}/src/main/resources/nonfiltered/eclipselink-test-src.zip/foundation/eclipselink.extension.oracle.spatial.test
mkdir -p ${BUNDLES_OTHERS_DIR}/src/main/resources/nonfiltered/eclipselink-test-src.zip/foundation/eclipselink.extension.oracle.test
mkdir -p ${BUNDLES_OTHERS_DIR}/src/main/resources/nonfiltered/eclipselink-test-src.zip/jpa/eclipselink.jpa.test
mkdir -p ${BUNDLES_OTHERS_DIR}/src/main/resources/nonfiltered/eclipselink-test-src.zip/jpa/plugins
mkdir -p ${BUNDLES_OTHERS_DIR}/src/main/resources/nonfiltered/eclipselink-test-src.zip/moxy/eclipselink.moxy.test
mkdir -p ${BUNDLES_OTHERS_DIR}/src/main/resources/nonfiltered/eclipselink-test-src.zip/sdo/eclipselink.sdo.test
mkdir -p ${BUNDLES_OTHERS_DIR}/src/main/resources/nonfiltered/eclipselink-test-src.zip/sdo/eclipselink.sdo.test.server
mkdir -p ${BUNDLES_OTHERS_DIR}/src/main/resources/nonfiltered/eclipselink-test-src.zip/sdo/plugins
${RSYNC_CMD} ../../*build.* ${BUNDLES_OTHERS_DIR}/src/main/resources/nonfiltered/eclipselink-test-src.zip
${RSYNC_CMD} ../../buildsystem/*.jar ${BUNDLES_OTHERS_DIR}/src/main/resources/nonfiltered/eclipselink-test-src.zip/buildsystem
${RSYNC_CMD} ../../foundation/eclipselink.core.test/*.properties ${BUNDLES_OTHERS_DIR}/src/main/resources/nonfiltered/eclipselink-test-src.zip/foundation/eclipselink.core.test
${RSYNC_CMD} ../../foundation/eclipselink.core.test/*.sql ${BUNDLES_OTHERS_DIR}/src/main/resources/nonfiltered/eclipselink-test-src.zip/foundation/eclipselink.core.test
${RSYNC_CMD} ../../foundation/eclipselink.core.test/*.xml ${BUNDLES_OTHERS_DIR}/src/main/resources/nonfiltered/eclipselink-test-src.zip/foundation/eclipselink.core.test
${RSYNC_CMD} ../../foundation/eclipselink.extension.nosql.test/*.properties ${BUNDLES_OTHERS_DIR}/src/main/resources/nonfiltered/eclipselink-test-src.zip/foundation/eclipselink.extension.nosql.test
${RSYNC_CMD} ../../foundation/eclipselink.extension.nosql.test/*.xml ${BUNDLES_OTHERS_DIR}/src/main/resources/nonfiltered/eclipselink-test-src.zip/foundation/eclipselink.extension.nosql.test
${RSYNC_CMD} ../../foundation/eclipselink.extension.oracle.nosql.test/*.properties ${BUNDLES_OTHERS_DIR}/src/main/resources/nonfiltered/eclipselink-test-src.zip/foundation/eclipselink.extension.oracle.nosql.test
${RSYNC_CMD} ../../foundation/eclipselink.extension.oracle.nosql.test/*.xml ${BUNDLES_OTHERS_DIR}/src/main/resources/nonfiltered/eclipselink-test-src.zip/foundation/eclipselink.extension.oracle.nosql.test
${RSYNC_CMD} ../../foundation/eclipselink.extension.oracle.spatial.test/*.properties ${BUNDLES_OTHERS_DIR}/src/main/resources/nonfiltered/eclipselink-test-src.zip/foundation/eclipselink.extension.oracle.spatial.test
${RSYNC_CMD} ../../foundation/eclipselink.extension.oracle.spatial.test/*.xml ${BUNDLES_OTHERS_DIR}/src/main/resources/nonfiltered/eclipselink-test-src.zip/foundation/eclipselink.extension.oracle.spatial.test
${RSYNC_CMD} ../../foundation/eclipselink.extension.oracle.test/*.properties ${BUNDLES_OTHERS_DIR}/src/main/resources/nonfiltered/eclipselink-test-src.zip/foundation/eclipselink.extension.oracle.test
${RSYNC_CMD} ../../foundation/eclipselink.extension.oracle.test/*.xml ${BUNDLES_OTHERS_DIR}/src/main/resources/nonfiltered/eclipselink-test-src.zip/foundation/eclipselink.extension.oracle.test
${RSYNC_CMD} ../../jpa/eclipselink.jpa.test/*.cmd ${BUNDLES_OTHERS_DIR}/src/main/resources/nonfiltered/eclipselink-test-src.zip/jpa/eclipselink.jpa.test
${RSYNC_CMD} ../../jpa/eclipselink.jpa.test/*.properties ${BUNDLES_OTHERS_DIR}/src/main/resources/nonfiltered/eclipselink-test-src.zip/jpa/eclipselink.jpa.test
${RSYNC_CMD} ../../jpa/eclipselink.jpa.test/*.py ${BUNDLES_OTHERS_DIR}/src/main/resources/nonfiltered/eclipselink-test-src.zip/jpa/eclipselink.jpa.test
${RSYNC_CMD} ../../jpa/eclipselink.jpa.test/*.sh ${BUNDLES_OTHERS_DIR}/src/main/resources/nonfiltered/eclipselink-test-src.zip/jpa/eclipselink.jpa.test
${RSYNC_CMD} ../../jpa/eclipselink.jpa.test/*.xml ${BUNDLES_OTHERS_DIR}/src/main/resources/nonfiltered/eclipselink-test-src.zip/jpa/eclipselink.jpa.test
${RSYNC_CMD} ../../moxy/eclipselink.moxy.test/*.properties ${BUNDLES_OTHERS_DIR}/src/main/resources/nonfiltered/eclipselink-test-src.zip/moxy/eclipselink.moxy.test
${RSYNC_CMD} ../../moxy/eclipselink.moxy.test/*.xml ${BUNDLES_OTHERS_DIR}/src/main/resources/nonfiltered/eclipselink-test-src.zip/moxy/eclipselink.moxy.test
${RSYNC_CMD} ../../sdo/eclipselink.sdo.test/*.properties ${BUNDLES_OTHERS_DIR}/src/main/resources/nonfiltered/eclipselink-test-src.zip/sdo/eclipselink.sdo.test
${RSYNC_CMD} ../../sdo/eclipselink.sdo.test/*.xml ${BUNDLES_OTHERS_DIR}/src/main/resources/nonfiltered/eclipselink-test-src.zip/sdo/eclipselink.sdo.test
${RSYNC_CMD} ../../sdo/eclipselink.sdo.test.server/*.properties ${BUNDLES_OTHERS_DIR}/src/main/resources/nonfiltered/eclipselink-test-src.zip/sdo/eclipselink.sdo.test.server
${RSYNC_CMD} ../../sdo/eclipselink.sdo.test.server/*.xml ${BUNDLES_OTHERS_DIR}/src/main/resources/nonfiltered/eclipselink-test-src.zip/sdo/eclipselink.sdo.test.server


echo "#####DISTRIBUTION module preparation - end#####"
