#!/bin/bash -e
#
#  Copyright (c) 2021 Oracle and/or its affiliates. All rights reserved.
#
#  This program and the accompanying materials are made available under the
#  terms of the Eclipse Public License v. 2.0 which is available at
#  http://www.eclipse.org/legal/epl-2.0,
#  or the Eclipse Distribution License v. 1.0 which is available at
#  http://www.eclipse.org/org/documents/edl-v10.php.
#
#  SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
#
# Arguments:
#  N/A

#
# Arguments:
#  $1 - ASM_VERSION
#  $2 - NEXT_ASM_VERSION
#  $3 - DRY_RUN
#  $4 - OVERWRITE

ASM_VERSION="${1}"
NEXT_ASM_VERSION="${2}"
DRY_RUN="${3}"
OVERWRITE="${4}"


export MAVEN_SKIP_RC="true"

. etc/jenkins/includes/maven.incl.sh
. etc/jenkins/includes/nexus.incl.sh

read_version 'ASM' "${ASM_DIR}"

if [ -z "${ASM_RELEASE_VERSION}" ]; then
  echo '-[ Missing required EclipseLink ASM release version number! ]--------------------------------'
  exit 1
fi

RELEASE_TAG="${ASM_RELEASE_VERSION}"
RELEASE_BRANCH="${ASM_RELEASE_VERSION}-RELEASE"

if [ ${DRY_RUN} = 'true' ]; then
  echo '-[ Dry run turned on ]----------------------------------------------------------'
  MVN_DEPLOY_ARGS='install'
  echo '-[ Skipping GitHub branch and tag checks ]--------------------------------------'
else
  MVN_DEPLOY_ARGS='deploy'
  GIT_ORIGIN=`git remote`
  echo '-[ Prepare branch ]-------------------------------------------------------------'
  if [[ -n `git branch -r | grep "${GIT_ORIGIN}/${RELEASE_BRANCH}"` ]]; then
    if [ "${OVERWRITE}" = 'true' ]; then
      echo "${GIT_ORIGIN}/${RELEASE_BRANCH} branch already exists, deleting"
      git push --delete origin "${RELEASE_BRANCH}" && true
    else
      echo "Error: ${GIT_ORIGIN}/${RELEASE_BRANCH} branch already exists"
      exit 1
    fi
  fi
  echo '-[ Release tag cleanup ]--------------------------------------------------------'
  if [[ -n `git ls-remote --tags ${GIT_ORIGIN} | grep "${RELEASE_TAG}"` ]]; then
    if [ "${OVERWRITE}" = 'true' ]; then
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
read_mvn_id 'ASM' "${ASM_DIR}"

# Set Nexus identifiers
ASM_STAGING_DESC="${ASM_GROUP_ID}:${ASM_ARTIFACT_ID}:${ASM_RELEASE_VERSION}"
ASM_STAGING_KEY=$(echo ${ASM_STAGING_DESC} | sed -e 's/\./\\\./g')

# Set release versions
echo '-[ EclipseLink ASM release version ]--------------------------------------------------------'
set_version 'ASM' "${ASM_DIR}" "${ASM_RELEASE_VERSION}" "${ASM_GROUP_ID}" "${ASM_ARTIFACT_ID}" ''

drop_artifacts "${ASM_STAGING_KEY}" "${ASM_DIR}"

echo '-[ Deploy artifacts to staging repository ]-----------------------------'
# Verify, sign and deploy release
(cd ${ASM_DIR} && \
  mvn --no-transfer-progress -U -C -B -V \
      -Poss-release,staging -DskipTests \
      -DstagingDescription="${ASM_STAGING_DESC}" \
      clean ${MVN_DEPLOY_ARGS})

echo '-[ Tag release ]----------------------------------------------------------------'
git tag "${RELEASE_TAG}" -m "EclipseLink ASM ${ASM_RELEASE_VERSION} release"

# Set next release cycle snapshot version
echo '-[ EclipseLink ASM next snapshot version ]--------------------------------------------------'
set_version 'ASM' "${ASM_DIR}" "${ASM_NEXT_SNAPSHOT}" "${ASM_GROUP_ID}" "${ASM_ARTIFACT_ID}" ''

if [ ${DRY_RUN} = 'true' ]; then
  echo '-[ Skipping GitHub update ]-----------------------------------------------------'
else
  echo '-[ Push branch and tag to GitHub ]----------------------------------------------'
  git push origin "${RELEASE_BRANCH}"
  git push origin "${RELEASE_TAG}"
fi
