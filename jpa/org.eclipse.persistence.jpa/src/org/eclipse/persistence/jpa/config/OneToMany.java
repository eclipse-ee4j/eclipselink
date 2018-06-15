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
public interface OneToMany {

    public JoinColumn addJoinColumn();
    public JoinField addJoinField();
    public AssociationOverride addMapKeyAssociationOverride();
    public AttributeOverride addMapKeyAttributeOverride();
    public Convert addMapKeyConvert();
    public JoinColumn addMapKeyJoinColumn();
    public Property addProperty();
    public OneToMany setAccess(String access);
    public AccessMethods setAccessMethods();
    public OneToMany setAttributeType(String attributeType);
    public BatchFetch setBatchFetch();
    public Cascade setCascade();
    public OneToMany setCascadeOnDelete(Boolean cascadeOnDelete);
    public Converter setConverter();
    public OneToMany setDeleteAll(Boolean deleteAll);
    public OneToMany setFetch(String fetch);
    public ForeignKey setForeignKey();
    public HashPartitioning setHashPartitioning();
    public OneToMany setJoinFetch(String joinFetch);
    public JoinTable setJoinTable();
    public MapKey setMapKey();
    public OneToMany setMapKeyClass(String mapKeyClass);
    public Column setMapKeyColumn();
    public OneToMany setMapKeyConvert(String mapKeyConvert);
    public Enumerated setMapKeyEnumerated();
    public ForeignKey setMapKeyForeignKey();
    public Temporal setMapKeyTemporal();
    public OneToMany setMappedBy(String mappedBy);
    public OneToMany setName(String name);
    public OneToMany setNonCacheable(Boolean nonCacheable);
    public ObjectTypeConverter setObjectTypeConverter();
    public OneToMany setOrderBy(String orderBy);
    public OrderColumn setOrderColumn();
    public OneToMany setOrphanRemoval(Boolean orphanRemoval);
    public Partitioning setPartitioning();
    public PinnedPartitioning setPinnedPartitioning();
    public OneToMany setPrivateOwned(Boolean privateOwned);
    public RangePartitioning setRangePartitioning();
    public ReplicationPartitioning setReplicationPartitioning();
    public RoundRobinPartitioning setRoundRobinPartitioning();
    public StructConverter setStructConverter();
    public OneToMany setTargetEntity(String targetEntity);
    public TypeConverter setTypeConverter();
    public UnionPartitioning setUnionPartitioning();
    public ValuePartitioning setValuePartitioning();

}
