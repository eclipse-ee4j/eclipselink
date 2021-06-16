/*
 * Copyright (c) 2018, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package org.eclipse.persistence.internal.identitymaps;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

public class QueueableWeakCacheKey extends WeakCacheKey {
    // This the reference queue from the owning map that
    // the weak references will be registered to.
    // makes for easy cleanup
    protected ReferenceQueue referenceQueue;

    public QueueableWeakCacheKey(Object primaryKey, Object object, Object writeLockValue, long readTime, ReferenceQueue refQueue, boolean isIsolated) {
        super(primaryKey, object, writeLockValue, readTime, isIsolated);
        this.referenceQueue = refQueue;
    }

    @Override
    public void setObject(Object object) {
        this.reference = new CacheKeyReference(object, referenceQueue, this);
    }

    /**
     * Avoid acquiring locks for unit of work.
     */
    @Override
    public void acquire() {
        return;
    }

    /**
     * Avoid acquiring locks for unit of work.
     */
    @Override
    public void acquire(boolean forMerge) {
        return;
    }

    /**
     * Avoid acquiring locks for unit of work.
     */
    @Override
    public boolean acquireNoWait() {
        return true;
    }

    /**
     * Avoid acquiring locks for unit of work.
     */
    @Override
    public boolean acquireNoWait(boolean forMerge) {
        return true;
    }

    /**
     * Avoid acquiring locks for unit of work.
     */
    @Override
    public void acquireDeferredLock() {
        return;
    }

    /**
     * Avoid acquiring locks for unit of work.
     */
    @Override
    public void checkReadLock() {
        return;
    }

    /**
     * Avoid acquiring locks for unit of work.
     */
    @Override
    public void acquireReadLock() {
        return;
    }

    /**
     * Avoid acquiring locks for unit of work.
     */
    @Override
    public boolean acquireReadLockNoWait() {
        return true;
    }

    /**
     * Avoid acquiring locks for unit of work.
     */
    @Override
    public boolean isAcquired() {
        return false;
    }

    /**
     * Avoid acquiring locks for unit of work.
     */
    @Override
    public void release() {
        return;
    }

    /**
     * Avoid acquiring locks for unit of work.
     */
    @Override
    public void releaseDeferredLock() {
        return;
    }

    /**
     * Avoid acquiring locks for unit of work.
     */
    @Override
    public void releaseReadLock() {
        return;
    }

    static class CacheKeyReference extends WeakReference{
        protected QueueableWeakCacheKey owner;
        public CacheKeyReference(Object object, ReferenceQueue referenceQueue, QueueableWeakCacheKey owner){
            super(object, referenceQueue);
            this.owner = owner;
        }

        public QueueableWeakCacheKey getOwner(){
            return owner;
        }
    }
}
