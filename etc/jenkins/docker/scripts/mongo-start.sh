#!/bin/bash
#
# Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.

# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License v. 2.0 which is available at
# http://www.eclipse.org/legal/epl-2.0,
# or the Eclipse Distribution License v. 1.0 which is available at
# http://www.eclipse.org/org/documents/edl-v10.php.

# SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause

# Start MySQL database

DATA_DIR='/var/lib/mongo'
LOG_FILE='/var/log/mongodb/mongod.log'
PID_FILE='/var/run/mongodb/mongodb.pid'
PORT='27017'

# Print timestamp and message to sdtout
# Arguments:
#   $1 First message to be printed
#   $2 Second message to be printed
#   ...
print() {
  echo '['`date '+%d.%m.%Y %H:%M:%S'`'] '$@
}

print '-[ Starting Mongo Database ]------------------------------'
if [ `id -u` = '0' ]; then
  sudo -u mongod mongod --port ${PORT} --dbpath ${DATA_DIR} --logpath ${LOG_FILE} --pidfilepath ${PID_FILE} &
else
  mongod --port ${PORT} --dbpath ${DATA_DIR} --logpath ${LOG_FILE} --pidfilepath ${PID_FILE} &
fi

print '--[ Waiting for Mongo Database to come up ]---------------'
while ! /usr/bin/mongo --eval "db.version()" > /dev/null 2>&1; do
  sleep 1;
  echo -n '.'
done
echo ' done'
