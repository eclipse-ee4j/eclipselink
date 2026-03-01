#!/bin/bash -e
#
# Copyright (c) 2019, 2023 Oracle and/or its affiliates. All rights reserved.
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

if [ ${CONTINUOUS_BUILD} = "true" ]; then
    echo '-[ EclipseLink SRG Tests ]-----------------------------------------------------------'
    mvn -B -V verify -Pstaging
else
    echo '-[ EclipseLink LRG Tests ]-----------------------------------------------------------'
    env
    /opt/bin/mysql-start.sh
    mysql -e 'status;' -uroot -proot
    mvn -B -V verify -Pstaging,mysql,test-lrg
    /opt/bin/mysql-stop.sh
fi



