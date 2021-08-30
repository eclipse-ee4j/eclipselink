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
public interface ManyToOne {

    JoinColumn addJoinColumn();
    JoinField addJoinField();
    Property addProperty();
    ManyToOne setAccess(String access);
    AccessMethods setAccessMethods();
    BatchFetch setBatchFetch();
    Cascade setCascade();
    ManyToOne setFetch(String fetch);
    ForeignKey setForeignKey();
    HashPartitioning setHashPartitioning();
    ManyToOne setId(Boolean id);
    ManyToOne setJoinFetch(String joinFetch);
    JoinTable setJoinTable();
    ManyToOne setMapsId(String mapsId);
    ManyToOne setName(String name);
    ManyToOne setNonCacheable(Boolean nonCacheable);
    ManyToOne setOptional(Boolean optional);
    ManyToOne setPartitioned(String partitioned);
    Partitioning setPartitioning();
    PinnedPartitioning setPinnedPartitioning();
    RangePartitioning setRangePartitioning();
    ReplicationPartitioning setReplicationPartitioning();
    RoundRobinPartitioning setRoundRobinPartitioning();
    ManyToOne setTargetEntity(String targetEntity);
    UnionPartitioning setUnionPartitioning();
    ValuePartitioning setValuePartitioning();

}
