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

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.server.ClientSession;

/**
 * PUBLIC:
 * FieldPartitioningPolicy partitions access to a database cluster by a field value from the object,
 * such as the object's id, location, or tenant.
 * All write or read request for object's with that value are sent to the server.
 * If a query does not include the field as a parameter, then it can either be sent
 * to all server's and unioned, or left to the sesion's default behavior.
 * @author James Sutherland
 * @since EclipseLink 2.2
 */
public abstract class FieldPartitioningPolicy extends PartitioningPolicy {

    /** The column to partition by. */
    protected DatabaseField partitionField;

    /** If query does not have the partition field in it, should the query be sent to all databases. */
    protected boolean unionUnpartitionableQueries = false;
    
    public FieldPartitioningPolicy() {
        super();
    }

    public FieldPartitioningPolicy(String partitionField) {
        super();
        this.partitionField = new DatabaseField(partitionField);
    }
    
    public FieldPartitioningPolicy(String partitionField, boolean unionUnpartitionableQueries) {
        this(partitionField);
        this.unionUnpartitionableQueries = unionUnpartitionableQueries;
    }
    
    /**
     * PUBLIC:
     * Return the database column or query parameter to partition queries by.
     * This is the table column name, not the class attribute name.
     * The column value must be included in the query and should normally be part of the object's Id.
     * This can also be the name of a query parameter.
     * If a query does not contain the field the query will not be partitioned.
     */
    public DatabaseField getPartitionField() {
        return partitionField;
    }
    
    /**
     * PUBLIC:
     * Set the database column or query parameter to partition queries by.
     * This is the table column name, not the class attribute name.
     * The column value must be included in the query and should normally be part of the object's Id.
     * This can also be the name of a query parameter.
     * If a query does not contain the field the query will not be partitioned.
     */
    public void setPartitionField(DatabaseField partitionField) {
        this.partitionField = partitionField;
    }
    
    /**
     * PUBLIC:
     * Return the database column or query parameter to partition queries by.
     * This is the table column name, not the class attribute name.
     * The column value must be included in the query and should normally be part of the object's Id.
     * This can also be the name of a query parameter.
     * If a query does not contain the field the query will not be partitioned.
     */
    public String getPartitionFieldName() {
        return getPartitionField().getName();
    }
    
    /**
     * PUBLIC:
     * Set the database column or query parameter to partition queries by.
     * This is the table column name, not the class attribute name.
     * The column value must be included in the query and should normally be part of the object's Id.
     * This can also be the name of a query parameter.
     * If a query does not contain the field the query will not be partitioned.
     */
    public void setPartitionField(String partitionField) {
        this.partitionField = new DatabaseField(partitionField);
    }
        
    /**
     * PUBLIC:
     * Return if queries that do not contain the partition field should be sent
     * to every database and have the result unioned.
     */
    public boolean getUnionUnpartitionableQueries() {
        return unionUnpartitionableQueries;
    }
    
    /**
     * PUBLIC:
     * Set if queries that do not contain the partition field should be sent
     * to every database and have the result unioned.
     */
    public void setUnionUnpartitionableQueries(boolean unionUnpartitionableQueries) {
        this.unionUnpartitionableQueries = unionUnpartitionableQueries;
    }
    
    /**
     * INTERNAL:
     * If persist should be partitioned, extra value from object.
     */
    protected Object extractPartitionValueForPersist(AbstractSession session, Object object, ClassDescriptor descriptor) {
        if (!session.isClientSession()) {
            return null;
        }
        ClientSession client = (ClientSession)session;
        // Only assign the connection if exclusive.
        if (!client.isExclusiveIsolatedClientSession() || client.hasWriteConnection()) {
            return null;
        }
        return descriptor.getObjectBuilder().extractValueFromObjectForField(object, this.partitionField, session);
    }
}
