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
public interface MappedSuperclass {

    Array addArray();
    AssociationOverride addAssociationOverride();
    AttributeOverride addAttributeOverride();
    Basic addBasic();
    CacheIndex addCacheIndex();
    Converter addConverter();
    ElementCollection addElementCollection();
    EntityListener addEntityListener();
    Embedded addEmbedded();
    FetchGroup addFetchGroup();
    Id addId();
    ManyToMany addManyToMany();
    ManyToOne addManyToOne();
    NamedNativeQuery addNamedNativeQuery();
    NamedPlsqlStoredFunctionQuery addNamedPLSQLStoredFunctionQuery();
    NamedPlsqlStoredProcedureQuery addNamedPLSQLStoredProcedureQuery();
    NamedQuery addNamedQuery();
    NamedStoredFunctionQuery addNamedStoredFunctionQuery();
    NamedStoredProcedureQuery addNamedStoredProcedureQuery();
    ObjectTypeConverter addObjectTypeConverter();
    OneToMany addOneToMany();
    OneToOne addOneToOne();
    OracleArray addOracleArray();
    OracleObject addOracleObject();
    PlsqlRecord addPlsqlRecord();
    PlsqlTable addPlsqlTable();
    Property addProperty();
    SqlResultSetMapping addSqlResultSetMapping();
    StructConverter addStructConverter();
    Structure addStructure();
    Transformation addTransformation();
    Transient addTransient();
    TypeConverter addTypeConverter();
    VariableOneToOne addVariableOneToOne();
    Version addVersion();
    MappedSuperclass setAccess(String access);
    AccessMethods setAccessMethods();
    AdditionalCriteria setAdditionalCriteria();
    Cache setCache();
    MappedSuperclass setCacheable(Boolean cacheable);
    CacheInterceptor setCacheInterceptor();
    ChangeTracking setChangeTracking();
    MappedSuperclass setClass(String cls);
    CloneCopyPolicy setCloneCopyPolicy();
    CopyPolicy setCopyPolicy();
    MappedSuperclass setCustomizer(String customizer);
    EmbeddedId setEmbeddedId();
    MappedSuperclass setExcludeDefaultListeners(Boolean excludeDefaultListeners);
    MappedSuperclass setExcludeDefaultMappings(Boolean excludeDefaultMappings);
    MappedSuperclass setExcludeSuperclassListeners(Boolean excludeSuperclassListeners);
    MappedSuperclass setExistenceChecking(String existenceChecking);
    HashPartitioning setHashPartitioning();
    MappedSuperclass setIdClass(String idClass);
    InstantiationCopyPolicy setInstantiationCopyPolicy();
    MappedSuperclass setMetadataComplete(Boolean metadataComplete);
    Multitenant setMultitenant();
    OptimisticLocking setOptimisticLocking();
    MappedSuperclass setParentClass(String parentClass);
    Partitioning setPartitioning();
    PinnedPartitioning setPinnedPartitioning();
    MappedSuperclass setPostLoad(String methodName);
    MappedSuperclass setPostPersist(String methodName);
    MappedSuperclass setPostRemove(String methodName);
    MappedSuperclass setPostUpdate(String methodName);
    MappedSuperclass setPrePersist(String methodName);
    MappedSuperclass setPreRemove(String methodName);
    MappedSuperclass setPreUpdate(String methodName);
    PrimaryKey setPrimaryKey();
    QueryRedirectors setQueryRedirectors();
    RangePartitioning setRangePartitioning();
    MappedSuperclass setReadOnly(Boolean readOnly);
    ReplicationPartitioning setReplicationPartitioning();
    RoundRobinPartitioning setRoundRobinPartitioning();
    SequenceGenerator setSequenceGenerator();
    TableGenerator setTableGenerator();
    UnionPartitioning setUnionPartitioning();
    UuidGenerator setUuidGenerator();
    ValuePartitioning setValuePartitioning();

}
