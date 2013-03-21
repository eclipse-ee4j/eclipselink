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
import java.util.Arrays;
import java.util.List;

import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.sessions.server.ServerSession;

/**
 * PUBLIC:
 * ReplicationPartitioningPolicy sends requests to a set of connection pools.
 * It is for replicating data across a cluster of database machines.
 * Only modification queries are replicated.
 * @author James Sutherland
 * @since EclipseLink 2.2
 */
public class ReplicationPartitioningPolicy extends PartitioningPolicy {
        
    protected List<String> connectionPools;
    
    public ReplicationPartitioningPolicy() {
        this.connectionPools = new ArrayList<String>();
    }

    public ReplicationPartitioningPolicy(String... pools) {
        this();
        this.connectionPools.addAll(Arrays.asList(pools));
    }

    public ReplicationPartitioningPolicy(List<String> pools) {
        this.connectionPools = pools;
    }
    
    /**
     * INTERNAL:
     * Default the connection pools to all pools if unset.
     */
    public void initialize(AbstractSession session) {
        super.initialize(session);
        if (getConnectionPools().isEmpty() && session.isServerSession()) {
            getConnectionPools().addAll(((ServerSession)session).getConnectionPools().keySet());
        }
    }
    
    /**
     * PUBLIC:
     * Return the list of connection pool names to replicate queries to.
     */
    public List<String> getConnectionPools() {
        return connectionPools;
    }

    /**
     * PUBLIC:
     * Set the list of connection pool names to replicate queries to.
     * A connection pool with the same name must be defined on the ServerSession.
     */
    public void setConnectionPools(List<String> connectionPools) {
        this.connectionPools = connectionPools;
    }

    /**
     * PUBLIC:
     * Add the connection pool name to the list of pools to rotate queries through.
     */
    public void addConnectionPool(String connectionPool) {
        getConnectionPools().add(connectionPool);
    }

    /**
     * INTERNAL:
     * Get a connection from each pool.
     */
    public List<Accessor> getConnectionsForQuery(AbstractSession session, DatabaseQuery query, AbstractRecord arguments) {
        if (!query.isModifyQuery()) {
            return null;
        }
        List<Accessor> accessors = new ArrayList<Accessor>(this.connectionPools.size());
        for (String poolName : this.connectionPools) {
            Accessor accessor = getAccessor(poolName, session, query, true);
            // Do not replicate to dead connection pools.
            if (accessor != null) {
                accessors.add(accessor);
            }
        }
        return accessors;
    }    
    
}
