#!/bin/bash -e
#
# Copyright (c) 2020, 2021 Oracle and/or its affiliates. All rights reserved.
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
# $1 -  ECLIPSELINK_VERSION         - Version to release
# $2 -  NEXT_ECLIPSELINK_VERSION    - Next snapshot version to set (e.g. 3.0.1-SNAPSHOT).
# $3 -  DRY_RUN                     - Do not publish artifacts to OSSRH and code changes to GitHub.
# $4 -  OVERWRITE_GIT               - Allows to overwrite existing version in git
# $5 -  OVERWRITE_STAGING           - Allows to overwrite existing version in OSSRH (Jakarta) staging repositories

echo '-[ EclipseLink Release ]-----------------------------------------------------------'

ECLIPSELINK_VERSION="${1}"
NEXT_ECLIPSELINK_VERSION="${2}"
DRY_RUN="${3}"
OVERWRITE_GIT="${4}"
OVERWRITE_STAGING="${5}"


export MAVEN_SKIP_RC="true"
export ECLIPSELINK_DIR="."

. etc/jenkins/includes/maven.incl.sh
. etc/jenkins/includes/nexus.incl.sh

read_version 'ECLIPSELINK' "${ECLIPSELINK_DIR}"

if [ -z "${ECLIPSELINK_RELEASE_VERSION}" ]; then
  echo '-[ Missing required EclipseLink release version number! ]--------------------------------'
  exit 1
fi

RELEASE_TAG="${ECLIPSELINK_RELEASE_VERSION}"
RELEASE_BRANCH="${ECLIPSELINK_RELEASE_VERSION}-RELEASE"

if [ ${DRY_RUN} = 'true' ]; then
  echo '-[ Dry run turned on ]----------------------------------------------------------'
  MVN_DEPLOY_ARGS='install'
  echo '-[ Skipping GitHub branch and tag checks ]--------------------------------------'
else
  MVN_DEPLOY_ARGS='deploy'
  GIT_ORIGIN=`git remote`
  echo '-[ Prepare branch ]-------------------------------------------------------------'
  if [[ -n `git branch -r | grep "${GIT_ORIGIN}/${RELEASE_BRANCH}"` ]]; then
    if [ "${OVERWRITE_GIT}" = 'true' ]; then
      echo "${GIT_ORIGIN}/${RELEASE_BRANCH} branch already exists, deleting"
      git push --delete origin "${RELEASE_BRANCH}" && true
    else
      echo "Error: ${GIT_ORIGIN}/${RELEASE_BRANCH} branch already exists"
      exit 1
    fi
  fi
  echo '-[ Release tag cleanup ]--------------------------------------------------------'
  if [[ -n `git ls-remote --tags ${GIT_ORIGIN} | grep "${RELEASE_TAG}"` ]]; then
    if [ "${OVERWRITE_GIT}" = 'true' ]; then
      echo "${RELEASE_TAG} tag already exists, deleting"
      git push --delete origin "${RELEASE_TAG}" && true
    else
      echo "Error: ${RELEASE_TAG} tag already exists"
      exit 1
    fi
  fi
fi

# Always delete local branch if exists
git branch --delete "${RELEASE_BRANCH}" && true
git checkout -b ${RELEASE_BRANCH}

# Always delete local tag if exists
git tag --delete "${RELEASE_TAG}" && true

# Read Maven identifiers
read_mvn_id 'ECLIPSELINK' "${ECLIPSELINK_DIR}"

# Set Nexus identifiers
ECLIPSELINK_STAGING_DESC="${ECLIPSELINK_GROUP_ID}:${ECLIPSELINK_ARTIFACT_ID}:${ECLIPSELINK_RELEASE_VERSION}"
ECLIPSELINK_STAGING_KEY=$(echo ${ECLIPSELINK_STAGING_DESC} | sed -e 's/\./\\\./g')

# Set release versions
echo '-[ EclipseLink release version ]--------------------------------------------------------'
set_version 'ECLIPSELINK' "${ECLIPSELINK_DIR}" "${ECLIPSELINK_RELEASE_VERSION}" "${ECLIPSELINK_GROUP_ID}" "${ECLIPSELINK_ARTIFACT_ID}" ''

if [ "${OVERWRITE_STAGING}" = 'true' ]; then
  drop_artifacts "${ECLIPSELINK_STAGING_KEY}" "${ECLIPSELINK_DIR}"
fi

echo '-[ Build project mvn clean install ]-----------------------------'
#This step is needed to populate local Maven repository with required but not deployed artifacts
mvn --no-transfer-progress -DskipTests clean install
#Deploy selected artifacts. There is Maven property -Ddeploy to control which modules will be deployed
echo '-[ Deploy artifacts to staging repository ]-----------------------------'
# Verify, sign and deploy release
(cd ${ECLIPSELINK_DIR} && \
  mvn --no-transfer-progress -U -C -B -V \
      -Poss-release,staging -DskipTests \
      -DskipTests -Ddoclint=none \
      -DstagingDescription="${ECLIPSELINK_STAGING_DESC}" \
      -Ddeploy \
      clean ${MVN_DEPLOY_ARGS})

echo '-[ Tag release ]----------------------------------------------------------------'
git tag "${RELEASE_TAG}" -m "EclipseLink ${ECLIPSELINK_RELEASE_VERSION} release"

# Set next release cycle snapshot version
echo '-[ EclipseLink next snapshot version ]--------------------------------------------------'
set_version 'ECLIPSELINK' "${ECLIPSELINK_DIR}" "${ECLIPSELINK_NEXT_SNAPSHOT}" "${ECLIPSELINK_GROUP_ID}" "${ECLIPSELINK_ARTIFACT_ID}" ''

if [ ${DRY_RUN} = 'true' ]; then
  echo '-[ Skipping GitHub update ]-----------------------------------------------------'
else
  echo '-[ Push branch and tag to GitHub ]----------------------------------------------'
  git push origin "${RELEASE_BRANCH}"
  git push origin "${RELEASE_TAG}"
fi
