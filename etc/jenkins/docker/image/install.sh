#!/bin/sh
#
# Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.

# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License v. 2.0 which is available at
# http://www.eclipse.org/legal/epl-2.0,
# or the Eclipse Distribution License v. 1.0 which is available at
# http://www.eclipse.org/org/documents/edl-v10.php.

# SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause

# This script file is processed with Maven resource filtering
# so variables expansion must be used with respect to defined
# maven POM properties.

# Docker image installation file
# Print timestamp and message to sdtout
# Arguments:
#   $1 First message to be printed
#   $2 Second message to be printed
#   ...
print() {
  echo '['`date '+%d.%m.%Y %H:%M:%S'`'] '$@
}

JENKINS_USER='${build.user.name}'
JENKINS_UID='${build.user.uid}'
JENKINS_GROUP='${build.group.name}'
JENKINS_GID='${build.group.gid}'

INSTALL_JAVA='${install.java}'

print "Adding Jenkins group ${JENKINS_GROUP}:${JENKINS_GID}"
groupadd -g "${JENKINS_GID}" "${JENKINS_GROUP}"

print "Adding Jenkins user ${JENKINS_USER}:${JENKINS_UID}"
useradd -u "${JENKINS_UID}"       -g "${JENKINS_GID}" \
        -s '${build.user.shell}' -c '${build.user.comment}' \
        -m "${JENKINS_USER}"

print "Updating the system and install additional utilities"
echo '--------------------------------------------------------------------------------'
yum -y update
yum -y install git tar wget zip unzip
echo '--------------------------------------------------------------------------------'

print "Configuring Java tools in ${INSTALL_JAVA}"

JDK_PATH=''
for i in `ls ${INSTALL_JAVA} | grep 'jdk-'`; do
  JDK_PATH="${JDK_PATH}:${INSTALL_JAVA}/${i}/bin"
done
print " - Open JDK ${JDK_PATH}"

ANT_PATH=''
for i in `ls ${INSTALL_JAVA} | grep '\-ant-'`; do
  ANT_PATH="${ANT_PATH}:${INSTALL_JAVA}/${i}/bin"
done
print " - Apache Ant ${ANT_PATH}"

MVN_PATH=''
for i in `ls ${INSTALL_JAVA} | grep '\-maven-'`; do
  MVN_PATH="${MVN_PATH}:${INSTALL_JAVA}/${i}/bin"
done
print " - Apache Maven ${MVN_PATH}"

if [ -n "${JDK_PATH}" ]; then
  TOOLS_PATH="${JDK_PATH}"
else
  TOOLS_PATH=''
fi
if [ -n "${ANT_PATH}" ]; then
  TOOLS_PATH="${TOOLS_PATH}${ANT_PATH}"
fi
if [ -n "${MVN_PATH}" ]; then
  TOOLS_PATH="${TOOLS_PATH}${MVN_PATH}"
fi

print " - Adding shell profile PATH ${TOOLS_PATH}"
echo '# Java tools path
PATH="${PATH}'"${TOOLS_PATH}"'"
' > /etc/profile.d/java_tools.sh

print "Fixing startup scripts permissions"
cd /opt/bin && chmod uog+x mongo-start.sh mongo-stop.sh mysql-start.sh mysql-stop.sh

print "Configuring MySQL 8 yum repository"
echo '--------------------------------------------------------------------------------'
cd /tmp && yum -y localinstall '${mysql.pkg}'
yum -y module disable mysql

echo '--------------------------------------------------------------------------------'
print "Installing MySQL 8"
echo '--------------------------------------------------------------------------------'
yum -y install sudo mysql-community-server
groupmod -o -g "${JENKINS_GID}" mysql
usermod -o -g "${JENKINS_GID}" -u "${JENKINS_UID}" mysql
chown -v mysql:mysql /var/log/mysqld.log
chown -v -R mysql:mysql /var/lib/mysql-files /var/lib/mysql-keyring /var/run/mysqld
mysqld --initialize --user=mysql
echo '--------------------------------------------------------------------------------'
cat /var/log/mysqld.log
echo '--------------------------------------------------------------------------------'

print "Setting up MySQL 8 for tests"
echo '--------------------------------------------------------------------------------'
#Addtional permanent settings in configuration file
#To disable check for some stored functions
echo 'log_bin_trust_function_creators = 1
' >> /etc/my.cnf
/opt/bin/mysql-start.sh
ROOT_PWD=`cat /var/log/mysqld.log | grep 'A temporary password is generated for root' | sed -e 's/^.*localhost: *//'`
echo "
  ALTER USER 'root'@'localhost' IDENTIFIED BY '${db.root.pwd}';
  CREATE DATABASE ${mysql.database};
  FLUSH PRIVILEGES;
" | mysql -v -u root --connect-expired-password --password="${ROOT_PWD}"
if [ '${db.user}' != 'root' ]; then
  echo "
    CREATE USER '${db.user}'@'localhost' IDENTIFIED BY '${db.pwd}';
    GRANT ALL PRIVILEGES ON ${mysql.database}.* TO '${db.user}'@'localhost';
    FLUSH PRIVILEGES;
  " | mysql -v -u root --password='${db.root.pwd}'
fi
/opt/bin/mysql-stop.sh
echo '--------------------------------------------------------------------------------'

print "Configuring Mongo DB 3 yum repository"
echo '--------------------------------------------------------------------------------'
echo '[Mongodb]
name=MongoDB Repository
baseurl=https://repo.mongodb.org/yum/redhat/8/mongodb-org/3.6/x86_64/
gpgcheck=0
enabled=1
gpgkey=https://www.mongodb.org/static/pgp/server-3.6.asc
' > /etc/yum.repos.d/mongodb.repo
chmod -v u=rw,og=r /etc/yum.repos.d/mongodb.repo
echo '--------------------------------------------------------------------------------'
print "Installing Mongo DB 3"
echo '--------------------------------------------------------------------------------'
yum install -y mongodb-org
groupmod -o -g "${JENKINS_GID}" mongod
usermod -o -g "${JENKINS_GID}" -u "${JENKINS_UID}" mongod
chown -v -R mongod:mongod /var/lib/mongo /var/log/mongodb /var/run/mongodb
echo '--------------------------------------------------------------------------------'
print "Post install cleanup"
echo '--------------------------------------------------------------------------------'
yum -y clean all
rm -vrf /var/cache/yum
