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

    public void setObject(Object object) {
        this.reference = new CacheKeyReference(object, referenceQueue, this);
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
