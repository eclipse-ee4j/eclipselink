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
package org.eclipse.persistence.internal.jpa.config.partitioning;

import java.util.ArrayList;

import org.eclipse.persistence.internal.jpa.config.MetadataImpl;
import org.eclipse.persistence.internal.jpa.metadata.partitioning.RoundRobinPartitioningMetadata;
import org.eclipse.persistence.jpa.config.RoundRobinPartitioning;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class RoundRobinPartitioningImpl extends MetadataImpl<RoundRobinPartitioningMetadata> implements RoundRobinPartitioning {

    public RoundRobinPartitioningImpl() {
        super(new RoundRobinPartitioningMetadata());

        getMetadata().setConnectionPools(new ArrayList<String>());
    }

    @Override
    public RoundRobinPartitioningImpl addConnectionPool(String connectionPool) {
        getMetadata().getConnectionPools().add(connectionPool);
        return this;
    }

    @Override
    public RoundRobinPartitioningImpl setName(String name) {
        getMetadata().setName(name);
        return this;
    }

    @Override
    public RoundRobinPartitioning setReplicateWrites(Boolean replicateWrites) {
        getMetadata().setReplicateWrites(replicateWrites);
        return this;
    }

}
