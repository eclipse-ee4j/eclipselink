#!/bin/sh

umask 0002
TARGET=$1
BRANCH=$2
if [ "$TARGET" = "" ]
then 
    echo "Target=${TARGET}"
fi
if [ ! "$BRANCH" = "" ]
then 
    BRANCH=branch/${BRANCH}
    echo "Branch=${BRANCH}"
fi

BOOTSTRAP_BLDFILE=bootstrap.xml
SVN_EXEC=`which svn`
if [ $? -ne 0 ]
then 
    echo "Error finding svn install!"
    exit
fi

#Define common variables and assumed dependencies
#JAVA_HOME=/shared/common/ibm-java2-ppc-50
#ANT_HOME=/shared/common/apache-ant-1.6.5
JAVA_HOME=/shared/common/jdk1.6.0_05
ANT_HOME=/shared/common/apache-ant-1.7.0
HOME_DIR=/shared/technology/eclipselink
LOG_DIR=${HOME_DIR}/log
JUNIT_HOME=${HOME_DIR}/org.junit_3.8.2.v200706111738
MAVENANT_DIR=${HOME_DIR}/staging
BRANCH_DIR=${HOME_DIR}/${BRANCH}trunk

JDBC_LOGIN_INFO_FILE=${HOME_DIR}/db.dat
DATED_LOG=${LOG_DIR}/bsb_`date '+%y%m%d-%H%M'`.log

PATH=${JAVA_HOME}/bin:${ANT_HOME}/bin:/usr/bin:/usr/local/bin:${PATH}
CLASSPATH=${CLASSPATH}:${JUNIT_HOME}/junit.jar:${ANT_HOME}/lib/ant-junit.jar:${MAILLIB_DIR}/mail.jar:${MAILLIB_DIR}/activation.jar:${MAVENANT_DIR}/maven-ant-tasks-2.0.8.jar

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

#GetBuildFromSVN () {
#    whereami=`pwd`
#    cd $HOME_DIR
#    svn co -q ${BUILD_URL}
#    cd $whereami    
#}
#--------- MAIN --------#

#Test for existence of dependencies, send email and fail if any of the above not found.
if [ ! -d ${JAVA_HOME} ] 
then
    echo "Java not found!"
    echo "Expecting Java at: $JAVA_HOME"
    exit
fi
if [ ! -d ${ANT_HOME} ] 
then 
    echo "Java not found!"
    echo "Expecting Java at: $ANT_HOME"
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
if [ ! -f ./${BOOTSTRAP_BLDFILE} ]
then
    echo "Need bootstrap buildfile (${BOOTSTRAP_BLDFILE}) in the current directory to proceed."
    exit 1
else
    cp ./${BOOTSTRAP_BLDFILE} ${HOME_DIR}/${BOOTSTRAP_BLDFILE}
fi
if [ ! -f $JDBC_LOGIN_INFO_FILE ]
then
    echo "No db Login info available!"
    exit
else 
    DB_USER=`cat $JDBC_LOGIN_INFO_FILE | cut -d: -f1`
    DB_PWD=`cat $JDBC_LOGIN_INFO_FILE | cut -d: -f2`
fi

#Set appropriate max Heap for VM and let Ant inherit JavaVM (OS's) proxy settings
ANT_OPTS="-Xmx128m"
ANT_ARGS="-autoproxy"
ANT_BASEARG="-f \"${BOOTSTRAP_BLDFILE}\" -l \"$DATED_LOG\" -Dbranch.name=\"${BRANCH}\""

if [ "$TARGET" = "test" ]
then
    #Only needed for dev behind firewall
    ANT_OPTS="-Dhttp.proxyHost=www-proxy.us.oracle.com $ANT_OPTS"
    TARGET=build
    ANT_BASEARG="${ANT_BASEARG} -D_Test=1"
else
fi    

export JAVA_HOME ANT_HOME HOME_DIR JUNIT_HOME MAVENANT_DIR BRANCH_DIR PATH CLASSPATH SVN_EXEC ANT_ARGS ANT_OPTS

cd ${HOME_DIR} 
touch $DATED_LOG

echo "ant ${ANT_BASEARG} $TARGET" >> $DATED_LOG
ant ${ANT_BASEARG} -Ddb.user="$DB_USER" -Ddb.pwd="$DB_PWD" $TARGET &
tail -f $DATED_LOG
