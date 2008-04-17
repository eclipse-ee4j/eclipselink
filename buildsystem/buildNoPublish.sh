# !/bin/sh

umask 0002

export JAVA_HOME=/shared/common/ibm-java2-ppc-50
export PATH=${JAVA_HOME}/bin:/shared/common/apache-ant-1.6.5/bin:/usr/bin:/usr/local/bin:${PATH}
export CLASSPATH=${CLASSPATH}:/shared/technology/eclipselink/trunk/foundation/eclipselink.core.lib/mail.jar:/shared/technology/eclipselink/trunk/foundation/eclipselink.core.lib/activation.jar:/shared/technology/eclipselink/org.junit_3.8.2.v200706111738/junit.jar:/shared/common/apache-ant-1.6.5/lib/ant-junit.jar

cd /shared/technology/eclipselink


echo "begin build"
ant -verbose clean build.no.javadoc build.test test 
#ant -verbose build.test test
echo "end build"




