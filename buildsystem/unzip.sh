# !/bin/sh

umask 0002

export JAVA_HOME=/shared/common/ibm-java2-ppc-50
export PATH=${JAVA_HOME}/bin:/shared/common/apache-ant-1.6.5/bin:/usr/bin:/usr/local/bin:${PATH}
export CLASSPATH=${CLASSPATH}:/shared/rt/eclipselink/org.junit_3.8.2.v200706111738/junit.jar:/shared/common/apache-ant-1.6.5/lib/ant-junit.jar:/shared/rt/eclipselink/trunk/foundation/eclipselink.core.lib/mail.jar:/shared/rt/eclipselink/trunk/foundation/eclipselink.core.lib/activation.jar:/shared/rt/eclipselink/staging/maven-ant-tasks-2.0.8.jar
HOME_DIR=/shared/rt/eclipselink
DATED_NB_LOG=$HOME_DIR/logs/nb/nb_`date '+%y%m%d'`.log

cd $HOME_DIR 

ant get.dependencies



