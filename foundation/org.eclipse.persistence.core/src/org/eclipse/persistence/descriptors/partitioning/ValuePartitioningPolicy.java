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

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
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
    /** Store the value partitions by name. Initialized at runtime. */
    protected Map<String, String> partitionNames = new HashMap<String, String>();
    
    /** The type name of the partition value names. Initialized at runtime */
    protected String partitionValueTypeName;
    
    /** The type of the partition values. Initialized from the type name at runtime. */
    protected Class partitionValueType;
    
    /** Use to track order for compute UCP index. */
    protected List<String> orderedPartitions = new ArrayList<String>();

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
     * INTERNAL:
     * Convert all the class-name-based settings to actual class-based settings. 
     * This method is used when converting a project that has been  built with 
     * class names to a project with classes.
     */
    public void convertClassNamesToClasses(ClassLoader classLoader) { 
        if (partitionValueType == null && partitionValueTypeName != null) {
            try {
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    try {
                        partitionValueType = (Class) AccessController.doPrivileged(new PrivilegedClassForName(partitionValueTypeName, true, classLoader));
                    } catch (PrivilegedActionException e) {
                        throw ValidationException.classNotFoundWhileConvertingClassNames(partitionValueTypeName, e.getException());
                    }
                } else {
                    partitionValueType = org.eclipse.persistence.internal.security.PrivilegedAccessHelper.getClassForName(partitionValueTypeName, true, classLoader);
                }
            } catch (Exception exception) {
                throw ValidationException.classNotFoundWhileConvertingClassNames(partitionValueTypeName, exception);
            }
        }  
        
        // Once we know we have a partition value type we can convert our partition names.
        if (partitionValueType != null) {
            for (String valueName : this.partitionNames.keySet()) {
                Object value = initObject(partitionValueType, valueName);
                if (getPartitions().containsKey(value)) {
                    throw ValidationException.duplicatePartitionValue(getName(), value);
                }
                addPartition(value, partitionNames.get(valueName));
            }
        }
    }
    
    /**
     * Convert the string value to the class type.
     * This will handle numbers, string, dates, and most other classes.
     */    
    private Object initObject(Class type, String value) {
        return ConversionManager.getDefaultManager().convertObject(value, type);
    }
    
    /** 
     * INTERNAL:
     */
    public void setPartitionValueTypeName(String partitionValueTypeName) {
        this.partitionValueTypeName = partitionValueTypeName;
    }
    
    public List<String> getOrderedPartitions() {
        return orderedPartitions;
    }

    public void setOrderedPartitions(List<String> orderedPartitions) {
        this.orderedPartitions = orderedPartitions;
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
        getOrderedPartitions().add(connectionPool);
    }
    
    /**
     * INTERNAL:
     * Add partition values by name (will be initialized at runtime with the
     * real class loader).
     */
    public void addPartitionName(String valueName, String connectionPool) {
        this.partitionNames.put(valueName, connectionPool);
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
            session.getPlatform().getPartitioningCallback().setPartitionId(getOrderedPartitions().indexOf(poolName));
            return null;
        }
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
        String poolName = this.partitions.get(value);
        if (poolName == null) {
            return;
        }
        if (session.getPlatform().hasPartitioningCallback()) {
            // UCP support.
            session.getPlatform().getPartitioningCallback().setPartitionId(getOrderedPartitions().indexOf(poolName));
        } else {
            getAccessor(poolName, session, null, false);
        }
    }
}
