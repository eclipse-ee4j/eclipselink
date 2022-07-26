#!/bin/bash
#
# Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.

# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License v. 2.0 which is available at
# http://www.eclipse.org/legal/epl-2.0,
# or the Eclipse Distribution License v. 1.0 which is available at
# http://www.eclipse.org/org/documents/edl-v10.php.

# SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause

# Stop MySQL database

# Print timestamp and message to sdtout
# Arguments:
#   $1 First message to be printed
#   $2 Second message to be printed
#   ...
print() {
  echo '['`date '+%d.%m.%Y %H:%M:%S'`'] '$@
}

print '-[ Stopping MySQL Database ]------------------------------'
if [ -f /var/run/mysqld/mysqld.pid ]; then
  MYSQL_PID=`cat /var/run/mysqld/mysqld.pid`
  if ! kill -s TERM "${MYSQL_PID}" ; then
    print '---[ MySQL could not be stopped! ]------------------------'
    exit 1
  fi
  print '--[ Waiting for MySQL Database to stop ]------------------'
  while mysqladmin --protocol=socket --user=root ping > /dev/null 2>&1; do
    sleep 1
    echo -n '.'
  done
  echo ' done'
else
  print '--[ MySQL PID file was not found! ]-----------------------'
  exit 1
fi
