#!/bin/sh

# User MUST set DRIVER_CLASSPATH to point to their desired driver jar(s), such as JDBC and J2C.  For example:
# DRIVER_CLASSPATH=/MySQL/MySQL4Driver.jar
#
# Note: DRIVER_CLASSPATH should NOT contain any classes for your persistent business objects - these are 
# configured in the Mapping Workbench project.
DRIVER_CLASSPATH=driver_directory; export DRIVER_CLASSPATH

# User MUST set JAVA_HOME to point a supported JRE
JAVA_HOME=%s_jreDirectory%; export JAVA_HOME
