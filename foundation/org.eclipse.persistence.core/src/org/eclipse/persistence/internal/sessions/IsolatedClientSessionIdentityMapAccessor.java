/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.internal.sessions;

import java.util.*;
import org.eclipse.persistence.internal.identitymaps.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.internal.helper.WriteLockManager;

/**
 * INTERNAL:
 * Internal subclass that provides access to identity maps through the session.
 * Implements the IdentityMapAccessor interface which provides all publicly available
 * identity map functionality to users.
 * This is the main class that should be used to access identity maps.  In general, any
 * function that accesses the identity map manager should go through this class
 * Any session specific functionality appears in subclasses
 */
public class IsolatedClientSessionIdentityMapAccessor extends org.eclipse.persistence.internal.sessions.IdentityMapAccessor {

    /**
     * INTERNAL:
     * An IdentityMapAccessor sits between the session and the identityMapManager
     * It needs references in both directions
     */
    public IsolatedClientSessionIdentityMapAccessor(AbstractSession session) {
        super(session);
    }

    /**
     * INTERNAL:
     * Deferred lock the identity map for the object, this is used for avoiding deadlock
     * The return cacheKey should be used to release the deferred lock
     */
    @Override
    public CacheKey acquireDeferredLock(Object primaryKey, Class javaClass, ClassDescriptor descriptor) {
        if (descriptor.isIsolated()) {
            return getIdentityMapManager().acquireDeferredLock(primaryKey, javaClass, descriptor);
        } else {
            return ((IsolatedClientSession)session).getParent().getIdentityMapAccessorInstance().acquireDeferredLock(primaryKey, javaClass, descriptor);
        }
    }

    /**
     * INTERNAL:
     * Provides access for setting a concurrency lock on an object in the IdentityMap.
     * called with true from the merge process, if true then the refresh will not refresh the object.
     */
    @Override
    public CacheKey acquireLock(Object primaryKey, Class domainClass, boolean forMerge, ClassDescriptor descriptor) {
        if (descriptor.isIsolated()) {
            return getIdentityMapManager().acquireLock(primaryKey, domainClass, forMerge, descriptor);
        } else {
            return ((IsolatedClientSession)session).getParent().getIdentityMapAccessorInstance().acquireLock(primaryKey, domainClass, forMerge, descriptor);
        }
    }

    /**
     * INTERNAL:
     * Provides access for setting a concurrency lock on an object in the IdentityMap.
     * called with true from the merge process, if true then the refresh will not refresh the object.
     */
    @Override
    public CacheKey acquireLockNoWait(Object primaryKey, Class domainClass, boolean forMerge, ClassDescriptor descriptor) {
        if (descriptor.isIsolated()) {
            return getIdentityMapManager().acquireLockNoWait(primaryKey, domainClass, forMerge, descriptor);
        } else {
            return ((IsolatedClientSession)session).getParent().getIdentityMapAccessorInstance().acquireLockNoWait(primaryKey, domainClass, forMerge, descriptor);
        }
    }

    /**
     * INTERNAL:
     * Provides access for setting a concurrency lock on an object in the IdentityMap.
     * called with true from the merge process, if true then the refresh will not refresh the object.
     */
    @Override
    public CacheKey acquireLockWithWait(Object primaryKey, Class domainClass, boolean forMerge, ClassDescriptor descriptor, int wait) {
        if (descriptor.isIsolated()) {
            return getIdentityMapManager().acquireLockWithWait(primaryKey, domainClass, forMerge, descriptor, wait);
        } else {
            return ((IsolatedClientSession)session).getParent().getIdentityMapAccessorInstance().acquireLockWithWait(primaryKey, domainClass, forMerge, descriptor, wait);
        }
    }

    /**
     * INTERNAL:
     * Find the cachekey for the provided primary key and place a readlock on it.
     * This will allow multiple users to read the same object but prevent writes to
     * the object while the read lock is held.
     */
    @Override
    public CacheKey acquireReadLockOnCacheKey(Object primaryKey, Class domainClass, ClassDescriptor descriptor) {
        if (descriptor.isIsolated()) {
            return getIdentityMapManager().acquireReadLockOnCacheKey(primaryKey, domainClass, descriptor);
        } else {
            return ((IsolatedClientSession)session).getParent().getIdentityMapAccessorInstance().acquireReadLockOnCacheKey(primaryKey, domainClass, descriptor);
        }
    }

    /**
     * INTERNAL:
     * Find the cachekey for the provided primary key and place a readlock on it.
     * This will allow multiple users to read the same object but prevent writes to
     * the object while the read lock is held.
     * If no readlock can be acquired then do not wait but return null.
     */
    @Override
    public CacheKey acquireReadLockOnCacheKeyNoWait(Object primaryKey, Class domainClass, ClassDescriptor descriptor) {
        if (descriptor.isIsolated()) {
            return getIdentityMapManager().acquireReadLockOnCacheKeyNoWait(primaryKey, domainClass, descriptor);
        } else {
            return ((IsolatedClientSession)session).getParent().getIdentityMapAccessorInstance().acquireReadLockOnCacheKeyNoWait(primaryKey, domainClass, descriptor);
        }
    }

    /**
    * INTERNAL:
    * Lock the entire cache if the cache isolation requires.
    * By default concurrent reads and writes are allowed.
    * By write, unit of work merge is meant.
    */
    @Override
    public boolean acquireWriteLock() {
        getIdentityMapManager().acquireWriteLock();
        // must lock the parents cache as well.
        return ((IsolatedClientSession)session).getParent().getIdentityMapAccessorInstance().acquireWriteLock();
    }

    /**
     * ADVANCED:
     * Return if their is an object for the primary key.
     */
    @Override
    public boolean containsObjectInIdentityMap(Object primaryKey, Class theClass, ClassDescriptor descriptor) {
        if (descriptor.isIsolated()) {
            if (this.identityMapManager == null) {
                return false;
            }
            return getIdentityMapManager().containsKey(primaryKey, theClass, descriptor);
        } else {
            return ((IsolatedClientSession)session).getParent().getIdentityMapAccessorInstance().containsObjectInIdentityMap(primaryKey, theClass, descriptor);
        }
    }

    /**
     * INTERNAL:
     * This method is used to get a list of those classes with IdentityMaps in the Session.
     */
    @Override
    public Vector getClassesRegistered() {
        Vector results = getIdentityMapManager().getClassesRegistered();
        results.addAll(((IsolatedClientSession)session).getParent().getIdentityMapAccessorInstance().getClassesRegistered());
        return results;
    }

    /**
     * ADVANCED:
     * Query the cache in-memory.
     * If the expression is too complex an exception will be thrown.
     * Only return objects that are invalid in the cache if specified.
     */
    @Override
    public Vector getAllFromIdentityMap(Expression selectionCriteria, Class theClass, Record translationRow, int valueHolderPolicy, boolean shouldReturnInvalidatedObjects) throws QueryException {
        if (session.getDescriptor(theClass).isIsolated()) {
            return getIdentityMapManager().getAllFromIdentityMap(selectionCriteria, theClass, translationRow, valueHolderPolicy, shouldReturnInvalidatedObjects);
        } else {
            return ((IsolatedClientSession)session).getParent().getIdentityMapAccessorInstance().getAllFromIdentityMap(selectionCriteria, theClass, translationRow, valueHolderPolicy, shouldReturnInvalidatedObjects);
        }
    }

    /**
     * INTERNAL:
     * Retrieve the cache key for the given identity information.
     * @param primaryKey the primary key of the cache key to be retrieved.
     * @param myClass the class of the cache key to be retrieved.
     */
    @Override
    public CacheKey getCacheKeyForObject(Object primaryKey, Class myClass, ClassDescriptor descriptor) {
        if (descriptor.isIsolated()) {
            if (this.identityMapManager == null) {
                return null;
            }
            return getIdentityMapManager().getCacheKeyForObject(primaryKey, myClass, descriptor);
        } else {
            return ((IsolatedClientSession)session).getParent().getIdentityMapAccessorInstance().getCacheKeyForObject(primaryKey, myClass, descriptor);
        }
    }

    /**
     * INTERNAL:
     * Retrieve the cache key for the given identity information.
     * @param primaryKey the primary key of the cache key to be retrieved.
     * @param myClass the class of the cache key to be retrieved.
     */
    @Override
    public CacheKey getCacheKeyForObjectForLock(Object primaryKey, Class myClass, ClassDescriptor descriptor) {
        if (descriptor.isIsolated()) {
            if (this.identityMapManager == null) {
                return null;
            }
            return getIdentityMapManager().getCacheKeyForObjectForLock(primaryKey, myClass, descriptor);
        } else {
            return ((IsolatedClientSession)session).getParent().getIdentityMapAccessorInstance().getCacheKeyForObjectForLock(primaryKey, myClass, descriptor);
        }
    }

    /**
     * ADVANCED:
     * Return the object from the identity with the primary and class.
     */
    @Override
    public Object getFromIdentityMap(Object primaryKey, Class theClass, boolean shouldReturnInvalidatedObjects, ClassDescriptor descriptor) {
        if (descriptor.isIsolated()) {
            if (this.identityMapManager == null) {
                return null;
            }
            return getIdentityMapManager().getFromIdentityMap(primaryKey, theClass, shouldReturnInvalidatedObjects, descriptor);
        } else {
            return ((IsolatedClientSession)session).getParent().getIdentityMapAccessorInstance().getFromIdentityMap(primaryKey, theClass, shouldReturnInvalidatedObjects, descriptor);
        }
    }
    
    /**
     * INTERNAL:
     * Return the object from the local identity map with the primary and class.
     * This avoids checking the parent cache for the unit of work.
     */
    public Object getFromLocalIdentityMap(Object primaryKey, Class theClass, boolean shouldReturnInvalidatedObjects, ClassDescriptor descriptor) {
        return getFromIdentityMap(primaryKey, theClass, shouldReturnInvalidatedObjects, descriptor);
    }

    /**
     * INTERNAL:
     * Query the cache in-memory.
     * If the object is not found null is returned.
     * If the expression is too complex an exception will be thrown.
     */
    @Override
    public Object getFromIdentityMap(Expression selectionCriteria, Class theClass, Record translationRow, int valueHolderPolicy, boolean conforming, boolean shouldReturnInvalidatedObjects, ClassDescriptor descriptor) {
        if (descriptor.isIsolated()) {
            return getIdentityMapManager().getFromIdentityMap(selectionCriteria, theClass, translationRow, valueHolderPolicy, conforming, shouldReturnInvalidatedObjects, descriptor);
        } else {
            return ((IsolatedClientSession)session).getParent().getIdentityMapAccessorInstance().getFromIdentityMap(selectionCriteria, theClass, translationRow, valueHolderPolicy, conforming, shouldReturnInvalidatedObjects, descriptor);
        }
    }

    /**
     * INTERNAL:
     * Return the object from the identity with the primary and class.
     * Only return invalidated objects if requested
     */
    @Override
    public Object getFromIdentityMapWithDeferredLock(Object primaryKey, Class theClass, boolean shouldReturnInvalidatedObjects, ClassDescriptor descriptor) {
        if (descriptor.isIsolated()) {
            if (this.identityMapManager == null) {
                return null;
            }
            return getIdentityMapManager().getFromIdentityMapWithDeferredLock(primaryKey, theClass, shouldReturnInvalidatedObjects, descriptor);
        } else {
            return ((IsolatedClientSession)session).getParent().getIdentityMapAccessorInstance().getFromIdentityMapWithDeferredLock(primaryKey, theClass, shouldReturnInvalidatedObjects, descriptor);
        }
    }

    /**
     * INTERNAL:
     * Get the IdentityMapManager for this IdentityMapAccessor
     * This method should be used for all IdentityMapManager access since it may
     * be overridden in sub classes.
     */
    @Override
    public IdentityMapManager getIdentityMapManager() {
        // PERF: Lazy init manager as normally isolated object are only read in the unit of work.
        if (this.identityMapManager == null) {
            this.identityMapManager = new IdentityMapManager(this.session);
        }
        return this.identityMapManager;
    }

    /**
     * INTERNAL:
     * Get the identity map for the given class from the IdentityMapManager
     */
    @Override
    public IdentityMap getIdentityMap(ClassDescriptor descriptor, boolean returnNullIfMissing) {
        if (descriptor.isIsolated()) {
            return getIdentityMapManager().getIdentityMap(descriptor, returnNullIfMissing);
        } else {
            return ((IsolatedClientSession)session).getParent().getIdentityMapAccessorInstance().getIdentityMap(descriptor, returnNullIfMissing);
        }
    }

    /**
     * INTERNAL:
     * Get the cached results associated with a query.  Results are cached by the
     * values of the parameters to the query so different parameters will have
     * different cached results.
     */
    @Override
    public Object getQueryResult(ReadQuery query, List parameters, boolean checkExpiry) {
        if (((IsolatedClientSession)session).isIsolatedQuery(query)) {
            return getIdentityMapManager().getQueryResult(query, parameters, checkExpiry);
        } else {
            return ((IsolatedClientSession)session).getParent().getIdentityMapAccessorInstance().getQueryResult(query, parameters, checkExpiry);
        }
    }

    /**
     * INTERNAL:
     * get the session associated with this IdentityMapAccessor
     */
    @Override
    public AbstractSession getSession() {
        return session;
    }

    /**
     * INTERNAL:
     * Get the wrapper object from the cache key associated with the given primary key,
     * this is used for EJB.
     */
    @Override
    public Object getWrapper(Object primaryKey, Class theClass) {
        if (session.getDescriptor(theClass).isIsolated()) {
            return getIdentityMapManager().getWrapper(primaryKey, theClass);
        } else {
            return ((IsolatedClientSession)session).getParent().getIdentityMapAccessorInstance().getWrapper(primaryKey, theClass);
        }
    }

    /**
     * INTERNAL:
     * Returns the single write Lock manager for this session
     */
    public WriteLockManager getWriteLockManager() {
        // As there should only be one write lock manager per server session
        // get the one from the parent.
        return ((IsolatedClientSession)session).getParent().getIdentityMapAccessorInstance().getWriteLockManager();
    }

    /**
     * ADVANCED:
     * Extract the write lock value from the identity map.
     */
    @Override
    public Object getWriteLockValue(Object primaryKey, Class theClass, ClassDescriptor descriptor) {
        if (descriptor.isIsolated()) {
            return getIdentityMapManager().getWriteLockValue(primaryKey, theClass, descriptor);
        } else {
            return ((IsolatedClientSession)session).getParent().getIdentityMapAccessorInstance().getWriteLockValue(primaryKey, theClass, descriptor);
        }
    }

    /**
     * PUBLIC:
     * Reset the entire object cache.
     * <p> NOTE: be careful using this method. This method blows away both this session's and its parents caches,
     * this includes the server cache or any other cache. This throws away any objects that have been read in.
     * Extream caution should be used before doing this because object identity will no longer
     * be maintained for any objects currently read in.  This should only be called
     * if the application knows that it no longer has references to object held in the cache.
     */
    @Override
    public void initializeAllIdentityMaps() {
        super.initializeAllIdentityMaps();
        ((IsolatedClientSession)session).getParent().getIdentityMapAccessorInstance().initializeAllIdentityMaps();
    }

    /**
     * PUBLIC:
     * Reset the identity map for only the instances of the class.
     * For inheritance the user must make sure that they only use the root class.
     * Caution must be used in doing this to ensure that the objects within the identity map
     * are not referenced from other objects of other classes or from the application.
     */
    @Override
    public void initializeIdentityMap(Class theClass) {
        getSession().log(SessionLog.FINER, SessionLog.CACHE, "initialize_identitymap", theClass);
        if (session.getDescriptor(theClass).isIsolated()) {
            getIdentityMapManager().initializeIdentityMap(theClass);
        } else {
            ((IsolatedClientSession)session).getParent().getIdentityMapAccessorInstance().initializeIdentityMap(theClass);
        }
    }

    /**
     * PUBLIC:
     * Reset the entire local object cache.
     * This throws away any objects that have been read in.
     * Extreme caution should be used before doing this because object identity will no longer
     * be maintained for any objects currently read in.  This should only be called
     * if the application knows that it no longer has references to object held in the cache.
     */
    @Override
    public void initializeIdentityMaps() {
        getSession().log(SessionLog.FINER, SessionLog.CACHE, "initialize_identitymaps");
        getIdentityMapManager().initializeIdentityMaps();
        getSession().getCommitManager().reinitialize();
    }

    /**
     * INTERNAL:
     * Set the results for a query.
     * Query results are cached based on the parameter values provided to the query
     * different parameter values access different caches.
     */
    @Override
    public void putQueryResult(ReadQuery query, List parameters, Object results) {
        if (((IsolatedClientSession)session).isIsolatedQuery(query)) {
            getIdentityMapManager().putQueryResult(query, parameters, results);
        } else {
            ((IsolatedClientSession)session).getParent().getIdentityMapAccessorInstance().putQueryResult(query, parameters, results);
        }
    }

    /**
     * PUBLIC:
     * Used to print all the objects in the identity map of the passed in class.
     * The output of this method will be logged to this session's SessionLog at SEVERE level.
     */
    @Override
    public void printIdentityMap(Class businessClass) {
        if (getSession().shouldLog(SessionLog.SEVERE, SessionLog.CACHE)) {
            if (session.getDescriptor(businessClass).isIsolated()) {
                getIdentityMapManager().printIdentityMap(businessClass);
            } else {
                ((IsolatedClientSession)session).getParent().getIdentityMapAccessorInstance().printIdentityMap(businessClass);
            }
        }
    }

    /**
     * PUBLIC:
     * Used to print all the objects in every identity map in this session.
     * The output of this method will be logged to this session's SessionLog at SEVERE level.
     */
    @Override
    public void printIdentityMaps() {
        if (getSession().shouldLog(SessionLog.SEVERE, SessionLog.CACHE)) {
            getIdentityMapManager().printIdentityMaps();
            ((IsolatedClientSession)session).getParent().getIdentityMapAccessorInstance().printIdentityMaps();
        }
    }

    /**
     * PUBLIC:
     * Used to print all the locks in every identity map in this session.
     * The output of this method will be logged to this session's SessionLog at FINEST level.
     */
    @Override
    public void printIdentityMapLocks() {
        if (getSession().shouldLog(SessionLog.FINEST, SessionLog.CACHE)) {
            getIdentityMapManager().printLocks();
            ((IsolatedClientSession)session).getParent().getIdentityMapAccessorInstance().printIdentityMapLocks();
        }
    }

    /**
     * ADVANCED:
     * Register the object with the identity map.
     * The object must always be registered with its version number if optimistic locking is used.
     * The readTime may also be included in the cache key as it is constructed.
     */
    public CacheKey internalPutInIdentityMap(Object domainObject, Object key, Object writeLockValue, long readTime, ClassDescriptor descriptor) {
        //no need to unwrap as the put will unwrap later anyway
        if (descriptor.isIsolated()) {
            return getIdentityMapManager().putInIdentityMap(domainObject, key, writeLockValue, readTime, descriptor);
        } else {
            return ((IsolatedClientSession)session).getParent().getIdentityMapAccessorInstance().internalPutInIdentityMap(domainObject, key, writeLockValue, readTime, descriptor);
        }
    }

    /**
     * ADVANCED:
     * Queries the cache in-memory with the passed in criteria and invalidates matching Objects.
     * If the expression is too complex either all or none object of theClass invalidated (depending on shouldInvalidateOnException value).
     * @param selectionCriteria Expression selecting the Objects to be returned
     * @param theClass Class to be considered
     * @param translationRow Record
     * @param shouldInvalidateOnException boolean indicates weather to invalidate the object if conform threw exception.
     */
    @Override
    public void invalidateObjects(Expression selectionCriteria, Class theClass, Record translationRow, boolean shouldInvalidateOnException) {
        if (session.getDescriptor(theClass).isIsolated()) {
            getIdentityMapManager().invalidateObjects(selectionCriteria, theClass, translationRow, shouldInvalidateOnException);
        } else {
            ((IsolatedClientSession)session).getParent().getIdentityMapAccessorInstance().invalidateObjects(selectionCriteria, theClass, translationRow, shouldInvalidateOnException);
        }
    }

    /**
    * INTERNAL:
    * Lock the entire cache if the cache isolation requires.
    * By default concurrent reads and writes are allowed.
    * By write, unit of work merge is meant.
    */
    @Override
    public void releaseWriteLock() {
        //release in the opposite order of the acquire
        ((IsolatedClientSession)session).getParent().getIdentityMapAccessorInstance().releaseWriteLock();
        getIdentityMapManager().releaseWriteLock();
    }

    /**
     * ADVANCED:
     * Remove the object from the object cache.
     */
    @Override
    public Object removeFromIdentityMap(Object key, Class theClass, ClassDescriptor descriptor, Object object) {
        if (descriptor.isIsolated()) {
            return getIdentityMapManager().removeFromIdentityMap(key, theClass, descriptor, object);
        } else {
            return ((IsolatedClientSession)session).getParent().getIdentityMapAccessorInstance().removeFromIdentityMap(key, theClass, descriptor, object);
        }
    }

    /**
     * INTERNAL:
     * Set the IdentityMapManager for this IdentityMapAccessor
     */
    @Override
    public void setIdentityMapManager(IdentityMapManager identityMapManager) {
        this.identityMapManager = identityMapManager;
    }

    /**
     * INTERNAL:
     * Update the wrapper object the cache key associated with the given primary key,
     * this is used for EJB.
     */
    @Override
    public void setWrapper(Object primaryKey, Class theClass, Object wrapper) {
        if (getSession().getDescriptor(theClass).isIsolated()) {
            getIdentityMapManager().setWrapper(primaryKey, theClass, wrapper);
        } else {
            ((IsolatedClientSession)session).getParent().getIdentityMapAccessorInstance().setWrapper(primaryKey, theClass, wrapper);
        }
    }

    /**
     * ADVANCED:
     * Update the write lock value in the identity map.
     */
    @Override
    public void updateWriteLockValue(Object primaryKey, Class theClass, Object writeLockValue) {
        if (getSession().getDescriptor(theClass).isIsolated()) {
            getIdentityMapManager().setWriteLockValue(primaryKey, theClass, writeLockValue);
        } else {
            ((IsolatedClientSession)session).getParent().getIdentityMapAccessorInstance().updateWriteLockValue(primaryKey, theClass, writeLockValue);
        }
    }
}
