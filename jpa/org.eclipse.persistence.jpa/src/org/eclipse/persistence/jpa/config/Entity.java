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
public interface Entity {

    public Array addArray();
    public AssociationOverride addAssociationOverride();
    public AttributeOverride addAttributeOverride();
    public Basic addBasic();
    public CacheIndex addCacheIndex();
    public Convert addConvert();
    public Converter addConverter();
    public ElementCollection addElementCollection();
    public EntityListener addEntityListener();
    public Embedded addEmbedded();
    public FetchGroup addFetchGroup();
    public Id addId();
    public Index addIndex();
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
    public PrimaryKeyJoinColumn addPrimaryKeyJoinColumn();
    public Property addProperty();
    public SecondaryTable addSecondaryTable();
    public SqlResultSetMapping addSqlResultSetMapping();
    public StructConverter addStructConverter();
    public Structure addStructure();
    public Transformation addTransformation();
    public Transient addTransient();
    public TypeConverter addTypeConverter();
    public VariableOneToOne addVariableOneToOne();
    public Version addVersion();
    public Entity setAccess(String access);
    public AccessMethods setAccessMethods();
    public AdditionalCriteria setAdditionalCriteria();
    public Cache setCache();
    public Entity setCacheable(Boolean cacheable);
    public CacheInterceptor setCacheInterceptor();
    public Entity setCascadeOnDelete(Boolean cascadeOnDelete);
    public ChangeTracking setChangeTracking();
    public Entity setClass(String cls);
    public Entity setClassExtractor(String classExtractor);
    public CloneCopyPolicy setCloneCopyPolicy();
    public CopyPolicy setCopyPolicy();
    public Entity setCustomizer(String customizer);
    public DiscriminatorColumn setDiscriminatorColumn();
    public Entity setDiscriminatorValue(String discriminatorValue);
    public EmbeddedId setEmbeddedId();
    public Entity setExcludeDefaultListeners(Boolean excludeDefaultListeners);
    public Entity setExcludeDefaultMappings(Boolean excludeDefaultMappings);
    public Entity setExcludeSuperclassListeners(Boolean excludeSuperclassListeners);
    public Entity setExistenceChecking(String existenceChecking);
    public HashPartitioning setHashPartitioning();
    public Entity setIdClass(String idClass);
    public Inheritance setInheritance();
    public InstantiationCopyPolicy setInstantiationCopyPolicy();
    public Entity setMetadataComplete(Boolean metadataComplete);
    public Multitenant setMultitenant();
    public Entity setName(String name);
    public NoSql setNoSql();
    public OptimisticLocking setOptimisticLocking();
    public Entity setParentClass(String parentClass);
    public Partitioning setPartitioning();
    public PinnedPartitioning setPinnedPartitioning();
    public Entity setPostLoad(String methodName);
    public Entity setPostPersist(String methodName);
    public Entity setPostRemove(String methodName);
    public Entity setPostUpdate(String methodName);
    public Entity setPrePersist(String methodName);
    public Entity setPreRemove(String methodName);
    public Entity setPreUpdate(String methodName);
    public PrimaryKey setPrimaryKey();
    public ForeignKey setPrimaryKeyForeignKey();
    public QueryRedirectors setQueryRedirectors();
    public RangePartitioning setRangePartitioning();
    public Entity setReadOnly(Boolean readOnly);
    public ReplicationPartitioning setReplicationPartitioning();
    public RoundRobinPartitioning setRoundRobinPartitioning();
    public SequenceGenerator setSequenceGenerator();
    public Struct setStruct();
    public Table setTable();
    public TableGenerator setTableGenerator();
    public UnionPartitioning setUnionPartitioning();
    public UuidGenerator setUuidGenerator();
    public ValuePartitioning setValuePartitioning();

}
