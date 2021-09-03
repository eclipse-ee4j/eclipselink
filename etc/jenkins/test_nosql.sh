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

echo '-[ EclipseLink Test NoSQL ]-----------------------------------------------------------'
/opt/bin/mongo-start.sh
mvn -B -V verify -pl :org.eclipse.persistence.nosql -P mongodb
/opt/bin/mongo-stop.sh
