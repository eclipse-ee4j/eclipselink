/*
 * Copyright (c) 2026 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
@jakarta.persistence.EntityListeners({PackageModels.FirstPackageListener.class,
        PackageModels.SecondPackageListener.class})
@jakarta.persistence.TableGenerator(name = "packageDescriptorGenerator",
        table = "JPA_PD_SEQUENCE", pkColumnName = "SEQ_NAME", valueColumnName = "SEQ_VALUE",
        pkColumnValue = "PACKAGE_RECORD", allocationSize = 1)
package org.eclipse.persistence.jpa.packagedescriptors.model;
