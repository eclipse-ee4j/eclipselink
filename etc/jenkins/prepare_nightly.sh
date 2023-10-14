#!/bin/bash -e
#
# Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Distribution License v. 1.0, which is available at
# http://www.eclipse.org/org/documents/edl-v10.php.
#
# SPDX-License-Identifier: BSD-3-Clause

#
# Arguments:
#  N/A

echo '-[ EclipseLink Nightly Build Preparation ]-----------------------------------------------------------'

if [ ${CONTINUOUS_BUILD} = "true" ]; then
    echo '-[ EclipseLink Continuous Build -> No preparing any artifacts]-------------------------------'
else
    echo '-[ EclipseLink Prepare Artifacts to Nightly ]-----------------------------------------------------------'
    mvn -V -B clean package -f bundles/nightly/pom.xml
fi



