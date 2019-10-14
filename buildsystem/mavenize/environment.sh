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

#EclipseLink
export ECLIPSE_LINK_PACKAGE_DIR="org/eclipse/persistence"

#IDE (Eclipse, IDEA Intelij project files)
export ECLIPSE_IDE_FILES='.settings .project .classpath'
export IDEA_IDE_FILES='*.iml .idea'

#ANT build files
export ANT_BUILD_FILES='build.properties antbuild.xml'

#Git settings
#export GIT_DRY_RUN='--dry-run'
export GIT_MV_CMD="git mv --force $GIT_DRY_RUN"
export GIT_RM_CMD="git rm $GIT_DRY_RUN -r"

#sync command rsync
#export RSYNC_CMD="rsync -av --delete"
export RSYNC_CMD="rsync -a"

#sync/move command
export SYNC_CMD=${RSYNC_CMD}
export MV_CMD="mv"

#####!!!!!!!BEWARE CODE TRANSFORMATION!!!!!!#######
#export SYNC_CMD=${GIT_MV_CMD}
#export MV_CMD=${GIT_MV_CMD}
#####!!!!!!!BEWARE CODE TRANSFORMATION!!!!!!#######


#remove/delete command
export RM_CMD=${GIT_MV_CMD}
