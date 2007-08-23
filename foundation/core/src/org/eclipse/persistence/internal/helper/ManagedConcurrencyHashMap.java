/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.helper;

import org.eclipse.persistence.internal.helper.ConcurrencyManager;
import java.util.Map;
import java.util.HashMap;

/**
 * INTERNAL:
 * A wrapper around HashMap that does concurrency control with a ConcurrencyManager
 *
 * Concurent implementations are provided for most of the HashMap functions. Functions that return
 * collections or iterators are not concurrent
 *
 * This is used for query caching in the IdentityMapManager where the user is not on JDK 1.5 and
 * cannot use ConcurrentHashMap
 */
public class ManagedConcurrencyHashMap extends HashMap {
    protected ConcurrencyManager mutex = new ConcurrencyManager();

    public ManagedConcurrencyHashMap() {
        super();
    }

    public ManagedConcurrencyHashMap(int initialCapacity) {
        super(initialCapacity);
    }

    public ManagedConcurrencyHashMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public ManagedConcurrencyHashMap(Map m) {
        super(m);
    }

    /**
     * Removes all mappings from this map.
     */
    public void clear() {
        getMutex().acquire();
        super.clear();
        getMutex().release();
    }

    /**
     * Returns a shallow copy of this <tt>HashMap</tt> instance: the keys and
     * values themselves are not cloned.
     *
     * @return a shallow copy of this map.
     */
    public Object clone() {
        getMutex().acquireReadLock();
        Object result = super.clone();
        getMutex().releaseReadLock();
        return result;
    }

    /**
     * Returns <tt>true</tt> if this map contains a mapping for the
     * specified key.
     *
     * @param   key   The key whose presence in this map is to be tested
     * @return <tt>true</tt> if this map contains a mapping for the specified
     * key.
     */
    public boolean containsKey(Object key) {
        getMutex().acquireReadLock();
        boolean result = super.containsKey(key);
        getMutex().releaseReadLock();
        return result;
    }

    /**
     * Returns <tt>true</tt> if this map maps one or more keys to the
     * specified value.
     *
     * @param value value whose presence in this map is to be tested.
     * @return <tt>true</tt> if this map maps one or more keys to the
     *         specified value.
     */
    public boolean containsValue(Object value) {
        getMutex().acquireReadLock();
        boolean result = super.containsValue(value);
        getMutex().releaseReadLock();
        return result;
    }

    /**
     * Returns the value to which the specified key is mapped in this identity
     * hash map, or <tt>null</tt> if the map contains no mapping for this key.
     * A return value of <tt>null</tt> does not <i>necessarily</i> indicate
     * that the map contains no mapping for the key; it is also possible that
     * the map explicitly maps the key to <tt>null</tt>. The
     * <tt>containsKey</tt> method may be used to distinguish these two cases.
     *
     * @param   key the key whose associated value is to be returned.
     * @return  the value to which this map maps the specified key, or
     *          <tt>null</tt> if the map contains no mapping for this key.
     * @see #put(Object, Object)
     */
    public Object get(Object key) {
        getMutex().acquireReadLock();
        Object result = super.get(key);
        getMutex().releaseReadLock();
        return result;
    }

    /**
     * Return the concurrency manager.
     */
    public ConcurrencyManager getMutex() {
        return mutex;
    }

    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for this key, the old
     * value is replaced.
     *
     * @param key key with which the specified value is to be associated.
     * @param value value to be associated with the specified key.
     * @return previous value associated with specified key, or <tt>null</tt>
     *           if there was no mapping for key.  A <tt>null</tt> return can
     *           also indicate that the HashMap previously associated
     *           <tt>null</tt> with the specified key.
     */
    public Object put(Object key, Object value) {
        getMutex().acquire();
        Object result = super.put(key, value);
        getMutex().release();
        return result;
    }

    /**
     * Copies all of the mappings from the specified map to this map
     * These mappings will replace any mappings that
     * this map had for any of the keys currently in the specified map.
     *
     * @param m mappings to be stored in this map.
     * @throws NullPointerException if the specified map is null.
     */
    public void putAll(Map m) {
        getMutex().acquire();
        super.putAll(m);
        getMutex().release();
    }

    /**
     * Removes the mapping for this key from this map if present.
     *
     * @param  key key whose mapping is to be removed from the map.
     * @return previous value associated with specified key, or <tt>null</tt>
     *           if there was no mapping for key.  A <tt>null</tt> return can
     *           also indicate that the map previously associated <tt>null</tt>
     *           with the specified key.
     */
    public Object remove(Object key) {
        getMutex().acquire();
        Object result = super.remove(key);
        getMutex().release();
        return result;
    }
}