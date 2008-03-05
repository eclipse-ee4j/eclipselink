#!/bin/sh
. `dirname $0`/setenv.sh

# User may increase Java memory setting(s) if desired:
JVM_ARGS=-Xmx256m
JVM_ARGS="${JVM_ARGS} -Dice.pilots.html4.ignoreNonGenericFonts=true"

# Please do not change any of the following lines:
CLASSPATH=`dirname $0`/../jlib/xercesImpl.jar:\
`dirname $0`/../jlib/connector.jar:\
`dirname $0`/../../jlib/eclipselink.jar:\
`dirname $0`/../jlib/elmwcore.jar:\
`dirname $0`/../jlib/eclipselinkmw.jar:\
`dirname $0`/../config:\
`dirname $0`/../lib/java/internal:\
${DRIVER_CLASSPATH}

WORKBENCH_ARGS=-open "$@"

${JAVA_HOME}/bin/java ${JVM_ARGS} -cp ${CLASSPATH} \
    org.eclipse.persistence.workbench.Main ${WORKBENCH_ARGS}
