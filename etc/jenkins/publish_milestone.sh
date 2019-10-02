#!/bin/bash -e
#
# Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Distribution License v. 1.0, which is available at
# http://www.eclipse.org/org/documents/edl-v10.php.
#
# SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause

#
# Arguments:
#  N/A
#==========================
#   Basic Env Setup
#
. ${HOME}/etc/jenkins/setEnvironment.sh


echo '-[ EclipseLink Publish to Milestones ]-----------------------------------------------------------'
scp -r ${MILESTONE_DNLD_DIR}/* genie.eclipselink@projects-storage.eclipse.org:${MILESTONE_DNLD_DIR_REMOTE}
scp -r ${MILESTONE_SITE_DIR}/* genie.eclipselink@projects-storage.eclipse.org:${MILESTONE_SITE_DIR_REMOTE}
