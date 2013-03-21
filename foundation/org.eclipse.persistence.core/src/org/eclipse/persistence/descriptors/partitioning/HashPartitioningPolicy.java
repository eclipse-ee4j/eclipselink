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

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.sessions.server.ServerSession;

/**
 * PUBLIC:
 * HashPartitioningPolicy partitions access to a database cluster by the hash of a field value from the object,
 * such as the object's location, or tenant.
 * The hash indexes into the list of connection pools.
 * All write or read request for object's with that hash value are sent to the server.
 * If a query does not include the field as a parameter, then it can either be sent
 * to all server's and unioned, or left to the sesion's default behavior.
 * @author James Sutherland
 * @since EclipseLink 2.2
 */
public class HashPartitioningPolicy extends FieldPartitioningPolicy {
    
    protected List<String> connectionPools = new ArrayList<String>();

    public HashPartitioningPolicy() {
        super();
    }

    public HashPartitioningPolicy(String partitionField) {
        super(partitionField);
    }
    
    public HashPartitioningPolicy(String partitionField, boolean unionUnpartitionableQueries) {
        super(partitionField, unionUnpartitionableQueries);
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
     * Get a connection from one of the pools in a round robin rotation fashion.
     */
    public List<Accessor> getConnectionsForQuery(AbstractSession session, DatabaseQuery query, AbstractRecord arguments) {
        Object value = arguments.get(this.partitionField);
        if (value == null) {
            if (this.unionUnpartitionableQueries) {
                // Use all connections.
                List<Accessor> accessors = new ArrayList<Accessor>(this.connectionPools.size());
                for (String poolName : this.connectionPools) {
                    accessors.add(getAccessor(poolName, session, query, false));
                }
                return accessors;
            } else {
                // Use default behavior.
                return null;
            }
        }
        int index = value.hashCode() % this.connectionPools.size();
        if (session.getPlatform().hasPartitioningCallback()) {
            // UCP support.
            session.getPlatform().getPartitioningCallback().setPartitionId(index);
            return null;
        }
        // Use the mapped connection pool.
        List<Accessor> accessors = new ArrayList<Accessor>(1);
        String poolName = this.connectionPools.get(index);
        accessors.add(getAccessor(poolName, session, query, false));
        return accessors;
    }

    /**
     * INTERNAL:
     * Allow for the persist call to assign the partition.
     */
    @Override
    public void partitionPersist(AbstractSession session, Object object, ClassDescriptor descriptor) {
        Object value = extractPartitionValueForPersist(session, object, descriptor);
        if (value == null) {
            return;
        }
        int index = value.hashCode() % this.connectionPools.size();
        if (session.getPlatform().hasPartitioningCallback()) {
            // UCP support.
            session.getPlatform().getPartitioningCallback().setPartitionId(index);
        } else {
            String poolName = this.connectionPools.get(index);
            getAccessor(poolName, session, null, false);
        }
    }
}
