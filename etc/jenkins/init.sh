#!/bin/bash -e
#
# Copyright (c) 2019, 2020 Oracle and/or its affiliates. All rights reserved.
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Distribution License v. 1.0, which is available at
# http://www.eclipse.org/org/documents/edl-v10.php.
#
# SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause

#
# Arguments:
#  N/A

echo '-[ EclipseLink Init ]-----------------------------------------------------------'

mkdir -p $HOME/extension.lib.external/mavenant

#DOWNLOAD SOME DEPENDENCIES
wget -nc https://repo1.maven.org/maven2/junit/junit/4.12/junit-4.12.jar -O $HOME/extension.lib.external/junit-4.12.jar
wget -nc https://repo1.maven.org/maven2/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar -O $HOME/extension.lib.external/hamcrest-core-1.3.jar
wget -nc https://repo1.maven.org/maven2/org/jmockit/jmockit/1.35/jmockit-1.35.jar -O $HOME/extension.lib.external/jmockit-1.35.jar
wget -nc https://repo1.maven.org/maven2/org/jboss/logging/jboss-logging/3.3.0.Final/jboss-logging-3.3.0.Final.jar -O $HOME/extension.lib.external/jboss-logging-3.3.0.Final.jar
wget -nc https://repo1.maven.org/maven2/org/glassfish/javax.el/3.0.1-b08/javax.el-3.0.1-b08.jar -O $HOME/extension.lib.external/javax.el-3.0.1-b08.jar
wget -nc https://repo1.maven.org/maven2/com/fasterxml/classmate/1.3.1/classmate-1.3.1.jar -O $HOME/extension.lib.external/classmate-1.3.1.jar
wget -nc https://repo1.maven.org/maven2/mysql/mysql-connector-java/5.1.48/mysql-connector-java-5.1.48.jar -O $HOME/extension.lib.external/mysql-connector-java-5.1.48.jar
wget -nc https://archive.apache.org/dist/ant/binaries/apache-ant-1.10.7-bin.tar.gz -O $HOME/extension.lib.external/apache-ant-1.10.7-bin.tar.gz
wget -nc https://archive.apache.org/dist/maven/ant-tasks/2.1.3/binaries/maven-ant-tasks-2.1.3.jar -O $HOME/extension.lib.external/mavenant/maven-ant-tasks-2.1.3.jar
wget -nc https://download.jboss.org/wildfly/15.0.1.Final/wildfly-15.0.1.Final.tar.gz -O $HOME/extension.lib.external/wildfly-15.0.1.Final.tar.gz
wget -nc https://download.eclipse.org/eclipse/downloads/drops4/R-4.10-201812060815/eclipse-SDK-4.10-linux-gtk-x86_64.tar.gz -O $HOME/extension.lib.external/eclipse-SDK-4.10-linux-gtk-x86_64.tar.gz
wget -nc https://archive.apache.org/dist/maven/maven-3/3.6.0/binaries/apache-maven-3.6.0-bin.tar.gz -O $HOME/extension.lib.external/apache-maven-3.6.0-bin.tar.gz

#UNPACK SOME  DEPENDENCIES
tar -x -z -C $HOME/extension.lib.external -f $HOME/extension.lib.external/wildfly-15.0.1.Final.tar.gz
tar -x -z -C $HOME/extension.lib.external -f $HOME/extension.lib.external/eclipse-SDK-4.10-linux-gtk-x86_64.tar.gz
tar -x -z -C $HOME/extension.lib.external -f $HOME/extension.lib.external/apache-maven-3.6.0-bin.tar.gz

#PREPARE build.properties FILE
echo "extensions.depend.dir=$HOME/extension.lib.external" >> $HOME/build.properties
echo "junit.lib=$HOME/extension.lib.external/junit-4.12.jar:$HOME/extension.lib.external/hamcrest-core-1.3.jar" >> $HOME/build.properties
echo 'jdbc.driver.jar=$HOME/extension.lib.external/mysql-connector-java-5.1.48.jar' >> $HOME/build.properties
echo 'db.driver=com.mysql.jdbc.Driver' >> $HOME/build.properties
echo "db.url=$TEST_DB_URL" >> $HOME/build.properties
echo "db.user=$TEST_DB_USERNAME" >> $HOME/build.properties
echo "db.pwd=$TEST_DB_PASSWORD" >> $HOME/build.properties
echo 'db.platform=org.eclipse.persistence.platform.database.MySQLPlatform' >> $HOME/build.properties
echo 'eclipse.install.dir=$HOME/extension.lib.external/eclipse' >> $HOME/build.properties
echo 'server.name=wildfly' >> $HOME/build.properties
echo "wildfly.home=$HOME/extension.lib.external/wildfly-15.0.1.Final" >> $HOME/build.properties
echo 'wildfly.config=standalone-full.xml' >> $HOME/build.properties
echo hudson.workspace=$WORKSPACE
