#!/bin/bash

#****************************************************************************************
# Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
# which accompanies this distribution.
#
# The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
# and the Eclipse Distribution License is available at
# http://www.eclipse.org/org/documents/edl-v10.php.
#
# Contributors:
#  - rfelcman - 16 August 2019 - Initial implementation
#****************************************************************************************

# This script sets environment variables used by other scripts in eclipselink.releng.

ANT_HOME_DEFAULT=/opt/java/ant
M2_HOME_DEFAULT=/opt/java/maven
HOME_DIR_DEFAULT=${WORKSPACE}
JAVA_HOME_DEFAULT=/usr/lib/jvm/java-1.8.0-openjdk-1.8.0.222.b10-0.fc30.x86_64
ANT_OPTS_DEFAULT="-Xms512m -Xmx1024m"
START_DATE_DEFAULT=`date '+%y%m%d-%H%M'`

DNLD_DIR_DEFAULT=${WORKSPACE}/home/data/httpd/download.eclipse.org/rt/eclipselink
RELEASE_SITE_DIR_DEFAULT=${WORKSPACE}/home/data/httpd/download.eclipse.org/rt/eclipselink/updates
RELEASE_DNLD_DIR_DEFAULT=${WORKSPACE}/home/data/httpd/download.eclipse.org/rt/eclipselink/releases
MILESTONE_DNLD_DIR_DEFAULT=${WORKSPACE}/home/data/httpd/download.eclipse.org/rt/eclipselink/milestones
MILESTONE_SITE_DIR_DEFAULT=${WORKSPACE}/home/data/httpd/download.eclipse.org/rt/eclipselink/milestone-updates
NIGHTLY_SITE_DIR_DEFAULT=${WORKSPACE}/home/data/httpd/download.eclipse.org/rt/eclipselink/nightly-updates

DNLD_DIR_REMOTE_DEFAULT=/home/data/httpd/download.eclipse.org/rt/eclipselink
RELEASE_SITE_DIR_REMOTE_DEFAULT=/home/data/httpd/download.eclipse.org/rt/eclipselink/updates
RELEASE_DNLD_DIR_REMOTE_DEFAULT=/home/data/httpd/download.eclipse.org/rt/eclipselink/releases
MILESTONE_DNLD_DIR_REMOTE_DEFAULT=/home/data/httpd/download.eclipse.org/rt/eclipselink/milestones
MILESTONE_SITE_DIR_REMOTE_DEFAULT=/home/data/httpd/download.eclipse.org/rt/eclipselink/milestone-updates
NIGHTLY_SITE_DIR_REMOTE_DEFAULT=/home/data/httpd/download.eclipse.org/rt/eclipselink/nightly-updates

SIGN_DIR=${HOME}/tmp/signing


# Set shell variable if value does not exist
 # Arguments:
 #   $1 Variable name
 #   $2 Value to set if variable is empty
 set_default_var() {
   if [ -z "${!1}" ]; then
     declare -g ${1}="${2}"
   fi
 }

set_default_var ANT_HOME ${ANT_HOME_DEFAULT}
set_default_var M2_HOME ${M2_HOME_DEFAULT}
set_default_var HOME_DIR ${HOME_DIR_DEFAULT}
set_default_var JAVA_HOME ${JAVA_HOME_DEFAULT}
set_default_var ANT_OPTS ${ANT_OPTS_DEFAULT}
set_default_var START_DATE ${START_DATE_DEFAULT}
set_default_var DNLD_DIR ${DNLD_DIR_DEFAULT}
set_default_var RELEASE_SITE_DIR ${RELEASE_SITE_DIR_DEFAULT}
set_default_var RELEASE_DNLD_DIR ${RELEASE_DNLD_DIR_DEFAULT}
set_default_var MILESTONE_DNLD_DIR ${MILESTONE_DNLD_DIR_DEFAULT}
set_default_var MILESTONE_SITE_DIR ${MILESTONE_SITE_DIR_DEFAULT}
set_default_var NIGHTLY_SITE_DIR ${NIGHTLY_SITE_DIR_DEFAULT}
set_default_var DNLD_DIR_REMOTE ${DNLD_DIR_REMOTE_DEFAULT}
set_default_var RELEASE_SITE_DIR_REMOTE ${RELEASE_SITE_DIR_REMOTE_DEFAULT}
set_default_var RELEASE_DNLD_DIR_REMOTE ${RELEASE_DNLD_DIR_REMOTE_DEFAULT}
set_default_var MILESTONE_DNLD_DIR_REMOTE ${MILESTONE_DNLD_DIR_REMOTE_DEFAULT}
set_default_var MILESTONE_SITE_DIR_REMOTE ${MILESTONE_SITE_DIR_REMOTE_DEFAULT}
set_default_var NIGHTLY_SITE_DIR_REMOTE ${NIGHTLY_SITE_DIR_REMOTE_DEFAULT}
