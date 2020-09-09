#!/bin/bash -e
#
# Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Distribution License v. 1.0, which is available at
# http://www.eclipse.org/org/documents/edl-v10.php.
#
# SPDX-License-Identifier: BSD-3-Clause

# Maven plugins
VERSIONS_PLUGIN='org.codehaus.mojo:versions-maven-plugin:2.7'
HELP_PLUGIN='org.apache.maven.plugins:maven-help-plugin:3.1.0'
#
# Arguments:
# $1 -  RELEASE_VERSION        - Version to release
# $2 -  NEXT_VERSION           - Next snapshot version to set (e.g. 3.0.1-SNAPSHOT).
# $3 -  DRY_RUN                - Do not publish artifacts to OSSRH and code changes to GitHub.

echo '-[ EclipseLink Release ]-----------------------------------------------------------'
. /etc/profile

RELEASE_VERSION="${1}"
NEXT_VERSION="${2}"
DRY_RUN="${3}"

CURRENT_VERSION=$(mvn -q -Dexec.executable=echo -Dexec.args='${project.version}' --non-recursive exec:exec)

echo "-[ Current Version: ${CURRENT_VERSION} ]-----------------------------------------------------------"
echo "-[ Release Version: ${RELEASE_VERSION} ]-----------------------------------------------------------"

# Project identifiers
ARTIFACT_ID=$(mvn -B ${HELP_PLUGIN}:evaluate -Dexpression=project.artifactId -Pstaging | grep -Ev '(^\[)')
GROUP_ID=$(mvn -B ${HELP_PLUGIN}:evaluate -Dexpression=project.groupId -Pstaging | grep -Ev '(^\[)')
STAGING_DESC="${GROUP_ID}:${ARTIFACT_ID}:${RELEASE_VERSION}"
STAGING_KEY=$(echo ${STAGING_DESC} | sed -e 's/\./\\\./g')

if [ ${DRY_RUN} = 'true' ]; then
  echo '-[ Dry run turned on ]----------------------------------------------------------'
  MVN_DEPLOY_ARGS='install'
else
  MVN_DEPLOY_ARGS='deploy'
fi

echo '-[ mvn version set ]--------------------------------------'
mvn --no-transfer-progress -DnewVersion=${RELEASE_VERSION} -DgenerateBackupPoms=false -Doracle.modules.subdirectory=bundles clean org.codehaus.mojo:versions-maven-plugin:2.7:set

echo '-[ Build project mvn clean install ]-----------------------------'
#This step is needed to populate local Maven repository with required but not deployed artifacts
mvn --no-transfer-progress -DskipTests clean install
#Deploy selected artifacts. There is Maven property -Ddeploy to control which modules will be deployed
echo '-[ Deploy artifacts to staging repository ]-----------------------------'
mvn --no-transfer-progress -U -C -B -Poss-release,staging \
    -DskipTests -Ddoclint=none \
    -DstagingDescription="${STAGING_DESC}" \
    -Ddeploy \
    clean ${MVN_DEPLOY_ARGS}
