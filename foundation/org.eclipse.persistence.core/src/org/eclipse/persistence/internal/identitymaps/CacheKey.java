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

import org.eclipse.persistence.exceptions.ConcurrencyException;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.sessions.Record;

/**
 * <p><b>Purpose</b>: Container class for storing objects in an IdentityMap.
 * <p><b>Responsibilities</b>:<ul>
 * <li> Hold key and object.
 * <li> Maintain and update the current writeLockValue.
 * </ul>
 * @since TOPLink/Java 1.0
 */
public class CacheKey extends ConcurrencyManager implements Cloneable {

    /** The key holds the vector of primary key values for the object. */
    protected Object key;

    protected Object object;
    
    //used to store a reference to the map this cachekey is in in cases where the
    //cache key is to be removed, prevents us from having to track down the owning
    //map
    protected IdentityMap mapOwner;

    /** The writeLock value is being held as an object so that it might contain a number or timestamp. */
    protected Object writeLockValue;

    /** The cached wrapper for the object, used in EJB. */
    protected Object wrapper;

    /** This is used for Document Preservation to cache the record that this object was built from */
    protected Record record;

    /** This attribute is the system time in milli seconds that the object was last refreshed on */

    //CR #4365 
    // CR #2698903 - fix for the previous fix. No longer using millis.
    protected long lastUpdatedQueryId;

    /** Invalidation State can be used to indicate whether this cache key is considered valid */
    protected int invalidationState = CHECK_INVALIDATION_POLICY;

    /** The following constants are used for the invalidationState variable */
    public static final int CHECK_INVALIDATION_POLICY = 0;
    public static final int CACHE_KEY_INVALID = -1;
    
    public static final int MAX_WAIT_TRIES = 10000;

    /** The read time stores the millisecond value of the last time the object help by
    this cache key was confirmed as up to date. */
    protected long readTime = 0;
    
    /**
     * Stores if this CacheKey instance is a wrapper for the underlying CacheKey.  CacheKey wrappers
     * may be used with cache interceptors.
     */
    protected boolean isWrapper = false;
    
    /**
     * Stores retrieved FK values for relationships that are not stored in the Entity
     */
    protected AbstractRecord protectedForeignKeys;
    
    /**
     * Set to true if this CacheKey comes from an IsolatedClientSession, or DatabaseSessionImpl.
     */
    protected boolean isIsolated;
        
    /**
     * The ID of the database transaction that last wrote the object.
     * This is used for database change notification.
     */
    protected Object transactionId;

    /**
     * Internal:
     * Only used by subclasses that may want to wrap the cache key.  Could be replaced
     * by switching to an interface.
     */
    protected CacheKey(){
    }
    
    public CacheKey(Object primaryKey) {
        this.key = primaryKey;
    }

    public CacheKey(Object primaryKey, Object object, Object lockValue) {
        this.key = primaryKey;
        this.writeLockValue = lockValue;
        //bug4649617  use setter instead of this.object = object to avoid hard reference on object in subclasses
        if (object != null) {
            setObject(object);
        }
    }

    public CacheKey(Object primaryKey, Object object, Object lockValue, long readTime, boolean isIsolated) {
        this.key = primaryKey;
        this.writeLockValue = lockValue;
        //bug4649617  use setter instead of this.object = object to avoid hard reference on object in subclasses
        if (object != null) {
            setObject(object);
        }
        this.readTime = readTime;
        this.isIsolated = isIsolated;
    }

    /**
     * Acquire the lock on the cache key object.
     */
    public void acquire() {
        if (this.isIsolated) {
            this.depth++;
            return;
        }
        super.acquire(false);
    }

    /**
     * Acquire the lock on the cache key object. For the merge process
     * called with true from the merge process, if true then the refresh will not refresh the object
     */
    public void acquire(boolean forMerge) {
        if (this.isIsolated) {
            this.depth++;
            return;
        }
        super.acquire(forMerge);
    }

    /**
     * Acquire the lock on the cache key object. But only if the object has no lock on it
     * Added for CR 2317
     */
    public boolean acquireNoWait() {
        if (this.isIsolated) {
            this.depth++;
            return true;
        }
        return super.acquireNoWait(false);
    }

    /**
     * Acquire the lock on the cache key object. Only acquire a lock if the cache key's
     * active thread is not set.
     * Added for Bug 5840635
     */

    public boolean acquireIfUnownedNoWait() {
        if (this.isIsolated) {
            if (this.depth > 0) {
                return false;
            }
            this.depth++;
            return true;
        }
        return super.acquireIfUnownedNoWait(false);
    }

    /**
     * Acquire the lock on the cache key object. But only if the object has no lock on it
     * Added for CR 2317
     * called with true from the merge process, if true then the refresh will not refresh the object
     */
    public boolean acquireNoWait(boolean forMerge) {
        if (this.isIsolated) {
            this.depth++;
            return true;
        }
        return super.acquireNoWait(forMerge);
    }

    /**
     * Acquire the lock on the cache key object. But only if the object has no lock on it
     * Added for CR 2317
     * called with true from the merge process, if true then the refresh will not refresh the object
     */
    public boolean acquireWithWait(boolean forMerge, int wait) {
        if (this.isIsolated) {
            this.depth++;
            return true;
        }
        return super.acquireWithWait(forMerge, wait);
    }

    /**
     * Acquire the deferred lock.
     */
    public void acquireDeferredLock() {
        if (this.isIsolated) {
            this.depth++;
            return;
        }
        super.acquireDeferredLock();
    }
    
    /**
     * Check the read lock on the cache key object.
     * This can be called to ensure the cache key has a valid built object.
     * It does not hold a lock, so the object could be refreshed afterwards.
     */
    public void checkReadLock() {
        if (this.isIsolated) {
            return;
        }
        super.checkReadLock();
    }
        
    /**
     * Check the deferred lock on the cache key object.
     * This can be called to ensure the cache key has a valid built object.
     * It does not hold a lock, so the object could be refreshed afterwards.
     */
    public void checkDeferredLock() {
        if (this.isIsolated) {
            return;
        }
        super.checkDeferredLock();
    }
    
    /**
     * Acquire the read lock on the cache key object.
     */
    public void acquireReadLock() {
        if (this.isIsolated) {
            return;
        }
        super.acquireReadLock();
    }

    /**
     * Acquire the read lock on the cache key object.  Return true if acquired.
     */
    public boolean acquireReadLockNoWait() {
        if (this.isIsolated) {
            return true;
        }
        return super.acquireReadLockNoWait();
    }

    /**
     * INTERNAL:
     * Clones itself.
     */
    public Object clone() {
        Object object = null;

        try {
            object = super.clone();
        } catch (Exception exception) {
            throw new InternalError(exception.toString());
        }

        return object;
    }

    /**
     * Determine if the receiver is equal to anObject.
     * If anObject is a CacheKey, do further comparison, otherwise, return false.
     * @see CacheKey#equals(CacheKey)
     */
    public boolean equals(Object object) {
        try {
            return equals((CacheKey)object);
        } catch (ClassCastException incorrectType) {
            return false;
        }
    }

    /**
     * Determine if the receiver is equal to key.
     * Use an index compare, because it is much faster than enumerations.
     */
    public boolean equals(CacheKey key) {
        if (this == key) {
            return true;
        }
        return this.key.equals(key.key);
    }

    /**
     * INTERNAL:
     * This method returns the system time in millis seconds at which this object was last refreshed
     * CR #4365
     * CR #2698903 ... instead of using millis we will now use id's instead. Method
     * renamed appropriately.
     */
    public long getLastUpdatedQueryId() {
        return this.lastUpdatedQueryId;
    }

    public Object getKey() {
        return key;
    }

    /**
     * Return the active thread.
     */
    public Thread getActiveThread() {
        if (this.isIsolated) {
            if (this.depth > 0) {
                return Thread.currentThread();
            } else {
                return null;
            }
        }
        return super.getActiveThread();
    }

    public Object getObject() {
        return object;
    }

    public IdentityMap getOwningMap(){
        return this.mapOwner;
    }
    
    /**
     * INTERNAL:
     * Return the current value of the Read Time variable
     */
    public long getReadTime() {
        return readTime;
    }

    public Record getRecord() {
        return record;
    }

    public Object getWrapper() {
        return wrapper;
    }
    
    /**
     * If a Wrapper subclasses this CacheKey this method will be used to unwrap the cache key.
     * @return
     */
    public CacheKey getWrappedCacheKey(){
        return this;
    }

    public Object getWriteLockValue() {
        return writeLockValue;
    }

    /**
     * Overrides hashCode() in Object to use the primaryKey's hashCode for storage in data structures.
     */
    public int hashCode() {
        return this.key.hashCode();
    }

    /**
     * Returns true if this CacheKey is from an IsolatedClientSession
     */
    public boolean isIsolated() {
        return isIsolated;
    }

    /**
     * Returns true if this Instance of CacheKey is a wrapper and should be unwrapped before passing
     * to IdentityMap APIs.  Wrapped CacheKeys may be used in the Cache Interceptors.
     */
    public boolean isWrapper(){
        return this.isWrapper;
    }
    
    /**
     * INTERNAL:
     * Return the FK cache
     */
    public AbstractRecord getProtectedForeignKeys(){
        if (this.protectedForeignKeys == null){
            this.protectedForeignKeys = new DatabaseRecord();
        }
        return this.protectedForeignKeys;
    }
    
    /**
     * INTERNAL:
     * Return the value of the invalidationState Variable
     * The return value will be a constant
     * CHECK_INVALIDATION_POLICY - The Invalidation policy is must be checked for this cache key's sate
     * CACHE_KEY_INVALID - This cache key has been labeled invalid.
     */
    public int getInvalidationState() {
        return invalidationState;
    }

    /**
     * Release the lock on the cache key object.
     */
    public void release() {
        if (this.isIsolated) {
            this.depth--;
            return;
        }
        super.release();
    }

    /**
     * Release the deferred lock
     */
    public void releaseDeferredLock() {
        if (this.isIsolated) {
            this.depth--;
            return;
        }
        super.releaseDeferredLock();
    }

    /**
     * Release the read lock on the cache key object.
     */
    public void releaseReadLock() {
        if (this.isIsolated) {
            return;
        }
        super.releaseReadLock();
    }

    /**
     * Removes this cacheKey from the owning map
     */
    public Object removeFromOwningMap(){
        if (getOwningMap() != null){
            return getOwningMap().remove(this);
        }
        return null;
    }
    
    /**
     * INTERNAL:
     * Set the value of the invalidationState Variable
     * The possible values are from an enumeration of constants
     * CHECK_INVALIDATION_POLICY - The invalidation policy is must be checked for this cache key's sate
     * CACHE_KEY_INVALID - This cache key has been labelled invalid.
     */
    public void setInvalidationState(int invalidationState) {
        this.invalidationState = invalidationState;
    }

    /**
     * INTERNAL:
     * This method sets the system time in millis seconds at which this object was last refreshed
     * CR #4365
     * CR #2698903 ... instead of using millis we will now use ids instead. Method
     * renamed appropriately.
     */
    public void setLastUpdatedQueryId(long id) {
        this.lastUpdatedQueryId = id;
    }

    public void setKey(Object key) {
        this.key = key;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public void setOwningMap(IdentityMap map){
        this.mapOwner = map;
    }
    
    public void setProtectedForeignKeys(AbstractRecord protectedForeignKeys) {
        this.protectedForeignKeys = protectedForeignKeys;
    }

    /**
     * INTERNAL:
     * Set the read time of this cache key
     */
    public void setReadTime(long readTime) {
        this.readTime = readTime;
        invalidationState = CHECK_INVALIDATION_POLICY;
    }

    public void setRecord(Record newRecord) {
        this.record = newRecord;
    }

    public void setWrapper(Object wrapper) {
        this.wrapper = wrapper;
    }

    public void setWriteLockValue(Object writeLockValue) {
        this.writeLockValue = writeLockValue;
    }

    public String toString() {
        int hashCode = 0;
        if (getObject() != null) {
            hashCode = getObject().hashCode();
        }

        return "[" + getKey() + ": " + hashCode + ": " + getWriteLockValue() + ": " + getReadTime() + ": " + getObject() + "]";
    }

    /**
     * Notifies that cache key that it has been accessed.
     * Allows the LRU sub-cache to be maintained.
     */
    public void updateAccess() {
        // Nothing required by default.
    }

    public void setIsolated(boolean isIsolated) {
        this.isIsolated = isIsolated;
    }

    public void setIsWrapper(boolean isWrapper) {
        this.isWrapper = isWrapper;
    }
    
    public Object getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Object transactionId) {
        this.transactionId = transactionId;
    }
    
    public synchronized Object waitForObject(){
        try {
            int count = 0;
            while (this.object == null && isAcquired()) {
                if (count > MAX_WAIT_TRIES)
                    throw ConcurrencyException.maxTriesLockOnBuildObjectExceded(getActiveThread(), Thread.currentThread());
                wait(10);
                ++count;
            }
        } catch(InterruptedException ex) {
            //ignore as the loop is broken
        }
        return this.object;
    }
}
