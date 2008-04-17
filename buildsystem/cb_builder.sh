# !/bin/sh

umask 0002

export JAVA_HOME=/shared/common/ibm-java2-ppc-50
export PATH=${JAVA_HOME}/bin:/usr/bin:/usr/local/bin:${PATH}
export CLASSPATH=${CLASSPATH}:/shared/technology/eclipselink/trunk/foundation/eclipselink.core.lib/mail.jar:/shared/technology/eclipselink/trunk/foundation/eclipselink.core.lib/activation.jar:/shared/technology/eclipselink/org.junit_3.8.2.v200706111738/junit.jar:/shared/common/apache-ant-1.6.5/lib/ant-junit.jar
HOME_DIR=/shared/technology/eclipselink
LATEST_LOG=$HOME_DIR/.latest_svn_log
CURRENT_LOG=$HOME_DIR/.current_svn_log
TEMP_FILE=$HOME_DIR/.temp_log
cd $HOME_DIR

source ~/.ssh-agent

echo "------------------------"
echo "continuous Build Started at: "
date
# check for latest log

if [ ! -f $LATEST_LOG ] ; then
	touch $LATEST_LOG 
fi

svn log svn://dev.eclipse.org/svnroot/technology/org.eclipse.persistence/trunk > $TEMP_FILE
head -n3 $TEMP_FILE > $CURRENT_LOG
#only build if needed
diff $LATEST_LOG $CURRENT_LOG
if [ ! $? -eq 0 ] ; then
	echo "REVISION CHANGE -- BUILDING"
  	ant  -Declipselink.logging.level=OFF -DMailLogger.properties.file=/shared/technology/eclipselink/cb_maillogger.properties -logger org.apache.tools.ant.listener.MailLogger clean build.continuous
else
        echo "NOTHING CHANGED- No build Needed"
fi

mv $CURRENT_LOG $LATEST_LOG
rm $TEMP_FILE
echo "Continuous Build ended at: "
date
echo "------------------------"
