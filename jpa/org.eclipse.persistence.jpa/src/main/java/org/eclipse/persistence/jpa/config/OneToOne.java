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
public interface OneToOne {

    JoinColumn addJoinColumn();
    JoinField addJoinField();
    PrimaryKeyJoinColumn addPrimaryKeyJoinColumn();
    Property addProperty();
    OneToOne setAccess(String access);
    AccessMethods setAccessMethods();
    BatchFetch setBatchFetch();
    Cascade setCascade();
    OneToOne setCascadeOnDelete(Boolean cascadeOnDelete);
    OneToOne setFetch(String fetch);
    ForeignKey setForeignKey();
    HashPartitioning setHashPartitioning();
    OneToOne setId(Boolean id);
    OneToOne setJoinFetch(String joinFetch);
    JoinTable setJoinTable();
    OneToOne setMappedBy(String mappedBy);
    OneToOne setMapsId(String mapsId);
    OneToOne setName(String name);
    OneToOne setNonCacheable(Boolean nonCacheable);
    OneToOne setOptional(Boolean optional);
    OneToOne setOrphanRemoval(Boolean orphanRemoval);
    OneToOne setPartitioned(String partitioned);
    Partitioning setPartitioning();
    PinnedPartitioning setPinnedPartitioning();
    ForeignKey setPrimaryKeyForeignKey();
    OneToOne setPrivateOwned(Boolean privateOwned);
    RangePartitioning setRangePartitioning();
    ReplicationPartitioning setReplicationPartitioning();
    RoundRobinPartitioning setRoundRobinPartitioning();
    OneToOne setTargetEntity(String targetEntity);
    UnionPartitioning setUnionPartitioning();
    ValuePartitioning setValuePartitioning();

}
