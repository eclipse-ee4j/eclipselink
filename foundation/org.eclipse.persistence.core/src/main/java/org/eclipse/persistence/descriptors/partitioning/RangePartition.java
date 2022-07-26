/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     James Sutherland (Oracle) - initial API and implementation
//      //     30/05/2012-2.4 Guy Pelletier
//       - 354678: Temp classloader is still being used during metadata processing
package org.eclipse.persistence.descriptors.partitioning;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;

/**
 * PUBLIC:
 * Represent a specific range partition.
 * Values {@literal >=} startValue and {@literal <=} endValue will be routed to the connection pool.
 * @author James Sutherland
 * @since EclipseLink 2.2
 */
public class RangePartition  {
    protected String endValueName;
    protected String startValueName;
    protected String partitionValueTypeName;
    protected String connectionPool;

    protected Class<?> partitionValueType;
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
            partitionValueType = PrivilegedAccessHelper.callDoPrivilegedWithException(
                    () -> PrivilegedAccessHelper.getClassForName(partitionValueTypeName, true, classLoader),
                    (ex) -> ValidationException.classNotFoundWhileConvertingClassNames(partitionValueTypeName, ex)
            );
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
    @SuppressWarnings({"unchecked"})
    protected <T> T initObject(Class<T> type, String value) {
        try {
            return PrivilegedAccessHelper.callDoPrivilegedWithException(
                    () -> PrivilegedAccessHelper.invokeConstructor(
                            PrivilegedAccessHelper.getConstructorFor(
                                    type, new Class<?>[]{String.class}, false), new Object[]{value})
            );
        } catch (Exception exception) {
            // Do nothing
        }
        return (T) value;
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
