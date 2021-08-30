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
public interface Entity {

    Array addArray();
    AssociationOverride addAssociationOverride();
    AttributeOverride addAttributeOverride();
    Basic addBasic();
    CacheIndex addCacheIndex();
    Convert addConvert();
    Converter addConverter();
    ElementCollection addElementCollection();
    EntityListener addEntityListener();
    Embedded addEmbedded();
    FetchGroup addFetchGroup();
    Id addId();
    Index addIndex();
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
    PrimaryKeyJoinColumn addPrimaryKeyJoinColumn();
    Property addProperty();
    SecondaryTable addSecondaryTable();
    SqlResultSetMapping addSqlResultSetMapping();
    StructConverter addStructConverter();
    Structure addStructure();
    Transformation addTransformation();
    Transient addTransient();
    TypeConverter addTypeConverter();
    VariableOneToOne addVariableOneToOne();
    Version addVersion();
    Entity setAccess(String access);
    AccessMethods setAccessMethods();
    AdditionalCriteria setAdditionalCriteria();
    Cache setCache();
    Entity setCacheable(Boolean cacheable);
    CacheInterceptor setCacheInterceptor();
    Entity setCascadeOnDelete(Boolean cascadeOnDelete);
    ChangeTracking setChangeTracking();
    Entity setClass(String cls);
    Entity setClassExtractor(String classExtractor);
    CloneCopyPolicy setCloneCopyPolicy();
    CopyPolicy setCopyPolicy();
    Entity setCustomizer(String customizer);
    DiscriminatorColumn setDiscriminatorColumn();
    Entity setDiscriminatorValue(String discriminatorValue);
    EmbeddedId setEmbeddedId();
    Entity setExcludeDefaultListeners(Boolean excludeDefaultListeners);
    Entity setExcludeDefaultMappings(Boolean excludeDefaultMappings);
    Entity setExcludeSuperclassListeners(Boolean excludeSuperclassListeners);
    Entity setExistenceChecking(String existenceChecking);
    HashPartitioning setHashPartitioning();
    Entity setIdClass(String idClass);
    Inheritance setInheritance();
    InstantiationCopyPolicy setInstantiationCopyPolicy();
    Entity setMetadataComplete(Boolean metadataComplete);
    Multitenant setMultitenant();
    Entity setName(String name);
    NoSql setNoSql();
    OptimisticLocking setOptimisticLocking();
    Entity setParentClass(String parentClass);
    Partitioning setPartitioning();
    PinnedPartitioning setPinnedPartitioning();
    Entity setPostLoad(String methodName);
    Entity setPostPersist(String methodName);
    Entity setPostRemove(String methodName);
    Entity setPostUpdate(String methodName);
    Entity setPrePersist(String methodName);
    Entity setPreRemove(String methodName);
    Entity setPreUpdate(String methodName);
    PrimaryKey setPrimaryKey();
    ForeignKey setPrimaryKeyForeignKey();
    QueryRedirectors setQueryRedirectors();
    RangePartitioning setRangePartitioning();
    Entity setReadOnly(Boolean readOnly);
    ReplicationPartitioning setReplicationPartitioning();
    RoundRobinPartitioning setRoundRobinPartitioning();
    SequenceGenerator setSequenceGenerator();
    Struct setStruct();
    Table setTable();
    TableGenerator setTableGenerator();
    UnionPartitioning setUnionPartitioning();
    UuidGenerator setUuidGenerator();
    ValuePartitioning setValuePartitioning();

}
