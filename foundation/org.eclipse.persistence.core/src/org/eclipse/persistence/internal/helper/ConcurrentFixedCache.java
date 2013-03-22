/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.helper;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.*;

/**
 * Provide a concurrent fixed size caching mechanism.
 * This is used for caching EJBQL parsed queries, Update calls,
 * and other places a fixed size cache is needed.
 * The default fixed size is 100.
 */
public class ConcurrentFixedCache implements Serializable {
    protected int maxSize;
    protected Map cache;

    /**
     * Create a new concurrent cache, with a fixed size of 100.
     */
    public ConcurrentFixedCache() {
        this(100);
    }
        
    /**
     * Create a new concurrent cache, with the max size.
     */
    public ConcurrentFixedCache(int maxSize) {
        // PERF: Use a concurrent map to allow concurrent gets.
        this.cache = new ConcurrentHashMap(maxSize);
        this.maxSize = maxSize;
    }

    /**
     * Return the fixed size of the parse cache.
     */
    public int getMaxSize() {
        return maxSize;
    }

    /**
     * Set the fixed size of the parse cache.
     * When the size is exceeded, subsequent EJBQL will not be cached.
     * The default size is 100;
     */
    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    /**
     * Return the pre-parsed query that represents the EJBQL string.
     * If the EJBQL has not been cached, null is returned.
     */
    public Object get(Object key) {
        return this.cache.get(key);
    }

    /**
     * Add the value to the cache.
     * Remove the
     */
    public void put(Object key, Object value) {
        if (this.maxSize == 0) {
            return;
        }
        this.cache.put(key, value);
        // Currently just removes the first one encountered, not LRU,
        // this is not ideal, but the most concurrent and quickest way to ensure fixed size.
        if (this.cache.size() > this.maxSize) {
            Iterator iterator = this.cache.keySet().iterator();
            try {
                while ((this.cache.size() > this.maxSize) && iterator.hasNext()) {
                    Object next = iterator.next();
                    // Do not remove what was just put in.
                    if (next != key) {
                        this.cache.remove(next);
                    }
                }
            } catch (Exception alreadyGone) {
                // Ignore.
            }
        }
    }

    /**
     * Return the cache.
     */
    public Map getCache() {
        return cache;
    }
}
