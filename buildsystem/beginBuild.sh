# !/bin/sh

umask 0002

export JAVA_HOME=/shared/common/ibm-java2-ppc-50
export PATH=${JAVA_HOME}/bin:/shared/common/apache-ant-1.6.5/bin:/usr/bin:/usr/local/bin:${PATH}
export CLASSPATH=${CLASSPATH}:/shared/technology/eclipselink/org.junit_3.8.2.v200706111738/junit.jar:/shared/common/apache-ant-1.6.5/lib/ant-junit.jar:/shared/technology/eclipselink/trunk/foundation/eclipselink.core.lib/mail.jar:/shared/technology/eclipselink/trunk/foundation/eclipselink.core.lib/activation.jar:/shared/technology/eclipselink/staging/maven-ant-tasks-2.0.8.jar
HOME_DIR=/shared/technology/eclipselink
DATED_NB_LOG=$HOME_DIR/logs/nb/nb_`date '+%y%m%d'`.log

cd $HOME_DIR 

echo build started at: `date` >> $DATED_NB_LOG
source ~/.ssh-agent >> $DATED_NB_LOG
ant -Declipselink.logging.level=OFF -DMailLogger.properties.file=/shared/technology/eclipselink/nb_maillogger.properties -logger org.apache.tools.ant.listener.MailLogger build.nightly >> $DATED_NB_LOG
# creating webpage for nighly builds
$HOME_DIR/cleanNightly.sh >> $DATED_NB_LOG
$HOME_DIR/buildNightlyList.sh >> $DATED_NB_LOG
echo build ended at: `date` >> $DATED_NB_LOG



