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
THIS=$0
ARG1=$1
PROGNAME=`basename ${THIS}`
CUR_DIR=`dirname ${THIS}`
umask 0002
ANT_ARGS=" "
ANT_OPTS="-Xmx512m"
START_DATE=`date '+%y%m%d-%H%M'`

#Directories
JAVA_HOME=/shared/common/jdk-1.6.x86_64
ANT_HOME=/shared/common/apache-ant-1.7.0
HOME_DIR=/shared/rt/eclipselink
LOG_DIR=${HOME_DIR}/logs

PATH=${JAVA_HOME}/bin:${ANT_HOME}/bin:/usr/bin:/usr/local/bin:${PATH}

# Export necessary global environment variables
export ANT_ARGS ANT_OPTS ANT_HOME HOME_DIR JAVA_HOME JUNIT_HOME LOG_DIR PATH BLD_DEPS_DIR JUNIT_HOME
#==========================
#   Functions Definitions
#
unset Usage
Usage() {
    echo "ERROR: Invalid usage detected!"
    echo "USAGE: ./${PROGNAME} [\> LOGFILE 2\>\&1]"
}

unset CreatePath
CreatePath() {
    #echo "Running CreateHome!"
    newdir=
    for directory in `echo $1 | tr '/' ' '`
    do
        newdir=${newdir}/${directory}
        if [ ! -d "/${newdir}" ] ; then
            #echo "creating ${newdir}"
            mkdir ${newdir}
            if [ $? -ne 0 ]
            then
                echo "    Create failed!"
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

unset getHandoff
getHandoff() {
    curdir=`pwd`
    cd $HOME_DIR

    handoff=`ls handoff-file*.dat | grep -m1 *`

}

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
        echo "BRANCH='${BRANCH}' QUALIFIER='${QUALIFIER}' PROC='${PROC}'"
    fi
}

unset runAnt
runAnt() {
    #Define run specific variables
    BRANCH=$1
    QUALIFIER=$2
    PROC=$3

    if [ ! "${BRANCH}" = "master" ] ; then
        BRANCH=${BRANCH}
        BRANCH=branches/${BRANCH}/
    else
        BRANCH_NM="trunk"
        BRANCH=
    fi

    #Directories
    BRANCH_PATH=${WORKSPACE}
    BLD_DEPS_DIR=${HOME_DIR}/bld_deps/${BRANCH_NM}
    JUNIT_HOME=${BLD_DEPS_DIR}/junit

    #Files
    JDBC_LOGIN_INFO_FILE=${HOME_DIR}/db-${BRANCH_NM}.dat
    LOGFILE_NAME=bsb-${BRANCH_NM}_pub${PROC}_${START_DATE}.log
    DATED_LOG=${LOG_DIR}/${LOGFILE_NAME}

    # Define build dependency paths (build needs em, NOT compile dependencies)
    OLD_CLASSPATH=${CLASSPATH}
    CLASSPATH=${JUNIT_HOME}/junit.jar:${ANT_HOME}/lib/ant-junit.jar

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
    ANT_BASEARG="-f \"${BOOTSTRAP_BLDFILE}\" -Dbranch.name=\"${BRANCH}\" ${ANT_BASEARG} -Dgit.hash=${GITHASH}"
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

unset postProcess
postProcess() {
    #genSafeTmpDir
    echo "PostProcessing..."
    echo "   Generating notification for ${PROC} publishing for ${BRANCH} build ${QUALIFIER}..."
    echo "   Not sent..."
    # cleanup
    #rm -r ${tmp}
    echo "Done."
}

#==========================
#   Main Begins
#
#==========================
#  If anything is in ARG1 then do a dummy "DEBUG"
#  run (Don't call ant, don't remove handoff, do report variable states
if [ -z "$ARG1" ] ; then
    DEBUG=false
    echo "Debug is off!"
else
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
    echo "runAnt ${BRANCH} ${QUALIFIER} ${PROC} ${BUILD_ARCHIVE_LOC}"
    postProcess
done

