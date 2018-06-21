/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
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
public interface ElementCollection {

    public AssociationOverride addAssociationOverride();
    public AttributeOverride addAttributeOverride();
    public Convert addConvert();
    public AssociationOverride addMapKeyAssociationOverride();
    public AttributeOverride addMapKeyAttributeOverride();
    public Convert addMapKeyConvert();
    public JoinColumn addMapKeyJoinColumn();
    public Property addProperty();
    public ElementCollection setAccess(String access);
    public AccessMethods setAccessMethods();
    public ElementCollection setAttributeType(String attributeType);
    public BatchFetch setBatchFetch();
    public ElementCollection setCascadeOnDelete(Boolean cascadeOnDelete);
    public CollectionTable setCollectionTable();
    public Column setColumn();
    public ElementCollection setCompositeMember(String compositeMember);
    public ElementCollection setConvert(String convert);
    public Converter setConverter();
    public ElementCollection setDeleteAll(Boolean deleteAll);
    public Enumerated setEnumerated();
    public ElementCollection setFetch(String fetch);
    public Field setField();
    public HashPartitioning setHashPartitioning();
    public ElementCollection setJoinFetch(String joinFetch);
    public Lob setLob();
    public MapKey setMapKey();
    public ElementCollection setMapKeyClass(String mapKeyClass);
    public Column setMapKeyColumn();
    public ElementCollection setMapKeyConvert(String mapKeyConvert);
    public Enumerated setMapKeyEnumerated();
    public ForeignKey setMapKeyForeignKey();
    public Temporal setMapKeyTemporal();
    public ElementCollection setName(String name);
    public ElementCollection setNonCacheable(Boolean nonCacheable);
    public ObjectTypeConverter setObjectTypeConverter();
    public ElementCollection setOrderBy(String orderBy);
    public OrderColumn setOrderColumn();
    public Partitioning setPartitioning();
    public PinnedPartitioning setPinnedPartitioning();
    public RangePartitioning setRangePartitioning();
    public ReplicationPartitioning setReplicationPartitioning();
    public RoundRobinPartitioning setRoundRobinPartitioning();
    public StructConverter setStructConverter();
    public ElementCollection setTargetClass(String targetClass);
    public Temporal setTemporal();
    public TypeConverter setTypeConverter();
    public UnionPartitioning setUnionPartitioning();
    public ValuePartitioning setValuePartitioning();
}
