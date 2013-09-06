/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Guy Pelletier - initial API and implementation
 ******************************************************************************/
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
    
    public RoundRobinPartitioningImpl addConnectionPool(String connectionPool) {
        getMetadata().getConnectionPools().add(connectionPool);
        return this;
    }

    public RoundRobinPartitioningImpl setName(String name) {
        getMetadata().setName(name);
        return this;
    }

    public RoundRobinPartitioning setReplicateWrites(Boolean replicateWrites) {
        getMetadata().setReplicateWrites(replicateWrites);
        return this;
    }

}
