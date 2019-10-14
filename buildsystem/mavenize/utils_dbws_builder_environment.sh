#!/usr/bin/env bash
#
# Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License v. 2.0 which is available at
# http://www.eclipse.org/legal/epl-2.0,
# or the Eclipse Distribution License v. 1.0 which is available at
# http://www.eclipse.org/org/documents/edl-v10.php.
#
# SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
#

. ./environment.sh

export DBWS_BUILDER_DIR="../../utils/org.eclipse.persistence.dbws.builder.maven"
export DBWS_BUILDER_ORIGIN_DIR="../../utils/org.eclipse.persistence.dbws.builder"
export DBWS_BUILDER_TEST_ORIGIN_DIR="../../utils/eclipselink.dbws.builder.test"

export DBWS_BUILDER_TEST_ORACLE="../../utils/eclipselink.dbws.builder.test.oracle.maven"
export DBWS_BUILDER_TEST_ORACLE_ORIGIN_DIR="../../utils/eclipselink.dbws.builder.test.oracle"

export DBWS_BUILDER_TEST_ORACLE_SERVER="../../utils/eclipselink.dbws.builder.test.oracle.server.maven"
export DBWS_BUILDER_TEST_ORACLE_SERVER_ORIGIN_DIR="../../utils/eclipselink.dbws.builder.test.oracle.server"
