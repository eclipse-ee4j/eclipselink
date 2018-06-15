/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
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

    public Array addArray();
    public AssociationOverride addAssociationOverride();
    public AttributeOverride addAttributeOverride();
    public Basic addBasic();
    public CacheIndex addCacheIndex();
    public Converter addConverter();
    public ElementCollection addElementCollection();
    public EntityListener addEntityListener();
    public Embedded addEmbedded();
    public FetchGroup addFetchGroup();
    public Id addId();
    public ManyToMany addManyToMany();
    public ManyToOne addManyToOne();
    public NamedNativeQuery addNamedNativeQuery();
    public NamedPlsqlStoredFunctionQuery addNamedPLSQLStoredFunctionQuery();
    public NamedPlsqlStoredProcedureQuery addNamedPLSQLStoredProcedureQuery();
    public NamedQuery addNamedQuery();
    public NamedStoredFunctionQuery addNamedStoredFunctionQuery();
    public NamedStoredProcedureQuery addNamedStoredProcedureQuery();
    public ObjectTypeConverter addObjectTypeConverter();
    public OneToMany addOneToMany();
    public OneToOne addOneToOne();
    public OracleArray addOracleArray();
    public OracleObject addOracleObject();
    public PlsqlRecord addPlsqlRecord();
    public PlsqlTable addPlsqlTable();
    public Property addProperty();
    public SqlResultSetMapping addSqlResultSetMapping();
    public StructConverter addStructConverter();
    public Structure addStructure();
    public Transformation addTransformation();
    public Transient addTransient();
    public TypeConverter addTypeConverter();
    public VariableOneToOne addVariableOneToOne();
    public Version addVersion();
    public MappedSuperclass setAccess(String access);
    public AccessMethods setAccessMethods();
    public AdditionalCriteria setAdditionalCriteria();
    public Cache setCache();
    public MappedSuperclass setCacheable(Boolean cacheable);
    public CacheInterceptor setCacheInterceptor();
    public ChangeTracking setChangeTracking();
    public MappedSuperclass setClass(String cls);
    public CloneCopyPolicy setCloneCopyPolicy();
    public CopyPolicy setCopyPolicy();
    public MappedSuperclass setCustomizer(String customizer);
    public EmbeddedId setEmbeddedId();
    public MappedSuperclass setExcludeDefaultListeners(Boolean excludeDefaultListeners);
    public MappedSuperclass setExcludeDefaultMappings(Boolean excludeDefaultMappings);
    public MappedSuperclass setExcludeSuperclassListeners(Boolean excludeSuperclassListeners);
    public MappedSuperclass setExistenceChecking(String existenceChecking);
    public HashPartitioning setHashPartitioning();
    public MappedSuperclass setIdClass(String idClass);
    public InstantiationCopyPolicy setInstantiationCopyPolicy();
    public MappedSuperclass setMetadataComplete(Boolean metadataComplete);
    public Multitenant setMultitenant();
    public OptimisticLocking setOptimisticLocking();
    public MappedSuperclass setParentClass(String parentClass);
    public Partitioning setPartitioning();
    public PinnedPartitioning setPinnedPartitioning();
    public MappedSuperclass setPostLoad(String methodName);
    public MappedSuperclass setPostPersist(String methodName);
    public MappedSuperclass setPostRemove(String methodName);
    public MappedSuperclass setPostUpdate(String methodName);
    public MappedSuperclass setPrePersist(String methodName);
    public MappedSuperclass setPreRemove(String methodName);
    public MappedSuperclass setPreUpdate(String methodName);
    public PrimaryKey setPrimaryKey();
    public QueryRedirectors setQueryRedirectors();
    public RangePartitioning setRangePartitioning();
    public MappedSuperclass setReadOnly(Boolean readOnly);
    public ReplicationPartitioning setReplicationPartitioning();
    public RoundRobinPartitioning setRoundRobinPartitioning();
    public SequenceGenerator setSequenceGenerator();
    public TableGenerator setTableGenerator();
    public UnionPartitioning setUnionPartitioning();
    public UuidGenerator setUuidGenerator();
    public ValuePartitioning setValuePartitioning();

}
