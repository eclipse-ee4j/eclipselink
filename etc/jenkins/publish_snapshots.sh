#!/bin/bash -e
#
# Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
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
    echo '-[ EclipseLink Publish to Jakarta Snapshots -> No publishing any artifacts]-----------------------------------------------------------'
else
    echo '-[ EclipseLink Publish to Jakarta Snapshots ]-----------------------------------------------------------'
    . /etc/profile
    mvn --no-transfer-progress -U -C -B -V \
      -Psnapshots -DskipTests \
      -Ddoclint=none -Ddeploy \
      deploy
fi
