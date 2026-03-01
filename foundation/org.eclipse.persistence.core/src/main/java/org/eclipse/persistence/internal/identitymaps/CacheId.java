/*
 * Copyright (c) 2011, 2025 Oracle and/or its affiliates. All rights reserved.
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

import java.io.Serializable;
import java.util.Arrays;

/**
 * Defines a wrapper for a primary key (Id) to use as a key in the cache.
 *
 * @since EclipseLink 2.1
 * @author James Sutherland
 */
public class CacheId implements Serializable, Comparable<CacheId> {
    public static final CacheId EMPTY = new CacheId(new Object[0]);
    private static final CacheIdComparator COMPARATOR = new CacheIdComparator();
    static final Class<?> APBYTE = byte[].class;
    static final Class<?> APCHAR = char[].class;

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
        final Object[] array = Arrays.copyOf(primaryKey, primaryKey.length + 1);
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
        if (obj.getClass() == APBYTE) {
            for (byte element : (byte[])obj) {
                result = 31 * result + element;
            }
        } else if (obj.getClass() == APCHAR) {
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
        if (!(object instanceof CacheId)) {
            return false;
        }
        return equals((CacheId)object);
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
        return compareTo(id) == 0;
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

    @Override
    public String toString() {
        return "[" + Arrays.asList(this.primaryKey) + ": " + this.hash + "]";
    }
}
