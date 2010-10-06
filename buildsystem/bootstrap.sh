# !/bin/sh

THIS=$0
PROGNAME=`basename ${THIS}`
CUR_DIR=`dirname ${THIS}`
umask 0002
LOCAL_REPOS=false
MILESTONE=false
RELEASE=false
TEST=false
ORACLEBLD=false
UD2M=false
TARGET=$1
BRANCH=$2
if [ ! "${TARGET}" = "" ]
then
    if [ ! "`echo ${TARGET} | grep '\.test'`" = "" ]
    then
        TARGET=`echo ${TARGET} | cut -d. -f1`
        TEST=true
    fi
    TARG_NM=${TARGET}
else
    TARG_NM="default"
fi
if [ "${TARGET}" = "oraclebld" ]
then
    ORACLEBLD=true
    if [ ! "${BRANCH}" = "" ]
    then
        TARGET=${BRANCH}
        BRANCH=$3
        TARG_NM=${TARGET}
    else
        TARG_NM="default"
        TARGET=
    fi
fi
if [ "${TARGET}" = "milestone" ]
then
    MILESTONE=true
    if [ ! "${BRANCH}" = "" ]
    then
        QUALIFIER=$3
        if [ ! "${QUALIFIER}" = "" ]
        then
            #temporarily store Name of Milestone in TARGET
            #Syntax: ./bootstrap.sh milestone M4 QUALIFIER [branch]
            #  milestone - tells system that it will be promoting a build to a milestone
            #  M4        - an example of the milestone. Used in dir storage, and maven.
            #              Also of note "release" is a special milestone value that tells
            #              the system to promote build to the release for the version.
            #  QUALIFIER - gives the system all the info it needs to identify the build
            #              to promote.
            TARGET=${BRANCH}
            BRANCH=$4
            TARG_NM=${TARGET}
            if [ "${TARGET}" = "release" ]
            then
                RELEASE=true
                TARGET=milestone
            fi
        else
            echo "ERROR: Qualifier of the build must be specified in ARG3!"
            echo "USAGE: ./boostrap.sh milestone M9 QUALIFIER [branch]"
            echo "USAGE: ./boostrap.sh milestone release QUALIFIER [branch]"
            exit
        fi
    else
        echo "ERROR: Name of milestone must be specified in ARG2!"
        echo "USAGE: ./boostrap.sh milestone M9 QUALIFIER [branch]"
        echo "USAGE: ./boostrap.sh milestone release QUALIFIER [branch]"
        exit
    fi
fi
if [ "${TARGET}" = "custom" ]
then
    CUSTOM=true
    if [ ! "${BRANCH}" = "" ]
    then
        CUSTOM_TARG=${BRANCH}
        BRANCH=$3
    else
        echo "ERROR: Name of custom target must be specified in ARG2!"
        echo "USAGE: ./boostrap.sh custom TARGET [branch]"
        exit
    fi
fi
if [ "${TARGET}" = "release" ]
then
    echo "Error: 'release' is not a valid initial target. Use 'milestone release'. Exiting..."
    exit 2
fi
if [ "${TARGET}" = "uploadDeps" ]
then
    UD2M=true
    TARGET=
    TARG_NM=uploadDeps
    if [ ! "${BRANCH}" = "" ]
    then
        BRANCH=
    fi
fi

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

SVN_EXEC=/usr/local/bin/svn
if [ ! -x ${SVN_EXEC} ]
then
    echo "Cannot find svn executable using default value. Attempting Autofind..."
    SVN_EXEC=`which svn`
    if [ $? -ne 0 ]
    then
        echo "Error: Unable to find SVN client install!"
        exit 1
    else
        echo "Found: ${SVN_EXEC}"
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
START_DATE=`date '+%y%m%d-%H%M'`
#Directories
BOOTSTRAP_BLDFILE=bootstrap.xml
UD2M_BLDFILE=uploadDepsToMaven.xml
if [ "${ORACLEBLD}" = "true" ]
then
    JAVA_HOME=/shared/common/jdk1.6.0_21
    ANT_HOME=/usr/share/ant
    HOME_DIR=/shared/el_continuous
else
    # Conditional to allow single branch testing of jdk and ant env
    if [ ! "${BRANCH}" = "" ]
    then
        JAVA_HOME=/shared/common/jdk-1.6.x86_64
    else
        JAVA_HOME=/shared/common/jdk-1.6.x86_64
    fi
    ANT_HOME=/shared/common/apache-ant-1.7.0
    HOME_DIR=/shared/rt/eclipselink
fi
LOG_DIR=${HOME_DIR}/logs
BRANCH_PATH=${HOME_DIR}/${BRANCH}trunk
BLD_DEPS_DIR=${HOME_DIR}/bld_deps/${BRANCH_NM}
#Files
LOGFILE_NAME=bsb-${BRANCH_NM}_${TARG_NM}_${START_DATE}.log
DATED_LOG=${LOG_DIR}/${LOGFILE_NAME}
JDBC_LOGIN_INFO_FILE=${HOME_DIR}/db-${BRANCH_NM}.dat

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
    echo "USAGE: ./${PROGNAME} [target [target params] ..] [branch]"
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

unset getPrevRevision
getPrevRevision() {
    ## find the revision of the last build
    ##
    for prev_log in `ls -1 ${LOG_DIR} | grep bsb-${BRANCH_NM}_${TARG_NM} | sort -t_ -k3 -r | grep -v ${LOGFILE_NAME}`
    do
        ## exclude "build unnecessary" cb's without effecting other build types
        if [ "`tail ${LOG_DIR}/${prev_log} | grep unnece | tr -d '[:punct:]'`" = "" ]
        then
            PREV_REV=`cat ${LOG_DIR}/${prev_log} | grep revision | grep -m1 svn | cut -d= -f2 | tr -d '\047'`
            break
        fi
    done
}

unset genTestSummary
genTestSummary() {
    infile=$1
    outfile=$2

    if [ -f ${infile} ]
    then
        if [ -n ${infile} ]
        then
            tests=0
            errors=0
            failures=0
            Ttests=0
            Terrors=0
            Tfailures=0
            first_line="true"
            for line in `cat ${infile}`
            do
                if [ "`echo $line | grep 'test-'`" = "" ]
                then
                    # if a test result line, accumulate counts
                    T=`echo $line | cut -d: -f3`
                    E=`echo $line | cut -d: -f7`
                    F=`echo $line | cut -d: -f5`
                    tests=`expr "$tests" + "$T"`
                    errors=`expr "$errors" + "$E"`
                    failures=`expr "$failures" + "$F"`
                    Ttests=`expr "$Ttests" + "$T"`
                    Terrors=`expr "$Terrors" + "$E"`
                    Tfailures=`expr "$Tfailures" + "$F"`
                else
                    # If a header line, and not the first line print totals, next header, and zero out counts
                    if [ ! "$first_line" = "true" ]
                    then
                        echo "  Tests run:${tests} Failures:${failures} Errors:${errors}" >> ${outfile}
                    else
                        first_line="false"
                    fi
                    echo `echo $line | cut -d: -f2 | tr "[:lower:]" "[:upper:]"` >> ${outfile}
                    tests=0; errors=0; failures=0
                fi
            done
            # print out final totals
            if [ ! "$first_line" = "true" ]
            then
                echo "  Tests run:${tests} Failures:${failures} Errors:${errors}" >> ${outfile}
            fi
        else
            echo "Postprocessing Error: No test results to summarize!"
        fi
    fi

    # Return status based upon existence of test success (all pass="true (0)" else="fail (1)")
    if [ \( "$Terrors" -gt 0 \) -o \( "$Tfailures" -gt 0 \) ]
    then
        returncode=1
    else
        returncode=0
    fi
    return $returncode
}

#unset cleanFailuresDir
#cleanFailuresDir() {
#    num_files=10
#
#    # leave only the last 10 failed build logs on the download server
#    index=0
#    for logs in `ls ${FailedNFSDir} | grep log | sort -t_ -k3 -r` ; do
#        index=`expr $index + 1`
#        if [ $index -gt $num_files ] ; then
#            rm -r $logs
#        fi
#    done
#}

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
if [ ! -f ${CUR_DIR}/${BOOTSTRAP_BLDFILE} ]
then
    echo "Need bootstrap buildfile (${BOOTSTRAP_BLDFILE}) in the current directory to proceed."
    exit 1
else
    if [ ! "${HOME_DIR}" = "${CUR_DIR}" ]
    then
        cp ${CUR_DIR}/${BOOTSTRAP_BLDFILE} ${HOME_DIR}/${BOOTSTRAP_BLDFILE}
    fi
fi
if [ ! -f $JDBC_LOGIN_INFO_FILE ]
then
    echo "No db Login info available!"
    exit
else
    DB_USER=`cat $JDBC_LOGIN_INFO_FILE | cut -d'*' -f1`
    DB_PWD=`cat $JDBC_LOGIN_INFO_FILE | cut -d'*' -f2`
    DB_URL=`cat $JDBC_LOGIN_INFO_FILE | cut -d'*' -f3`
    DB_NAME=`cat $JDBC_LOGIN_INFO_FILE | cut -d'*' -f4`
fi

#Set appropriate max Heap for VM and let Ant inherit JavaVM (OS's) proxy settings
ANT_ARGS=" "
ANT_OPTS="-Xmx512m"
ANT_BASEARG="-f \"${BOOTSTRAP_BLDFILE}\" -Dbranch.name=\"${BRANCH}\""

# Test for "milestone" flag to start a build promotion rather than a real build
if [ "${MILESTONE}" = "true" ]
then
    ## Parse $QUALIFIER for build date value
    BLDDATE=`echo ${QUALIFIER} | cut -s -d'-' -f1 | cut -s -dv -f2`
    if [ "${BLDDATE}" = "" ]
    then
        echo "BLDDATE Error: There is something wrong with QUALIFIER. ('$QUALIFIER' should be vDATE-rREV)!"
        exit 2
    fi

    ## Parse $QUALIFIER for SVN revision value
    SVNREV=`echo ${QUALIFIER} | cut -s -d'-' -f2 | cut -s -dr -f2`
    if [ "${SVNREV}" = "" ]
    then
        echo "SVNREV Error: There is something wrong with QUALIFIER. ('$QUALIFIER' should be vDATE-rREV)!"
        exit 2
    fi

    # Setup parameters for Ant build
    ANT_BASEARG="${ANT_BASEARG} -Dsvn.revision=${SVNREV} -Dbuild.date=${BLDDATE}"
    if [ "${RELEASE}" = "true" ]
    then
        ANT_BASEARG="${ANT_BASEARG} -Dbuild.type=RELEASE"
    else
        ANT_BASEARG="${ANT_BASEARG} -Dbuild.type=${TARGET}"
    fi
    TARGET=milestone
fi

if [ "${LOCAL_REPOS}" = "true" ]
then
    ANT_BASEARG="${ANT_BASEARG} -D_LocalRepos=1"
fi

#Depends upon a valid putty install and config for "eclipse-dev"
#if [ "${ORACLEBLD}" = "true" ]
#then
#    #Only needed for dev behind firewall
#    ANT_OPTS="${ANT_OPTS}"
#    ANT_BASEARG="${ANT_BASEARG} -Dsvn.server.name=eclipse-dev"
#fi

## Save for future reference
#if [ "${RHB}" = "true" ]
#then
#    #Only needed for dev behind firewall
#    ANT_OPTS="-Dhttp.proxyHost=www-proxy.us.oracle.com ${ANT_OPTS}"
#    ANT_ARGS="-autoproxy"
#    ANT_BASEARG="${ANT_BASEARG} -D_RHB=1"
#fi

if [ "${TEST}" = "true" ]
then
    ANT_BASEARG="${ANT_BASEARG} -D_NoUpdateSrc=1"
fi

if [ "${UD2M}" = "true" ]
then
    ANT_BASEARG="-f \"${UD2M_BLDFILE}\" -Dbranch.name=\"${BRANCH}\" -D_NoUpdateSrc=1"
fi

if [ "${CUSTOM}" = "true" ]
then
    ANT_BASEARG="${ANT_BASEARG} -Dcustom.target=\"${CUSTOM_TARG}\""
fi

export ANT_ARGS ANT_OPTS ANT_HOME BRANCH_PATH HOME_DIR LOG_DIR JAVA_HOME JUNIT_HOME MAVENANT_DIR PATH CLASSPATH
export SVN_EXEC BLD_DEPS_DIR JUNIT_HOME TARGET BRANCH_NM

cd ${HOME_DIR}
echo "Results logged to: ${DATED_LOG}"
touch ${DATED_LOG}

echo "Cleaning the db for build..."
echo "Cleaning the db for build..." >> ${DATED_LOG}
echo "drop schema ${DB_NAME};" > sql.sql
echo "create schema ${DB_NAME};" >> sql.sql
mysql -u${DB_USER} -p${DB_PWD} < sql.sql
rm sql.sql
echo "done."
echo "done." >> ${DATED_LOG}

echo "Build starting..."
echo "Build started at: `date`" >> ${DATED_LOG}
#source ~/.ssh-agent >> ${DATED_LOG} 2>&1
echo "ant ${ANT_BASEARG} ${TARGET}" >> ${DATED_LOG}
ant ${ANT_BASEARG} -Ddb.user="${DB_USER}" -Ddb.pwd="${DB_PWD}" -Ddb.url="${DB_URL}" ${TARGET} >> ${DATED_LOG} 2>&1
echo "Build completed at: `date`" >> ${DATED_LOG}
echo "Build complete."

# If promoting a build just exit. there is no post-build processing
if [ \( "${MILESTONE}" = "true" \) -o \( "${CUSTOM}" = "true" \) ]
then
    exit
fi
##  ---------------------------------------- ####### ------------------------------------  ##
echo "Post-build Processing Starting..."
## Post-build Processing
##
MAILFROM=eric.gwin@oracle.com
if [ "${ORACLEBLD}" = "true" ]
then
    MAIL_EXEC=/usr/bin/mail
    MAILLIST="ejgwin@gmail.com"
    SUCC_MAILLIST="eric.gwin@oracle.com"
    FAIL_MAILLIST="eric.gwin@oracle.com"
#    SUCC_MAILLIST="eric.gwin@oracle.com edwin.tang@oracle.com"
#    FAIL_MAILLIST="peter.krogh@oracle.com david.twelves@oracle.com blaise.doughan@oracle.com tom.ware@oracle.com ejgwin@gmail.com"
    FailedNFSDir="/home/data/httpd/download.eclipse.org/rt/eclipselink/recent-failure-logs"
else
    MAIL_EXEC=/bin/mail
    MAILLIST="ejgwin@gmail.com"
    SUCC_MAILLIST="eric.gwin@oracle.com edwin.tang@oracle.com"
    FAIL_MAILLIST="eclipselink-dev@eclipse.org ejgwin@gmail.com"
    FailedNFSDir="/home/data/httpd/download.eclipse.org/rt/eclipselink/recent-failure-logs"
fi
PARSE_RESULT_FILE=${tmp}/raw-summary.txt
COMPILE_RESULT_FILE=${tmp}/compile-error-summary.txt
SORTED_RESULT_FILE=${tmp}/sorted-summary.txt
TESTDATA_FILE=${tmp}/testsummary-${BRANCH_NM}_${TARG_NM}.txt
SVN_LOG_FILE=${tmp}/svnlog-${BRANCH_NM}_${TARG_NM}.txt
MAILBODY=${tmp}/mailbody-${BRANCH_NM}_${TARG_NM}.txt
BUILD_FAILED="false"
TESTS_FAILED="false"

#set -x
## Verify Build Started before bothering with setting up for an email or post-processing
## if [ not "build unnecessary"  ] - (if build wasn't aborted due to no changes)
##
if [ "`tail ${DATED_LOG} | grep unnece | tr -d '[:punct:]'`" = "" ]
then
    ## find the current version (cannot use $BRANCH, because need current version stored in ANT buildfiles)
    ##
    VERSION=`cat ${DATED_LOG} | grep -m1 "EL version" | cut -d= -f2 | tr -d '\047'`
    BUILD_STR=`cat ${DATED_LOG} | grep -m1 version.string | cut -d= -f2 | tr -d '\047'`
    echo "Generating summary email for ${VERSION} build '${BUILD_STR}'."

    ## find the current revision
    ##
    CUR_REV=`cat ${DATED_LOG} | grep revision | grep -m1 svn | cut -d= -f2 | tr -d '\047'`

    ## find the revision of the last build
    ##
    getPrevRevision
    if [ ! "$PREV_REV" = "" ]
    then
        ## Include everything but the revision of the last build (jump 1 up from it)
        PREV_REV=`expr "${PREV_REV}" + "1"`
        ## Prepend the ":" for the "to" syntax of the "svn log" command
        PREV_REV=:${PREV_REV}
    fi
    echo "  changes included are from current revision to earliest not previously built (${CUR_REV}${PREV_REV}) inclusive."

    ## Generate transaction log for this revision
    ##
    svn log -r ${CUR_REV}${PREV_REV} -q -v  svn+ssh://dev.eclipse.org/svnroot/rt/org.eclipse.persistence/${BRANCH}trunk >> ${SVN_LOG_FILE}

    ## Verify Compile complete before bothering with post-build processing for test results
    ## if [ not build failed ]
    ##
    if [ ! "`tail ${DATED_LOG} | grep 'BUILD SUCCESSFUL'`" = "" ]
    then
        ## Ant log preprocessing to generate test results files
        ##
        cat ${DATED_LOG} | grep -n '^test-' | grep -v "\-jar" | grep -v "\-errors:" | grep -v lrg | grep -v t-srg | tr -d ' ' > ${PARSE_RESULT_FILE}
        cat ${DATED_LOG} | grep -n 'Errors: [0-9]' | tr -d ' ' | tr ',' ':' >> ${PARSE_RESULT_FILE}
        cat ${PARSE_RESULT_FILE} | sort -n -t: > ${SORTED_RESULT_FILE}

        ## Capture any 'allowed' compiler errors
        ##
        cat ${DATED_LOG} | grep -n '[javac]*errors' > ${COMPILE_RESULT_FILE}

        ## make sure TESTDATA_FILE is empty
        ##
        if [ -f ${TESTDATA_FILE} ]
        then
            rm -f ${TESTDATA_FILE}
        fi
        touch ${TESTDATA_FILE}

        ## postpend notification if signing failed
        if [ ! "`cat ${DATED_LOG} | grep 'SigningAborted'`" = "" ]
        then
            CAVEAT_TXT=" Signing failed. P2 not generated!"
        fi

        ## run routine to generate test results file and generate MAIL_SUBJECT based upon exit status
        ##
        genTestSummary ${SORTED_RESULT_FILE} ${TESTDATA_FILE}
        if [ $? -eq 0  ]
        then
            MAIL_SUBJECT="${BRANCH_NM} ${TARG_NM} build complete.${CAVEAT_TXT}"
            MAILLIST=${SUCC_MAILLIST}
        else
            MAIL_SUBJECT="${BRANCH_NM} ${TARG_NM} build has test failures!${CAVEAT_TXT}"
            TESTS_FAILED="true"
        fi

    else
        MAIL_SUBJECT="${BRANCH_NM} ${TARG_NM} build failed!"
        BUILD_FAILED="true"
    fi

    if [ "${TESTS_FAILED}" = "true" ]
    then
        echo "Build had Test issues that need to be resolved."
        LOGPREFIX=TestFail
        if [ "${TARG_NM}" = "cb" ]
        then
            # Zip up test results and copy them to appropriate location
            ant ${ANT_BASEARG} -l ${LOG_DIR}/SaveTstResults_${LOGFILE_NAME} -Dtest.result.dest.dir="${FailedNFSDir}" -Dtest.result.zip="TestResult_-${BRANCH_NM}_${TARG_NM}_${START_DATE}.zip" save-tst-results
            echo "Command to zip and copy test results"
            echo "   ant ${ANT_BASEARG} -l ${LOG_DIR}/SaveTstResults_${LOGFILE_NAME} -Dtest.result.dest.dir="${FailedNFSDir}" -Dtest.result.zip="TestResult_-${BRANCH_NM}_${TARG_NM}_${START_DATE}.zip" save-tst-results"
       fi
    fi

    if [ "${BUILD_FAILED}" = "true" ]
    then
        LOGPREFIX=BuildFail
        echo "Build had issues to be resolved."
    fi

    if [ \( "${BUILD_FAILED}" = "true" \) -o \( "${TESTS_FAILED}" = "true" \) ]
    then
        cp ${DATED_LOG} ${FailedNFSDir}/${LOGPREFIX}${LOGFILE_NAME}
        MAILLIST=${FAIL_MAILLIST}
        echo "Updating 'failed build' site..."
        chmod 755 ${BRANCH_PATH}/buildsystem/buildFailureList.sh
        ${BRANCH_PATH}/buildsystem/buildFailureList.sh
    fi

    ## Build Body text of email
    ##
    if [ -f ${MAILBODY} ]; then rm ${MAILBODY}; fi
    echo "Build summary for ${BUILD_STR}" > ${MAILBODY}
    echo "-----------------------------------" >> ${MAILBODY}
    echo "Non-critical compilation issues (if any) reported in" >> ${MAILBODY}
    echo "the format [BUILDLOG_LINE#: NUMBER_OF_ERRORS]:" >> ${MAILBODY}
    cat ${COMPILE_RESULT_FILE} >> ${MAILBODY}
    echo "-----------------------------------" >> ${MAILBODY}
    if [ \( -f ${TESTDATA_FILE} \) -a \( -n ${TESTDATA_FILE} \) ]
    then
        cat ${TESTDATA_FILE} >> ${MAILBODY}
        echo "-----------------------------------" >> ${MAILBODY}
        echo "" >> ${MAILBODY}
        rm ${TESTDATA_FILE}
    else
        touch ${MAILBODY}
    fi
    echo "Build log: (${DATED_LOG})" >> ${MAILBODY}
    if [ \( "${BUILD_FAILED}" = "true" \) -o \( "${TESTS_FAILED}" = "true" \) ]
    then
        if [ \( ! "${TARG_NM}" = "cb" \) -a \( "${TESTS_FAILED}" = "true" \) ]
        then
            echo "Test logs can be found on the download server at:" >> ${MAILBODY}
            echo "    http://www.eclipse.org/eclipselink/downloads/nightly.php" >> ${MAILBODY}
        fi
        echo "Build logs can be found on the download server at:" >> ${MAILBODY}
        echo "    http://www.eclipse.org/eclipselink/downloads/build-failures.php" >> ${MAILBODY}
    fi
    echo "-----------------------------------" >> ${MAILBODY}
    echo "" >> ${MAILBODY}
    echo "SVN Changes since Last Build:" >> ${MAILBODY}
    cat ${SVN_LOG_FILE} >> ${MAILBODY}

    ## Send result email
    ##
    echo "Sending email..."
    cat ${MAILBODY} | ${MAIL_EXEC} -r ${MAILFROM} -s "BUILD STATUS:: ${MAIL_SUBJECT}" ${MAILLIST}
    if [ $? -eq 0  ]
    then
       echo "     complete."
    else
       echo "     failed."
    fi
else
    echo "Build was aborted...   Post-Processing unecessary."
    if [ "`cat ${DATED_LOG} | grep -n  -m1 env.TARGET |  cut -d= -f2 | tr -d '\047' | tr -d ' '`" = "nightly" ]
    then
        if [ -f ${MAILBODY} ]; then rm ${MAILBODY}; fi
        echo "Because no changes to the repository were detected." > ${MAILBODY}
        echo " " >> ${MAILBODY}
        echo "The build log can be found at: ${DATED_LOG}" >> ${MAILBODY}
        MAIL_SUBJECT="${BRANCH_NM} ${TARG_NM} build aborted."
        MAILLIST=${SUCC_MAILLIST}
        echo "Sending 'Nightly Aborted' email..."
        cat ${MAILBODY} | ${MAIL_EXEC} -r ${MAILFROM} -s "BUILD STATUS:: ${MAIL_SUBJECT}" ${MAILLIST}
    fi
fi

## Remove tmp directory
##
rm -rf $tmp
echo "Post-build Processing Complete."

CLASSPATH=${OLD_CLASSPATH}
