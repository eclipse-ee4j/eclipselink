/*
 * Copyright (c) 2011, 2019 Oracle and/or its affiliates. All rights reserved.
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
//     James Sutherland - initial API and implementation
package org.eclipse.persistence.internal.identitymaps;

import java.io.*;
import java.util.Arrays;

import org.eclipse.persistence.internal.helper.*;

/**
 * Defines a wrapper for a primary key (Id) to use as a key in the cache.
 *
 * @since EclipseLink 2.1
 * @author James Sutherland
 */
public class CacheId implements Serializable, Comparable<CacheId> {
    public static final CacheId EMPTY = new CacheId(new Object[0]);
    private static final CacheIdComparator COMPARATOR = new CacheIdComparator();

    /** The primary key values. */
    protected Object[] primaryKey;

    /** Cached hashcode. */
    protected int hash;

    /** Indicates whether at least one element of primaryKey is array. */
    protected boolean hasArray;

    public CacheId() {
    }

    public CacheId(Object[] primaryKey) {
        this.primaryKey = primaryKey;
        this.hash = computeHash(primaryKey);
    }

    public Object[] getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(Object[] primaryKey) {
        this.primaryKey = primaryKey;
        this.hash = computeHash(primaryKey);
    }

    /**
     * Append the value to the end of the primary key values.
     */
    public void add(Object value) {
        Object[] array = new Object[this.primaryKey.length + 1];
        System.arraycopy(this.primaryKey, 0, array, 0, this.primaryKey.length);
        array[this.primaryKey.length] = value;
        setPrimaryKey(array);
    }

    /**
     * Set the value in the primary key values.
     */
    public void set(int index, Object value) {
        this.primaryKey[index] = value;
        setPrimaryKey(this.primaryKey);
    }

    /**
     * Pre-compute the hash to optimize hash calls.
     */
    protected int computeHash(Object[] primaryKey) {
        int result = 1;
        for (Object value : primaryKey) {
            if (value != null) {
                //bug5709489, gf bug 1193: fix to handle array hashcodes properly
                if (value.getClass().isArray()) {
                    result = computeArrayHashCode(result, value);
                    this.hasArray = true;
                } else {
                    result = 31 * result + value.hashCode();
                }
            } else {
                result = 31 * result;
            }
        }
        return result;
    }

    /**
     * Compute the hashcode for supported array types.
     */
    private int computeArrayHashCode(int result, Object obj) {
        if (obj.getClass() == ClassConstants.APBYTE) {
            for (byte element : (byte[])obj) {
                result = 31 * result + element;
            }
        } else if (obj.getClass() == ClassConstants.APCHAR) {
            for (char element : (char[])obj) {
                result = 31 * result + element;
            }
        } else {
            for (Object element : (Object[])obj) {
                result = 31 * result + (element == null ? 0 : element.hashCode());
            }
        }
        return result;
    }

    /**
     * Return the precomputed hashcode.
     */
    @Override
    public int hashCode() {
        return this.hash;
    }

    /**
     * Determine if the receiver is equal to anObject.
     * If anObject is a CacheKey, do further comparison, otherwise, return false.
     * @see CacheKey#equals(CacheKey)
     */
    @Override
    public boolean equals(Object object) {
        try {
            return equals((CacheId)object);
        } catch (ClassCastException incorrectType) {
            return false;
        }
    }

    /**
     * Determine if the receiver is equal to key.
     * Use an index compare, because it is much faster than enumerations.
     */
    public boolean equals(CacheId id) {
        if (this == id) {
            return true;
        }
        if (this.hash != id.hash) {
            return false;
        }
        if (this.hasArray != id.hasArray) {
            return false;
        }
        // PERF: Using direct variable access.
        int size = this.primaryKey.length;
        Object[] otherKey = id.primaryKey;
        if (size == otherKey.length) {
            for (int index = 0; index < size; index++) {
                Object value = this.primaryKey[index];
                Object otherValue = otherKey[index];
                if (value == null) {
                    if (otherValue != null) {
                        return false;
                    } else {
                        continue;
                    }
                } else if (otherValue == null) {
                    return false;
                }
                if (this.hasArray) {
                    Class valueClass = value.getClass();
                    if (valueClass.isArray()) {
                        Class otherClass = otherValue.getClass();
                        //gf bug 1193: fix array comparison logic to exit if they don't match, and continue the loop if they do
                        if (((valueClass == ClassConstants.APBYTE) && (otherClass == ClassConstants.APBYTE)) ) {
                            if (!Helper.compareByteArrays((byte[])value, (byte[])otherValue)){
                                return false;
                            }
                        } else if (((valueClass == ClassConstants.APCHAR) && (otherClass == ClassConstants.APCHAR)) ) {
                            if (!Helper.compareCharArrays((char[])value, (char[])otherValue)){
                                return false;
                            }
                        } else {
                            if (!Helper.compareArrays((Object[])value, (Object[])otherValue)) {
                                return false;
                            }
                        }
                    } else {
                        if (!(value.equals(otherValue))) {
                            return false;
                        }
                    }
                } else {
                    if (!(value.equals(otherValue))) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Determine if the receiver is greater or less than the key.
     */
    @Override
    public int compareTo(CacheId otherId) {
        return COMPARATOR.compare(this, otherId);
    }

    public boolean hasArray() {
        return this.hasArray;
    }

    public String toString() {
        return "[" + Arrays.asList(this.primaryKey) + ": " + this.hash + "]";
    }
}
