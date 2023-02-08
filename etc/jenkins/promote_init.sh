#!/bin/bash -e
#
# Copyright (c) 2020, 2023 Oracle and/or its affiliates. All rights reserved.
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Distribution License v. 1.0, which is available at
# http://www.eclipse.org/org/documents/edl-v10.php.
#
# SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause

#
# Arguments:
#  N/A

echo '-[ EclipseLink Promotion Init ]-----------------------------------------------------------'
. ${WORKSPACE}/etc/jenkins/setEnvironment.sh
mkdir -p ${DNLD_DIR}/nightly/${VERSION}
mkdir -p ${RELEASE_DNLD_DIR}/${VERSION}
mkdir -p ${RELEASE_SITE_DIR}
mkdir -p ${MILESTONE_SITE_DIR}
mkdir -p ${NIGHTLY_SITE_DIR}
mkdir -p ${SIGN_DIR}
scp -r genie.eclipselink@projects-storage.eclipse.org:${DNLD_DIR_REMOTE}/nightly/${VERSION}/* ${DNLD_DIR}/nightly/${VERSION}
if [ ${RELEASE} = "true" ]; then
  mkdir -p ${MILESTONE_DNLD_DIR}/${VERSION}/${RELEASE_CANDIDATE_ID}
  scp -r genie.eclipselink@projects-storage.eclipse.org:${MILESTONE_DNLD_DIR_REMOTE}/${VERSION}/${RELEASE_CANDIDATE_ID}/* ${MILESTONE_DNLD_DIR}/${VERSION}/${RELEASE_CANDIDATE_ID}
fi
