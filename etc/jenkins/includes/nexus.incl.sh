#  Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
#
#  This program and the accompanying materials are made available under the
#  terms of the Eclipse Public License v. 2.0 which is available at
#  http://www.eclipse.org/legal/epl-2.0,
#  or the Eclipse Distribution License v. 1.0 which is available at
#  http://www.eclipse.org/org/documents/edl-v10.php.
#
#  SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause

# Drop old artifacts from staging repository
# Arguments:
#  $1 - Staging key value with grep REGEX prefixes
#  $2 - Build directory
drop_artifacts() {
  echo '-[ Drop old staging repository deployments ]------------------------------------'
  for staging_key in `(cd ${2} && mvn -B nexus-staging:rc-list | egrep "^\[INFO\] [A-Z,a-z,-]+-[0-9]+\s+[A-Z]+\s+${1}" | awk '{print $2}')`; do
    echo "Repository ID: ${staging_key}"
    (cd ${2} && \
      mvn -U -C \
          -DstagingRepositoryId="${staging_key}" \
          nexus-staging:rc-drop)
  done
}
