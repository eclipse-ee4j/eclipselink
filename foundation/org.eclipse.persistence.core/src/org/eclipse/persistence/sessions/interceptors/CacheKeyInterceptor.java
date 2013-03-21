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
 *     Gordon Yorke - Interceptor feature https://bugs.eclipse.org/bugs/show_bug.cgi?id=219683
 ******************************************************************************/  
package org.eclipse.persistence.sessions.interceptors;

import org.eclipse.persistence.internal.identitymaps.AbstractIdentityMap;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.identitymaps.IdentityMap;
import org.eclipse.persistence.sessions.Record;

/**
 * The CacheKeyInterceptor allows a Cache Interceptor implementation to wrap the EclipseLink CacheKey.
 * The CacheKey is an object that wraps the object and maintains cached based information about the object
 * like primary key, write lock value and locking.  The EclipseLink runtime will access the CacheKey and directly
 * when releasing locks.
 * @author Gordon Yorke
 *
 */

public class CacheKeyInterceptor extends CacheKey{
        protected CacheKey wrappedKey;
        
        public CacheKeyInterceptor(CacheKey cacheKey){
            wrappedKey = cacheKey;
            this.isWrapper = true;
        }
        /**
         * Acquire the lock on the cache key object.
         */
        public void acquire() {
            wrappedKey.acquire();
        }

        /**
         * Acquire the lock on the cache key object. For the merge process
         * called with true from the merge process, if true then the refresh will not refresh the object
         */
        public void acquire(boolean forMerge) {
            wrappedKey.acquire(forMerge);
        }

        /**
         * Acquire the lock on the cache key object. But only if the object has no lock on it
         * Added for CR 2317
         */
        public boolean acquireNoWait() {
            return wrappedKey.acquireNoWait();
        }

        /**
         * Acquire the lock on the cache key object. Only acquire a lock if the cache key's
         * active thread is not set.
         * Added for Bug 5840635
         */

        public boolean acquireIfUnownedNoWait() {
            return wrappedKey.acquireIfUnownedNoWait();
        }

        /**
         * Acquire the lock on the cache key object. But only if the object has no lock on it
         * Added for CR 2317
         * called with true from the merge process, if true then the refresh will not refresh the object
         */
        public boolean acquireNoWait(boolean forMerge) {
            return wrappedKey.acquireNoWait(forMerge);
        }

        /**
         * Acquire the deferred lock.
         */
        public void acquireDeferredLock() {
            wrappedKey.acquireDeferredLock();
        }
        
        public void checkReadLock() {
            wrappedKey.checkReadLock();
        }
            
        public void checkDeferredLock() {
            wrappedKey.checkDeferredLock();
        }
        
        /**
         * Acquire the read lock on the cache key object.
         */
        public void acquireReadLock() {
            wrappedKey.acquireReadLock();
        }

        /**
         * Acquire the read lock on the cache key object.
         */
        public boolean acquireReadLockNoWait() {
            return wrappedKey.acquireReadLockNoWait();
        }

        /**
         * Return the active thread.
         */
        public Thread getActiveThread() {
            return wrappedKey.getActiveThread();
        }
        
        public Object clone() {
            return new CacheKeyInterceptor((CacheKey)wrappedKey.clone());
        }

        public boolean equals(CacheKey key) {
            if (key instanceof CacheKeyInterceptor){
                return wrappedKey.equals(((CacheKeyInterceptor)key).wrappedKey);
            }
            return wrappedKey.equals(key);
        }

        public long getLastUpdatedQueryId() {
            return wrappedKey.getLastUpdatedQueryId();
        }

        public Object getKey() {
            return wrappedKey.getKey();
        }

        public Object getObject() {
            return wrappedKey.getObject();
        }

        public IdentityMap getOwningMap(){
            return wrappedKey.getOwningMap();
        }
        
        /**
         * INTERNAL:
         * Return the current value of the Read Time variable
         */
        public long getReadTime() {
            return wrappedKey.getReadTime();
        }

        public Record getRecord() {
            return wrappedKey.getRecord();
        }

        /**
         * If a Wrapper subclasses this CacheKey this method will be used to unwrap the cache key.
         * @return
         */
        public CacheKey getWrappedCacheKey(){
            return this.wrappedKey;
        }

        public Object getWrapper() {
            return wrappedKey.getWrapper();
        }

        public Object getWriteLockValue() {
            return wrappedKey.getWriteLockValue();
        }

        public int hashCode() {
            return wrappedKey.hashCode();
        }

        public boolean isAcquired() {
            return wrappedKey.isAcquired();
        }

        public int getInvalidationState() {
            return wrappedKey.getInvalidationState();
        }

        /**
         * Release the lock on the cache key object.
         */
        public void release() {
            wrappedKey.release();
        }

        /**
         * Release the deferred lock
         */
        public void releaseDeferredLock() {
            wrappedKey.releaseDeferredLock();
        }

        /**
         * Release the read lock on the cache key object.
         */
        public void releaseReadLock() {
            wrappedKey.releaseReadLock();
        }

        /**
         * Removes this cacheKey from the owning map
         */
        public Object removeFromOwningMap(){
            if (this.wrappedKey.getOwningMap() != null){
                return getOwningMap().remove(this.wrappedKey);
            }
            return null;
        }

        public void setInvalidationState(int invalidationState) {
            wrappedKey.setInvalidationState(invalidationState);
        }

        public void setLastUpdatedQueryId(long id) {
            wrappedKey.setLastUpdatedQueryId(id);
        }

        public void setKey(Object key) {
            wrappedKey.setKey(key);
        }

        public void setObject(Object object) {
            wrappedKey.setObject(object);
        }

        public void setOwningMap(AbstractIdentityMap map){
            wrappedKey.setOwningMap(map);
        }
        
        public void setReadTime(long readTime) {
            wrappedKey.setReadTime(readTime);
        }

        public void setRecord(Record newRecord) {
            wrappedKey.setRecord(newRecord);
        }

        public void setWrapper(Object wrapper) {
            wrappedKey.setWrapper(wrapper);
        }

        public void setWriteLockValue(Object writeLockValue) {
            wrappedKey.setWriteLockValue(writeLockValue);
        }

        public String toString() {
            return "NamedCacheInterceptor wrapper : " + wrappedKey.toString();
        }

        public void updateAccess() {
            wrappedKey.updateAccess();
        }
    }
