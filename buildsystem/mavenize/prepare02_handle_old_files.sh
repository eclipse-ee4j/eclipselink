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

#EclipseLink
export ECLIPSE_LINK_BACKUP_DIRECTORY="../../eclipseLink.backup"

export RM_CMD="rm -rfv"

#Git settings
#export GIT_DRY_RUN='--dry-run'
export GIT_MV_CMD="git mv --force $GIT_DRY_RUN"
export GIT_RM_CMD="git rm --force $GIT_DRY_RUN -r"


handle_el_old_file() {
    #Move to backup directory
    ${GIT_MV_CMD} ../../$1 $ECLIPSE_LINK_BACKUP_DIRECTORY
    #To remove rest of tracked files
    ${GIT_RM_CMD} ../../$1
    #Remove rest of files (untracked)
    ${RM_CMD} ../../$1
}

mkdir $ECLIPSE_LINK_BACKUP_DIRECTORY
OLD_FILES=$(cat prepare02_old_files.lst)
for OLD_FILE in ${OLD_FILES[@]:0}; do
    handle_el_old_file $OLD_FILE
done
