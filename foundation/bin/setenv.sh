#!/bin/sh

# User MUST set DRIVER_CLASSPATH to point to their desired driver jar(s), such as JDBC and J2C.  For example:
# DRIVER_CLASSPATH=/some_dir/jdbc/ojdbc14.jar:/some_other_dir/j2c/aqapi.jar; export DRIVER_CLASSPATH
#
# Note: DRIVER_CLASSPATH should NOT contain any classes for your persistent business objects - these are 
# configured in the Mapping Workbench project.

# User MUST set INSTALL_JAVA_HOME to point a supported JRE. If none
# is provided for INSTALL_JAVA_HOME then the system JAVA_HOME
# value will be used
INSTALL_JAVA_HOME=%s_jreDirectory%;
if  [ "$JAVA_HOME" == "" ]; then
    JAVA_HOME=$INSTALL_JAVA_HOME; export JAVA_HOME
fi
