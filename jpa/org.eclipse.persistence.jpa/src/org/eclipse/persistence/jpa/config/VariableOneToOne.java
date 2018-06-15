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
public interface VariableOneToOne {

    public DiscriminatorClass addDiscriminatorClass();
    public JoinColumn addJoinColumn();
    public Property addProperty();
    public VariableOneToOne setAccess(String access);
    public AccessMethods setAccessMethods();
    public Cascade setCascade();
    public DiscriminatorColumn setDiscriminatorColumn();
    public VariableOneToOne setFetch(String fetch);
    public HashPartitioning setHashPartitioning();
    public VariableOneToOne setName(String name);
    public VariableOneToOne setNonCacheable(Boolean nonCacheable);
    public VariableOneToOne setOptional(Boolean optional);
    public VariableOneToOne setOrphanRemoval(Boolean orphanRemoval);
    public VariableOneToOne setPartitioned(String partitioned);
    public Partitioning setPartitioning();
    public PinnedPartitioning setPinnedPartitioning();
    public VariableOneToOne setPrivateOwned(Boolean privateOwned);
    public RangePartitioning setRangePartitioning();
    public ReplicationPartitioning setReplicationPartitioning();
    public RoundRobinPartitioning setRoundRobinPartitioning();
    public VariableOneToOne setTargetInterface(String targetInterface);
    public UnionPartitioning setUnionPartitioning();
    public ValuePartitioning setValuePartitioning();

}
