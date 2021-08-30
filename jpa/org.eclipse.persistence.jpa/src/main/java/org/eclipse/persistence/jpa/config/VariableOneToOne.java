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
public interface VariableOneToOne {

    DiscriminatorClass addDiscriminatorClass();
    JoinColumn addJoinColumn();
    Property addProperty();
    VariableOneToOne setAccess(String access);
    AccessMethods setAccessMethods();
    Cascade setCascade();
    DiscriminatorColumn setDiscriminatorColumn();
    VariableOneToOne setFetch(String fetch);
    HashPartitioning setHashPartitioning();
    VariableOneToOne setName(String name);
    VariableOneToOne setNonCacheable(Boolean nonCacheable);
    VariableOneToOne setOptional(Boolean optional);
    VariableOneToOne setOrphanRemoval(Boolean orphanRemoval);
    VariableOneToOne setPartitioned(String partitioned);
    Partitioning setPartitioning();
    PinnedPartitioning setPinnedPartitioning();
    VariableOneToOne setPrivateOwned(Boolean privateOwned);
    RangePartitioning setRangePartitioning();
    ReplicationPartitioning setReplicationPartitioning();
    RoundRobinPartitioning setRoundRobinPartitioning();
    VariableOneToOne setTargetInterface(String targetInterface);
    UnionPartitioning setUnionPartitioning();
    ValuePartitioning setValuePartitioning();

}
