/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
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

/**
 * PUBLIC:
 * Represent a specific range partition.
 * Values >= startValue and <= endValue will be routed to the connection pool.
 * @author James Sutherland
 * @since EclipseLink 2.2
 */
public class RangePartition  {
    
    protected Comparable startValue;
    protected Comparable endValue;
    protected String connectionPool;
    
    public RangePartition() {
        
    }
    
    /**
     * PUBLIC:
     * Create the partition for the connectionPool and start/end values.
     */
    public RangePartition(String connectionPool, Comparable startValue, Comparable endValue) {
        this.connectionPool = connectionPool;
        this.startValue = startValue;
        this.endValue = endValue;
    }
    
    /**
     * PUBLIC:
     * Return the range start value.  Values greater or equal to this value are part of this partition.
     */
    public Comparable getStartValue() {
        return startValue;
    }
    
    /**
     * PUBLIC:
     * Set the range start value.  Values greater or equal to this value are part of this partition.
     */
    public void setStartValue(Comparable startValue) {
        this.startValue = startValue;
    }
    
    /**
     * PUBLIC:
     * Return the range end value.  Values less than or equal this value are part of this partition.
     */
    public Comparable getEndValue() {
        return endValue;
    }
    
    /**
     * PUBLIC:
     * Set the range end value.  Values less than or equal this value are part of this partition.
     */
    public void setEndValue(Comparable endValue) {
        this.endValue = endValue;
    }
    
    /**
     * PUBLIC:
     * Return the connection pool to use for this partition.
     */
    public String getConnectionPool() {
        return connectionPool;
    }
    
    /**
     * PUBLIC:
     * Return the connection pool to use for this partition.
     */     
    public void setConnectionPool(String connectionPool) {
        this.connectionPool = connectionPool;
    }
    
    /**
     * INTERNAL:
     * Return if the value is in the partitions range.
     */
    public boolean isInRange(Object value) {
        if ((this.startValue != null) && (this.startValue.compareTo(value) > 0)) {
            return false;
        }
        if ((this.endValue != null) && (this.endValue.compareTo(value) < 0)) {
            return false;
        }
        return true;
    }
    
}
