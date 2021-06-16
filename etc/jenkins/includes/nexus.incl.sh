#
# Copyright (c) 2020, 2021 Oracle and/or its affiliates. All rights reserved.
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License v. 2.0, which is available at
# http://www.eclipse.org/legal/epl-2.0.
#
# This Source Code may also be made available under the following Secondary
# Licenses when the conditions for such availability set forth in the
# Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
# version 2 with the GNU Classpath Exception, which is available at
# https://www.gnu.org/software/classpath/license.html.
#
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
#

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
