#!/bin/bash -e
#
# Copyright (c) 2019, 2020 Oracle and/or its affiliates. All rights reserved.
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Distribution License v. 1.0, which is available at
# http://www.eclipse.org/org/documents/edl-v10.php.
#
# SPDX-License-Identifier: BSD-3-Clause

#
# Arguments:
#  N/A

echo '-[ EclipseLink Tests LRG ]-----------------------------------------------------------'
. /etc/profile

if [ ${CONTINUOUS_BUILD} = "true" ]; then
    echo '-[ EclipseLink SRG Tests ]-----------------------------------------------------------'
    mvn verify
else
    echo '-[ EclipseLink LRG Tests ]-----------------------------------------------------------'
    /opt/bin/mysql-start.sh
    mvn verify -Pmysql,test-lrg
    /opt/bin/mysql-stop.sh
fi



