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

#Maven module/directory extension
export MAVEN_MODULE_EXT=".maven"


#Git settings
#export GIT_DRY_RUN='--dry-run'
export GIT_MV_CMD="git mv --force $GIT_DRY_RUN"


rename_maven_module() {
    echo "renaming $1 to ${1/$MAVEN_MODULE_EXT/""}"
    ${GIT_MV_CMD} ../../$1 ../../${1/$MAVEN_MODULE_EXT/""}
}

MAVEN_MODULES=$(cat prepare03_maven_modules.lst)
for MAVEN_MODULE in ${MAVEN_MODULES[@]:0}; do
    rename_maven_module $MAVEN_MODULE
done

sed -i -e 's+.maven</module>+</module>+g' ../../pom.xml
sed -i -e 's+.maven</module>+</module>+g' ../../pom_oracle.xml