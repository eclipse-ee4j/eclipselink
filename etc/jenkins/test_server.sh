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

if [ ${CONTINUOUS_BUILD} = "true" ]; then
    echo '-[ EclipseLink Continuous Build -> No server tests]-------------------------------'
else
    echo '-[ EclipseLink Test Server ]-----------------------------------------------------------'
    . /etc/profile
    /opt/bin/mysql-start.sh
    mvn --batch-mode verify -pl :org.eclipse.persistence.jpa.test -P server-test-jpa-lrg1,mysql
    mvn --batch-mode verify -pl :org.eclipse.persistence.jpa.test -P server-test-jpa-lrg2,mysql
    /opt/bin/mysql-stop.sh
fi
