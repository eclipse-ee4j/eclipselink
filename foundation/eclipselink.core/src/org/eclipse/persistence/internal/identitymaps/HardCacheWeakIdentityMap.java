/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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

import java.util.*;
import org.eclipse.persistence.internal.helper.linkedlist.*;

/**
 * <p><b>Purpose</b>: A HardCacheWeakIdentityMap is identical to the weak identity map, however the weak reference
 * can be a performance problem for some types of apps because it can cause too much garbage collection
 * of objects read causing them to be re-read and re-built (this defeats the purpose of the cache).
 * The hard weak cache solves this by also holding a fixed number of objects in memory to improve caching.<br>
 * This class makes use of an exposed node linked list to maintain the objects by storing the link nodes in the cache key.
 * <p><b>Responsibilities</b>:<ul>
 * <li> Guarantees identity
 * <li> Allows garbage collection
 * <li> Increases performance by maintaining a fixed size cache of LRU objects when memory is available
 * <li> The default size of the reference cache is half the max size
 * </ul>
 * @since TOPLink/Java 1.2
 */
public class HardCacheWeakIdentityMap extends WeakIdentityMap {
    /** A subset of cache entries have hard references maintained in this list to reduce garbage collection frequency */
    protected ExposedNodeLinkedList referenceCache;

    public HardCacheWeakIdentityMap(int size) {
        super(size);
        this.referenceCache = new ExposedNodeLinkedList();
    }

    /**
     * Use a ReferenceCacheKey that also stores the linked list node to manage
     * the LRU sub-cache of references.
     */
    public CacheKey createCacheKey(Vector primaryKey, Object object, Object writeLockValue, long readTime) {
        return new ReferenceCacheKey(primaryKey, object, writeLockValue, readTime);
    }

    /**
     * Return the linked reference cache.
     */
    public ExposedNodeLinkedList getReferenceCache() {
        return referenceCache;
    }

    /**
     * Allows subclass to create a SoftReference to the object.
     * @param object is the domain object to cache.
     */
    public Object buildReference(Object object) {
        return object;
    }

    /**
     * Checks if the object is null, or reference's object is null.
     * @param the object for hard or the reference for soft.
     */
    public boolean hasReference(Object reference) {
        return reference != null;
    }

    /**
     * Store the object in the cache with the cache key.
     * Also store the linked list node in the cache key.
     */
    protected void put(CacheKey cacheKey) {
        ReferenceCacheKey referenceCacheKey = (ReferenceCacheKey)cacheKey;
        LinkedNode node = null;
        synchronized (getReferenceCache()) {
            node = getReferenceCache().addFirst(buildReference(referenceCacheKey.getObject()));
        }
        referenceCacheKey.setReferenceCacheNode(node);
        super.put(cacheKey);
    }

    /**
     * Also insert the link if the cacheKey is put.
     */
    protected CacheKey getCacheKeyIfAbsentPut(CacheKey searchKey) {
        CacheKey cacheKey = super.getCacheKeyIfAbsentPut(searchKey);
        if (cacheKey == null) {
            ReferenceCacheKey referenceCacheKey = (ReferenceCacheKey)searchKey;
            LinkedNode node = null;
            synchronized (getReferenceCache()) {
                node = getReferenceCache().addFirst(buildReference(referenceCacheKey.getObject()));
            }
            referenceCacheKey.setReferenceCacheNode(node);
        }
        return cacheKey;
    }

    /**
     * Remove the cache key from the map and the sub-cache list.
     */
    public Object remove(CacheKey cacheKey) {
        if (cacheKey == null) {
            return null;
        }
        ReferenceCacheKey referenceCacheKey = (ReferenceCacheKey)cacheKey;
        synchronized (getReferenceCache()) {
            getReferenceCache().remove(referenceCacheKey.getReferenceCacheNode());
        }
        return super.remove(cacheKey);
    }

    /**
     * This method will be used to update the max cache size.
     */
    public synchronized void updateMaxSize(int maxSize) {
        setMaxSize(maxSize);
        synchronized (getReferenceCache()) {
            // Remove the LRU items if max size exceeded.
            while (getReferenceCache().size() > getMaxSize()) {
                getReferenceCache().removeLast();
            }
        }
    }

    /**
     * Inner class to define the specialized weak cache key.
     * Keeps track of the linked list node to allow quick repositioning.
     */
    public class ReferenceCacheKey extends WeakCacheKey {
        protected LinkedNode referenceNode;

        public ReferenceCacheKey(Vector primaryKey, Object object, Object writeLockValue, long readTime) {
            super(primaryKey, object, writeLockValue, readTime);
        }

        public LinkedNode getReferenceCacheNode() {
            return referenceNode;
        }

        public void setReferenceCacheNode(LinkedNode referenceNode) {
            this.referenceNode = referenceNode;
        }

        public ExposedNodeLinkedList getReferenceCache() {
            return referenceCache;
        }

        /**
         * Notifies that cache key that it has been accessed.
         * Allows the LRU sub-cache to be maintained,
         * the cache node must be moved to the front of the list.
         */
        public void updateAccess() {
            // PERF: Synchronize on the linked list.
            synchronized (getReferenceCache()) {
                // Check if the node's contents is null (was removed),
                // also the object is null on initial put of acquired cache key,
                // or ref value may have garbage collected so reset it.
                if (!hasReference(getReferenceCacheNode().getContents())) {
                    getReferenceCacheNode().setContents(buildReference(getObject()));
                }

                // This is a fast constant time operations because of the linked list usage.
                getReferenceCache().moveFirst(getReferenceCacheNode());
                // Remove the old LRU items if max size exceeded (if was removed).
                while (getReferenceCache().size() > getMaxSize()) {
                    getReferenceCache().removeLast();
                }
            }
        }
    }
}