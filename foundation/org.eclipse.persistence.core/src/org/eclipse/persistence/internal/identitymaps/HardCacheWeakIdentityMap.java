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
package org.eclipse.persistence.internal.identitymaps;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.linkedlist.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;

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

    public HardCacheWeakIdentityMap(int size, ClassDescriptor descriptor, AbstractSession session, boolean isIsolated) {
        super(size, descriptor, session, isIsolated);
        this.referenceCache = new ExposedNodeLinkedList();
    }

    /**
     * Use a ReferenceCacheKey that also stores the linked list node to manage
     * the LRU sub-cache of references.
     */
    @Override
    public CacheKey createCacheKey(Object primaryKey, Object object, Object writeLockValue, long readTime) {
        return new ReferenceCacheKey(primaryKey, object, writeLockValue, readTime, isIsolated);
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
     * Remove the cache key from the map and the sub-cache list.
     */
    public Object remove(CacheKey cacheKey) {
        if (cacheKey == null) {
            return null;
        }
        LinkedNode node = ((ReferenceCacheKey)cacheKey).getReferenceCacheNode();
        // Node is initially null while object is being built.
        if (node != null) {
            synchronized (this.referenceCache) {
                this.referenceCache.remove(node);
            }
        }
        return super.remove(cacheKey);
    }
    
    /**
     * Store the object in the cache at its primary key, and add to sub-cache list.
     */
    @Override
    public CacheKey put(Object primaryKey, Object object, Object writeLockValue, long readTime) {
        CacheKey cacheKey = super.put(primaryKey, object, writeLockValue, readTime);
        cacheKey.updateAccess();
        return cacheKey;
    }

    /**
     * This method will be used to update the max cache size.
     */
    public synchronized void updateMaxSize(int maxSize) {
        setMaxSize(maxSize);
        synchronized (this.referenceCache) {
            // Remove the LRU items if max size exceeded.
            while (this.referenceCache.size() > this.maxSize) {
                this.referenceCache.removeLast();
            }
        }
    }

    /**
     * Inner class to define the specialized weak cache key.
     * Keeps track of the linked list node to allow quick repositioning.
     */
    public class ReferenceCacheKey extends WeakCacheKey {
        protected LinkedNode referenceNode;

        public ReferenceCacheKey(Object primaryKey, Object object, Object writeLockValue, long readTime, boolean isIsolated) {
            super(primaryKey, object, writeLockValue, readTime, isIsolated);
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
            // Check if the node's contents is null (was removed),
            // or ref value may have garbage collected so reset it.
            if ((this.referenceNode != null) && (!hasReference(this.referenceNode.getContents()))) {
                this.referenceNode.setContents(buildReference(getObject()));
            }
            // PERF: Synchronize on the linked list.
            synchronized (referenceCache) {
                // If reference node is null, add to start (new cache key).
                if (this.referenceNode == null) {
                    this.referenceNode = referenceCache.addFirst(buildReference(getObject()));
                } else {
                    // This is a fast constant time operations because of the linked list usage.
                    referenceCache.moveFirst(getReferenceCacheNode());
                }
                // Remove the old LRU items if max size exceeded (if was removed).
                while (referenceCache.size() > maxSize) {
                    referenceCache.removeLast();
                }
            }
        }
    }
}
