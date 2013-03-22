/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     James Sutherland (Oracle) - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.descriptors.partitioning;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.DatabaseQuery;

/**
 * PUBLIC:
 * UnionPartitionPolicy sends queries to all connection pools and unions the results.
 * This is for queries or relationships that span partitions when partitioning is used,
 * such as on a ManyToMany cross partition relationship.
 * @author James Sutherland
 * @since EclipseLink 2.2
 */
public class UnionPartitioningPolicy extends ReplicationPartitioningPolicy {

    protected boolean replicateWrites = false;

    public UnionPartitioningPolicy() {
        super();
    }
    
    public UnionPartitioningPolicy(boolean replicateWrites) {
        super();
        this.replicateWrites = replicateWrites;
    }

    public UnionPartitioningPolicy(String... pools) {
        super(pools);
    }

    public UnionPartitioningPolicy(List<String> pools) {
        super(pools);
    }
        
    /**
     * PUBLIC:
     * Return if write queries should be replicated.
     * Writes are normally not replicated when unioning,
     * but can be for ManyToMany relationships, when the join table needs to be replicated.
     */
    public boolean getReplicateWrites() {
        return replicateWrites;
    }
    
    /**
     * PUBLIC:
     * Set if write queries should be replicated.
     * Writes are normally not replicated when unioning,
     * but can be for ManyToMany relationships, when the join table needs to be replicated.
     */
    public void setReplicateWrites(boolean replicateWrites) {
        this.replicateWrites = replicateWrites;
    }

    /**
     * INTERNAL:
     * Get a connection from one of the pools in a round robin rotation fashion.
     */
    public List<Accessor> getConnectionsForQuery(AbstractSession session, DatabaseQuery query, AbstractRecord arguments) {
        if (!this.replicateWrites && query.isModifyQuery()) {
            return null;
        }
        List<Accessor> accessors = new ArrayList<Accessor>(this.connectionPools.size());
        for (String poolName : this.connectionPools) {
            accessors.add(getAccessor(poolName, session, query, false));
        }
        return accessors;
    }
    
}
