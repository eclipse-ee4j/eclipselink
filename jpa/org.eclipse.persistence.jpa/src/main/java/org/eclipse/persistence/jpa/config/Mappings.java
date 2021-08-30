/*
 * Copyright (c) 2013, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Guy Pelletier - initial API and implementation
package org.eclipse.persistence.jpa.config;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public interface Mappings {

    Converter addConverter();
    ConverterClass addConverterClass();
    Embeddable addEmbeddable();
    Entity addEntity();
    HashPartitioning addHashPartitioning();
    MappedSuperclass addMappedSuperclass();
    NamedNativeQuery addNamedNativeQuery();
    NamedPlsqlStoredFunctionQuery addNamedPlsqlStoredFunctionQuery();
    NamedPlsqlStoredProcedureQuery addNamedPlsqlStoredProcedureQuery();
    NamedQuery addNamedQuery();
    NamedStoredFunctionQuery addNamedStoredFunctionQuery();
    NamedStoredProcedureQuery addNamedStoredProcedureQuery();
    ObjectTypeConverter addObjectTypeConverter();
    OracleArray addOracleArray();
    OracleObject addOracleObject();
    Partitioning addPartitioning();
    PinnedPartitioning addPinnedPartitioning();
    PlsqlRecord addPlsqlRecord();
    PlsqlTable addPlsqlTable();
    RangePartitioning addRangePartitioning();
    ReplicationPartitioning addReplicationPartititioning();
    RoundRobinPartitioning addRoundRobinPartitioning();
    SequenceGenerator addSequenceGenerator();
    SqlResultSetMapping addSqlResultSetMapping();
    StructConverter addStructConverter();
    TableGenerator addTableGenerator();
    TenantDiscriminatorColumn addTenantDiscriminatorColumn();
    TypeConverter addTypeConverter();
    UnionPartitioning addUnionPartitioning();
    UuidGenerator addUuidGenerator();
    ValuePartitioning addValuePartitioning();
    Mappings setAccess(String access);
    AccessMethods setAccessMethods();
    Mappings setCatalog(String catalog);
    Mappings setPackage(String pkg);
    PersistenceUnitMetadata setPersistenceUnitMetadata();
    Mappings setSchema(String schema);
    Mappings setVersion(String version);

}
