#!/bin/sh
. `dirname $0`/setenv.sh 

# User may increase Java memory setting(s) if desired:
JVM_ARGS="-Xmx256m"

# If going through a proxy, set the proxy host and proxy port below, then uncomment the line
# JVM_ARGS="${JVM_ARGS} -DproxySet=true -Dhttp.proxyHost= -Dhttp.proxyPort="

# Please do not change any of the following lines:
CLASSPATH=`dirname $0`/../jlib/moxy/javax.xml.stream_1.0.1.v201004272200.jar:\
`dirname $0`/../jlib/moxy/javax.xml.bind_2.2.0.v201105210648.jar:\
`dirname $0`/../jlib/moxy/javax.activation_1.1.0.v201108011116.jar:\
`dirname $0`/../jlib/moxy/com.sun.xml.bind_2.2.0.v201004141950.jar:\
`dirname $0`/../jlib/moxy/com.sun.tools.xjc_2.2.0.jar:\
`dirname $0`/../jlib/eclipselink.jar
JAVA_ARGS="$@"

${JAVA_HOME}/bin/java ${JVM_ARGS} -cp ${CLASSPATH} -Djava.endorsed.dirs=../jlib/moxy org.eclipse.persistence.jaxb.xjc.MOXyXJC ${JAVA_ARGS}
