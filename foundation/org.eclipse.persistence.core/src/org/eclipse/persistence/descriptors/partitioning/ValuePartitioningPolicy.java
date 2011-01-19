/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.DatabaseQuery;

/**
 * PUBLIC:
 * ValuePartitioningPolicy partitions access to a database cluster by a field value from the object,
 * such as the object's location, or tenant.
 * Each value is assigned a specific server.
 * All write or read request for object's with that value are sent to the server.
 * If a query does not include the field as a parameter, then it can either be sent
 * to all server's and unioned, or left to the sesion's default behavior.
 * @author James Sutherland
 * @since EclipseLink 2.2
 */
public class ValuePartitioningPolicy extends FieldPartitioningPolicy {

    /** Store the value partitions. Each partition maps a value to a connectionPool. */
    protected Map<Object, String> partitions = new HashMap<Object, String>();
    
    /** The default connection pool is used for any unmapped values. */
    protected String defaultConnectionPool;

    public ValuePartitioningPolicy() {
        super();
    }

    public ValuePartitioningPolicy(String partitionField) {
        super(partitionField);
    }
    
    public ValuePartitioningPolicy(String partitionField, boolean unionUnpartitionableQueries) {
        super(partitionField, unionUnpartitionableQueries);
    }

    /**
     * PUBLIC:
     * Return the default connection pool used for any unmapped values.
     */
    public String getDefaultConnectionPool() {
        return defaultConnectionPool;
    }

    /**
     * PUBLIC:
     * Set the default connection pool used for any unmapped values.
     */
    public void setDefaultConnectionPool(String defaultConnectionPool) {
        this.defaultConnectionPool = defaultConnectionPool;
    }
    
    /**
     * PUBLIC:
     * Return the value partitions.
     * Each partition maps a value to a connectionPool.
     */
    public Map<Object, String> getPartitions() {
        return partitions;
    }
    
    /**
     * PUBLIC:
     * Set the value partitions.
     * Each partition maps a value to a connectionPool.
     */
    public void setPartitions(Map<Object, String> partitions) {
        this.partitions = partitions;
    }
    
    /**
     * PUBLIC:
     * Add the value partition.
     */
    public void addPartition(Object value, String connectionPool) {
        getPartitions().put(value, connectionPool);
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
                List<Accessor> accessors = new ArrayList<Accessor>(this.partitions.size());
                for (String poolName : this.partitions.values()) {
                    accessors.add(getAccessor(poolName, session, query, false));
                }
                return accessors;
            } else {
                // Use default behavior.
                return null;
            }
        }
        // Use the mapped connection pool.
        List<Accessor> accessors = new ArrayList<Accessor>(1);
        String poolName = this.partitions.get(value);
        if (poolName == null) {
            if (this.defaultConnectionPool == null) {
                // Use default behavior.
                return null;
            }
            poolName = this.defaultConnectionPool;
        }
        if (session.getPlatform().hasPartitioningCallback()) {
            // UCP support.
            session.getPlatform().getPartitioningCallback().setPartitionId(Integer.parseInt(poolName));
            return null;
        }
        accessors.add(getAccessor(poolName, session, query, false));
        return accessors;
    }
    
}
