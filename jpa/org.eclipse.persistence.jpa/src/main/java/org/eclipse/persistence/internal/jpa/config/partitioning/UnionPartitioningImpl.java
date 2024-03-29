/*
 * Copyright (c) 2013, 2024 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.jpa.config.partitioning;

import java.util.ArrayList;

import org.eclipse.persistence.internal.jpa.config.MetadataImpl;
import org.eclipse.persistence.internal.jpa.metadata.partitioning.UnionPartitioningMetadata;
import org.eclipse.persistence.jpa.config.UnionPartitioning;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class UnionPartitioningImpl extends MetadataImpl<UnionPartitioningMetadata> implements UnionPartitioning {

    public UnionPartitioningImpl() {
        super(new UnionPartitioningMetadata());

        getMetadata().setConnectionPools(new ArrayList<>());
    }

    @Override
    public UnionPartitioning addConnectionPool(String connectionPool) {
        getMetadata().getConnectionPools().add(connectionPool);
        return this;
    }

    @Override
    public UnionPartitioning setName(String name) {
        getMetadata().setName(name);
        return this;
    }

    @Override
    public UnionPartitioning setReplicateWrites(Boolean replicateWrites) {
        getMetadata().setReplicateWrites(replicateWrites);
        return this;
    }

}
