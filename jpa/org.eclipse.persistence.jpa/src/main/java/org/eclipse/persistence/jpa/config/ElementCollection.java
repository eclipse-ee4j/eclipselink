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
public interface ElementCollection {

    AssociationOverride addAssociationOverride();
    AttributeOverride addAttributeOverride();
    Convert addConvert();
    AssociationOverride addMapKeyAssociationOverride();
    AttributeOverride addMapKeyAttributeOverride();
    Convert addMapKeyConvert();
    JoinColumn addMapKeyJoinColumn();
    Property addProperty();
    ElementCollection setAccess(String access);
    AccessMethods setAccessMethods();
    ElementCollection setAttributeType(String attributeType);
    BatchFetch setBatchFetch();
    ElementCollection setCascadeOnDelete(Boolean cascadeOnDelete);
    CollectionTable setCollectionTable();
    Column setColumn();
    ElementCollection setCompositeMember(String compositeMember);
    ElementCollection setConvert(String convert);
    Converter setConverter();
    ElementCollection setDeleteAll(Boolean deleteAll);
    Enumerated setEnumerated();
    ElementCollection setFetch(String fetch);
    Field setField();
    HashPartitioning setHashPartitioning();
    ElementCollection setJoinFetch(String joinFetch);
    Lob setLob();
    MapKey setMapKey();
    ElementCollection setMapKeyClass(String mapKeyClass);
    Column setMapKeyColumn();
    ElementCollection setMapKeyConvert(String mapKeyConvert);
    Enumerated setMapKeyEnumerated();
    ForeignKey setMapKeyForeignKey();
    Temporal setMapKeyTemporal();
    ElementCollection setName(String name);
    ElementCollection setNonCacheable(Boolean nonCacheable);
    ObjectTypeConverter setObjectTypeConverter();
    ElementCollection setOrderBy(String orderBy);
    OrderColumn setOrderColumn();
    Partitioning setPartitioning();
    PinnedPartitioning setPinnedPartitioning();
    RangePartitioning setRangePartitioning();
    ReplicationPartitioning setReplicationPartitioning();
    RoundRobinPartitioning setRoundRobinPartitioning();
    StructConverter setStructConverter();
    ElementCollection setTargetClass(String targetClass);
    Temporal setTemporal();
    TypeConverter setTypeConverter();
    UnionPartitioning setUnionPartitioning();
    ValuePartitioning setValuePartitioning();
}
