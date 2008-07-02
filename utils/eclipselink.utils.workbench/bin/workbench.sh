#!/bin/sh
. `dirname $0`../../../bin/setenv.sh

# User may increase Java memory setting(s) if desired:
JVM_ARGS=-Xmx256m

# Please do not change any of the following lines:
CLASSPATH=`dirname $0`/../jlib/xercesImpl.jar:\
`dirname $0`/../jlib/connector.jar:\
`dirname $0`/../../../jlib/eclipselink.jar:\
`dirname $0`/../jlib/elmwcore.jar:\
`dirname $0`/../jlib/eclipselinkmw.jar:\
`dirname $0`/../../../jlib/jpa/javax.persistence_1.0.0.jar:\
`dirname $0`/../config:\
${DRIVER_CLASSPATH}

WORKBENCH_ARGS=-open "$@"

${JAVA_HOME}/bin/java ${JVM_ARGS} -cp ${CLASSPATH} \
    org.eclipse.persistence.tools.workbench.Main ${WORKBENCH_ARGS}
