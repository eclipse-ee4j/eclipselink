#!/bin/bash -e
#
# Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Distribution License v. 1.0, which is available at
# http://www.eclipse.org/org/documents/edl-v10.php.
#
# SPDX-License-Identifier: BSD-3-Clause

# Script Parameters:
#  - ECLIPSELINK_VERSION
#  - JPA_TCK_URL
#  - JDBC_BUNDLE_URL

echo '-[ Parameters ]------------------------------------------------------------------'
echo "JPA_TCK_URL:            ${JPA_TCK_URL}"
echo "ECLIPSELINK_BUNDLE_URL: ${ECLIPSELINK_BUNDLE_URL}"
echo "JDBC_BUNDLE_URL:        ${JDBC_BUNDLE_URL}"

echo '-[ Shell Environment ]-----------------------------------------------------------'
. /etc/profile

# Download and extract TCK tests
echo '-[ Downloading and extracting TCK test ]-----------------------------------------'
wget -q ${JPA_TCK_URL} -O persistence-tck.zip && unzip -q persistence-tck.zip
mkdir persistence-tck/JTwork
mkdir persistence-tck/JTreport

# Download and extract EclipseLink bundle
echo '-[ Downloading EclipseLink ]-----------------------------------------------------'
mkdir download
echo '<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <parent>
    <groupId>org.eclipse.ee4j</groupId>
    <artifactId>project</artifactId>
    <version>1.0.6</version>
  </parent>
  <groupId>download</groupId>
  <artifactId>ri</artifactId>
  <version>1.0.0</version>
</project>
' > 'download/pom.xml'

mvn -U -C -B -f download/pom.xml \
    -Pstaging -Psnapshots \
    -Dartifact=org.eclipse.persistence:org.eclipse.persistence.distribution:${ECLIPSELINK_VERSION}:zip \
    -DoutputDirectory="${WORKSPACE}/download" \
    org.apache.maven.plugins:maven-dependency-plugin:3.1.2:copy

(cd "${WORKSPACE}/download" && unzip ${WORKSPACE}/download/org.eclipse.persistence.distribution*.zip)

# Download and extract MySQL JDBC driver
echo '-[ Downloading and extracting MySQL JDBC driver ]--------------------------------'
wget -q ${JDBC_BUNDLE_URL} -O - | tar xfz -
JDBC_JAR="${WORKSPACE}/`ls mysql-connector-*/mysql-connector*[0-9].jar`"

ls -la

TS_HOME="${WORKSPACE}/persistence-tck"
TS_JTE="${TS_HOME}/bin/ts.jte"

EL_CLASS=`find ${WORKSPACE}/download/eclipselink -name '*.jar'`
EL_CLASSPATH=''
for cp in ${EL_CLASS}; do
  if [ ! -z "${EL_CLASSPATH}" ]; then
    EL_CLASSPATH="${EL_CLASSPATH}:"
  fi
  EL_CLASSPATH="${EL_CLASSPATH}${cp}"
done

#Configure TCK test
echo '-[ Configuribg TCK test ]--------------------------------------------------------'
mv ${TS_JTE} ${TS_JTE}.orig
sed \
    -e '/^jakarta.persistence.provider=.*$/ {
i\
mysql.server=localhost
i\
mysql.port=3306
i\
mysql.dbName=ecltests
    }' \
    -e "s/^jpa.classes=.*/jpa.classes=${EL_CLASSPATH//\//\\\/}/" \
    -e "s/^jdbc.driver.classes=.*/jdbc.driver.classes=${JDBC_JAR//\//\\\/}/" \
    -e 's/^jdbc.db=.*/jdbc.db=mysql/' \
    -e 's/^jakarta.persistence.provider=.*/jakarta.persistence.provider=org.eclipse.persistence.jpa.PersistenceProvider/' \
    -e 's/^jakarta.persistence.jdbc.driver=.*/jakarta.persistence.jdbc.driver=com.mysql.jdbc.Driver/' \
    -e 's/^jakarta.persistence.jdbc.url=.*/jakarta.persistence.jdbc.url=jdbc:mysql:\/\/\${mysql.server}:\${mysql.port}\/\${mysql.dbName}\?useSSL=false/' \
    -e 's/^jakarta.persistence.jdbc.user=.*/jakarta.persistence.jdbc.user=root/' \
    -e 's/^jakarta.persistence.jdbc.password=.*/jakarta.persistence.jdbc.password=root/' \
    -e 's/^db.supports.sequence=.*/db.supports.sequence=false/' \
    ${TS_JTE}.orig > ${TS_JTE}
    
echo '-[ ts.jte ]----------------------------------------------------------------------'
cat ${TS_JTE}

echo '-[ Starting MySQL database ]-----------------------------------------------------'
# Start MySQL database
/opt/bin/mysql-start.sh

echo "SET GLOBAL sql_mode='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION,ANSI_QUOTES';
SELECT @@GLOBAL.sql_mode;
" | mysql -u root --password=root

# Initialize TCK tests
echo '-[ Initializing TCK test ]-------------------------------------------------------'
if [ -f ${TS_HOME}/bin/testsuite.jtd ]; then
  mv -v  ${TS_HOME}/bin/testsuite.jtd  ${TS_HOME}/bin/testsuite.jtd.orig
fi

cd ${TS_HOME}/bin
ant -f initdb.xml
