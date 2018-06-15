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
public interface ManyToOne {

    public JoinColumn addJoinColumn();
    public JoinField addJoinField();
    public Property addProperty();
    public ManyToOne setAccess(String access);
    public AccessMethods setAccessMethods();
    public BatchFetch setBatchFetch();
    public Cascade setCascade();
    public ManyToOne setFetch(String fetch);
    public ForeignKey setForeignKey();
    public HashPartitioning setHashPartitioning();
    public ManyToOne setId(Boolean id);
    public ManyToOne setJoinFetch(String joinFetch);
    public JoinTable setJoinTable();
    public ManyToOne setMapsId(String mapsId);
    public ManyToOne setName(String name);
    public ManyToOne setNonCacheable(Boolean nonCacheable);
    public ManyToOne setOptional(Boolean optional);
    public ManyToOne setPartitioned(String partitioned);
    public Partitioning setPartitioning();
    public PinnedPartitioning setPinnedPartitioning();
    public RangePartitioning setRangePartitioning();
    public ReplicationPartitioning setReplicationPartitioning();
    public RoundRobinPartitioning setRoundRobinPartitioning();
    public ManyToOne setTargetEntity(String targetEntity);
    public UnionPartitioning setUnionPartitioning();
    public ValuePartitioning setValuePartitioning();

}
