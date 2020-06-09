#!/bin/sh
. `dirname $0`/setenv.sh 

# User may increase Java memory setting(s) if desired:
JVM_ARGS="-Xmx256m"

# If going through a proxy, set the proxy host and proxy port below, then uncomment the line
# JVM_ARGS="${JVM_ARGS} -DproxySet=true -Dhttp.proxyHost= -Dhttp.proxyPort="

# Please do not change any of the following lines:
CLASSPATH=`dirname $0`/../jlib/moxy/jaxb-core_2.2.11.v201407311112.jar:\
`dirname $0`/../jlib/moxy/jaxb-xjc_2.2.11.v201407311112.jar:\
`dirname $0`/../jlib/moxy/org.glassfish.javax.json_1.0.4.v201311181159.jar:\
`dirname $0`/../jlib/moxy/javax.validation_1.1.0.v201304101302.jar:\
`dirname $0`/../jlib/eclipselink.jar
JAVA_ARGS="$@"

${JAVA_HOME}/bin/java ${JVM_ARGS} -cp ${CLASSPATH} -Djava.endorsed.dirs=../jlib/moxy/api org.eclipse.persistence.jaxb.xjc.MOXyXJC ${JAVA_ARGS}
