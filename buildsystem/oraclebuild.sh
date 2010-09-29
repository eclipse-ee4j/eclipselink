# !/bin/sh

THIS=$0
PROGNAME=`basename ${THIS}`
CUR_DIR=`dirname ${THIS}`
umask 0002
TARGET=oracle
TARG_NM="oracle"
BRANCH=$1


##-- Convert "BRANCH" to BRANCH_NM (version or trunk) and BRANCH (svn branch path)
#    BRANCH_NM is used for reporting and naming purposes
#    BRANCH    is used to quailify the actual Branch path
if [ ! "${BRANCH}" = "" ]
then
    BRANCH_NM=${BRANCH}
    BRANCH=branches/${BRANCH}/
else
    BRANCH_NM="trunk"
fi

echo "Target     ='${TARGET}'"
echo "Target name='${TARG_NM}'"
echo "Branch     ='${BRANCH}'"
echo "Branch name='${BRANCH_NM}'"

SVN_EXEC=`which svn`
if [ $? -ne 0 ]
then
    echo "Cannot autofind svn executable. Using default value."
    SVN_EXEC=/usr/local/bin/svn
    if [ ! -f ${SVN_EXEC} ]
    then
        echo "Error finding svn install!"
        exit 1
    fi
fi

# safe temp directory
tmp=${TMPDIR-/tmp}
tmp=$tmp/somedir.$RANDOM.$RANDOM.$RANDOM.$$
(umask 077 && mkdir $tmp) || {
  echo "Could not create temporary directory! Exiting." 1>&2
  exit 1
}
echo "results stored in: '${tmp}'"

#Define common variables
PUTTY_SESSION=eclipse-dev
START_DATE=`date '+%y%m%d-%H%M'`
DEFAULT_PREVREV=5400
DEFAULT_PREVCOMMIT=5401
#Directories
HOME_DIR=/shared/rt/eclipselink/oracle
JAVA_HOME=/shared/common/jdk1.6.0_21
ANT_HOME=/usr/share/ant
LOG_DIR=${HOME_DIR}/logs
ORACLE_ROOT=foundation/org.eclipse.persistence.oracle
ORACLE_CI_DIR=foundation/plugins
BRANCH_PATH=${HOME_DIR}/${BRANCH}trunk
BLD_DEPS_DIR=./bld_deps/${BRANCH_NM}
#URLs
HOME_URL=svnroot/rt/org.eclipse.persistence
BRANCH_URL=${HOME_URL}/${BRANCH}trunk
#Files
BOOTSTRAP_BLDFILE=bootstrap.xml
LOGFILE_NAME=bsb-${BRANCH_NM}_${TARG_NM}_${START_DATE}.log
PREV_BUILD_REV_NAME=lastbuild-${BRANCH_NM}.dat
DATED_LOG=${LOG_DIR}/${LOGFILE_NAME}
PREVREV_FILE=${LOG_DIR}/${PREV_BUILD_REV_NAME}
JDBC_LOGIN_INFO_FILE=${HOME_DIR}/db-${BRANCH_NM}.dat
TEMP_FILE=${tmp}/SVNLOG-${BRANCH_NM}_${TARG_NM}_${START_DATE}.txt

#Post-processing
MAIL_EXEC=/usr/bin/mail
MAILFROM=eric.gwin@oracle.com
MAILLIST="ejgwin@gmail.com"
SUCC_MAILLIST="eric.gwin@oracle.com"
#FAIL_MAILLIST="eclipselink-dev@eclipse.org ejgwin@gmail.com"
FAIL_MAILLIST="eric.gwin@oracle.com ejgwin@gmail.com"
TESTDATA_FILE=${tmp}/testsummary-${BRANCH_NM}_${TARG_NM}.txt
SVN_LOG_FILE=${tmp}/svnlog-${BRANCH_NM}_${TARG_NM}.txt
PROJ_LOG_FILE=${tmp}/projlog-${BRANCH_NM}_${TARG_NM}.txt
MAILBODY=${tmp}/mailbody-${BRANCH_NM}_${TARG_NM}.txt
FailedNFSDir="/home/data/httpd/download.eclipse.org/rt/eclipselink/recent-failure-logs"
BUILD_FAILED="false"
TESTS_FAILED="false"

#set -x
#Define build dependency dirs (build needs em, NOT compile dependencies)
JUNIT_HOME=${BLD_DEPS_DIR}/junit
MAVENANT_DIR=${BLD_DEPS_DIR}/mavenant
MAILLIB_DIR=${BRANCH_PATH}/foundation/eclipselink.core.lib

PATH=${JAVA_HOME}/bin:${ANT_HOME}/bin:/usr/bin:/usr/local/bin:${PATH}
OLD_CLASSPATH=${CLASSPATH}
CLASSPATH=${JUNIT_HOME}/junit.jar:${ANT_HOME}/lib/ant-junit.jar:${MAILLIB_DIR}/mail.jar:${MAILLIB_DIR}/activation.jar:${MAVENANT_DIR}/maven-ant-tasks-2.0.8.jar

#------- Subroutines -------#
unset Usage
Usage() {
    echo "ERROR: Invalid usage detected!"
    echo "USAGE: ./${PROGNAME} [branch]"
}

unset CreatePath
CreatePath() {
    #echo "Running CreateHome!"
    newdir=
    for directory in `echo $1 | tr '/' ' '`
    do
        newdir=${newdir}/${directory}
        if [ ! -d "/${newdir}" ]
        then
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

#--------- MAIN --------#

#Test for existence of dependencies, send email and fail if any of the above not found.
if [ ! -d ${JAVA_HOME} ]
then
    echo "Java not found!"
    echo "Expecting Java at: ${JAVA_HOME}"
    JAVA_HOME=/shared/common/jdk1.6.0_05
    if [ ! -d ${JAVA_HOME} ]
    then
        echo "Java not found!"
        echo "Expecting Java at: ${JAVA_HOME}"
        exit
    else
        echo "Java found at: ${JAVA_HOME}"
    fi
fi
if [ ! -d ${ANT_HOME} ]
then
    echo "Ant not found!"
    echo "Expecting Ant at: ${ANT_HOME}"
    exit
fi
if [ ! -d ${HOME_DIR} ]
then
    echo "Need to create HOME_DIR (${HOME_DIR})"
    CreatePath ${HOME_DIR}
fi
if [ ! -d ${LOG_DIR} ]
then
    echo "Need to create LOG_DIR (${LOG_DIR})"
    CreatePath ${LOG_DIR}
fi
if [ ! -d ${BLD_DEPS_DIR} ]
then
    echo "Need to create BLD_DEPS_DIR (${BLD_DEPS_DIR})"
    CreatePath ${BLD_DEPS_DIR}
fi
#if [ ! -f $JDBC_LOGIN_INFO_FILE ]
#then
#    echo "No db Login info available!"
#    exit
#else
#    DB_USER=`cat $JDBC_LOGIN_INFO_FILE | cut -d'*' -f1`
#    DB_PWD=`cat $JDBC_LOGIN_INFO_FILE | cut -d'*' -f2`
#    DB_URL=`cat $JDBC_LOGIN_INFO_FILE | cut -d'*' -f3`
#    DB_NAME=`cat $JDBC_LOGIN_INFO_FILE | cut -d'*' -f4`
#fi

#Set appropriate max Heap for VM and let Ant inherit JavaVM (OS's) proxy settings
ANT_ARGS=" "
ANT_OPTS="-Xmx512m"
ANT_BASEARG="-f \"${BOOTSTRAP_BLDFILE}\" -Dbranch.name=\"${BRANCH}\" -Dsvn.server.name=eclipse-dev"

export ANT_ARGS ANT_OPTS ANT_HOME BRANCH_PATH HOME_DIR LOG_DIR JAVA_HOME JUNIT_HOME MAVENANT_DIR PATH CLASSPATH
export SVN_EXEC BLD_DEPS_DIR JUNIT_HOME TARGET BRANCH_NM

cd ${HOME_DIR}
echo "Results logged to: ${DATED_LOG}"
touch ${DATED_LOG}

echo "Testing if build is needed..." >> ${DATED_LOG}
echo "Testing if build is needed..."
if [ -f ${PREVREV_FILE} ]; then
    PREV_REV=`cat ${PREVREV_FILE} | cut -d: -f1`
    PREV_COMMIT=`cat ${PREVREV_FILE} | cut -d: -f2`
fi
if [ "${PREV_REV}" = "" ]; then PREV_REV=${DEFAULT_PREVREV}; fi
if [ "${PREV_COMMIT}" = "" ]; then PREV_COMMIT=${DEFAULT_PREVCOMMIT}; fi
# Test to make sure noone else checked in a new version of the oracle jars independant of this process
svn log -q -r HEAD:${PREV_REV} svn+ssh://${PUTTY_SESSION}/${BRANCH_URL}/${ORACLE_CI_DIR} > ${TEMP_FILE}
PREV_COMMIT=`cat ${TEMP_FILE} | grep -m1 -v "\-\-\-" | cut -d' ' -f1 | cut -c 2-`
echo "    previous Revs (Proj:Commit): '${PREV_REV}:${PREV_COMMIT}'" >> ${DATED_LOG}
echo "    previous Revs (Proj:Commit): '${PREV_REV}:${PREV_COMMIT}'"
svn log -q -r HEAD:${PREV_REV} svn+ssh://${PUTTY_SESSION}/${BRANCH_URL}/${ORACLE_ROOT} > ${TEMP_FILE}
CURRENT_REV=`cat ${TEMP_FILE} | grep -m1 -v "\-\-\-" | cut -d' ' -f1 | cut -c 2-`
echo "    curProjRev: '${CURRENT_REV}'" >> ${DATED_LOG}
echo "    curProjRev: '${CURRENT_REV}'"
if [ "${CURRENT_REV}" -gt "${PREV_REV}" ]
then
    # Get Current view revision for later use
    svn info ${BRANCH_PATH} > ${TEMP_FILE}
    VIEW_REV=`cat ${TEMP_FILE} | grep -m1 Revision | cut -d: -f2 | tr -d ' '`
    echo "    curViewRev: '${VIEW_REV}'"
    # remove potential "conflicts"
    echo "Cleanup previous build for new checkout..." >> ${DATED_LOG}
    echo "Cleanup previous build for new checkout..."
    rm -f ${BRANCH_PATH}/eclipselink*
    rm -f ${BRANCH_PATH}/${ORACLE_CI_DIR}/org.eclipse.persistence.oracle*
    echo "Retrieving latest source from subversion..." >> ${DATED_LOG}
    echo "Retrieving latest source from subversion..."
    svn co --non-interactive svn+ssh://${PUTTY_SESSION}/${BRANCH_URL} ${BRANCH_PATH}
    # Get Current view revision for later use
    svn info ${BRANCH_PATH} > ${TEMP_FILE}
    NEW_VIEW_REV=`cat ${TEMP_FILE} | grep -m1 Revision | cut -d: -f2 | tr -d ' '`
    echo "    newViewRev: '${NEW_VIEW_REV}' (If matches SVN_REV can remove some postprocessing code)."
    echo "Copying latest ${BOOTSTRAP_BLDFILE} for build..." >> ${DATED_LOG}
    echo "Copying latest ${BOOTSTRAP_BLDFILE} for build..."
    cp ${BRANCH_PATH}/buildsystem/${BOOTSTRAP_BLDFILE} ./${BOOTSTRAP_BLDFILE}
    echo "Oracle Extension Build started at: `date`" >> ${DATED_LOG}
    echo "Oracle Extension Build started."
    echo "ant ${ANT_BASEARG} ${TARGET}" >> ${DATED_LOG}
    ant ${ANT_BASEARG} -Ddb.user="${DB_USER}" -Ddb.pwd="${DB_PWD}" -Ddb.url="${DB_URL}" ${TARGET} >> ${DATED_LOG} 2>&1
    echo "Build completed at: `date`" >> ${DATED_LOG}
    echo "Build completed."
    echo "Updating Revision info..." >> ${DATED_LOG}
    echo "Updating Revision info..."
    svn log -q -r HEAD:${PREV_REV} svn+ssh://${PUTTY_SESSION}/${BRANCH_URL}/${ORACLE_CI_DIR} > ${TEMP_FILE}
    COMMIT_REV=`cat ${TEMP_FILE} | grep -m1 -v "\-\-\-" | cut -d' ' -f1 | cut -c 2-`
    echo "    Commit revisions (New:Prev): '${COMMIT_REV}:${PREV_COMMIT}'" >> ${DATED_LOG}
    echo "    Commit revisions (New:Prev): '${COMMIT_REV}:${PREV_COMMIT}'"
    if [ "${COMMIT_REV}" -gt "${PREV_COMMIT}" ]
    then
        COMMIT=true
        echo "${CURRENT_REV}:${COMMIT_REV}" > ${PREVREV_FILE}
        echo "   New revision info stored, commit appears to have completed successfully." >> ${DATED_LOG}
        echo "   New revision info stored, commit appears to have completed successfully."
    else
        echo "   It appears there was no commit. Aborting Revision update..." >> ${DATED_LOG}
        echo "   It appears there was no commit. Aborting Revision update..."
    fi

    #----------- Post-processing ------------#
    #----------------------------------------#
    echo "Beginning Post-Build processing..."
    ## find the current version (cannot use $BRANCH, because need current version stored in ANT buildfiles)
    ##
    VERSION=`cat ${DATED_LOG} | grep -m1 "EL version" | cut -d= -f2 | tr -d '\047'`
    SVN_REV=`cat ${DATED_LOG} | grep -m1 "svn.revision" | cut -d= -f2 | tr -d '\047'`
    echo "Generating summary email for ${VERSION} build..."
    echo "    Revision info (project code:built using:artifact ci): '${CURRENT_REV}:${SVN_REV}:${COMMIT_REV}'"
    echo "    DEBUG: SVN_REV: '${SVN_REV}'"

    echo "Getting View transaction log..."
    ## fixup the revision of the previous view to not include itself
    ##
    if [ ! "$VIEW_REV" = "" ]
    then
        ## Include everything but the revision of the last build (jump 1 up from it)
        PREV_VIEW=`expr "${VIEW_REV}" + "1"`
        ## Prepend the ":" for the "to" syntax of the "svn log" command
        PREV_VIEW=:${PREV_VIEW}
    else
        echo "    ERROR: What the heck's going on here? There's no VIEW_REV?"
    fi
    echo "    change log will be from the current retrieved codebase to earliest"
    echo "    not previously checked-out (${SVN_REV}${PREV_VIEW}) inclusive."
    ## Generate transaction log for latest build
    ##
    svn log -q -r ${SVN_REV}${PREV_VIEW} -v  svn+ssh://${PUTTY_SESSION}/${BRANCH_URL} >> ${SVN_LOG_FILE}

    echo "Getting  Project transaction log..."
    ## fixup the revision of the last project build to not include itself
    ##
    if [ ! "$PREV_REV" = "" ]
    then
        ## Include everything but the revision of the last build (jump 1 up from it)
        OLDEST_TRAN=`expr "${PREV_REV}" + "1"`
        ## Prepend the ":" for the "to" syntax of the "svn log" command
        OLDEST_TRAN=:${OLDEST_TRAN}
    else
        echo "    ERROR: What the heck's going on here? There's no PREV_REV?"
    fi
    echo "    change log will be from latest project transaction to earliest"
    echo "    not previously built (${CURRENT_REV}${OLDEST_TRAN}) inclusive."
    ## Generate transaction log for oracle project changes
    ##
    svn log -q -r ${CURRENT_REV}${OLDEST_TRAN} -v  svn+ssh://${PUTTY_SESSION}/${BRANCH_URL}/${ORACLE_ROOT} >> ${PROJ_LOG_FILE}

    ## Verify Compile complete
    ## if [ not build failed ]
    ##
    echo "Verifying build status..."
    if [ ! "`tail ${DATED_LOG} | grep 'BUILD SUCCESSFUL'`" = "" ]
    then
        echo "    Build was successful."
        MAIL_SUBJECT="${BRANCH_NM} Oracle Extension Nightly build complete."
        MAILLIST=${SUCC_MAILLIST}
    else
        echo "    Build had issues to be resolved."
        MAIL_SUBJECT="${BRANCH_NM} Oracle Extension Nightly build failed!"
        MAILLIST=${FAIL_MAILLIST}
    fi

    ## Build Body text of email
    ##
    if [ -f ${MAILBODY} ]; then rm ${MAILBODY}; fi
    echo "Build summary for o.e.p.oracle project revision ${CURRENT_REV} (Full codebase rev '${SVN_REV}')." > ${MAILBODY}
    echo "-----------------------------------" >> ${MAILBODY}
    echo "Full Build log can be found on the Oracle Build machine at:" >> ${MAILBODY}
    echo "    ${DATED_LOG}" >> ${MAILBODY}
    echo "-----------------------------------" >> ${MAILBODY}
    if [ "${COMMIT}" = "true" ]; then
        echo "This build of Oracle Extension committed as Revision ${COMMIT_REV}" >> ${MAILBODY}
        echo "-----------------------------------" >> ${MAILBODY}
    else
        MAIL_SUBJECT="${MAIL_SUBJECT} Commit Failed!"
        echo "Commit FAILED for this build of Oracle Extension" >> ${MAILBODY}
        echo "-----------------------------------" >> ${MAILBODY}
    fi
    echo "" >> ${MAILBODY}
    echo "Project Changes since Last Build:" >> ${MAILBODY}
    cat ${PROJ_LOG_FILE} >> ${MAILBODY}
    echo "View Changes since Last Build:" >> ${MAILBODY}
    cat ${SVN_LOG_FILE} >> ${MAILBODY}

    ## Send result email
    ##
    echo "Sending email..."
    cat ${MAILBODY} | ${MAIL_EXEC} -user=${MAILFROM} --subject="BUILD STATUS:: ${MAIL_SUBJECT}" ${MAILLIST}
    if [ $? -eq 0  ]
    then
       echo "     complete."
    else
       echo "     failed."
    fi

    echo "Printing Project Transaction log..."
    echo " "
    cat ${PROJ_LOG_FILE}
    echo "#####################################################################"
    echo "Printing Transaction log for Build View..."
    cat ${SVN_LOG_FILE}
else
    echo "Oracle Extension Nightly build aborted... no code changes" >> ${DATED_LOG}
    echo "Oracle Extension Nightly build aborted... no code changes"
    if [ -f ${MAILBODY} ]; then rm ${MAILBODY}; fi
    echo "Because no changes to the repository were detected." > ${MAILBODY}
    echo " " >> ${MAILBODY}
    echo "The build log can be found at: ${DATED_LOG}" >> ${MAILBODY}
    MAIL_SUBJECT="${BRANCH_NM} Oracle Extension Nightly build aborted."
    MAILLIST=${SUCC_MAILLIST}

    echo "Sending 'Oracle Nightly Aborted' email..."
    cat ${MAILBODY} | ${MAIL_EXEC} --user=${MAILFROM} --subject="BUILD STATUS:: ${MAIL_SUBJECT}" ${MAILLIST}
    if [ $? -eq 0  ]
    then
       echo "     complete."
    else
       echo "     failed."
    fi
fi

rm -rf $tmp
#----------------------------------------#
CLASSPATH=${OLD_CLASSPATH}
