# !/bin/sh

#------------------------------------------------------------------------------------------------------
#    This script is designed to run from cron on the Eclipse foundation's build server
#       It tests for the existence of a completed build or test run
#       then initiates the publication of the results to the appropriate locations and
#       sends out notifiacation when complete.
#
#    Author: Eric Gwin (Oracle)
#

#==========================
#   Basic Env Setup
#

#Define common variables
ARG1=$1
umask 0002
ANT_ARGS=" "
ANT_OPTS="-Xmx512m"
START_DATE=`date '+%y%m%d-%H%M'`

#Directories
ANT_HOME=/shared/common/apache-ant-1.7.0
HOME_DIR=/shared/rt/eclipselink
JAVA_HOME=/shared/common/jdk-1.6.x86_64
LOG_DIR=${HOME_DIR}/logs

#Files
ant.buildfile=

# Need to export after parsing:
# BLD_DEPS_DIR
BRANCH
GITHASH
BLDDATE
handoff

PATH=${JAVA_HOME}/bin:${ANT_HOME}/bin:/usr/bin:/usr/local/bin:${PATH}

# Export necessary global environment variables
export ANT_ARGS ANT_OPTS ANT_HOME HOME_DIR JAVA_HOME LOG_DIR PATH
#==========================
#   Functions Definitions
#
unset parseHandoff
parseHandoff() {
    handoff_file=$1
    handoff_error_string="Error: Invalid handoff_filename: '${handoff_file}'! Should be 'handoff-file-<PROC>-<BRANCH>-<QUALIFIER>.dat'"
    handoff_content_error="Error: Invalid handoff_file contents: '`cat ${handoff_file}`'! Should be 'extract.loc=<BUILD_ARCHIVE_LOC> host=<HOST>'"

    ## Parse handoff_file for BRANCH, QUALIFIER, and HOST
    BRANCH=`echo ${handoff_file} | cut -s -d'-' -f4`
    if [ "${BRANCH}" = "" ] ; then
        echo "BRANCH ${handoff_error_string}"
        exit 2
    fi
    QUALIFIER=`echo ${handoff_file} | cut -s -d'-' -f5,6 | cut -d'.' -f1`
    if [ "${QUALIFIER}" = "" ] ; then
        echo "QUALIFIER ${handoff_error_string}"
        exit 2
    fi
    PROC=`echo ${handoff_file} | cut -s -d'-' -f3`
    if [ !  \( \( "${PROC}" = "test" \) -o \( "${PROC}" = "build" \) \) ] ; then
        echo "PROC ${handoff_error_string}"
        exit 2
    fi
    BUILD_ARCHIVE_LOC=`cat ${handoff_file} | cut -d' ' -f1 | cut -d'=' -f2`
    if [ "${BUILD_ARCHIVE_LOC}" = "" ] ; then
        echo "BUILD_ARCHIVE_LOC ${handoff_content_error}"
        exit 2
    fi
    HOST=`cat ${handoff_file} | cut -d' ' -f2 | cut -d'=' -f2`
    if [ "${HOST}" = "" ] ; then
        echo "HOST ${handoff_content_error}"
        exit 2
    fi
    if [ "${DEBUG}" = "true" ] ; then
        echo "BRANCH='${BRANCH}' QUALIFIER='${QUALIFIER}' PROC='${PROC}' HOST='${HOST}' BUILD_ARCHIVE_LOC='${BUILD_ARCHIVE_LOC}'"
    fi
}

unset runAnt
runAnt() {
    #Define run specific variables
    BRANCH=$1
    QUALIFIER=$2
    PROC=$3
    HOST=$4
    BUILD_ARCHIVE_LOC=$5

    if [ ! "${BRANCH}" = "master" ] ; then
        # Need to figure out version
    else
        $Need to figure out version
        BRANCH=$Version
    fi

    #Directories
#    BRANCH_PATH=${WORKSPACE}
#    BLD_DEPS_DIR=${HOME_DIR}/bld_deps/${BRANCH}
#    JUNIT_HOME=${BLD_DEPS_DIR}/junit

    #Files
#    JDBC_LOGIN_INFO_FILE=${HOME_DIR}/db-${BRANCH_NM}.dat
    LOGFILE_NAME=bsb-${BRANCH_NM}_pub${PROC}_${START_DATE}.log
    DATED_LOG=${LOG_DIR}/${LOGFILE_NAME}

    # Define build dependency paths (build needs em, NOT compile dependencies)
#    OLD_CLASSPATH=${CLASSPATH}
#   CLASSPATH=${JUNIT_HOME}/junit.jar:${ANT_HOME}/lib/ant-junit.jar

    # Export run specific variables
    export BLD_DEPS_DIR BRANCH_NM BRANCH_PATH CLASSPATH JUNIT_HOME TARGET

    ## Parse $QUALIFIER for build date value
    BLDDATE=`echo ${QUALIFIER} | cut -s -d'-' -f1 | cut -s -dv -f2`
    if [ "${BLDDATE}" = "" ] ; then
        echo "BLDDATE Error: There is something wrong with QUALIFIER. ('$QUALIFIER' should be vDATE-rREV)!"
        exit 2
    fi

    ## Parse $QUALIFIER for SVN revision value
    GITHASH=`echo ${QUALIFIER} | cut -s -d'-' -f2`
    if [ "${GITHASH}" = "" ] ; then
        echo "GITHASH Error: There is something wrong with QUALIFIER. ('$QUALIFIER' should be vDATE-rREV)!"
        exit 2
    fi

    # Setup parameters for Ant build
    ANT_BASEARG="-f \"${ant.buildfile}\" -Dbranch.name=\"${BRANCH}\" -Dgit.hash=${GITHASH}"
    ANT_BASEARG="${ANT_BASEARG} -Dbuild.date=${BLDDATE} -Dhandoff.file=${handoff}"

    cd ${HOME_DIR}
    echo "Results logged to: ${DATED_LOG}"
    touch ${DATED_LOG}

    echo ""
    echo "${PROC} publish starting for ${BRANCH} build:${QUALIFIER} from '`pwd`'..."
    echo "${PROC} publish started at: '`date`' for ${BRANCH} build:${QUALIFIER} from '`pwd`'..." >> ${DATED_LOG}
    echo "   ant ${ANT_BASEARG} publish-${PROC}"
    echo "ant ${ANT_BASEARG} publish-${PROC}" >> ${DATED_LOG}
    if [ ! "$DEBUG" = "true" ] ; then
        #ant ${ANT_BASEARG} publish-${PROC} >> ${DATED_LOG} 2>&1
        echo "Not running: ant ${ANT_BASEARG} publish-${PROC}"
    fi
    echo "Publish completed (skipped) at: `date`" >> ${DATED_LOG}
    echo "Publish complete."
}

#==========================
#   Main Begins
#
#==========================
#  If anything is in ARG1 then do a dummy "DEBUG"
#  run (Don't call ant, don't remove handoff, do report variable states
DEBUG=false
if [ -n "$ARG1" ] ; then
    DEBUG=true
    echo "Debug is on!"
fi

#==========================
#     Test for handoff
#        if not exit with minimal work done.
curdir=`pwd`
cd $HOME_DIR
for handoff in `ls handoff-file*.dat` ; do
    echo "Detected handoff file:'${handoff}'. Process starting..."
    # Do stuff
    parseHandoff ${handoff}
    runAnt ${BRANCH} ${QUALIFIER} ${PROC} ${BUILD_ARCHIVE_LOC} ${HOST}
    echo "Done."
done

