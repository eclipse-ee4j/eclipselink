/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.identitymaps;

import java.util.*;

/**
 * <p><b>Purpose</b>: Provide the capability to not cache objects at all.
 * <p><b>Responsibilities</b>:<ul>
 * <li> Do nothing when an object is cached.
 * </ul>
 * @since TOPLink/Java 1.0
 */
public class NoIdentityMap extends IdentityMap {
    public NoIdentityMap(int size) {
        super(size);
    }

    /**
     * NoIdentityMap has no locks.
     */
    public void collectLocks(HashMap threadList) {
        return;
    }

    /**
     * Return an empty enumerator.
     */
    public Enumeration elements() {
        return new Vector(0).elements();
    }

    /**
     * Return null as no objects are cached.
     */
    public Object get(Vector primaryKey) {
        return null;
    }

    /**
     * Return null as no objects are cached.
     */
    protected CacheKey getCacheKey(CacheKey searchKey) {
        return null;
    }
    
    /**
     * Return null as no objects are cached.
     */
    protected CacheKey getCacheKeyIfAbsentPut(CacheKey cacheKey) {
        return null;
    }

    /**
     * Return 0 as no objects are cached.
     */
    public int getSize() {
        return 0;
    }

    /**
     * Return 0 as no objects are cached.
     */
    public int getSize(Class myClass, boolean recurse) {
        return 0;
    }

    /**
     * Return null as no objects are cached.
     */
    public Object getWriteLockValue(Vector primaryKey) {
        return null;
    }

    /**
     * Return an empty enumerator.
     */
    public Enumeration keys() {
        return new Vector(1).elements();
    }

    /**
     * Do Nothing.
     */
    public CacheKey put(Vector aVector, Object object, Object writeLockValue, long readTime) {
        return null;
    }

    /**
     * Do Nothing.
     */
    public void put(CacheKey key) {
        return;
    }

    /**
     * Do Nothing.
     * Return null, since no objects are cached.
     */
    public Object remove(Vector primaryKey) {
        return null;
    }

    /**
     * Do Nothing.
     * Return null, since no objects are cached.
     */
    public Object remove(CacheKey searchKey) {
        return null;
    }
    
    /**
     * Do Nothing.
     */
    public void setWriteLockValue(Vector primaryKey, Object writeLockValue) {
        return;
    }
}