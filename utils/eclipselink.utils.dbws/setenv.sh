#!/bin/sh

# User MUST set DRIVER_CLASSPATH to point to their desired JDBC driver jar(s). For example:
# DRIVER_CLASSPATH=/some/directory/mysql-connector-java-5.1.6-bin.jar
DRIVER_CLASSPATH=/some/directory/driver.jar; export DRIVER_CLASSPATH

# User MUST set JAVA_HOME to point a JavaSE 6 JDK installation
JAVA_HOME=/some/directory; export JAVA_HOME