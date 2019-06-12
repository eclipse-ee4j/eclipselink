#!/usr/bin/env bash
#****************************************************************************************
# Copyright (c) 2012, 2019 Oracle and/or its affiliates. All rights reserved.
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
# which accompanies this distribution.
#
# The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
# and the Eclipse Distribution License is available at
# http://www.eclipse.org/org/documents/edl-v10.php.
#
# SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
#
# Contributors:
#  - egwin - 13 September 2012 - Initial implementation
#  - egwin - 07 March 2013     - Updates for staging to SonatypeOSS
#  - rfelcman - August 2019    - Migration to new Eclipse.org CI infrastructure
#****************************************************************************************

#----------------------------------------------------------------------------------------
#    This script is designed to be run interactively to promote an existing published
#    build to a milestone, or a Milestone build to a release. It expects to be run from
#    the 'build.eclipse.com' server.
#----------------------------------------------------------------------------------------

#==========================
#   Basic Env Setup
#
. ${HOME}/etc/jenkins/setEnvironment.sh

#Define common variables
THIS=$0
PROGNAME=`basename ${THIS}`
CUR_DIR=`dirname ${THIS}`
umask 0002
BUILD=$1
MILESTONE=$2
BRANCH_NM=$3
SIGN=$4
DEBUG_ARG=$5

ANT_ARGS=" "
START_DATE=`date '+%y%m%d-%H%M'`

#Directories
EXEC_DIR=${HOME_DIR}
LOG_DIR=${HOME_DIR}/logs
RELENG_REPO=${HOME}/etc/jenkins
RUNTIME_REPO=${WORKSPACE}

#ANT Invokation Variables
BUILDFILE=${RUNTIME_REPO}/autobuild.xml
ANT_TARGET=build-milestone

#Global Variables
RELEASE=false

PATH=${JAVA_HOME}/bin:${ANT_HOME}/bin:${RELENG_REPO}:/usr/bin:/usr/local/bin:${PATH}

# Export necessary global environment variables
export ANT_ARGS ANT_OPTS ANT_HOME HOME_DIR JAVA_HOME LOG_DIR PATH
#==========================
#   Functions Definitions
#
unset usage
usage() {
    echo "Usage: ${PROGNAME} (BUILD |'release') MILESTONE BRANCH_NM SIGN [debug]"
    echo "   BUILD     - full build identifier, or 'release' (example: 2.4.1.v201209013-98ef31a). Used to generate branch,"
    echo "               version, date and hash info needed. If 'release', tells promote to release the specified"
    echo "               MILESTONE."
    echo "   MILESTONE - a milestone (exampe: M4) to promote the specified build to. Also used in dir storage, and maven."
    echo "               storage, and Maven publishing."
    echo "   BRANCH_NM - The git branchname for the branch the build was based upon (Example: master, 2.4, 2.3, etc.)"
    echo "   SIGN      - Sing content of promoted jar files"
    echo "                  true - sign"
    echo "                  false - don't sign"
    echo "   DEBUG_ARG - if defined, designates a run should be 'debug'."
}

unset createPath
createPath() {
    # Usage: createPath path
    path=$1

    if [ "${DEBUG}" = "true" ] ; then
        echo "createPath: Attempting to create '${path}' path."
    fi
    newdir=
    for directory in `echo ${path} | tr '/' ' '`
    do
        newdir=${newdir}/${directory}
        if [ ! -d "${newdir}" ] ; then
            if [ "${DEBUG}" = "true" ] ; then
                echo "createPath: Creating subdir: '${newdir}'"
            fi
            mkdir ${newdir}
            if [ $? -ne 0 ]
            then
                echo "   Error (createPath):  Creation of ${newdir} failed!"
                exit
            fi
        fi
    done
}

unset genSafeTmpDir
genSafeTmpDir() {
    tmp=${TMPDIR-/tmp}
    tmp=$tmp/somedir.$RANDOM.$RANDOM.$RANDOM.$$
    (umask 077 && mkdir $tmp) || {
      echo "Could not create temporary directory! Exiting." 1>&2
      exit 1
    }
    echo "results stored in: '${tmp}'"
}

unset parseBuild
parseBuild() {
    build=$1

    echo "- parseBuild -"

    # cut parameters: -s: only print if delimeter exists in input; -d delimeter; -f field(s) to print
    BRANCH=`echo ${build} | cut -s -d'.' -f1-2`
    if [ "${BRANCH}" = "" ] ; then
        usage
        echo "BRANCH Error: There is something wrong with BUILD. ('$build' should be VERSION.QUALIFIER)!"
        echo "              VERSION should be in the 3 part OSGi standard - Major.Minor.patch"
        echo " "
        exit 2
    fi

    VERSION=`echo ${build} | cut -s -d'.' -f1-3`
    if [ "${VERSION}" = "" ] ; then
        usage
        echo "VERSION Error: There is something wrong with BUILD. ('$build' should be VERSION.QUALIFIER)!"
        echo "               VERSION should be in the 3 part OSGi standard - Major.Minor.patch"
        echo " "
        exit 2
    fi

    QUALIFIER=`echo ${build} | cut -s -d'.' -f4`
    if [ "${QUALIFIER}" = "" ] ; then
        usage
        echo "QUALIFIER Error: There is something wrong with BUILD. ('$build' should be VERSION.QUALIFIER)!"
        echo "                 QUALIFIER should be in the form: vDATE-HASH where DATE is YYYYMMDD"
        echo " "
        exit 2
    fi

    # assign value of first field delimited by '-' (only use values containing '-' (-s)), with 'v' stripped, to DATE
    BLD_DATE=`echo ${QUALIFIER} | cut -s -d'-' -f1 | cut -s -d'v' -f2`
    if [ "${BLD_DATE}" = "" ] ; then
        usage
        echo "BLD_DATE Error: There is something wrong with QUALIFIER!"
        echo "                '$qualifier' should be in the form:"
        echo "                     vDATE-HASH where DATE is YYYYMMDD"
        echo " "
        exit 2
    fi

    # assign value of 2nd field delimited by '-' (only use values containing '-' (-s)), to HASH
    GIT_HASH=`echo ${QUALIFIER} | cut -s -d'-' -f2`
    if [ "${GIT_HASH}" = "" ] ; then
        usage
        echo "GIT_HASH Error: There is something wrong with QUALIFIER!"
        echo "                '$qualifier' should be in the form:"
        echo "                     vDATE-HASH where DATE is YYYYMMDD"
        echo " "
        exit 2
    fi

    if [ "$DEBUG" = "true" ] ; then
        echo "build    ='$build'"
        echo "BRANCH   ='$BRANCH'"
        echo "VERSION  ='$VERSION'"
        echo "QUALIFIER='$QUALIFIER'"
        echo "BLD_DATE ='$BLD_DATE'"
        echo "GIT_HASH ='$GIT_HASH'"
    fi
}

unset validateGitRepo
validateGitRepo() {

    echo "- validateGitRepo -"

    #Must run git commands from Git repo dir so, store current dir, and switch to repo
    curdir=`pwd`
    cd ${RUNTIME_REPO}

    # parse status of repo for current branch
    ststus_msg=`${GIT_EXEC} status`
    gitbranch=`echo $ststus_msg | grep -m1 "#" | cut -s -d' ' -f4`
    echo "Currently on '${gitbranch}' in '${RUNTIME_REPO}'"

    # is current branch the desired branch?
    error_cnt=0
    if [ "${BRANCH_NM}" = "${gitbranch}" ] ; then
       echo "Git repo already on branch '$BRANCH_NM'."
    else
       # switch to desired branch
       ${GIT_EXEC} checkout ${BRANCH_NM}
       if [ "$?" = "0" ] ; then
           # parse status of repo for current branch
           ststus_msg=`${GIT_EXEC} status`
           gitbranch=`echo $ststus_msg | grep -m1 "#" | cut -s -d' ' -f4`
           echo "Now on '${gitbranch}' in '${RUNTIME_REPO}'"
           echo "Git checkout complete."
       else
           # if encountered error, increment error_cnt
           error_cnt=`expr ${error_cnt} + 1`
       fi
    fi

    # verify switch took place
    if [ "$error_cnt" = "0" ] ; then
        echo "Git Repo on correct branch for promotion to continue..."
    else
        echo "Error detected switching branches. exiting..."
        exit 1
    fi

    # ensure repo is up-to-date
    # has to occur after setting the correct banch because "git pull" only grabs changes on the active branch.
    echo "Ensuring repo is up-to-date."
    ${GIT_EXEC} pull

    cd $curdir
}

unset validateBuild
validateBuild() {
    echo "- validateBuild -"

    if [ -d ${DNLD_DIR}/nightly/${VERSION}/${BLD_DATE} ] ; then
        echo "Valid build dir: '${DNLD_DIR}/nightly/${VERSION}/${BLD_DATE}'"
        if [ -e ${DNLD_DIR}/nightly/${VERSION}/${BLD_DATE}/eclipselink-${VERSION}.${QUALIFIER}.zip ] ; then
            echo "Valid build: '${DNLD_DIR}/nightly/${VERSION}/${BLD_DATE}/eclipselink-${VERSION}.${QUALIFIER}.zip' found."
        else
            echo "Invalid build: '${DNLD_DIR}/nightly/${VERSION}/${BLD_DATE}/eclipselink-${VERSION}.${QUALIFIER}.zip' not found."
            echo "Valid builds are:"
            ls ${DNLD_DIR}/nightly/${VERSION}/${BLD_DATE}/eclipselink-${VERSION}*.zip
            exit 1
        fi
    else
            echo "Invalid build dir: '${DNLD_DIR}/nightly/${VERSION}/${BLD_DATE}'"
            echo "Valid build dates are:"
            ls ${DNLD_DIR}/nightly/${VERSION}
            exit 1
   fi
   #cd $curdir

}

# TODO: again NEED branch to verify instead of branch_NM, but need branch_NM to interact with Git
unset validateMilestone
validateMilestone() {
    milestone=$1
    echo "- validateMilestone -"

    # TODO: Verify ${milestone} is 'release', or starts with M# or RC#
    if [ -d ${DNLD_DIR}/milestones/${VERSION}/${milestone} ] ; then
        echo "Milestone dir: '${DNLD_DIR}/milestones/${VERSION}/${milestone}' already exists."
        if [ -e ${DNLD_DIR}/milestones/${VERSION}/${milestone}/eclipselink-${VERSION}.${QUALIFIER}.zip ] ; then
            echo "     Milestone ${milestone} Build: '${DNLD_DIR}/milestones/${VERSION}/${milestone}/eclipselink-${VERSION}.${QUALIFIER}.zip' already promoted."
        else
            promotedBuild=`ls ${DNLD_DIR}/milestones/${VERSION}/${milestone}/eclipselink-${VERSION}*.zip`
            echo "     Milestone ${milestone} Build: '${promotedBuild}' found."
        fi
        echo "     You should either choose another Milestone number, or clean previous promote (if partial) before running again."
        exit 1
    else
        echo "Milestone dir: '${DNLD_DIR}/milestone/${VERSION}/${milestone}' not preexisting."
        echo "Continuing..."
    fi
}

unset parseVersion
parseVersion() {
    branch_nm=$1
    echo "- parseVersion -"

    if [ -d ${DNLD_DIR}/milestones ] ; then
        if [ "$branch_nm" = "master" ] ; then
            # Version is 'highest' dir ending in '0'
            VERSION=`ls -r ${DNLD_DIR}/milestones | grep -m1 0$`
        else
            # Version is 'highest' dir begining with value of $branch_nm
            VERSION=`ls -r ${DNLD_DIR}/milestones | grep -m1 $branch_nm`
        fi
    else
        echo "Cannot find milestones. '${DNLD_DIR}/milestones' not found."
    fi
    echo "Version= '$VERSION'"
}

unset validateReleaseMilestone
validateReleaseMilestone() {
    milestone=$1
    echo "- validateReleaseMilestone -"

    # if milestone begins with either "M" or "RC" (doesn't (not begin with M) and (not begin with RC))
    test1=`echo $milestone | grep -m1 ^M`
    test2=`echo $milestone | grep -m1 ^RC`
    if [ ! \( \( "$test1" = "" \) -a \( "$test2" = "" \) \) ] ; then
        if [ -d ${DNLD_DIR}/milestones/${VERSION}/${milestone} ] ; then
            echo " Target Milestone dir: '${DNLD_DIR}/milestones/${VERSION}/${milestone}' exists. Continuing..."
        else
            echo "Milestone dir: '${DNLD_DIR}/milestones/${VERSION}/${milestone}' doesn't exist."
            exit 1
        fi
    else
        usage
        echo "Invalid Milestone detected: '$milestone' doesn't begin with 'M' or 'RC'"
        exit 1
    fi
}

unset parseReleaseMilestone
parseReleaseMilestone() {
    #find 'build' data from published milestone
    echo "Target Milestone: '${MILESTONE}'"

    BUILD=`ls ${DNLD_DIR}/milestones/${VERSION}/${MILESTONE} | grep -m1 P2signed | cut -d'-' -f3-4 | cut -d'.' -f1-4`
    echo "BUILD: '${BUILD}'"

    # set Milestone to 'release' for ant script
    # TODO: fix ant so will promote from published milestone rather than nightly
    MILESTONE=RELEASE
    parseBuild ${BUILD}
}

#TODO Must have Git validation and setup completed first
unset callAnt
callAnt() {
    #Need milestine branch, version, qualifier, date, githash
    milestone=$1
    branch=$2
    branch_nm=$3
    version=$4
    qualifier=$5
    blddate=$6
    githash=$7

    echo " "
    echo "- callAnt -"

    # Define SYSTEM variables needed
    BldDepsDir=$HOME/extension.lib.external    # Needed for Eclipse dependencies when publishing/promoting
    if [ ! -d "${BldDepsDir}" ] ; then
        echo "${BldDepsDir} not found!"
    fi
    if [ ! -d "${RELENG_REPO}" ] ; then
        echo "${RELENG_REPO} not found!"
    fi

    #verify src, root dest, and needed variables exist before proceeding
    if [ \( ! "${milestone}" = "" \) -a \( ! "${branch}" = "" \) -a \( ! "${blddate}" = "" \) -a \( ! "${version}" = "" \) -a \( ! "${qualifier}" = "" \) ] ; then
        echo "Preparing to promote ${milestone} for ${version}...."
        if [ "${DEBUG}" = "true" ] ; then
            echo "callAnt: Required data verified... proceeding..."
            echo "   milestone = '${milestone}'"
            echo "   branch    = '${branch}'"
            echo "   blddate   = '${blddate}'"
            echo "   version   = '${version}'"
            echo "   qualifier = '${qualifier}'"
            echo "   githash   = '${githash}'"
        fi

        #Invoke Antscript for Branch specific promotion
        arguments="-Dbuild.deps.dir=${BldDepsDir} -Dreleng.repo.dir=${RELENG_REPO} -Dgit.exec=${GIT_EXEC} -Declipselink.root.download.dir=${DNLD_DIR} -Dsigning.dir=${SIGN_DIR}"
        arguments="${arguments} -Dbranch.name=${branch_nm} -Drelease.version=${version} -Dbuild.type=${milestone} -Dbranch=${branch}"
        arguments="${arguments} -Dversion.qualifier=${qualifier} -Dbuild.date=${blddate} -Dgit.hash=${githash}"
        echo "   sign   = '${SIGN}'"
        if [ "${SIGN}" = "true" ] ; then
          arguments="${arguments} -Dsigning.script=${RELENG_REPO}/sign.sh"
        else
          arguments="${arguments} -Dsigning.script=${RELENG_REPO}/noSign.sh"
        fi

        # Run Ant from ${exec_location} using ${buildfile} ${arguments}
        echo "pwd='`pwd`"
        echo "ant ${BUILDFILE} ${arguments} ${ANT_TARGET}"
        if [ -f ${BUILDFILE} ] ; then
            ant -f ${BUILDFILE} ${arguments} ${ANT_TARGET}
            if [ "$?" = "0" ]
            then
                echo "Ant promote complete."
            else
                echo "Ant Promote Failed!"
            fi
        else
            echo "'${BUILDFILE}' doesn't exist. Aborting ant run..."
        fi
    else
        # Something is not right! skipping.."
        echo "    Required locations and data failed to verify... aborting Promote...."
        ERROR=true
        if [ "${DEBUG}" = "true" ] ; then
            echo "callAnt: Required locations and data:"
            echo "   milestone = '${milestone}'"
            echo "   branch    = '${branch}'"
            echo "   blddate   = '${blddate}'"
            echo "   version   = '${version}'"
            echo "   qualifier = '${qualifier}'"
            echo "   githash   = '${githash}'"
        fi
    fi
}



#==========================
#   Main Begins

#==========================
#   Validate run parameters
if [ "${BUILD}" = "" ] ; then
    usage
    echo " "
    echo "BUILD not specified! Exiting..."
    exit 1
fi
if [ "${MILESTONE}" = "" ] ; then
    usage
    echo " "
    echo "MILESTONE not specified! Exiting..."
    exit 1
fi
if [ "${BRANCH_NM}" = "" ] ; then
    usage
    echo " "
    echo "BRANCH_NM not specified! Exiting..."
    exit 1
fi
#  If anything is in DEBUG_ARG then do a dummy "DEBUG" run
#  (Do not call ant, do not modify or create files, do report variable states)
DEBUG=false
if [ -n "$DEBUG_ARG" ] ; then
    DEBUG=true
    echo "Debug is on!"
fi

#==========================
#     Define Environment
echo "-= Validate Environment =- "
if [ ! -d ${JAVA_HOME} ] ; then
    echo "Expecting Java at: '${JAVA_HOME}', but is not there!"
    JAVA_HOME=/shared/common/jdk1.6.0_05
    if [ ! -d ${JAVA_HOME} ] ; then
        echo "Tried again. Expecting Java at: '${JAVA_HOME}', but is not there!"
        #exit
    fi
fi
echo "JAVA_HOME verified at: '${JAVA_HOME}'"

if [ ! -d ${ANT_HOME} ] ; then
    echo "Expecting Ant at: '${ANT_HOME}', but is not there!"
    #exit
fi
echo "ANT_HOME verified at: '${ANT_HOME}'"

if [ ! -d ${HOME_DIR} ] ; then
    echo "Need to create HOME_DIR '${HOME_DIR}'"
    if [ "${DEBUG}" = "false" ] ; then
        echo "DEBUG=$DEBUG"
        createPath ${HOME_DIR}
    else
        echo "    Debug on, No actual work being done."
    fi
fi
if [ ! -d ${LOG_DIR} ] ; then
    echo "Need to create LOG_DIR '${LOG_DIR}'"
    if [ "${DEBUG}" = "false" ] ; then
        createPath ${LOG_DIR}
    else
        echo "    Debug on, No actual work being done."
    fi
fi
GIT_EXEC=/usr/local/bin/git
if [ ! -x ${GIT_EXEC} ] ; then
    echo "Cannot find Git executable using default value '$GIT_EXEC'. Attempting Autofind..."
    GIT_EXEC=`which git`
    if [ $? -ne 0 ] ; then
        echo "Error: Unable to find GIT executable! Git functionality disabled."
        GIT_EXEC=false
        exit 1
    else
        echo "Found: ${GIT_EXEC}"
    fi
else
    echo "Found: ${GIT_EXEC}"
fi
if [ ! -d ${RELENG_REPO} ] ; then
    echo "Releng repo missing! Will try to clone."
    echo "${GIT_EXEC} clone ssh://git.eclipse.org:29418/eclipselink/eclipselink.releng.git"
    if [ ! -d ${RELENG_REPO} ] ; then
        echo "Still cannot find '${RELENG_REPO}'. Something went wrong... aborting."
        exit 1
    else
        echo "Cloning successful."
    fi
else
    echo "Releng repo '${RELENG_REPO}' found."
fi
if [ ! -d ${RUNTIME_REPO} ] ; then
    echo "EclipseLink Runtime repo missing! Will try to clone."
    echo "${GIT_EXEC} clone git@github.com:eclipse-ee4j/eclipselink.git eclipselink.runtime"
    if [ ! -d ${RUNTIME_REPO} ] ; then
        echo "Still cannot find '${RUNTIME_REPO}'. Something went wrong... aborting."
        exit 1
    else
        echo "Cloning successful."
    fi
else
    echo "EclipseLink Runtime repo '${RUNTIME_REPO}' found."
fi
echo "   Validated."
echo " "

## Convert "BRANCH" to BRANCH_NM (version or trunk) and BRANCH (svn branch path)
#    BRANCH_NM is used for reporting and naming purposes
#    BRANCH    is used to quailify the actual Branch path

#==========================
#   Begin WORK
echo "Promote begin at: ${START_DATE}"

#Determine if 'release' run or regular Milestone promotion
if [ "$BUILD" = "release" ] ; then
    RELEASE=true
    # Figure out VERSION based upon BRANCH_NM
    parseVersion $BRANCH_NM

    # verify that specified milestone exists.
    validateReleaseMilestone $MILESTONE

    # relevant build coordinats from Milestone artifacts
    parseReleaseMilestone

    # Ensure we are on the correct branch in the repo, if build info is good
    validateGitRepo
else
    # Get needed info from BUILD
    parseBuild $BUILD

    # Validate BUILD Exists
    validateBuild

    # Ensure we are on the correct branch in the repo, if build info is good
    validateGitRepo

    # Validate MILESTONE against convention, and verify not preexisting
    validateMilestone $MILESTONE
fi

echo "BUILD      ='${BUILD}'"
echo "  BRANCH   ='$BRANCH'"
echo "  VERSION  ='$VERSION'"
echo "QUALIFIER  ='$QUALIFIER'"
echo "  BLD_DATE ='$BLD_DATE'"
echo "  GIT_HASH ='$GIT_HASH'"
echo "MILESTONE  ='${MILESTONE}'"
echo "BRANCH_NM  ='${BRANCH_NM}'"

callAnt $MILESTONE $BRANCH $BRANCH_NM $VERSION $QUALIFIER $BLD_DATE $GIT_HASH

echo "Promote complete at: `date '+%y%m%d-%H%M'`"
echo " "
echo " "
