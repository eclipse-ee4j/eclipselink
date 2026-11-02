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
@jakarta.persistence.NamedQueries({
    @jakarta.persistence.NamedQuery(name = "packageContainerFind", query = "select r from PackageDescriptorRecord r order by r.id"),
    @jakarta.persistence.NamedQuery(name = "packageContainerValues", query = "select r.value from PackageDescriptorRecord r order by r.id")
})
@jakarta.persistence.NamedNativeQueries({
    @jakarta.persistence.NamedNativeQuery(name = "packageContainerNative",
            query = "select ROW_ID, ROW_VALUE from JPA_PD_RECORD order by ROW_ID",
            entities = @jakarta.persistence.EntityResult(
                    entityClass = org.eclipse.persistence.jpa.packagedescriptors.model.PackageModels.Record.class,
                    fields = {@jakarta.persistence.FieldResult(name = "id", column = "ROW_ID"),
                            @jakarta.persistence.FieldResult(name = "value", column = "ROW_VALUE")})),
    @jakarta.persistence.NamedNativeQuery(name = "packageContainerColumns",
            query = "select ROW_ID from JPA_PD_RECORD order by ROW_ID",
            columns = @jakarta.persistence.ColumnResult(name = "ROW_ID", type = Long.class))
})
@jakarta.persistence.NamedStatements({
    @jakarta.persistence.NamedStatement(name = "packageContainerRename",
            statement = "update PackageDescriptorRecord r set r.value = :value where r.id = :id"),
    @jakarta.persistence.NamedStatement(name = "packageContainerDelete",
            statement = "delete from PackageDescriptorRecord r where r.id = :id")
})
@jakarta.persistence.NamedNativeStatements({
    @jakarta.persistence.NamedNativeStatement(name = "packageContainerNativeRename",
            statement = "update JPA_PD_RECORD set ROW_VALUE = ? where ROW_ID = ?"),
    @jakarta.persistence.NamedNativeStatement(name = "packageContainerNativeDelete",
            statement = "delete from JPA_PD_RECORD where ROW_ID = ?")
})
package org.eclipse.persistence.jpa.packagedescriptors.queries;
