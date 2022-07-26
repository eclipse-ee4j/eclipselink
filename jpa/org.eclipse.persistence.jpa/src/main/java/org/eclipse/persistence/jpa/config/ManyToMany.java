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
public interface ManyToMany {

    JoinField addJoinField();
    AssociationOverride addMapKeyAssociationOverride();
    AttributeOverride addMapKeyAttributeOverride();
    Convert addMapKeyConvert();
    JoinColumn addMapKeyJoinColumn();
    Property addProperty();
    ManyToMany setAccess(String access);
    AccessMethods setAccessMethods();
    ManyToMany setAttributeType(String attributeType);
    BatchFetch setBatchFetch();
    Cascade setCascade();
    ManyToMany setCascadeOnDelete(Boolean cascadeOnDelete);
    Converter setConverter();
    ManyToMany setFetch(String fetch);
    HashPartitioning setHashPartitioning();
    ManyToMany setJoinFetch(String joinFetch);
    JoinTable setJoinTable();
    MapKey setMapKey();
    ManyToMany setMapKeyClass(String mapKeyClass);
    Column setMapKeyColumn();
    ManyToMany setMapKeyConvert(String mapKeyConvert);
    Enumerated setMapKeyEnumerated();
    ForeignKey setMapKeyForeignKey();
    Temporal setMapKeyTemporal();
    ManyToMany setMappedBy(String mappedBy);
    ManyToMany setName(String name);
    ManyToMany setNonCacheable(Boolean nonCacheable);
    ObjectTypeConverter setObjectTypeConverter();
    ManyToMany setOrderBy(String orderBy);
    OrderColumn setOrderColumn();
    Partitioning setPartitioning();
    PinnedPartitioning setPinnedPartitioning();
    RangePartitioning setRangePartitioning();
    ReplicationPartitioning setReplicationPartitioning();
    RoundRobinPartitioning setRoundRobinPartitioning();
    StructConverter setStructConverter();
    ManyToMany setTargetEntity(String targetEntity);
    TypeConverter setTypeConverter();
    UnionPartitioning setUnionPartitioning();
    ValuePartitioning setValuePartitioning();

}
