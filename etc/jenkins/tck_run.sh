#!/bin/bash -e
#
# Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Distribution License v. 1.0, which is available at
# http://www.eclipse.org/org/documents/edl-v10.php.
#
# SPDX-License-Identifier: BSD-3-Clause

echo '-[ Shell Environment ]-----------------------------------------------------------'
. /etc/profile
TS_HOME="${WORKSPACE}/persistence-tck"

# Run TCK test
echo '-[ Running TCK test ]------------------------------------------------------------'
cd ${TS_HOME}/bin
ant run.all | tee jpatck.log
/opt/bin/mysql-stop.sh
