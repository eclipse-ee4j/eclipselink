#
# Copyright (c) 2020, 2021 Oracle and/or its affiliates. All rights reserved.
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License v. 2.0 which is available at
# http://www.eclipse.org/legal/epl-2.0,
# or the Eclipse Distribution License v. 1.0 which is available at
# http://www.eclipse.org/org/documents/edl-v10.php.
#
# SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
#

# Maven plugins
VERSIONS_PLUGIN='org.codehaus.mojo:versions-maven-plugin:2.7'
HELP_PLUGIN='org.apache.maven.plugins:maven-help-plugin:3.2.0'

# Compute version strings for next development cycle.
# Version strings are set as new shell variables with provided prefix.
# Arguments:
#  $1 - Variable prefix
#  $2 - Source version
# Variables set:
#  "${1}_NEXT_VERSION"  - Next version string: Source string with last component increased by 1
#  "${1}_NEXT_SNAPSHOT" - Next snapshot string: Next version string with '-SNAPSHOT' suffix
next_version() {
  set -f
  local NEXT_COMPONENTS=(${2//\./ })
  local LAST_INDEX=$((${#NEXT_COMPONENTS[@]} - 1))
  local NEXT_COMPONENTS[${LAST_INDEX}]=$((${NEXT_COMPONENTS[${LAST_INDEX}]} + 1))
  local COMPONENTS_STR="${NEXT_COMPONENTS[@]}"
  local NEXT_VERSION="${COMPONENTS_STR// /.}"
  local NEXT_SNAPSHOT="${NEXT_VERSION}-SNAPSHOT"
  echo "${1} Next Version:    ${NEXT_VERSION}"
  echo "${1} Next Snapshot:   ${NEXT_SNAPSHOT}"
  eval "${1}_NEXT_VERSION"="${NEXT_VERSION}"
  eval "${1}_NEXT_SNAPSHOT"="${NEXT_SNAPSHOT}"
}

# Prepare release version string and next development cycle versions.
# Version strings are set as new shell variables with provided prefix.
# Arguments:
#  $1 - Variable prefix
#  $2 - Build directory
# Source variables:
#  "${1}_VERSION" - Release version override (optional)
# Variables set:
#  "${1}_RELEASE_VERSION" - Release version
read_version() {
  local VERSION_VAR="${1}_VERSION"
  local SNAPSHOT_VERSION=`(cd ${2} && mvn -V -B ${HELP_PLUGIN}:evaluate -Dexpression=project.version 2> /dev/null | grep -E '^[0-9]+(\.[0-9]+)+-SNAPSHOT$')`
  if [ -z "${!VERSION_VAR}" ]; then
    local RELEASE_VERSION="${SNAPSHOT_VERSION/-SNAPSHOT/}"
  else
    local RELEASE_VERSION="${!VERSION_VAR}"
  fi
  echo "${1} Release Version: ${RELEASE_VERSION}"
  eval "${1}_RELEASE_VERSION"="${RELEASE_VERSION}"
  next_version "${1}" "${RELEASE_VERSION}"
}

# Read Maven identifier (groupId and artifactId).
# Maven identifier is set as new shell variables with provided prefix.
# Arguments:
#  $1 - Variable prefix
#  $2 - Build directory
# Variables set:
#  "${1}_GROUP_ID" - Maven groupId
#  "${1}_ARTIFACT_ID" - Maven artifactId
read_mvn_id() {
  local GROUP_ID=`(cd ${2} && mvn -B -V ${HELP_PLUGIN}:evaluate -Dexpression=project.groupId | grep -Ev '(^\[)')`
  local ARTIFACT_ID=`(cd ${2} && mvn -B -V ${HELP_PLUGIN}:evaluate -Dexpression=project.artifactId | grep -Ev '(^\[)')`
  echo "${1} Group ID:    ${GROUP_ID}"
  echo "${1} Artifact ID: ${ARTIFACT_ID}"
  eval "${1}_GROUP_ID"="${GROUP_ID}"
  eval "${1}_ARTIFACT_ID"="${ARTIFACT_ID}"
}

# Set Maven artifact version.
# Arguments:
#  $1 - Artifact identifier (e.g. 'SPEC', 'API', 'RI')
#  $2 - Build directory
#  $3 - Version to set
#  $4 - Group ID
#  $5 - Artifact ID
#  $6 - Additional Maven arguments
set_version() {
  echo '--[ Set version ]---------------------------------------------------------------'
  # Set release version
  (cd ${2} && \
    mvn -V -B -U -C \
        ${6} \
        -DnewVersion="${3}" \
        -DgenerateBackupPoms=false \
        clean ${VERSIONS_PLUGIN}:set)
  echo '--[ Commit modified pom.xml files ]---------------------------------------------'
  local POM_FILES=`git status | grep -E 'modified:.*pom.*\.xml' | sed -e 's/[[:space:]][[:space:]]*modified:[[:space:]][[:space:]]*//'`
  git add ${POM_FILES} && \
  git commit -s -m "Update ${1} version of ${4}:${5} to ${3}"
}
