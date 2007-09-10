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

import java.util.Vector;

/**
 * Cache key used in the unit of work identity map.
 * This cache avoid locking as the unit of work is single threaded.
 * @author James Sutherland
 * @since TopLink 10.1.3.1
 */
public class UnitOfWorkCacheKey extends CacheKey {

    public UnitOfWorkCacheKey(Vector primaryKeys) {
        super(primaryKeys);
    }

    public UnitOfWorkCacheKey(Vector primaryKey, Object object, Object lockValue) {
        super(primaryKey, object, lockValue);
    }

    public UnitOfWorkCacheKey(Vector primaryKey, Object object, Object lockValue, long readTime) {
        super(primaryKey, object, lockValue, readTime);
    }

    /**
     * Avoid acquiring locks for unit of work.
     */
    public void acquire() {
        return;
    }

    /**
     * Avoid acquiring locks for unit of work.
     */
    public void acquire(boolean forMerge) {
        return;
    }

    /**
     * Avoid acquiring locks for unit of work.
     */
    public boolean acquireNoWait() {
        return true;
    }

    /**
     * Avoid acquiring locks for unit of work.
     */
    public boolean acquireNoWait(boolean forMerge) {
        return true;
    }

    /**
     * Avoid acquiring locks for unit of work.
     */
    public void acquireDeferredLock() {
        return;
    }
    
    /**
     * Avoid acquiring locks for unit of work.
     */
    public void checkReadLock() {
        return;
    }
    
    /**
     * Avoid acquiring locks for unit of work.
     */
    public void acquireReadLock() {
        return;
    }

    /**
     * Avoid acquiring locks for unit of work.
     */
    public boolean acquireReadLockNoWait() {
        return true;
    }

    /**
     * Avoid acquiring locks for unit of work.
     */
    public boolean isAcquired() {
        return false;
    }

    /**
     * Avoid acquiring locks for unit of work.
     */
    public void release() {
        return;
    }

    /**
     * Avoid acquiring locks for unit of work.
     */
    public void releaseDeferredLock() {
        return;
    }

    /**
     * Avoid acquiring locks for unit of work.
     */
    public void releaseReadLock() {
        return;
    }
}