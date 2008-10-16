# !/bin/sh

THIS=$0
PROGNAME=`basename ${THIS}`
CUR_DIR=`dirname ${THIS}`
umask 0002
LOCAL_REPOS=false
MILESTONE=false
TEST=false
RHB=false
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
        TARGET=${BRANCH}
        BRANCH=$3
        TARG_NM=${TARGET}
    else
        echo "ERROR: Name of milestone must be specified in ARG2!"
        echo "USAGE: ./boostrap.sh milestone M9 [branch]"
        exit
    fi
fi

echo "Target=${TARGET}"

if [ ! "${BRANCH}" = "" ]
then
    BRANCH_NM=${BRANCH}
    BRANCH=branches/${BRANCH}/
    echo "Branch=${BRANCH}"
else
    BRANCH_NM="trunk"
fi

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

#Define common variables
BOOTSTRAP_BLDFILE=bootstrap.xml
JAVA_HOME=/shared/common/ibm-java-jdk-ppc-60
ANT_HOME=/shared/common/apache-ant-1.7.0
HOME_DIR=/shared/rt/eclipselink
LOG_DIR=${HOME_DIR}/logs
BRANCH_PATH=${HOME_DIR}/${BRANCH}trunk
BLD_DEPS_DIR=${HOME_DIR}/bld_deps/${BRANCH_NM}
START_DATE=`date '+%y%m%d-%H%M'`
DATED_LOG=${LOG_DIR}/bsb-${BRANCH_NM}_${TARG_NM}_${START_DATE}.log
JDBC_LOGIN_INFO_FILE=${HOME_DIR}/db.dat

#Define build dependency dirs (build needs em, NOT compile dependencies)
JUNIT_HOME=${BLD_DEPS_DIR}/junit
MAVENANT_DIR=${BLD_DEPS_DIR}/mavenant
MAILLIB_DIR=${BRANCH_PATH}/foundation/eclipselink.core.lib

PATH=${JAVA_HOME}/bin:${ANT_HOME}/bin:/usr/bin:/usr/local/bin:${PATH}
OLD_CLASSPATH=${CLASSPATH}
CLASSPATH=${JUNIT_HOME}/junit.jar:${ANT_HOME}/lib/ant-junit.jar:${MAILLIB_DIR}/mail.jar:${MAILLIB_DIR}/activation.jar:${MAVENANT_DIR}/maven-ant-tasks-2.0.8.jar

#------- Subroutines -------#
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
fi

#Set appropriate max Heap for VM and let Ant inherit JavaVM (OS's) proxy settings
ANT_ARGS=" "
ANT_OPTS="-Xmx128m"
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
    ANT_BASEARG="${ANT_BASEARG} -Dbuild.type=RELEASE"
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
else
    ANT_BASEARG="${ANT_BASEARG} -DMailLogger.properties.file=${BRANCH_PATH}/buildsystem/maillogger.properties -logger org.apache.tools.ant.listener.MailLogger"
fi

export ANT_ARGS ANT_OPTS ANT_HOME BRANCH_PATH HOME_DIR LOG_DIR JAVA_HOME JUNIT_HOME MAVENANT_DIR PATH CLASSPATH
export SVN_EXEC BLD_DEPS_DIR JUNIT_HOME TARGET

cd ${HOME_DIR}
echo "Results logged to: ${DATED_LOG}"

touch ${DATED_LOG}
echo "Build started at: `date`" >> ${DATED_LOG}
source ~/.ssh-agent >> ${DATED_LOG} 2>&1
echo "ant ${ANT_BASEARG} ${TARGET}" >> ${DATED_LOG}
ant ${ANT_BASEARG} -Ddb.user="${DB_USER}" -Ddb.pwd="${DB_PWD}" -Ddb.url="${DB_URL}" ${TARGET} >> ${DATED_LOG} 2>&1
echo "Build completed at: `date`" >> ${DATED_LOG}

CLASSPATH=${OLD_CLASSPATH}
