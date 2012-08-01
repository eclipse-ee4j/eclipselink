package org.eclipse.persistence.internal.identitymaps;

import java.lang.ref.ReferenceQueue;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.sessions.AbstractSession;

public class WeakUnitOfWorkIdentityMap extends UnitOfWorkIdentityMap {

    protected ReferenceQueue referenceQueue;
    /** Keep track of a counter to amortize cleanup of dead cache keys */
    protected volatile int cleanupCount;

    /** PERF: Keep track of a cleanup size to avoid cleanup bottleneck for large caches. */
    protected volatile int cleanupSize;

    public WeakUnitOfWorkIdentityMap(int size, ClassDescriptor descriptor, AbstractSession session, boolean isolated) {
        super(size, descriptor, session, isolated);
        this.cleanupCount = 0;
        this.cleanupSize = size;
        this.referenceQueue = new ReferenceQueue<QueueableWeakCacheKey.CacheKeyReference>();
    }
    
    /**
     * Search for any cache keys that have been garbage collected and remove them.
     * This must be done because although the objects held by the cache keys will garbage collect,
     * the keys themselves will not and must be cleaned up.  This is a linear operation so
     * is amortized through the cleanupCount to occur only once per cycle averaging to make
     * the total time still constant.
     */
    protected void cleanupDeadCacheKeys() {
        QueueableWeakCacheKey.CacheKeyReference reference = (QueueableWeakCacheKey.CacheKeyReference)referenceQueue.poll();
        while ( reference != null) {
            CacheKey key = reference.getOwner();
            remove(key);
            reference = (QueueableWeakCacheKey.CacheKeyReference)referenceQueue.poll();
        }
    }

    @Override
    public CacheKey createCacheKey(Object primaryKey, Object object, Object writeLockValue, long readTime) {
        return new QueueableWeakCacheKey(primaryKey, object, writeLockValue, readTime, referenceQueue, isIsolated);
    }

    /**
     * Need to check for cleanup on put.
     */
    @Override
    protected CacheKey putCacheKeyIfAbsent(CacheKey searchKey) {
        CacheKey cacheKey = super.putCacheKeyIfAbsent(searchKey);
        if (cacheKey == null) {
            checkCleanup();
        }
        return cacheKey;
    }

    /**
     * Check if garbage collected cache keys need to be cleaned up.
     */
    protected void checkCleanup() {
        // PERF: Avoid synchronization if cleanup not required (counts are volatile).
        if (this.cleanupCount > this.cleanupSize) {
            synchronized (this) {
                if (this.cleanupCount > this.cleanupSize) {
                    cleanupDeadCacheKeys();
                    this.cleanupCount = 0;
                    // PERF: Avoid cleanup bottleneck for large cache sizes, increase next cleanup.
                    int size = getSize();
                    if (size > this.cleanupSize) {
                        this.cleanupSize = size;
                    }
                }
            }
        }
        this.cleanupCount++;
    }
}
