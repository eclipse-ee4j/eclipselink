# !/bin/sh

THIS=$0
PROGNAME=`basename ${THIS}`
CUR_DIR=`dirname ${THIS}`
umask 0002
LOCAL_REPOS=false
MILESTONE=false
RELEASE=false
TEST=false
RHB=false
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
if [ "${TARGET}" = "redhat" ]
then
    RHB=true
    if [ ! "${BRANCH}" = "" ]
    then
        TARGET=${BRANCH}
        BRANCH=
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
        #temporarily store Name of Milestone in TARGET
        #Syntax: ./bootstrap.sh milestone M4 [branch]
        TARGET=${BRANCH}
        BRANCH=$3
        TARG_NM=${TARGET}
    else
        echo "ERROR: Name of milestone must be specified in ARG2!"
        echo "USAGE: ./boostrap.sh milestone M9 [branch]"
        exit
    fi
fi
if [ "${TARGET}" = "release" ]
then
    RELEASE=true
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
if [ ! "${BRANCH}" = "" ]
then
    BRANCH_NM=${BRANCH}
    BRANCH=branches/${BRANCH}/
else
    BRANCH_NM="trunk"
fi

echo "Target     ='${TARGET}'"
echo "Branch name='${BRANCH_NM}'"
echo "Branch     ='${BRANCH}'"

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
HOME_DIR=/shared/rt/eclipselink
BOOTSTRAP_BLDFILE=bootstrap.xml
UD2M_BLDFILE=uploadDepsToMaven.xml
JAVA_HOME=/shared/common/ibm-java-jdk-ppc-60
ANT_HOME=/shared/common/apache-ant-1.7.0
LOG_DIR=${HOME_DIR}/logs
BRANCH_PATH=${HOME_DIR}/${BRANCH}trunk
BLD_DEPS_DIR=${HOME_DIR}/bld_deps/${BRANCH_NM}
START_DATE=`date '+%y%m%d-%H%M'`
LOGFILE_NAME=bsb-${BRANCH_NM}_${TARG_NM}_${START_DATE}.log
DATED_LOG=${LOG_DIR}/${LOGFILE_NAME}
JDBC_LOGIN_INFO_FILE=${HOME_DIR}/db.dat

#Define build dependency dirs (build needs em, NOT compile dependencies)
JUNIT_HOME=${BLD_DEPS_DIR}/junit
MAVENANT_DIR=${BLD_DEPS_DIR}/mavenant
MAILLIB_DIR=${BRANCH_PATH}/foundation/eclipselink.core.lib

PATH=${JAVA_HOME}/bin:${ANT_HOME}/bin:/usr/bin:/usr/local/bin:${PATH}
OLD_CLASSPATH=${CLASSPATH}
CLASSPATH=${JUNIT_HOME}/junit.jar:${ANT_HOME}/lib/ant-junit.jar:${MAILLIB_DIR}/mail.jar:${MAILLIB_DIR}/activation.jar:${MAVENANT_DIR}/maven-ant-tasks-2.0.8.jar

#------- Subroutines -------#
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
getPrevRevision()
{
    ## find the revision of the last build
    ##
    for prev_log in `ls -1 ${LOG_DIR} | grep bsb-${BRANCH_NM}_${TARG_NM} | sort -t_ -k3 -r | grep -v ${LOGFILE_NAME}`
    do
        ## exclude "build unnecessary" cb's without effecting other build types
        if [ "`tail ${LOG_DIR}/${prev_log} | grep unnece | tr -d '[:punct:]'`" = "" ]
        then
            PREV_REV=`head -175 ${LOG_DIR}/${prev_log} | grep revision | grep -m1 svn | cut -d= -f2 | tr -d '\047'`
            echo "prev_rev='${PREV_REV}'" 
            break
        fi
    done
}

unset genTestSummary
genTestSummary()
{
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
            echo "No test results to summarize!"
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

unset cleanFailuresDir
cleanFailuresDir()
{
    num_files=10
    
    # leave only the last 10 failed build logs on the download server
    index=0
    for logs in `ls ${FailedNFSDir} | grep log | sort -t_ -k3 -r` ; do
        index=`expr $index + 1`
        if [ $index -gt $num_files ] ; then
            rm -r $contentdir
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

# May need to add "milestone" flag to alert build
if [ "${MILESTONE}" = "true" ]
then
    ANT_BASEARG="${ANT_BASEARG} -Dbuild.type=${TARGET} -Dbuild_id=${TARGET}"
    TARGET="milestone"
fi

# May need to add "release" flag to alert build
if [ "${RELEASE}" = "true" ]
then
    ANT_BASEARG="${ANT_BASEARG} -Dbuild.type=RELEASE -Dbuild_id= "
    TARGET="milestone"
fi

if [ "${LOCAL_REPOS}" = "true" ]
then
    ANT_BASEARG="${ANT_BASEARG} -D_LocalRepos=1"
fi

if [ "${RHB}" = "true" ]
then
    #Only needed for dev behind firewall
    ANT_OPTS="-Dhttp.proxyHost=www-proxy.us.oracle.com ${ANT_OPTS}"
    ANT_ARGS="-autoproxy"
    ANT_BASEARG="${ANT_BASEARG} -D_RHB=1"
fi

if [ "${TEST}" = "true" ]
then
    ANT_BASEARG="${ANT_BASEARG} -D_DoNotUpdate=1"
fi

if [ "${UD2M}" = "true" ]
then
    ANT_BASEARG="-f \"${UD2M_BLDFILE}\" -Dbranch.name=\"${BRANCH}\" -D_DoNotUpdate=1"
fi

export ANT_ARGS ANT_OPTS ANT_HOME BRANCH_PATH HOME_DIR LOG_DIR JAVA_HOME JUNIT_HOME MAVENANT_DIR PATH CLASSPATH
export SVN_EXEC BLD_DEPS_DIR JUNIT_HOME TARGET

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

##
## Post-build Processing
##
MAIL_EXEC=/bin/mail
MAILLIST="eric.gwin@oracle.com ejgwin@gmail.com tom.ware@oracle.com"
SUCC_MAILLIST="eric.gwin@oracle.com ejgwin@gmail.com tom.ware@oracle.com"
FAIL_MAILLIST="eclipselink-dev@eclipse.org ejgwin@gmail.com"
PARSE_RESULT_FILE=${tmp}/raw-summary.txt
SORTED_RESULT_FILE=${tmp}/sorted-summary.txt
TESTDATA_FILE=${tmp}/testsummary-${BRANCH_NM}_${TARG_NM}.txt
SVN_LOG_FILE=${tmp}/svnlog-${BRANCH_NM}_${TARG_NM}.txt
MAILBODY=${tmp}/mailbody-${BRANCH_NM}_${TARG_NM}.txt
FailedNFSDir="/home/data/httpd/download.eclipse.org/rt/eclipselink/recent-failure-logs"
BUILD_FAILED="false"

#set -x
## Verify Build Started bothering with setting up for an email or post-processing
## if [ not "build unnecessary"  ] - (an aborted cb attempt due to no changes)
##
if [ "`tail ${DATED_LOG} | grep unnece | tr -d '[:punct:]'`" = "" ]
then
    ## find the current version (cannot use $BRANCH, because need current version stored in ANT buildfiles)
    ##
    VERSION=`head -175 ${DATED_LOG} | grep -m1 "EL version" | cut -d= -f2 | tr -d '\047'`
    echo $VERSION
    
    ## find the current revision
    ##
    CUR_REV=`head -175 ${DATED_LOG} | grep revision | grep -m1 svn | cut -d= -f2 | tr -d '\047'`
    echo CUR_REV=$CUR_REV
    
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
    echo PREV_REV=$PREV_REV

    ## Generate transaction log for this revision
    ##
    svn log -r ${CUR_REV}${PREV_REV} -q -v  http://dev.eclipse.org/svnroot/rt/org.eclipse.persistence/${BRANCH}trunk >> ${SVN_LOG_FILE}
    
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
        
        ## make sure TESTDATA_FILE is empty
        ##
        if [ -f ${TESTDATA_FILE} ]
        then
            rm -f ${TESTDATA_FILE}
        fi
        touch ${TESTDATA_FILE}
        
        ## run routine to generate test results file and generate MAIL_SUBJECT based upon exit status
        ##
        genTestSummary ${SORTED_RESULT_FILE} ${TESTDATA_FILE}
        if [ $? -eq 0  ]
        then
            MAIL_SUBJECT="${BRANCH_NM} ${TARG_NM} build complete."
            MAILLIST=${SUCC_MAILLIST}
        else
            MAIL_SUBJECT="${BRANCH_NM} ${TARG_NM} build has test failures!"
            BUILD_FAILED="true"
        fi
       
    else
        MAIL_SUBJECT="${BRANCH_NM} ${TARG_NM} build failed!"
        BUILD_FAILED="true"
    fi
    
    if [ "${BUILD_FAILED}" = "true" ]
    then
        cp ${DATED_LOG} ${FailedNFSDir}/${LOGFILE_NAME}
        cleanFailuresDir
        .${BRANCH_PATH}/buildsystem/buildFailuresList.sh
        MAILLIST=${FAIL_MAILLIST}
    fi
    
    ## Build Body text of email
    ##
    rm ${MAILBODY}
    if [ \( -f ${TESTDATA_FILE} \) -a \( -n ${TESTDATA_FILE} \) ]
    then
        cp ${TESTDATA_FILE} ${MAILBODY}
        echo "-----------------------------------" >> ${MAILBODY}
        echo "" >> ${MAILBODY}
        rm ${TESTDATA_FILE}
    else
        touch ${MAILBODY}
    fi
    echo "Full Build log can be found on the build machine at:" >> ${MAILBODY}
    echo "    ${DATED_LOG}" >> ${MAILBODY}
    if [ "${BUILD_FAILED}" = "true" ]
    then
        echo "or on the download server at:" >> ${MAILBODY}
        echo "    http://www.eclipse.org/eclipselink/downloads/build-failures.php" >> ${MAILBODY}
	fi
    echo "-----------------------------------" >> ${MAILBODY}
    echo "" >> ${MAILBODY}
	echo "SVN Changes since Last Build:" >> ${MAILBODY}
    cat ${SVN_LOG_FILE} >> ${MAILBODY}
    
    ## Send result email
    ##
    cat ${MAILBODY} | ${MAIL_EXEC} -s "${MAIL_SUBJECT}" ${MAILLIST}  
fi

## Remove tmp directory
##
rm -rf $tmp

CLASSPATH=${OLD_CLASSPATH}
