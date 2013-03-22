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

import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.sessions.server.ClientSession;
import org.eclipse.persistence.sessions.server.ServerSession;

/**
 * PUBLIC:
 * RoundRobinPartitioningPolicy sends requests in a round robin fashion to the set of connection pools.
 * It is for load-balancing read queries across a cluster of database machines.
 * It requires that the full database be replicated on each machine, so does not support partitioning.
 * The data should either be read-only, or writes should be replicated on the database.
 * @author James Sutherland
 * @since EclipseLink 2.2
 */
public class RoundRobinPartitioningPolicy extends ReplicationPartitioningPolicy {
    
    protected volatile int currentIndex = 0;

    protected boolean replicateWrites = false;

    public RoundRobinPartitioningPolicy() {
        super();
    }
    
    public RoundRobinPartitioningPolicy(boolean replicateWrites) {
        super();
        this.replicateWrites = replicateWrites;
    }

    public RoundRobinPartitioningPolicy(String... pools) {
        super(pools);
    }

    public RoundRobinPartitioningPolicy(List<String> pools) {
        super(pools);
    }
        
    /**
     * PUBLIC:
     * Return if write queries should be replicated.
     * This allows for a set of database to be written to and kept in synch,
     * and have reads load-balanced across the databases.
     */
    public boolean getReplicateWrites() {
        return replicateWrites;
    }
    
    /**
     * PUBLIC:
     * Set if write queries should be replicated.
     * This allows for a set of database to be written to and kept in synch,
     * and have reads load-balanced across the databases.
     */
    public void setReplicateWrites(boolean replicateWrites) {
        this.replicateWrites = replicateWrites;
    }

    /**
     * INTERNAL:
     * Get a connection from one of the pools in a round robin rotation fashion.
     */
    public List<Accessor> getConnectionsForQuery(AbstractSession session, DatabaseQuery query, AbstractRecord arguments) {
        if (this.replicateWrites && query.isModifyQuery()) {
            return super.getConnectionsForQuery(session, query, arguments);
        }
        if (session.getPlatform().hasPartitioningCallback()) {
            // UCP support.
            session.getPlatform().getPartitioningCallback().setPartitionId(nextIndex());
            return null;
        }
        List<Accessor> accessors = new ArrayList<Accessor>(1);
        if (session.isClientSession()) {
            ClientSession client = (ClientSession)session;
            // If the client session already has a connection for the transaction, then just use it.
            if (client.hasWriteConnection() && (session.isExclusiveIsolatedClientSession() || session.isInTransaction())) {
                accessors.add(client.getWriteConnection());
                return accessors;
            }
            Accessor accessor = nextAccessor((ServerSession)session.getParent(), query);
            accessors.add(accessor);
            // Assign a write connection for the duration of the transaction.
            if (session.isExclusiveIsolatedClientSession() || session.isInTransaction()) {
                accessor = ((ClientSession)session).addWriteConnection(accessor.getPool().getName(), accessor);
            }
        } else if (session.isServerSession()) {
            Accessor accessor = nextAccessor((ServerSession)session, query);
            accessors.add(accessor);
        } else {
            throw QueryException.partitioningNotSupported(session, query);
        }
        return accessors;
    }
    
    /**
     * INTERNAL:
     * Return the next pool index to use.
     */
    public int nextIndex() {
        int index = ++this.currentIndex;
        if (index >= this.connectionPools.size()) {
            this.currentIndex = 0;
            index = 0;
        }
        return index;
    }

    /**
     * INTERNAL:
     * Return the next connection accessor.
     */
    public Accessor nextAccessor(ServerSession session, DatabaseQuery query) {
        int index = nextIndex();
        String poolName = this.connectionPools.get(index);
        Accessor accessor = acquireAccessor(poolName, session, query, true);
        // If the connection pools is dead, check the next one.
        while (accessor == null) {
            int nextIndex = nextIndex();
            poolName = this.connectionPools.get(nextIndex);
            if (index == nextIndex) {
                return acquireAccessor(poolName, session, query, false);
            }
            accessor = acquireAccessor(poolName, session, query, true);
        }
        return accessor;
    }
    
}
