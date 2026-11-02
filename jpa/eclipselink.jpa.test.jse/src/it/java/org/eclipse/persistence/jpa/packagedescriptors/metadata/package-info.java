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
@jakarta.persistence.NamedStatement(name = "packageDescriptorRename",
        statement = "update PackageDescriptorRecord r set r.value = :value where r.id = :id")
@jakarta.persistence.NamedNativeStatement(name = "packageDescriptorNativeRename",
        statement = "update JPA_PD_RECORD set ROW_VALUE = ? where ROW_ID = ?")
@jakarta.persistence.NamedQuery(name = "packageDescriptorFind",
        query = "select r from PackageDescriptorRecord r order by r.id",
        flush = jakarta.persistence.QueryFlushMode.NO_FLUSH,
        hints = @jakarta.persistence.QueryHint(name = "eclipselink.jdbc.fetch-size", value = "7"))
@jakarta.persistence.NamedNativeQuery(name = "packageDescriptorNative",
        query = "select ROW_ID, ROW_VALUE from JPA_PD_RECORD order by ROW_ID",
        resultClass = org.eclipse.persistence.jpa.packagedescriptors.model.PackageModels.Record.class)
package org.eclipse.persistence.jpa.packagedescriptors.metadata;
