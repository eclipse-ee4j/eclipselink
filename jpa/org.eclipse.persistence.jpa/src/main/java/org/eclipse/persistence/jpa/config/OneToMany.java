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
public interface OneToMany {

    JoinColumn addJoinColumn();
    JoinField addJoinField();
    AssociationOverride addMapKeyAssociationOverride();
    AttributeOverride addMapKeyAttributeOverride();
    Convert addMapKeyConvert();
    JoinColumn addMapKeyJoinColumn();
    Property addProperty();
    OneToMany setAccess(String access);
    AccessMethods setAccessMethods();
    OneToMany setAttributeType(String attributeType);
    BatchFetch setBatchFetch();
    Cascade setCascade();
    OneToMany setCascadeOnDelete(Boolean cascadeOnDelete);
    Converter setConverter();
    OneToMany setDeleteAll(Boolean deleteAll);
    OneToMany setFetch(String fetch);
    ForeignKey setForeignKey();
    HashPartitioning setHashPartitioning();
    OneToMany setJoinFetch(String joinFetch);
    JoinTable setJoinTable();
    MapKey setMapKey();
    OneToMany setMapKeyClass(String mapKeyClass);
    Column setMapKeyColumn();
    OneToMany setMapKeyConvert(String mapKeyConvert);
    Enumerated setMapKeyEnumerated();
    ForeignKey setMapKeyForeignKey();
    Temporal setMapKeyTemporal();
    OneToMany setMappedBy(String mappedBy);
    OneToMany setName(String name);
    OneToMany setNonCacheable(Boolean nonCacheable);
    ObjectTypeConverter setObjectTypeConverter();
    OneToMany setOrderBy(String orderBy);
    OrderColumn setOrderColumn();
    OneToMany setOrphanRemoval(Boolean orphanRemoval);
    Partitioning setPartitioning();
    PinnedPartitioning setPinnedPartitioning();
    OneToMany setPrivateOwned(Boolean privateOwned);
    RangePartitioning setRangePartitioning();
    ReplicationPartitioning setReplicationPartitioning();
    RoundRobinPartitioning setRoundRobinPartitioning();
    StructConverter setStructConverter();
    OneToMany setTargetEntity(String targetEntity);
    TypeConverter setTypeConverter();
    UnionPartitioning setUnionPartitioning();
    ValuePartitioning setValuePartitioning();

}
