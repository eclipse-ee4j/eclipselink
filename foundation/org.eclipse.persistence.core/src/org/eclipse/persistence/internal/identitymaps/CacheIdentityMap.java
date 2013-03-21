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
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * <p><b>Purpose</b>: A fixed size LRU cache<p>
 * Using a linked list as well as the map from the superclass a LRU cache is maintained.
 * When a get is executed the LRU list is updated and when a new object is inserted the object
 * at the start of the list is deleted (provided the maxSize has been reached).
 * <p><b>Responsibilities</b>:<ul>
 *    <li> Guarantees identity through primary key values
 *    <li> Keeps the LRU linked list updated.
 * </ul>
 * @since TOPLink/Java 1.0
 */
public class CacheIdentityMap extends FullIdentityMap {

    /** Provide handles on the linked list */
    protected LinkedCacheKey first;

    /** Provide handles on the linked list */
    protected LinkedCacheKey last;

    public CacheIdentityMap(int size, ClassDescriptor descriptor, AbstractSession session, boolean isolated) {
        super(size, descriptor, session, isolated);
        this.first = new LinkedCacheKey(CacheId.EMPTY, null, null, 0, isIsolated);
        this.last = new LinkedCacheKey(CacheId.EMPTY, null, null, 0, isIsolated);
        this.first.setNext(this.last);
        this.last.setPrevious(this.first);
    }
    
    @Override
    public CacheKey createCacheKey(Object primaryKey, Object object, Object writeLockValue, long readTime) {
        return new LinkedCacheKey(primaryKey, object, writeLockValue, readTime, isIsolated);
    }

    /**
     * Reduces the size of the receiver down to the maxSize removing objects from the
     * start of the linked list.
     */
    protected void ensureFixedSize() {
        // protect the case where someone attempts to break the cache by
        // setting max size to 0.
        synchronized(this.first) {
            while (getMaxSize() > 0 && getSize() > getMaxSize()) {
                remove(last.getPrevious());
            }
        }
    }

    /**
     * Access the object within the table for the given primaryKey.
     * Move the accessed key to the top of the order keys linked list to maintain LRU.
     * @param primaryKeys is the primary key for the object to search for.
     * @return the LinkedCacheKey or null if none found for primaryKey
     */
    @Override
    public CacheKey getCacheKey(Object primaryKeys, boolean forMerge) {
        LinkedCacheKey cacheKey = (LinkedCacheKey)super.getCacheKey(primaryKeys, forMerge);
        if (cacheKey != null) {
            synchronized (this.first) {
                removeLink(cacheKey);
                insertLink(cacheKey);
            }
        }
        return cacheKey;
    }

    /**
     * Insert a new element into the linked list of LinkedCacheKeys.
     * New elements (Recently Used) are added at the end (last).
     * @return the added LinkedCacheKey
     */
    protected LinkedCacheKey insertLink(LinkedCacheKey key) {
        if (key == null){
            return key;
        }
        // No sence on locking the entire cache, just lock on the list.
        synchronized (this.first){
            this.first.getNext().setPrevious(key);
            key.setNext(this.first.getNext());
            key.setPrevious(this.first);
            this.first.setNext(key);
        }
        return key;
    }

    /**
     * Also insert the link if the cacheKey is put.
     */
    protected CacheKey putCacheKeyIfAbsent(CacheKey searchKey) {
        CacheKey cacheKey = super.putCacheKeyIfAbsent(searchKey);
        if (cacheKey == null) {
            insertLink((LinkedCacheKey)searchKey);
            ensureFixedSize();
        }
        return cacheKey;
    }

    /**
     * Remove the LinkedCacheKey from the cache as well as from the linked list.
     * @return the LinkedCacheKey to be removed.
     */
    public Object remove(CacheKey key) {
        super.remove(key);
        // The key may be null if was missing, just null should be returned in this case.
        if (key == null) {
            return null;
        }
        return removeLink((LinkedCacheKey)key).getObject();
    }

    /**
     * Remove the LinkedCacheKey from the linked list.
     * @return the removed LinkedCacheKey.
     */
    protected LinkedCacheKey removeLink(LinkedCacheKey key) {
        if (key == null){
            return key;
        }
        synchronized (this.first) {
            if (key.getPrevious() == null || key.getNext() == null){
                //already removed by a competing thread, just return
                return key;
            }
            key.getPrevious().setNext(key.getNext());
            key.getNext().setPrevious(key.getPrevious());
            key.setNext(null);
            key.setPrevious(null);
        }
        return key;
    }

    /**
     * INTERNAL:
     * This method will be used to update the max cache size, any objects exceeding the max cache size will
     * be remove from the cache. Please note that this does not remove the object from the identityMap, except in
     * the case of the CacheIdentityMap.
     */
    public synchronized void updateMaxSize(int maxSize) {
        setMaxSize(maxSize);
        ensureFixedSize();
    }
}
