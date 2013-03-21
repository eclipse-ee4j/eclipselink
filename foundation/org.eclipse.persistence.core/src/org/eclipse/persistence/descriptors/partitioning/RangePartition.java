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

import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.security.PrivilegedActionException;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.internal.security.PrivilegedGetConstructorFor;
import org.eclipse.persistence.internal.security.PrivilegedInvokeConstructor;

/**
 * PUBLIC:
 * Represent a specific range partition.
 * Values >= startValue and <= endValue will be routed to the connection pool.
 * @author James Sutherland
 * @since EclipseLink 2.2
 */
public class RangePartition  {
    protected String endValueName;
    protected String startValueName;
    protected String partitionValueTypeName;
    protected String connectionPool;
    
    protected Class partitionValueType;
    protected Comparable startValue;
    protected Comparable endValue;
    
    public RangePartition() {}
    
    /**
     * INTERNAL:
     * COnstructor used from metadata processing to avoid classloader 
     * dependencies. Class names are converted/initialized in the 
     * convertClassNamesToClasses method.
     */
    public RangePartition(String connectionPool, String partitionValueTypeName, String startValueName, String endValueName) {
        this.connectionPool = connectionPool;
        this.endValue = null;
        this.endValueName = endValueName;
        this.startValue = null;
        this.startValueName = startValueName;
        this.partitionValueTypeName = partitionValueTypeName;
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
     * INTERNAL:
     * Convert all the class-name-based settings to actual class-based settings. 
     * This method is used when converting a project that has been built with 
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
        
        // Once we know we have a partition value type we can convert our partition ranges.
        if (partitionValueType != null) {
            if (startValueName != null) {
                startValue = (Comparable) initObject(partitionValueType, startValueName);
            }
        
            if (endValueName != null) {
                endValue = (Comparable) initObject(partitionValueType, endValueName);
            } 
        }
    }
    
    /**
     * PUBLIC:
     * Return the range start value.  Values greater or equal to this value are part of this partition.
     */
    public Comparable getStartValue() {
        return startValue;
    }
    
    /**
     * INTERNAL:
     * TODO: clean up the exception handling.
     */    
    protected Object initObject(Class type, String value) {
        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
            try {
                Constructor constructor = (Constructor) AccessController.doPrivileged(new PrivilegedGetConstructorFor(type, new Class[] {String.class}, false));
                return AccessController.doPrivileged(new PrivilegedInvokeConstructor(constructor, new Object[] {value}));
            } catch (PrivilegedActionException exception) {
                //throwInitObjectException(exception, type, value, isData);
            }
        } else {
            try {
                Constructor constructor = PrivilegedAccessHelper.getConstructorFor(type, new Class[] {String.class}, false);
                return PrivilegedAccessHelper.invokeConstructor(constructor, new Object[] {value});
            } catch (Exception exception) {
                //throwInitObjectException(exception, type, value, isData);
            }
        }
        
        return value;
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
