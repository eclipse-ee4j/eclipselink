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

#Prepare/synchronize ANTLR module
#There are no any preparation steps required for ANTLR

#Prepare/synchronize ASM module
./asm_prepare.sh

#Prepare/synchronize JPQL module (Hermes parser)
./jpql_prepare.sh

#Prepare/synchronize CORE module
./core_prepare.sh

#Prepare/synchronize NoSQL Extension module
./extension_nosql_prepare.sh

#Prepare/synchronize Extension module
./extension_prepare.sh

#Prepare/synchronize CORBA Extension module
./extension_corba_prepare.sh

#Prepare/synchronize Oracle Extension module
./extension_oracle_prepare.sh

#Prepare synchronized Oracle NoSQL Extension module
./extension_oracle_nosql_prepare.sh

#Prepare synchronized Oracle Spatial Extension Test module
./extension_oracle_spatial_prepare.sh

#Prepare/synchronize JPA module
./jpa_prepare.sh

#Prepare/synchronize JPA TEST sources/resources
./jpa_test_prepare.sh

#Prepare/synchronize JPA NoSQL TEST sources/resources
./jpa_test_nosql_prepare.sh

#Prepare/synchronize JPA ORACLE TEST sources/resources
./jpa_test_oracle_prepare.sh

#Prepare/synchronize JPA JPA-RS module
./jpa_jpars_prepare.sh

#Prepare/synchronize JPA TEST JSE module
./jpa_test_jse_prepare.sh

#Prepare/synchronize JPA TEST JAX-RS module
./jpa_test_jaxrs_prepare.sh

#Prepare/synchronize JPA TEST SPRING module
./jpa_test_spring_prepare.sh

#Prepare/synchronize JPA TEST WDF module
./jpa_test_wdf_prepare.sh

#Prepare/synchronize JPA JPA-RS test server sources/resources
./jpa_test_jpars_server_prepare.sh

#Prepare/synchronize JPA MODEL GENERATOR module
./jpa_modelgen_prepare.sh

#Prepare/synchronize MOXy module
./moxy_prepare.sh

#Prepare/synchronize MOXy XJC module
./moxy_xjc_prepare.sh

#Prepare/synchronize DBWS module and DBWS test oracle module
./dbws_prepare.sh

#Prepare/synchronize SDO module and SDO test server module
./sdo_prepare.sh

#Prepare/synchronize UTILS DBWS builder module
./utils_dbws_builder_prepare.sh

#Prepare/synchronize UTILS RENAME module
./utils_rename_prepare.sh

#Prepare/synchronize UTILS SIGCOMPARE module
./utils_sigcompare_prepare.sh

#Prepare/synchronize Performance Tests module
./performance_tests_prepare.sh

#Prepare/synchronize Distribution module
./distribution_prepare.sh
