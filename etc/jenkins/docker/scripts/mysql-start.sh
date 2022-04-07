#!/bin/bash
#
# Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Distribution License v. 1.0, which is available at
# http://www.eclipse.org/org/documents/edl-v10.php.
#
# SPDX-License-Identifier: BSD-3-Clause

# Start MySQL database

# Print timestamp and message to sdtout
# Arguments:
#   $1 First message to be printed
#   $2 Second message to be printed
#   ...
print() {
  echo '['`date '+%d.%m.%Y %H:%M:%S'`'] '$@
}

print '-[ Starting MySQL Database ]------------------------------'
if [ `id -u` = '0' ]; then
  sudo -u mysql mysqld &
else
  mysqld &
fi

print '--[ Waiting for MySQL Database to come up ]---------------'
while ! mysqladmin --protocol=socket --user=root ping > /dev/null 2>&1; do
  sleep 1
  echo -n '.'
done
echo ' done'
