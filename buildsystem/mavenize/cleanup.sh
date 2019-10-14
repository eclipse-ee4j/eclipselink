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

#Cleanup synchronized ASM sources/resources
./asm_cleanup.sh

#Cleanup synchronized JPQL (Hermes parser) sources/resources
./jpql_cleanup.sh

#Cleanup synchronized CORE sources/resources
./core_cleanup.sh

#Cleanup synchronized NoSQL Extension sources/resources
./extension_nosql_cleanup.sh

#Cleanup synchronized Extension sources/resources
./extension_cleanup.sh

#Cleanup synchronized CORBA Extension sources/resources
./extension_corba_cleanup.sh

#Cleanup synchronized Oracle Extension sources/resources
./extension_oracle_cleanup.sh

#Cleanup synchronized Oracle NoSQL Extension sources/resources
./extension_oracle_nosql_cleanup.sh

#Cleanup synchronized Oracle Spatial Extension sources/resources
./extension_oracle_spatial_cleanup.sh

#Cleanup synchronized JPA sources/resources
./jpa_cleanup.sh

#Cleanup synchronized JPA TEST sources/resources
./jpa_test_cleanup.sh

#Cleanup synchronized JPA NoSQL TEST sources/resources
./jpa_test_nosql_cleanup.sh

#Cleanup synchronized JPA Oracle TEST sources/resources
./jpa_test_oracle_cleanup.sh

#Cleanup synchronized JPA JPA-RS test sources/resources
./jpa_jpars_cleanup.sh

#Cleanup synchronized JPA JSE test sources/resources
./jpa_test_jse_cleanup.sh

#Cleanup synchronized JPA JAXRS test sources/resources
./jpa_test_jaxrs_cleanup.sh

#Cleanup synchronized JPA SPRING test sources/resources
./jpa_test_spring_cleanup.sh

#Cleanup synchronized JPA WDF test sources/resources
./jpa_test_wdf_cleanup.sh

#Cleanup synchronized JPA JPA-RS test server sources/resources
./jpa_test_jpars_server_cleanup.sh

#Cleanup synchronized JPA MODEL GENERATOR sources/resources
./jpa_modelgen_cleanup.sh

#Cleanup synchronized MOXy sources/resources
./moxy_cleanup.sh

#Cleanup synchronized MOXy XJC sources/resources
./moxy_xjc_cleanup.sh

#Cleanup synchronized DBWS and DBWS test oracle sources/resources
./dbws_cleanup.sh

#Cleanup synchronized SDO and SDO test server sources/resources
./sdo_cleanup.sh

#Cleanup synchronized UTILS DBWS sources/resources
./utils_dbws_builder_cleanup.sh

#Cleanup synchronized UTILS RENAME sources/resources
./utils_rename_cleanup.sh

#Cleanup synchronized UTILS SIGCOMPARE sources/resources
./utils_sigcompare_cleanup.sh

#Cleanup synchronized Performance Tests module
./performance_tests_cleanup.sh

#Cleanup synchronized Distribution resources
./distribution_cleanup.sh
