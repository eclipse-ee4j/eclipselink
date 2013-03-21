/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * Some parts Copyright (c) 2010 Mark Wolochuk
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     Mark Wolochuk - Bug 321041 ConcurrentModificationException on getFromIdentityMap() fix
 ******************************************************************************/  
package org.eclipse.persistence.internal.identitymaps;

import java.security.AccessController;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.io.*;
import java.lang.reflect.*;

import org.eclipse.persistence.config.ReferenceMode;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.descriptors.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.internal.localization.*;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.SessionProfiler;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedGetConstructorFor;
import org.eclipse.persistence.internal.security.PrivilegedInvokeConstructor;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.descriptors.CacheIndex;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.invalidation.CacheInvalidationPolicy;

/**
 * <p><b>Purpose</b>: Maintain identity maps for domain classes mapped with EclipseLink.
 * <p><b>Responsibilities</b>:<ul>
 *    <li> Build new identity maps lazily using info from the descriptor
 *    <li> Insert objects into appropriate identity map
 *    <li> Get object from appropriate identity map using object or primary key with class
 *    <li> Get and Set write lock values for cached objects
 * </ul>
 * @since TOPLink/Java 1.0
 */
public class IdentityMapManager implements Serializable, Cloneable {
    protected static final String MONITOR_PREFIX = SessionProfiler.CacheSize;

    /** A table of identity maps with the key being the domain Class. */
    protected Map<Class, IdentityMap> identityMaps;

    /** A table of identity maps with the key being the query */
    protected Map<Object, IdentityMap> queryResults;
    
    /** A map of class to list of queries that need to be invalidated when that class changes. */
    protected Map<Class, List> queryResultsInvalidationsByClass;
    
    /** A map of indexes on the cache. */
    protected Map<CacheIndex, IdentityMap> cacheIndexes;

    /** A reference to the session owning this manager. */
    protected AbstractSession session;

    /** Ensure mutual exclusion depending on the cache isolation.*/
    protected transient ConcurrencyManager cacheMutex;

    /** PERF: Optimize the object retrieval from the identity map. */
    protected IdentityMap lastAccessedIdentityMap = null;

    /** Used to store the write lock manager used for merging. */
    protected transient WriteLockManager writeLockManager;

    /** PERF: Used to avoid readLock and profiler checks to improve performance. */
    protected boolean isCacheAccessPreCheckRequired;

    protected IdentityMapManager() {        
    }
    
    public IdentityMapManager(AbstractSession session) {
        this.session = session;
        this.cacheMutex = new ConcurrencyManager();
        // PERF: Avoid query cache for uow as never used.
        if (session.isUnitOfWork()) {
            this.identityMaps = new HashMap();
        } else if (session.isIsolatedClientSession()) {
            this.identityMaps = new HashMap();
            this.queryResults = new HashMap();
            this.queryResultsInvalidationsByClass = new HashMap();
            this.cacheIndexes = new HashMap();
        } else {
            this.identityMaps = new ConcurrentHashMap();
            this.queryResults = new ConcurrentHashMap();
            this.queryResultsInvalidationsByClass = new ConcurrentHashMap();
            this.cacheIndexes = new ConcurrentHashMap();
        }
        checkIsCacheAccessPreCheckRequired();
    }

    /**
     * Provides access for setting a deferred lock on an object in the IdentityMap.
     */
    public CacheKey acquireDeferredLock(Object primaryKey, Class domainClass, ClassDescriptor descriptor, boolean isCacheCheckComplete) {
        CacheKey cacheKey = null;
        if (this.isCacheAccessPreCheckRequired) {
            this.session.startOperationProfile(SessionProfiler.Caching);
            acquireReadLock();
            IdentityMap identityMap = getIdentityMap(descriptor, false);
            try {
                cacheKey = identityMap.acquireDeferredLock(primaryKey, isCacheCheckComplete);
            } finally {
                releaseReadLock();
            }
            this.session.endOperationProfile(SessionProfiler.Caching);
            if (!this.session.isUnitOfWork() && cacheKey.getObject() == null) {
                this.session.updateProfile(MONITOR_PREFIX + domainClass.getSimpleName(), identityMap.getSize());
            }
        } else {
            cacheKey = getIdentityMap(descriptor, false).acquireDeferredLock(primaryKey, isCacheCheckComplete);
        }

        return cacheKey;
    }

    /**
     * Provides access for setting a concurrency lock on an object in the IdentityMap.
     * called with true from the merge process, if true then the refresh will not refresh the object.
     */
    public CacheKey acquireLock(Object primaryKey, Class domainClass, boolean forMerge, ClassDescriptor descriptor, boolean isCacheCheckComplete) {
        if (primaryKey == null) {
            CacheKey cacheKey = new CacheKey(primaryKey);
            cacheKey.acquire();
            return cacheKey;
        }
        CacheKey cacheKey = null;
        if (this.isCacheAccessPreCheckRequired) {
            this.session.startOperationProfile(SessionProfiler.Caching);
            acquireReadLock();
            IdentityMap identityMap = getIdentityMap(descriptor, false);
            try {
                cacheKey = identityMap.acquireLock(primaryKey, forMerge, isCacheCheckComplete);
            } finally {
                releaseReadLock();
            }
            this.session.endOperationProfile(SessionProfiler.Caching);
            if (!this.session.isUnitOfWork() && (cacheKey != null) && cacheKey.getObject() == null) {
                this.session.updateProfile(MONITOR_PREFIX + domainClass.getSimpleName(), identityMap.getSize());
            }
        } else {
            cacheKey = getIdentityMap(descriptor, false).acquireLock(primaryKey, forMerge, isCacheCheckComplete);
        }

        return cacheKey;
    }

    /**
     * Provides access for setting a concurrency lock on an object in the IdentityMap.
     * called with true from the merge process, if true then the refresh will not refresh the object.
     */
    public CacheKey acquireLockNoWait(Object primaryKey, Class domainClass, boolean forMerge, ClassDescriptor descriptor) {
        if (primaryKey == null) {
            CacheKey cacheKey = new CacheKey(primaryKey);
            cacheKey.acquire();
            return cacheKey;
        }
        CacheKey cacheKey = null;
        if (this.isCacheAccessPreCheckRequired) {
            this.session.startOperationProfile(SessionProfiler.Caching);
            acquireReadLock();
            IdentityMap identityMap = getIdentityMap(descriptor, false);
            try {
                cacheKey = identityMap.acquireLockNoWait(primaryKey, forMerge);
            } finally {
                releaseReadLock();
            }
            this.session.endOperationProfile(SessionProfiler.Caching);
            if (!this.session.isUnitOfWork() && (cacheKey != null) && (cacheKey.getObject() == null)) {
                this.session.updateProfile(MONITOR_PREFIX + domainClass.getSimpleName(), identityMap.getSize());
            }
        } else {
            cacheKey = getIdentityMap(descriptor, false).acquireLockNoWait(primaryKey, forMerge);
        }

        return cacheKey;
    }

    /**
     * Provides access for setting a concurrency lock on an object in the IdentityMap.
     * called with true from the merge process, if true then the refresh will not refresh the object.
     */
    public CacheKey acquireLockWithWait(Object primaryKey, Class domainClass, boolean forMerge, ClassDescriptor descriptor, int wait) {
        if (primaryKey == null) {
            CacheKey cacheKey = new CacheKey(primaryKey);
            cacheKey.acquire();
            return cacheKey;
        }
        CacheKey cacheKey = null;
        if (this.isCacheAccessPreCheckRequired) {
            this.session.startOperationProfile(SessionProfiler.Caching);
            acquireReadLock();
            IdentityMap identityMap = getIdentityMap(descriptor, false);
            try {
                cacheKey = identityMap.acquireLockWithWait(primaryKey, forMerge, wait);
            } finally {
                releaseReadLock();
            }
            this.session.endOperationProfile(SessionProfiler.Caching);
            if (!this.session.isUnitOfWork() && (cacheKey != null) && cacheKey.getObject() == null) {
                this.session.updateProfile(MONITOR_PREFIX + domainClass.getSimpleName(), identityMap.getSize());
            }
        } else {
            cacheKey = getIdentityMap(descriptor, false).acquireLockWithWait(primaryKey, forMerge, wait);
        }

        return cacheKey;
    }

    /**
     * PERF: Used to micro optimize cache access.
     * Avoid the readLock and profile checks if not required.
     */
    public void checkIsCacheAccessPreCheckRequired() {
        if ((this.session.getProfiler() != null)
                || ((this.session.getDatasourceLogin() != null) && this.session.getDatasourceLogin().shouldSynchronizedReadOnWrite())) {
            this.isCacheAccessPreCheckRequired = true;
        } else {
            this.isCacheAccessPreCheckRequired = false;
        }
    }

    /**
     * Provides access for setting a concurrency lock on an IdentityMap.
     */
    public void acquireReadLock() {
        this.session.startOperationProfile(SessionProfiler.Caching);

        if (this.session.getDatasourceLogin().shouldSynchronizedReadOnWrite()) {
            getCacheMutex().acquireReadLock();
        }

        this.session.endOperationProfile(SessionProfiler.Caching);
    }

    /**
     * INTERNAL:
     * Find the cachekey for the provided primary key and place a readlock on it.
     * This will allow multiple users to read the same object but prevent writes to
     * the object while the read lock is held.
     */
    public CacheKey acquireReadLockOnCacheKey(Object primaryKey, Class domainClass, ClassDescriptor descriptor) {
        if (primaryKey == null) {
            CacheKey cacheKey = new CacheKey(primaryKey);
            cacheKey.acquireReadLock();
            return cacheKey;
        }
        CacheKey cacheKey = null;
        if (this.isCacheAccessPreCheckRequired) {
            this.session.startOperationProfile(SessionProfiler.Caching);
            acquireReadLock();
            try {
                cacheKey = getIdentityMap(descriptor, false).acquireReadLockOnCacheKey(primaryKey);
            } finally {
                releaseReadLock();
            }
            this.session.endOperationProfile(SessionProfiler.Caching);
        } else {
            cacheKey = getIdentityMap(descriptor, false).acquireReadLockOnCacheKey(primaryKey);
        }

        return cacheKey;
    }

    /**
     * INTERNAL:
     * Find the cachekey for the provided primary key and place a readlock on it.
     * This will allow multiple users to read the same object but prevent writes to
     * the object while the read lock is held.
     * If no readlock can be acquired then do not wait but return null.
     */
    public CacheKey acquireReadLockOnCacheKeyNoWait(Object primaryKey, Class domainClass, ClassDescriptor descriptor) {
        if (primaryKey == null) {
            CacheKey cacheKey = new CacheKey(primaryKey);
            cacheKey.acquireReadLock();
            return cacheKey;
        }
        CacheKey cacheKey = null;
        if (this.isCacheAccessPreCheckRequired) {
            this.session.startOperationProfile(SessionProfiler.Caching);
            acquireReadLock();
            try {
                cacheKey = getIdentityMap(descriptor, false).acquireReadLockOnCacheKeyNoWait(primaryKey);
            } finally {
                releaseReadLock();
            }
            this.session.endOperationProfile(SessionProfiler.Caching);
        } else {
            cacheKey = getIdentityMap(descriptor, false).acquireReadLockOnCacheKeyNoWait(primaryKey);
        }

        return cacheKey;
    }

    /**
     * Lock the entire cache if the cache isolation requires.
     * By default concurrent reads and writes are allowed.
     * By write, unit of work merge is meant.
     */
    public boolean acquireWriteLock() {
        if (this.session.getDatasourceLogin().shouldSynchronizedReadOnWrite() || this.session.getDatasourceLogin().shouldSynchronizeWrites()) {
            getCacheMutex().acquire();
            return true;
        }
        return false;
    }
    
    /**
     * INTERNAL: (Public to allow testing to access)
     * Return a new empty identity map to cache instances of the class.
     */
    public IdentityMap buildNewIdentityMap(ClassDescriptor descriptor) {
        if (this.session.isUnitOfWork()) {
            ReferenceMode mode = ((UnitOfWorkImpl)this.session).getReferenceMode();
            if (mode == ReferenceMode.FORCE_WEAK){
                return new WeakUnitOfWorkIdentityMap(32, descriptor, this.session, true);
            } else if (mode == ReferenceMode.WEAK && descriptor.getObjectChangePolicy().isAttributeChangeTrackingPolicy()) {
                return new WeakUnitOfWorkIdentityMap(32, descriptor, this.session, true);        
            } else {
                return new UnitOfWorkIdentityMap(32, descriptor, this.session, true);
            }
        }

        // Remote session has its own setting.
        if (this.session.isRemoteSession()) {
            return buildNewIdentityMap(descriptor.getRemoteIdentityMapClass(), descriptor.getRemoteIdentityMapSize(), descriptor, true);
        } else {
            return buildNewIdentityMap(descriptor.getIdentityMapClass(), descriptor.getIdentityMapSize(), descriptor, this.session.isIsolatedClientSession());
        }
    }
    
    /**
     * INTERNAL:
     * Return a new empty identity map of the class type.
     */
    protected IdentityMap buildNewIdentityMap(Class identityMapClass, int size, ClassDescriptor descriptor, boolean isIsolated) throws DescriptorException {
        if ((descriptor == null) || (descriptor.getCachePolicy().getCacheInterceptorClass() == null)) {
            // PERF: Avoid reflection.
            if (identityMapClass == ClassConstants.SoftCacheWeakIdentityMap_Class) {
                return new SoftCacheWeakIdentityMap(size, descriptor, this.session, isIsolated);
            } else if (identityMapClass == ClassConstants.HardCacheWeakIdentityMap_Class) {
                return new HardCacheWeakIdentityMap(size, descriptor, this.session, isIsolated);
            } else if (identityMapClass == ClassConstants.SoftIdentityMap_Class) {
                return new SoftIdentityMap(size, descriptor, this.session, isIsolated);
            } else if (identityMapClass == ClassConstants.WeakIdentityMap_Class) {
                return new WeakIdentityMap(size, descriptor, this.session, isIsolated);
            } else if (identityMapClass == ClassConstants.FullIdentityMap_Class) {
                return new FullIdentityMap(size, descriptor, this.session, isIsolated);
            } else if (identityMapClass == ClassConstants.CacheIdentityMap_Class) {
                return new CacheIdentityMap(size, descriptor, this.session, isIsolated);
            }
        }
        try {
            Class[] parameters = new Class[]{ClassConstants.PINT, ClassDescriptor.class, AbstractSession.class, boolean.class};
            Object[] values = new Object[]{Integer.valueOf(size), descriptor, this.session, isIsolated};
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
                Constructor constructor = (Constructor)AccessController.doPrivileged(new PrivilegedGetConstructorFor(identityMapClass, parameters, false));
                IdentityMap map = (IdentityMap)AccessController.doPrivileged(new PrivilegedInvokeConstructor(constructor, values));
                if ((descriptor != null) && (descriptor.getCachePolicy().getCacheInterceptorClass() != null)) {
                    constructor = (Constructor)AccessController.doPrivileged(new PrivilegedGetConstructorFor(descriptor.getCacheInterceptorClass(), new Class[] { IdentityMap.class, AbstractSession.class }, false));
                    Object params[] = new Object[]{map, this.session};
                    map = (IdentityMap)AccessController.doPrivileged(new PrivilegedInvokeConstructor(constructor, params));
                }
                return map;
            } else {
                Constructor constructor = PrivilegedAccessHelper.getConstructorFor(identityMapClass, parameters, false);
                IdentityMap map = (IdentityMap)PrivilegedAccessHelper.invokeConstructor(constructor, values);
                if ((descriptor != null) && (descriptor.getCacheInterceptorClass() != null)) {
                    constructor = PrivilegedAccessHelper.getConstructorFor(descriptor.getCacheInterceptorClass(), new Class[] { IdentityMap.class, AbstractSession.class }, false);
                    Object params[] = new Object[]{map, this.session};
                    map = (IdentityMap)PrivilegedAccessHelper.invokeConstructor(constructor, params);
                }
                return map;
            }
        } catch (Exception exception) {
            throw DescriptorException.invalidIdentityMap(descriptor, exception);
        }
    }
    
    /**
     * INTERNAL:
     * Clear the the lastAccessedIdentityMap and the lastAccessedIdentityMapClass
     */
    public void clearLastAccessedIdentityMap() {
        this.lastAccessedIdentityMap = null;
    }

    /**
     * INTERNAL:
     * Clones itself, used for uow commit and resume on failure.
     */
    public Object clone() {
        IdentityMapManager manager = null;
        try {
            manager = (IdentityMapManager)super.clone();
            manager.setIdentityMaps(new ConcurrentHashMap());
            for (Iterator iterator = this.identityMaps.entrySet().iterator(); iterator.hasNext();) {
                Map.Entry entry = (Map.Entry)iterator.next();
                manager.identityMaps.put((Class)entry.getKey(), (IdentityMap)((IdentityMap)entry.getValue()).clone());
            }
        } catch (CloneNotSupportedException exception) {
            throw new InternalError(exception.toString());
        }
        return manager;
    }

    /**
     * Clear all the query caches.
     */
    public void clearQueryCache() {
        this.queryResults = new ConcurrentHashMap();
        this.queryResultsInvalidationsByClass = new ConcurrentHashMap();
    }

    /**
     * Clear all index caches.
     */
    public void clearCacheIndexes() {
        this.cacheIndexes = new ConcurrentHashMap();
    }

    /**
     * Remove the cache key related to a query.
     * Note this method is not synchronized and care should be taken to ensure
     * there are no other threads accessing the cache key.
     * This is used to clean up cached clones of queries.
     */
    public void clearQueryCache(ReadQuery query) {
        if (query != null) {// PERF: use query name, unless no name.
            Object queryKey = query.getName();
            if ((queryKey == null) || ((String)queryKey).length() == 0) {
                queryKey = query;
            }
            this.queryResults.remove(queryKey);
        }
    }

    /**
     * Invalidate/remove any results for the class from the query cache.
     * This is used to invalidate the query cache on any change.
     */
    public void invalidateQueryCache(Class classThatChanged) {
        if (this.queryResultsInvalidationsByClass == null) {
            return;
        }
        List invalidations = this.queryResultsInvalidationsByClass.get(classThatChanged);
        if (invalidations != null) {
            for (Object queryKey : invalidations) {
                this.queryResults.remove(queryKey);                
            }
        }
        Class superClass = classThatChanged.getSuperclass();
        if ((superClass != null) && (superClass != ClassConstants.OBJECT)) {
            invalidateQueryCache(superClass);
        }
    }
    
    /**
     * Return true if an CacheKey with the primary key is in the map.
     * User API.
     * @param key is the primary key for the object to search for.
     */
    public boolean containsKey(Object key, Class theClass, ClassDescriptor descriptor) {
        if (key == null) {
            return false;
        }
        IdentityMap map = getIdentityMap(descriptor, true);
        if (map == null) {
            return false;
        }
        if (this.isCacheAccessPreCheckRequired) {
            this.session.startOperationProfile(SessionProfiler.Caching);
            acquireReadLock();
            try {
                return map.containsKey(key);
            } finally {
                releaseReadLock();
                this.session.endOperationProfile(SessionProfiler.Caching);
            }
        } else {
            return map.containsKey(key);
        }
    }

    /**
     * Query the cache in-memory.
     */
    public Vector getAllFromIdentityMap(Expression selectionCriteria, Class theClass, Record translationRow, int valueHolderPolicy, boolean shouldReturnInvalidatedObjects) {
        ClassDescriptor descriptor = this.session.getDescriptor(theClass);
        this.session.startOperationProfile(SessionProfiler.Caching);
        Vector objects = null;
        try {
            if (selectionCriteria != null) {
                // PERF: Avoid clone of expression.            
                ExpressionBuilder builder = selectionCriteria.getBuilder();
                if (builder != null && builder.getSession() == null) {
                    builder.setSession(this.session.getRootSession(null));
                    builder.setQueryClass(theClass);
                }
            }
            objects = new Vector();
            IdentityMap map = getIdentityMap(descriptor, false);
            // bug 327900 - If don't read subclasses is set on the descriptor heed it.
            boolean readSubclassesOrNoInheritance = (!descriptor.hasInheritance() || descriptor.getInheritancePolicy().shouldReadSubclasses());

            // cache the current time to avoid calculating it every time through the loop
            long currentTimeInMillis = System.currentTimeMillis();
            for (Enumeration cacheEnum = map.keys(); cacheEnum.hasMoreElements();) {
                CacheKey key = (CacheKey)cacheEnum.nextElement();
                if ((key.getObject() == null) || (!shouldReturnInvalidatedObjects && descriptor.getCacheInvalidationPolicy().isInvalidated(key, currentTimeInMillis))) {
                    continue;
                }
                Object object = key.getObject();

                // Bug # 3216337 - key.getObject() should check for null; object may be GC'd (MWN)
                if (object == null) {
                    continue;
                }

                // Must check for inheritance.
                // bug 327900
                if ((object.getClass() == theClass) || (readSubclassesOrNoInheritance && (theClass.isInstance(object)))) {
                    if (selectionCriteria == null) {
                        objects.add(object);
                    } else {
                        try {
                            if (selectionCriteria.doesConform(object, this.session, (AbstractRecord)translationRow, valueHolderPolicy)) {
                                objects.add(object);
                            }
                        } catch (QueryException queryException) {
                            if (queryException.getErrorCode() == QueryException.MUST_INSTANTIATE_VALUEHOLDERS) {
                                if (valueHolderPolicy == InMemoryQueryIndirectionPolicy.SHOULD_IGNORE_EXCEPTION_RETURN_CONFORMED) {
                                    objects.add(object);
                                } else if (valueHolderPolicy == InMemoryQueryIndirectionPolicy.SHOULD_THROW_INDIRECTION_EXCEPTION) {
                                    throw queryException;
                                }
                            } else {
                                throw queryException;
                            }
                        }
                    }
                }
            }
        } finally {
            this.session.endOperationProfile(SessionProfiler.Caching);
        }
        return objects;
    }

    /**
     * ADVANCED:
     * Using a list of Entity PK this method will attempt to bulk load the entire list from the cache.
     * In certain circumstances this can have large performance improvements over loading each item individually.
     * @param pkList List of Entity PKs to extract from the cache
     * @param ClassDescriptor Descriptor type to be retrieved.
     * @return Map of Entity PKs associated to the Entities that were retrieved
     * @throws QueryException
     */
    public Map<Object, Object> getAllFromIdentityMapWithEntityPK(Object[] pkList, ClassDescriptor descriptor, AbstractSession session){
        return getIdentityMap(descriptor).getAllFromIdentityMapWithEntityPK(pkList, descriptor, session);
    }

    /**
     * Invalidate objects meeting selectionCriteria.
     */
    public void invalidateObjects(Expression selectionCriteria, Class theClass, Record translationRow, boolean shouldInvalidateOnException) {
        ClassDescriptor descriptor = this.session.getDescriptor(theClass);
        this.session.startOperationProfile(SessionProfiler.Caching);
        try {
            IdentityMap map = getIdentityMap(descriptor, true);
            if(map == null) {
                return;
            }
            boolean isChildDescriptor = descriptor.isChildDescriptor();
            if (selectionCriteria != null) {
                // PERF: Avoid clone of expression.            
                ExpressionBuilder builder = selectionCriteria.getBuilder();
                if (builder.getSession() == null) {
                    builder.setSession(this.session.getRootSession(null));
                    builder.setQueryClass(theClass);
                }
                CacheInvalidationPolicy cacheInvalidationPolicy = descriptor.getCacheInvalidationPolicy();
                int inMemoryQueryIndirectionPolicy = InMemoryQueryIndirectionPolicy.SHOULD_IGNORE_EXCEPTION_RETURN_NOT_CONFORMED;
                if (shouldInvalidateOnException) {
                    inMemoryQueryIndirectionPolicy = InMemoryQueryIndirectionPolicy.SHOULD_IGNORE_EXCEPTION_RETURN_CONFORMED;
                }

                // cache the current time to avoid calculating it every time through the loop
                long currentTimeInMillis = System.currentTimeMillis();
                //Enumeration doesn't checkReadLocks
                for (Enumeration cacheEnum = map.keys(false); cacheEnum.hasMoreElements();) {
                    CacheKey key = (CacheKey)cacheEnum.nextElement();
                    Object object = key.getObject();
                    if (object == null || cacheInvalidationPolicy.isInvalidated(key, currentTimeInMillis)) {
                        continue;
                    }

                    // Must check for inheritance.
                    if (!isChildDescriptor || (object.getClass() == theClass) || (theClass.isInstance(object))) {
                        try {
                            if (selectionCriteria.doesConform(object, this.session, (AbstractRecord)translationRow, inMemoryQueryIndirectionPolicy)) {
                                key.setInvalidationState(CacheKey.CACHE_KEY_INVALID);
                            }
                        } catch (QueryException queryException) {
                            if(queryException.getErrorCode() == QueryException.CANNOT_CONFORM_EXPRESSION) {
                                // if the expression can't be confirmed for the object it's likely the same will happen to all other objects -
                                // invalidate all objects of theClass if required, otherwise leave.
                                if (shouldInvalidateOnException) {
                                    invalidateObjects(null, theClass, null, true);
                                } else {
                                    return;
                                }
                            } else {
                                if (shouldInvalidateOnException) {
                                    key.setInvalidationState(CacheKey.CACHE_KEY_INVALID);
                                }
                            }
                        } catch (RuntimeException runtimeException) {
                            if (shouldInvalidateOnException) {
                                key.setInvalidationState(CacheKey.CACHE_KEY_INVALID);
                            }
                        }
                    }
                }
            } else {
                // selectionCriteria == null
                if(isChildDescriptor) {
                    // Must check for inheritance.
                    for (Enumeration cacheEnum = map.keys(false); cacheEnum.hasMoreElements();) {
                        CacheKey key = (CacheKey)cacheEnum.nextElement();
                        Object object = key.getObject();
                        if (object == null) {
                            continue;
                        }

                        if ((object.getClass() == theClass) || (theClass.isInstance(object))) {
                            key.setInvalidationState(CacheKey.CACHE_KEY_INVALID);
                        }
                    }
                } else {
                    // if it's either a root class or there is no inheritance just invalidate the whole identity map
                    for (Enumeration cacheEnum = map.keys(false); cacheEnum.hasMoreElements();) {
                        CacheKey key = (CacheKey)cacheEnum.nextElement();
                        key.setInvalidationState(CacheKey.CACHE_KEY_INVALID);
                    }
                }
            }
            invalidateQueryCache(theClass);
        } finally {
            this.session.endOperationProfile(SessionProfiler.Caching);
        }
    }

    /**
     * Retrieve the cache key for the given identity information.
     */
    public CacheKey getCacheKeyForObjectForLock(Object primaryKey, Class theClass, ClassDescriptor descriptor) {
        if (primaryKey == null) {
            return null;
        }
        IdentityMap map = getIdentityMap(descriptor, true);
        if (map == null) {
            return null;
        }
        CacheKey cacheKey = null;
        if (this.isCacheAccessPreCheckRequired) {
            this.session.startOperationProfile(SessionProfiler.Caching);
            acquireReadLock();
            try {
                cacheKey = map.getCacheKeyForLock(primaryKey);
            } finally {
                releaseReadLock();
                this.session.endOperationProfile(SessionProfiler.Caching);
            }
        } else {
            cacheKey = map.getCacheKeyForLock(primaryKey);
        }
        return cacheKey;
    }

    /**
     * Retrieve the cache key for the given identity information.
     */
    public CacheKey getCacheKeyForObject(Object primaryKey, Class theClass, ClassDescriptor descriptor, boolean forMerge) {
        if (primaryKey == null) {
            return null;
        }
        IdentityMap map = getIdentityMap(descriptor, true);
        if (map == null) {
            return null;
        }
        CacheKey cacheKey = null;
        if (this.isCacheAccessPreCheckRequired) {
            this.session.startOperationProfile(SessionProfiler.Caching);
            acquireReadLock();
            try {
                cacheKey = map.getCacheKey(primaryKey, forMerge);
            } finally {
                releaseReadLock();
                this.session.endOperationProfile(SessionProfiler.Caching);
            }
        } else {
            cacheKey = map.getCacheKey(primaryKey, forMerge);
        }
        return cacheKey;
    }
    /**
     * Return the cache mutex.
     * This allows for the entire cache to be locked.
     * This is done for transaction isolations on merges, although never locked by default.
     */
    public ConcurrencyManager getCacheMutex() {
        return cacheMutex;
    }

    /**
     * This method is used to get a list of those classes with IdentityMaps in the Session.
     */
    public Vector getClassesRegistered() {
        Iterator classes = getIdentityMaps().keySet().iterator();
        Vector results = new Vector(getIdentityMaps().size());
        while (classes.hasNext()) {
            results.add(((Class)classes.next()).getName());
        }
        return results;
    }

    /**
     * Get the object from the identity map which has the same identity information
     * as the given object.
     */
    public Object getFromIdentityMap(Object object) {
        ClassDescriptor descriptor = this.session.getDescriptor(object);
        Object primaryKey = descriptor.getObjectBuilder().extractPrimaryKeyFromObject(object, this.session);
        return getFromIdentityMap(primaryKey, object.getClass(), descriptor);
    }

    /**
     * Get the object from the identity map which has the given primary key and class.
     */
    public Object getFromIdentityMap(Object key, Class theClass, ClassDescriptor descriptor) {
        return getFromIdentityMap(key, theClass, true, descriptor);
    }

    /**
     * Get the object from the identity map which has the given primary key and class.
     * Only return the object if it has not been invalidated.
     */
    public Object getFromIdentityMap(Object key, Class theClass, boolean shouldReturnInvalidatedObjects, ClassDescriptor descriptor) {
        if (key == null) {
            return null;
        }

        CacheKey cacheKey;
        IdentityMap map = getIdentityMap(descriptor, true);
        if (map == null) {
            return null;
        }
        Object domainObject = null;
        if (this.isCacheAccessPreCheckRequired) {
            this.session.startOperationProfile(SessionProfiler.Caching);
            acquireReadLock();
            try {
                cacheKey = map.getCacheKey(key, false);
            } finally {
                releaseReadLock();
            }
        } else {
            cacheKey = map.getCacheKey(key, false);
        }

        if ((cacheKey != null) && (shouldReturnInvalidatedObjects || !descriptor.getCacheInvalidationPolicy().isInvalidated(cacheKey))) {
            // BUG#4772232 - The read-lock must be checked to avoid returning a partial object,
            // PERF: Just check the read-lock to avoid acquire if not locked.
            // This is ok if you get the object first, as the object cannot gc and identity is always maintained.
            domainObject = cacheKey.getObject();
            cacheKey.checkReadLock();
            // Resolve the inheritance issues.
            domainObject = checkForInheritance(domainObject, theClass, descriptor);
        }

        if (this.isCacheAccessPreCheckRequired) {
            this.session.endOperationProfile(SessionProfiler.Caching);
        }

        return domainObject;
    }

    public Object getFromIdentityMap(Expression selectionCriteria, Class theClass, Record translationRow, int valueHolderPolicy, boolean conforming, boolean shouldReturnInvalidatedObjects, ClassDescriptor descriptor) {
        UnitOfWorkImpl unitOfWork = (conforming) ? (UnitOfWorkImpl)this.session : null;
        this.session.startOperationProfile(SessionProfiler.Caching);
        try {
            if (selectionCriteria != null) {
                // PERF: Avoid clone of expression.            
                ExpressionBuilder builder = selectionCriteria.getBuilder();
                if (builder.getSession() == null) {
                    builder.setSession(this.session.getRootSession(null));
                    builder.setQueryClass(theClass);
                }
            }
            IdentityMap map = getIdentityMap(descriptor, false);

            // Bug #321041 - if policy is set to trigger indirection, then make a copy of the cache keys collection
            // and iterate over that to avoid a ConcurrentModificationException.
            // This happens when the indirect attribute is of the same type (or has same mapped superclass) as
            // the parent object. EclipseLink inserts the object into the same collection it is iterating over,
            // which results in a ConcurrentModificationException.
            // There's a slight performance hit in copying the collection, but we are already taking a hit
            // by triggering indirection in the first place.
            boolean copyKeyCollection = valueHolderPolicy == InMemoryQueryIndirectionPolicy.SHOULD_TRIGGER_INDIRECTION;
            Vector cacheKeys = null;
            if (copyKeyCollection) {
                cacheKeys = new Vector(map.getSize());
                for (Enumeration cacheEnum = map.keys(); cacheEnum.hasMoreElements();) {
                    CacheKey key = (CacheKey)cacheEnum.nextElement();
                    cacheKeys.add(key);
                }
            }
            
            Enumeration cacheEnum = copyKeyCollection ? cacheKeys.elements() : map.keys();
            
            // cache the current time to avoid calculating it every time through the loop
            long currentTimeInMillis = System.currentTimeMillis();
            while (cacheEnum.hasMoreElements()) {
                CacheKey key = (CacheKey)cacheEnum.nextElement();
                if (!shouldReturnInvalidatedObjects && descriptor.getCacheInvalidationPolicy().isInvalidated(key, currentTimeInMillis)) {
                    continue;
                }
                Object object = key.getObject();

                // Bug # 3216337 - key.getObject() should check for null; object may be GC'd (MWN)
                if (object == null) {
                    continue;
                }

                // Must check for inheritance.
                if ((object.getClass() == theClass) || (theClass.isInstance(object))) {
                    if (selectionCriteria == null) {
                        // bug 2782991: if first found was deleted nothing returned. 
                        if (!(conforming && unitOfWork.isObjectDeleted(object))) {
                            return object;
                        }
                    }

                    //CR 3677 integration of a ValueHolderPolicy
                    try {
                        if (selectionCriteria.doesConform(object, this.session, (AbstractRecord)translationRow, valueHolderPolicy)) {
                            // bug 2782991: if first found was deleted nothing returned. 
                            if (!(conforming && unitOfWork.isObjectDeleted(object))) {
                                return object;
                            }
                        }
                    } catch (QueryException queryException) {
                        if (queryException.getErrorCode() == QueryException.MUST_INSTANTIATE_VALUEHOLDERS) {
                            if (valueHolderPolicy == InMemoryQueryIndirectionPolicy.SHOULD_IGNORE_EXCEPTION_RETURN_CONFORMED) {
                                // bug 2782991: if first found was deleted nothing returned. 
                                if (!(conforming && unitOfWork.isObjectDeleted(object))) {
                                    return object;
                                }
                            } else if (valueHolderPolicy == InMemoryQueryIndirectionPolicy.SHOULD_IGNORE_EXCEPTION_RETURN_NOT_CONFORMED) {
                                // For bug 2667870 just skip this item, but do not abort.
                            } else {
                                throw queryException;
                            }
                        } else {
                            throw queryException;
                        }
                    }
                }
            }
        } finally {
            this.session.endOperationProfile(SessionProfiler.Caching);
        }
        return null;
    }

    /**
     * Get the object from the cache with the given primary key and class.
     * Do not return the object if it was invalidated.
     */
    public Object getFromIdentityMapWithDeferredLock(Object key, Class theClass, boolean shouldReturnInvalidatedObjects, ClassDescriptor descriptor) {
        if (key == null) {
            return null;
        }

        IdentityMap map = getIdentityMap(descriptor, true);
        if (map == null) {
            return null;
        }
        CacheKey cacheKey;
        Object domainObject = null;
        if (this.isCacheAccessPreCheckRequired) {
            this.session.startOperationProfile(SessionProfiler.Caching);
            acquireReadLock();
            try {
                cacheKey = map.getCacheKey(key, false);
            } finally {
                releaseReadLock();
            }
        } else {
            cacheKey = map.getCacheKey(key, false);
        }

        if ((cacheKey != null) && (shouldReturnInvalidatedObjects || !descriptor.getCacheInvalidationPolicy().isInvalidated(cacheKey))) {
            // PERF: Just check the read-lock to avoid acquire if not locked.
            // This is ok if you get the object first, as the object cannot gc and identity is always maintained.
            domainObject = cacheKey.getObject();
            cacheKey.checkDeferredLock();
            // Reslove inheritance issues.
            domainObject = checkForInheritance(domainObject, theClass, descriptor);
        }


        if (this.isCacheAccessPreCheckRequired) {
            this.session.endOperationProfile(SessionProfiler.Caching);
        }

        return domainObject;
    }

    /**
     * INTERNAL:
     * Return the identity map for the class, if missing create a new one.
     */
    public IdentityMap getIdentityMap(ClassDescriptor descriptor) {
        return  getIdentityMap(descriptor, false);
    }

    /**
     * INTERNAL:
     * Return the identity map for the class.
     * @param returnNullIfNoMap if true return null if no map, otherwise create one.
     */
    public IdentityMap getIdentityMap(ClassDescriptor descriptor, boolean returnNullIfNoMap) {
        // Ensure that an identitymap is only used for the root descriptor for inheritance.
        // This is required to obtain proper cache hits.
        if (descriptor.hasInheritance()) {
            descriptor = descriptor.getInheritancePolicy().getRootParentDescriptor();
        }
        Class descriptorClass = descriptor.getJavaClass();

        // PERF: First check if same as lastAccessedIdentityMap to avoid lookup.
        IdentityMap tempMap = this.lastAccessedIdentityMap;
        if ((tempMap != null) && (tempMap.getDescriptorClass() == descriptorClass)) {
            return tempMap;
        }

        // PERF: Avoid synchronization through get and putIfAbsent double-check.
        IdentityMap identityMap = this.identityMaps.get(descriptorClass);
        if (identityMap == null) {
            if (returnNullIfNoMap && descriptor.getCachePolicy().getCacheInterceptorClass() == null) {
                //interceptor monitors the identity map and needs to know that a request occurred.
                return null;
            }
            IdentityMap newIdentityMap = null;
            if (this.session.isUnitOfWork() || this.session.isIsolatedClientSession()) {
                newIdentityMap = buildNewIdentityMap(descriptor);
                identityMap = this.identityMaps.put(descriptorClass, newIdentityMap);
            } else {
                newIdentityMap = buildNewIdentityMap(descriptor);
                identityMap = (IdentityMap)((ConcurrentMap)this.identityMaps).putIfAbsent(descriptorClass, newIdentityMap);
            }
            if (identityMap == null) {
                identityMap = newIdentityMap;
            }
        }
        this.lastAccessedIdentityMap = identityMap;
            
        return identityMap;
    }

    protected Map<Class, IdentityMap> getIdentityMaps() {
        return identityMaps;
    }
    
    /**
     * Return an iterator of the classes in the identity map. 
     */
    public Iterator getIdentityMapClasses() {
        return getIdentityMaps().keySet().iterator();
    }

    /**
     * Get the cached results associated with a query.  Results are cached by the
     * values of the parameters to the query so different parameters will have
     * different cached results.
     */
    public Object getQueryResult(ReadQuery query, List parameters, boolean shouldCheckExpiry) {
        if (query.getQueryResultsCachePolicy() == null) {
            return null;
        }
        // PERF: use query name, unless no name.
        Object queryKey = query.getName();
        if ((queryKey == null) || ((String)queryKey).length() == 0) {
            queryKey = query;
        }
        IdentityMap map = this.queryResults.get(queryKey);
        if (map == null) {
            return null;
        }

        Object lookupParameters;
        if ((parameters == null) || parameters.isEmpty()) {
            lookupParameters = CacheId.EMPTY;
        } else {
            lookupParameters = new CacheId(parameters.toArray());
        }

        CacheKey key = map.getCacheKey(lookupParameters, false);
        if ((key == null) || (shouldCheckExpiry && query.getQueryResultsCachePolicy().getCacheInvalidationPolicy().isInvalidated(key))) {
            return null;
        }
        return key.getObject();
    }

    /**
     * Return the cache key for the cache index or null if not found.
     */
    public CacheKey getCacheKeyByIndex(CacheIndex index, CacheId indexValues, boolean shouldCheckExpiry, ClassDescriptor descriptor) {
        if (this.cacheIndexes == null) {
            return null;
        }       
        IdentityMap map = this.cacheIndexes.get(index);
        if (map == null) {
            return null;
        }

        CacheKey cacheKey = map.getCacheKey(indexValues, false);
        if (cacheKey == null) {
            return null;
        }
        // The cache key is nested as the object cache key is put into the cache.
        cacheKey = (CacheKey)cacheKey.getObject();
        if (cacheKey == null) {
            return null;
        }
        if (shouldCheckExpiry && descriptor.getCacheInvalidationPolicy().isInvalidated(cacheKey)) {
            return null;
        }
        return cacheKey;
    }

    /**
     * Index the cache key by the index values.
     */
    public void putCacheKeyByIndex(CacheIndex index, CacheId indexValues, CacheKey cacheKey, ClassDescriptor descriptor) {
        if (this.cacheIndexes == null) {
            return;
        }
        if (indexValues == null) {
            return;
        }
        IdentityMap map = this.cacheIndexes.get(index);
        if (map == null) {
            synchronized (this.cacheIndexes) {
                map = this.cacheIndexes.get(index);
                if (map == null) {
                    map = buildNewIdentityMap(index.getCacheType(), index.getCacheSize(), null, false);
                    this.cacheIndexes.put(index, map);
                }
            }
        }
        map.put(indexValues, cacheKey, null, 0);
    }

    protected AbstractSession getSession() {
        return session;
    }

    /**
     * Get the wrapper object from the cache key associated with the given primary key,
     * this is used for EJB.
     */
    public Object getWrapper(Object primaryKey, Class theClass) {
        ClassDescriptor descriptor = this.session.getDescriptor(theClass);
        IdentityMap map = getIdentityMap(descriptor, false);
        Object wrapper;
        if (this.isCacheAccessPreCheckRequired) {
            this.session.startOperationProfile(SessionProfiler.Caching);
            acquireReadLock();
            try {
                wrapper = map.getWrapper(primaryKey);
            } finally {
                releaseReadLock();
            }
            this.session.endOperationProfile(SessionProfiler.Caching);
        } else {
            wrapper = map.getWrapper(primaryKey);
        }
        return wrapper;
    }

    /**
     * Returns the single write Lock manager for this session
     */
    public WriteLockManager getWriteLockManager() {
        // With Isolated Sessions not all Identity maps need a WriteLockManager so
        //lazy initialize
        synchronized (this) {
            if (this.writeLockManager == null) {
                this.writeLockManager = new WriteLockManager();
            }
        }
        return this.writeLockManager;
    }

    /**
     * Retrieve the write lock value of the cache key associated with the given primary key,
     */
    public Object getWriteLockValue(Object primaryKey, Class domainClass, ClassDescriptor descriptor) {
        if (primaryKey == null) {
            return null;
        }
        IdentityMap map = getIdentityMap(descriptor, false);
        Object value;
        if (this.isCacheAccessPreCheckRequired) {
            this.session.startOperationProfile(SessionProfiler.Caching);
            acquireReadLock();
            try {
                value = map.getWriteLockValue(primaryKey);
            } finally {
                releaseReadLock();
            }
            this.session.endOperationProfile(SessionProfiler.Caching);
        } else {
            value = map.getWriteLockValue(primaryKey);
        }
        return value;
    }

    /**
     * Reset the identity map for only the instances of the class.
     * For inheritance the user must make sure that they only use the root class.
     */
    public void initializeIdentityMap(Class theClass) throws EclipseLinkException {
        ClassDescriptor descriptor = this.session.getDescriptor(theClass);

        if (descriptor == null) {
            throw ValidationException.missingDescriptor(String.valueOf(theClass));
        }
        if (descriptor.isChildDescriptor()) {
            throw ValidationException.childDescriptorsDoNotHaveIdentityMap();
        }
        // Bug 3736313 - look up identity map by descriptor's java class
        Class javaClass = descriptor.getJavaClass();
        IdentityMap identityMap = buildNewIdentityMap(descriptor);
        getIdentityMaps().put(javaClass, identityMap);
        clearLastAccessedIdentityMap();
        invalidateQueryCache(theClass);
    }

    public void initializeIdentityMaps() {
        clearLastAccessedIdentityMap();
        setIdentityMaps(new ConcurrentHashMap());
        clearQueryCache();
        clearCacheIndexes();
    }

    /**
     * Used to print all the objects in the identity map of the passed in class.
     * The output of this method will be logged to this session's SessionLog at SEVERE level.
     */
    public void printIdentityMap(Class businessClass) {
        String cr = Helper.cr();
        ClassDescriptor descriptor = this.session.getDescriptor(businessClass);
        int cacheCounter = 0;
        StringWriter writer = new StringWriter();
        if (descriptor.isDescriptorTypeAggregate()) {
            return; //do nothing if descriptor is aggregate
        }

        IdentityMap map = getIdentityMap(descriptor, true);
        if (map == null) {
            return;
        }
        writer.write(LoggingLocalization.buildMessage("identitymap_for", new Object[] { cr, Helper.getShortClassName(map.getClass()), Helper.getShortClassName(businessClass) }));
        if (descriptor.hasInheritance()) {
            if (descriptor.getInheritancePolicy().isRootParentDescriptor()) {
                writer.write(LoggingLocalization.buildMessage("includes"));
                List<ClassDescriptor> childDescriptors = descriptor.getInheritancePolicy().getChildDescriptors();
                if ((childDescriptors != null) && (childDescriptors.size() != 0)) {//Bug#2675242
                    Iterator<ClassDescriptor> iterator = childDescriptors.iterator();
                    writer.write(Helper.getShortClassName(iterator.next().getJavaClass()));
                    while (iterator.hasNext()) {
                        writer.write(", " + Helper.getShortClassName(iterator.next().getJavaClass()));
                    }
                }
                writer.write(")");
            }
        }

        for (Enumeration enumtr = map.keys(); enumtr.hasMoreElements();) {
            org.eclipse.persistence.internal.identitymaps.CacheKey cacheKey = (org.eclipse.persistence.internal.identitymaps.CacheKey)enumtr.nextElement();
            Object object = cacheKey.getObject();
            if (businessClass.isInstance(object)) {
                cacheCounter++;
                Object key = cacheKey.getKey();
                if (object == null) {
                    writer.write(LoggingLocalization.buildMessage("key_object_null", new Object[] { cr, key, "\t" }));
                } else {
                    String hashCode = String.valueOf(System.identityHashCode(object));
                    if (descriptor.usesOptimisticLocking() && descriptor.usesVersionLocking()) {
                        // Obtain writeLockValue and convert the value to String
                        Object writeLockValue = descriptor.getOptimisticLockingPolicy().getWriteLockValue(object, key, session);
                        String version = (String) session.getPlatform().convertObject(writeLockValue, String.class);
                        writer.write(LoggingLocalization.buildMessage("key_version_identity_hash_code_object", new Object[] { cr, key, "\t", hashCode, object, version }));
                    } else {
                        writer.write(LoggingLocalization.buildMessage("key_identity_hash_code_object", new Object[] { cr, key, "\t", hashCode, object }));
                    }
                }
            }
        }
        writer.write(LoggingLocalization.buildMessage("elements", new Object[] { cr, String.valueOf(cacheCounter) }));
        this.session.log(SessionLog.SEVERE, SessionLog.CACHE, writer.toString(), null, null, false);
    }

    /**
     * Used to print all the objects in every identity map in this session.
     * The output of this method will be logged to this session's SessionLog at SEVERE level.
     */
    public void printIdentityMaps() {
        for (Iterator iterator = this.session.getDescriptors().keySet().iterator();
                 iterator.hasNext();) {
            Class businessClass = (Class)iterator.next();
            ClassDescriptor descriptor = this.session.getDescriptor(businessClass);
            if (descriptor.hasInheritance()) {
                if (descriptor.getInheritancePolicy().isRootParentDescriptor()) {
                    printIdentityMap(businessClass);
                }
            } else {
                printIdentityMap(businessClass);
            }
        }
    }

    /**
     * Used to print all the Locks in every identity map in this session.
     * The output of this method will be logged to this session's SessionLog at FINEST level.
     */
    public void printLocks() {
        StringWriter writer = new StringWriter();
        HashMap threadCollection = new HashMap();
        writer.write(TraceLocalization.buildMessage("lock_writer_header", (Object[])null) + Helper.cr());
        Iterator idenityMapsIterator = this.session.getIdentityMapAccessorInstance().getIdentityMapManager().getIdentityMaps().values().iterator();
        while (idenityMapsIterator.hasNext()) {
            IdentityMap idenityMap = (IdentityMap)idenityMapsIterator.next();
            idenityMap.collectLocks(threadCollection);
        }
        Object[] parameters = new Object[1];
        for (Iterator threads = threadCollection.keySet().iterator(); threads.hasNext();) {
            Thread activeThread = (Thread)threads.next();
            parameters[0] = activeThread.getName();
            writer.write(TraceLocalization.buildMessage("active_thread", parameters) + Helper.cr());
            for (Iterator cacheKeys = ((HashSet)threadCollection.get(activeThread)).iterator();
                     cacheKeys.hasNext();) {
                CacheKey cacheKey = (CacheKey)cacheKeys.next();
                if (cacheKey.isAcquired() && cacheKey.getActiveThread() == activeThread){
                    parameters[0] = cacheKey.getObject();
                    writer.write(TraceLocalization.buildMessage("locked_object", parameters) + Helper.cr());
                    writer.write("PK: " + cacheKey.getKey() + Helper.cr());
                    parameters[0] = new Integer(cacheKey.getDepth());
                    writer.write(TraceLocalization.buildMessage("depth", parameters) + Helper.cr());
                    Exception stack = cacheKey.getStack();
                    if (stack != null) stack.printStackTrace(new PrintWriter(writer));
                } else{
                    writer.write(TraceLocalization.buildMessage("cachekey_released", new Object[]{}));
                    parameters[0] = cacheKey.getObject();
                    writer.write(TraceLocalization.buildMessage("locked_object", parameters) + Helper.cr());
                    writer.write("PK: " + cacheKey.getKey() + Helper.cr());
                }
            }
            DeferredLockManager deferredLockManager = ConcurrencyManager.getDeferredLockManager(activeThread);
            if (deferredLockManager != null) {
                for (Iterator deferredLocks = deferredLockManager.getDeferredLocks().iterator();
                         deferredLocks.hasNext();) {
                    ConcurrencyManager lock = (ConcurrencyManager)deferredLocks.next();
                    if (lock instanceof CacheKey){
                        parameters[0] = ((CacheKey)lock).getObject();
                        writer.write(TraceLocalization.buildMessage("deferred_locks", parameters) + Helper.cr());
                    }
                }
            }
        }
        writer.write(Helper.cr() + TraceLocalization.buildMessage("lock_writer_footer", (Object[])null) + Helper.cr());
        this.session.log(SessionLog.SEVERE, SessionLog.CACHE, writer.toString(), null, null, false);
    }

    /**
     * Used to print all the Locks in the specified identity map in this session.
     * The output of this method will be logged to this session's SessionLog at FINEST level.
     */
    public void printLocks(Class theClass) {
        ClassDescriptor descriptor = this.session.getDescriptor(theClass);
        StringWriter writer = new StringWriter();
        HashMap threadCollection = new HashMap();
        writer.write(TraceLocalization.buildMessage("lock_writer_header", (Object[])null) + Helper.cr());
        IdentityMap identityMap = getIdentityMap(descriptor, false);
        identityMap.collectLocks(threadCollection);

        Object[] parameters = new Object[1];
        for (Iterator threads = threadCollection.keySet().iterator(); threads.hasNext();) {
            Thread activeThread = (Thread)threads.next();
            parameters[0] = activeThread.getName();
            writer.write(TraceLocalization.buildMessage("active_thread", parameters) + Helper.cr());
            for (Iterator cacheKeys = ((HashSet)threadCollection.get(activeThread)).iterator();
                     cacheKeys.hasNext();) {
                CacheKey cacheKey = (CacheKey)cacheKeys.next();
                parameters[0] = cacheKey.getObject();
                writer.write(TraceLocalization.buildMessage("locked_object", parameters) + Helper.cr());
                parameters[0] = Integer.valueOf(cacheKey.getDepth());
                writer.write(TraceLocalization.buildMessage("depth", parameters) + Helper.cr());
            }
            DeferredLockManager deferredLockManager = ConcurrencyManager.getDeferredLockManager(activeThread);
            if (deferredLockManager != null) {
                for (Iterator deferredLocks = deferredLockManager.getDeferredLocks().iterator();
                         deferredLocks.hasNext();) {
                    ConcurrencyManager lock = (ConcurrencyManager)deferredLocks.next();
                    if (lock instanceof CacheKey){
                        parameters[0] = ((CacheKey)lock).getObject();
                        writer.write(TraceLocalization.buildMessage("deferred_locks", parameters) + Helper.cr());
                    }
                }
            }
        }
        writer.write(Helper.cr() + TraceLocalization.buildMessage("lock_writer_footer", (Object[])null) + Helper.cr());
        this.session.log(SessionLog.SEVERE, SessionLog.CACHE, writer.toString(), null, null, false);
    }

    /**
     * Register the object with the identity map.
     * The object must always be registered with its version number if optimistic locking is used.
     * The readTime may also be included in the cache key as it is constructed
     */
    public CacheKey putInIdentityMap(Object domainObject, Object keys, Object writeLockValue, long readTime, ClassDescriptor descriptor) {
        if (keys == null) {
            return null;
        }
        ObjectBuilder builder = descriptor.getObjectBuilder();
        Object implementation = builder.unwrapObject(domainObject, this.session);

        IdentityMap map = getIdentityMap(descriptor, false);
        CacheKey cacheKey;

        if (this.isCacheAccessPreCheckRequired) {
            this.session.startOperationProfile(SessionProfiler.Caching);
            // This is atomic so considered a read lock.
            acquireReadLock();
            try {
                cacheKey = map.put(keys, implementation, writeLockValue, readTime);
            } finally {
                releaseReadLock();
            }
            this.session.endOperationProfile(SessionProfiler.Caching);
        } else {
            cacheKey = map.put(keys, implementation, writeLockValue, readTime);
        }
        return cacheKey;
    }

    /**
     * Set the results for a query.
     * Query results are cached based on the parameter values provided to the query
     * different parameter values access different caches.
     */
    public void putQueryResult(ReadQuery query, List parameters, Object results) {
        if ((results == null) || (results == InvalidObject.instance())) {
            if (query.getQueryResultsCachePolicy().isNullIgnored()) {
                return;
            }
        }
        // PERF: use query name, unless no name.
        Object queryKey = query.getName();
        if ((queryKey == null) || ((String)queryKey).length() == 0) {
            queryKey = query;
        }
        IdentityMap map = this.queryResults.get(queryKey);
        if (map == null) {
            synchronized (this.queryResults) {
                map = this.queryResults.get(queryKey);
                if (map == null) {
                    int size = query.getQueryResultsCachePolicy().getMaximumCachedResults();
                    // PERF: If no parameters, then there can only be one result.
                    if ((parameters == null) || parameters.isEmpty()) {
                        size = 1;
                    }
                    map = buildNewIdentityMap(query.getQueryResultsCachePolicy().getCacheType(), size, null, false);
                    this.queryResults.put(queryKey, map);
                    // Mark the query to be invalidated for the query classes.
                    if (query.getQueryResultsCachePolicy().getInvalidateOnChange()) {
                        for (Class queryClass : query.getQueryResultsCachePolicy().getInvalidationClasses()) {
                            List invalidations = this.queryResultsInvalidationsByClass.get(queryClass);
                            if (invalidations == null) {
                                invalidations = new ArrayList();
                                this.queryResultsInvalidationsByClass.put(queryClass, invalidations);
                            }
                            invalidations.add(queryKey);
                        }
                    }
                }
            }
        }
        Object lookupParameters;
        if ((parameters == null) || parameters.isEmpty()) {
            lookupParameters = CacheId.EMPTY;
        } else {
            lookupParameters = new CacheId(parameters.toArray());
        }
        long queryTime = 0;
        if (query.isObjectLevelReadQuery()) {
            queryTime = ((ObjectLevelReadQuery)query).getExecutionTime();
        }
        if (queryTime == 0) {
            queryTime = System.currentTimeMillis();
        }
        // Bug 6138532 - store InvalidObject for "no results", do not store null
        if (results == null) {
            results = InvalidObject.instance();
        }
        map.put(lookupParameters, results, null, queryTime);
    }

    /**
     * Read-release the local-map and the entire cache.
     */
    protected void releaseReadLock() {
        if (this.session.getDatasourceLogin().shouldSynchronizedReadOnWrite()) {
            getCacheMutex().releaseReadLock();
        }
    }

    /**
     * Lock the entire cache if the cache isolation requires.
     * By default concurrent reads and writes are allowed.
     * By write, unit of work merge is meant.
     */
    public void releaseWriteLock() {
       if (this.session.getDatasourceLogin().shouldSynchronizedReadOnWrite() || this.session.getDatasourceLogin().shouldSynchronizeWrites()) {
             getCacheMutex().release();
        }
    }

    /**
     * Remove the object from the object cache.
     */
    public Object removeFromIdentityMap(Object key, Class domainClass, ClassDescriptor descriptor, Object objectToRemove) {
        if (key == null) {
            return null;
        }
        IdentityMap map = getIdentityMap(descriptor, false);
        Object value;

        if (this.isCacheAccessPreCheckRequired) {
            this.session.startOperationProfile(SessionProfiler.Caching);
            // This is atomic so considered a read lock.
            acquireReadLock();
            try {
                value = map.remove(key, objectToRemove);
            } finally {
                releaseReadLock();
            }
            this.session.endOperationProfile(SessionProfiler.Caching);
        } else {
            value = map.remove(key, objectToRemove);
        }
        return value;
    }

    /**
     * Set the cache mutex.
     * This allows for the entire cache to be locked.
     * This is done for transaction isolations on merges, although never locked by default.
     */
    protected void setCacheMutex(ConcurrencyManager cacheMutex) {
        this.cacheMutex = cacheMutex;
    }

    public void setIdentityMaps(ConcurrentMap identityMaps) {
        clearLastAccessedIdentityMap();
        this.identityMaps = identityMaps;
    }

    protected void setSession(AbstractSession session) {
        this.session = session;
    }

    /**
     * Update the wrapper object the cache key associated with the given primary key,
     * this is used for EJB.
     */
    public void setWrapper(Object primaryKey, Class theClass, Object wrapper) {
        ClassDescriptor descriptor = this.session.getDescriptor(theClass);
        IdentityMap map = getIdentityMap(descriptor, false);

        if (this.isCacheAccessPreCheckRequired) {
            this.session.startOperationProfile(SessionProfiler.Caching);
            // This is atomic so considered a read lock.
            acquireReadLock();
            try {
                map.setWrapper(primaryKey, wrapper);
            } finally {
                releaseReadLock();
            }
            this.session.endOperationProfile(SessionProfiler.Caching);
        } else {
            map.setWrapper(primaryKey, wrapper);
        }
    }

    /**
     * Update the write lock value of the cache key associated with the given primary key,
     */
    public void setWriteLockValue(Object primaryKey, Class theClass, Object writeLockValue) {
        if (primaryKey == null) {
            return;
        }
        ClassDescriptor descriptor = this.session.getDescriptor(theClass);
        IdentityMap map = getIdentityMap(descriptor, false);

        if (this.isCacheAccessPreCheckRequired) {
            this.session.startOperationProfile(SessionProfiler.Caching);
            // This is atomic so considered a read lock.
            acquireReadLock();
            try {
                map.setWriteLockValue(primaryKey, writeLockValue);
            } finally {
                releaseReadLock();
            }
            this.session.endOperationProfile(SessionProfiler.Caching);
        } else {
            map.setWriteLockValue(primaryKey, writeLockValue);
        }
    }

    /**
     * This method is used to resolve the inheritance issues arisen when conforming from the identity map
     * 1. Avoid reading the unintended subclass during in-memory query(e.g. when querying on large project, do not want
     *    to check small project,  both are inherited from the project, and stored in the same identity map).
     * 2. EJB container-generated classes broke the inheritance hierarchy. Need to use associated descriptor to track
     *    the relationship. CR4005-2612426, King-Sept-18-2002
     */
    protected Object checkForInheritance(Object domainObject, Class superClass, ClassDescriptor descriptor) {
        if ((domainObject != null) && ((domainObject.getClass() != superClass) && (!superClass.isInstance(domainObject)))) {
            // Before returning null, check if we are using EJB inheritance.
            if (descriptor.hasInheritance() && descriptor.getInheritancePolicy().getUseDescriptorsToValidateInheritedObjects()) {
                // EJB inheritance on the descriptors, not the container-generated classes/objects. We need to check the
                // identity map for the bean instance through the descriptor.
                if (descriptor.getInheritancePolicy().getSubclassDescriptor(domainObject.getClass()) == null) {
                    return null;
                }
                return domainObject;
            }
            return null;
        }
        return domainObject;
    }
}
