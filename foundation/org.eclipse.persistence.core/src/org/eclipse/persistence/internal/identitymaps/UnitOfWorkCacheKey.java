/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.identitymaps;

/**
 * Cache key used in the unit of work identity map.
 * This cache avoid locking as the unit of work is single threaded.
 * @author James Sutherland
 * @since TopLink 10.1.3.1
 */
public class UnitOfWorkCacheKey extends CacheKey {

    public UnitOfWorkCacheKey(Object primaryKeys) {
        super(primaryKeys);
    }

    public UnitOfWorkCacheKey(Object primaryKey, Object object, Object lockValue) {
        super(primaryKey, object, lockValue);
    }

    public UnitOfWorkCacheKey(Object primaryKey, Object object, Object lockValue, long readTime, boolean isIsolated) {
        super(primaryKey, object, lockValue, readTime, isIsolated);
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
