/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Gordon Yorke - Interceptor feature https://bugs.eclipse.org/bugs/show_bug.cgi?id=219683
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
        @Override
        public void acquire() {
            wrappedKey.acquire();
        }

        /**
         * Acquire the lock on the cache key object. For the merge process
         * called with true from the merge process, if true then the refresh will not refresh the object
         */
        @Override
        public void acquire(boolean forMerge) {
            wrappedKey.acquire(forMerge);
        }

        /**
         * Acquire the lock on the cache key object. But only if the object has no lock on it
         * Added for CR 2317
         */
        @Override
        public boolean acquireNoWait() {
            return wrappedKey.acquireNoWait();
        }

        /**
         * Acquire the lock on the cache key object. Only acquire a lock if the cache key's
         * active thread is not set.
         * Added for Bug 5840635
         */

        @Override
        public boolean acquireIfUnownedNoWait() {
            return wrappedKey.acquireIfUnownedNoWait();
        }

        /**
         * Acquire the lock on the cache key object. But only if the object has no lock on it
         * Added for CR 2317
         * called with true from the merge process, if true then the refresh will not refresh the object
         */
        @Override
        public boolean acquireNoWait(boolean forMerge) {
            return wrappedKey.acquireNoWait(forMerge);
        }

        /**
         * Acquire the deferred lock.
         */
        @Override
        public void acquireDeferredLock() {
            wrappedKey.acquireDeferredLock();
        }

        @Override
        public void checkReadLock() {
            wrappedKey.checkReadLock();
        }

        @Override
        public void checkDeferredLock() {
            wrappedKey.checkDeferredLock();
        }

        /**
         * Acquire the read lock on the cache key object.
         */
        @Override
        public void acquireReadLock() {
            wrappedKey.acquireReadLock();
        }

        /**
         * Acquire the read lock on the cache key object.
         */
        @Override
        public boolean acquireReadLockNoWait() {
            return wrappedKey.acquireReadLockNoWait();
        }

        /**
         * Return the active thread.
         */
        @Override
        public Thread getActiveThread() {
            return wrappedKey.getActiveThread();
        }

        @Override
        public Object clone() {
            return new CacheKeyInterceptor((CacheKey)wrappedKey.clone());
        }

        @Override
        public boolean equals(CacheKey key) {
            if (key instanceof CacheKeyInterceptor){
                return wrappedKey.equals(((CacheKeyInterceptor)key).wrappedKey);
            }
            return wrappedKey.equals(key);
        }

        @Override
        public long getLastUpdatedQueryId() {
            return wrappedKey.getLastUpdatedQueryId();
        }

        @Override
        public Object getKey() {
            return wrappedKey.getKey();
        }

        @Override
        public Object getObject() {
            return wrappedKey.getObject();
        }

        @Override
        public IdentityMap getOwningMap(){
            return wrappedKey.getOwningMap();
        }

        /**
         * INTERNAL:
         * Return the current value of the Read Time variable
         */
        @Override
        public long getReadTime() {
            return wrappedKey.getReadTime();
        }

        @Override
        public Record getRecord() {
            return wrappedKey.getRecord();
        }

        /**
         * If a Wrapper subclasses this CacheKey this method will be used to unwrap the cache key.
         * @return
         */
        @Override
        public CacheKey getWrappedCacheKey(){
            return this.wrappedKey;
        }

        @Override
        public Object getWrapper() {
            return wrappedKey.getWrapper();
        }

        @Override
        public Object getWriteLockValue() {
            return wrappedKey.getWriteLockValue();
        }

        @Override
        public int hashCode() {
            return wrappedKey.hashCode();
        }

        @Override
        public boolean isAcquired() {
            return wrappedKey.isAcquired();
        }

        @Override
        public int getInvalidationState() {
            return wrappedKey.getInvalidationState();
        }

        /**
         * Release the lock on the cache key object.
         */
        @Override
        public void release() {
            wrappedKey.release();
        }

        /**
         * Release the deferred lock
         */
        @Override
        public void releaseDeferredLock() {
            wrappedKey.releaseDeferredLock();
        }

        /**
         * Release the read lock on the cache key object.
         */
        @Override
        public void releaseReadLock() {
            wrappedKey.releaseReadLock();
        }

        /**
         * Removes this cacheKey from the owning map
         */
        @Override
        public Object removeFromOwningMap(){
            if (this.wrappedKey.getOwningMap() != null){
                return getOwningMap().remove(this.wrappedKey);
            }
            return null;
        }

        @Override
        public void setInvalidationState(int invalidationState) {
            wrappedKey.setInvalidationState(invalidationState);
        }

        @Override
        public void setLastUpdatedQueryId(long id) {
            wrappedKey.setLastUpdatedQueryId(id);
        }

        @Override
        public void setKey(Object key) {
            wrappedKey.setKey(key);
        }

        @Override
        public void setObject(Object object) {
            wrappedKey.setObject(object);
        }

        public void setOwningMap(AbstractIdentityMap map){
            wrappedKey.setOwningMap(map);
        }

        @Override
        public void setReadTime(long readTime) {
            wrappedKey.setReadTime(readTime);
        }

        @Override
        public void setRecord(Record newRecord) {
            wrappedKey.setRecord(newRecord);
        }

        @Override
        public void setWrapper(Object wrapper) {
            wrappedKey.setWrapper(wrapper);
        }

        @Override
        public void setWriteLockValue(Object writeLockValue) {
            wrappedKey.setWriteLockValue(writeLockValue);
        }

        @Override
        public String toString() {
            return "NamedCacheInterceptor wrapper : " + wrappedKey.toString();
        }

        @Override
        public void updateAccess() {
            wrappedKey.updateAccess();
        }
    }
