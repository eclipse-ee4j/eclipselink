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
 *      *     30/05/2012-2.4 Guy Pelletier    
 *       - 354678: Temp classloader is still being used during metadata processing
 ******************************************************************************/  
package org.eclipse.persistence.descriptors.partitioning;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.DatabaseQuery;

/**
 * PUBLIC:
 * RangePartitioningPolicy partitions access to a database cluster by a field value from the object,
 * such as the object's id, location, or tenant.
 * Each server is assigned a range of values.
 * All write or read request for object's with that value are sent to the server.
 * If a query does not include the field as a parameter, then it can either be sent
 * to all server's and unioned, or left to the sesion's default behavior.
 * @author James Sutherland
 * @since EclipseLink 2.2
 */
public class RangePartitioningPolicy extends FieldPartitioningPolicy {
    
    protected List<RangePartition> partitions = new ArrayList<RangePartition>();

    public RangePartitioningPolicy() {
        super();
    }

    public RangePartitioningPolicy(String partitionField) {
        super(partitionField);
    }
    
    public RangePartitioningPolicy(String partitionField, boolean unionUnpartitionableQueries) {
        super(partitionField, unionUnpartitionableQueries);
    }

    public RangePartitioningPolicy(String partitionField, RangePartition... partitions) {
        this(partitionField);
        for (RangePartition partition : partitions) {
            addPartition(partition);
        }
    }
    
    /**
     * INTERNAL:
     * Convert all the class-name-based settings to actual class-based settings. 
     * This method is used when converting a project that has been  built with 
     * class names to a project with classes.
     */
    public void convertClassNamesToClasses(ClassLoader classLoader) { 
        for (RangePartition rangePartition : partitions) {
            rangePartition.convertClassNamesToClasses(classLoader);
        }
    }
    
    /**
     * PUBLIC:
     * Return the range partitions.
     * Each partition represents a range of value to route to a connection pool.
     * Range values should not overlap.
     */
    public List<RangePartition> getPartitions() {
        return partitions;
    }
    
    /**
     * PUBLIC:
     * Set the range partitions.
     * Each partition represents a range of value to route to a connection pool.
     * Range values should not overlap.
     */
    public void setPartitions(List<RangePartition> partitions) {
        this.partitions = partitions;
    }
    
    /**
     * PUBLIC:
     * Add the range partition.
     */
    public void addPartition(String connectionPool, Comparable startValue, Comparable endValue) {
        getPartitions().add(new RangePartition(connectionPool, startValue, endValue));
    }
    
    /**
     * PUBLIC:
     * Add the range partition.
     */
    public void addPartition(RangePartition partition) {
        getPartitions().add(partition);
    }

    /**
     * INTERNAL:
     * Get a connection from one of the pools in a round robin rotation fashion.
     */
    public List<Accessor> getConnectionsForQuery(AbstractSession session, DatabaseQuery query, AbstractRecord arguments) {
        Object value = arguments.get(this.partitionField);
        List<Accessor> accessors = null;
        if (value == null) {
            if (this.unionUnpartitionableQueries) {
                accessors = new ArrayList<Accessor>(this.partitions.size());
            } else {
                return null;
            }
        } else {
            accessors = new ArrayList<Accessor>(1);
        }
        int size = this.partitions.size();
        for (int index = 0; index < size; index++) {
            RangePartition partition = this.partitions.get(index);
            if ((value == null) || partition.isInRange(value)) {
                if (session.getPlatform().hasPartitioningCallback()) {
                    // UCP support.
                    session.getPlatform().getPartitioningCallback().setPartitionId(index);
                    return null;
                }
                accessors.add(getAccessor(partition.getConnectionPool(), session, query, false));
                if (value != null) {
                    break;
                }
            }
        }
        if (accessors.isEmpty()) {
            return null;
        }
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
        int size = this.partitions.size();
        for (int index = 0; index < size; index++) {
            RangePartition partition = this.partitions.get(index);
            if (partition.isInRange(value)) {
                if (session.getPlatform().hasPartitioningCallback()) {
                    // UCP support.
                    session.getPlatform().getPartitioningCallback().setPartitionId(index);
                } else {
                    getAccessor(partition.getConnectionPool(), session, null, false);
                }
                return;
            }
        }        
    }
}
