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
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.identitymaps.*;
import org.eclipse.persistence.internal.sessions.IdentityMapAccessor;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.queries.*;

/**
 * INTERNAL:
 * IdentityMapAccessor subclass for UnitOfWork
 * Overrides some initialization functionality and some behavior having to do with
 * getting objects from identity maps.
 */
public class UnitOfWorkIdentityMapAccessor extends IdentityMapAccessor {
    public UnitOfWorkIdentityMapAccessor(AbstractSession session, IdentityMapManager identityMapManager) {
        super(session, identityMapManager);
    }

    /**
     * ADVANCED:
     * Clear all the query caches
     */
    public void clearQueryCache() {
        ((UnitOfWorkImpl)getSession()).getParent().getIdentityMapAccessor().clearQueryCache();
    }

    /**
     * ADVANCED:
     * Clear the query class associated with the passed-in read query
     */
    public void clearQueryCache(ReadQuery query) {
        ((UnitOfWorkImpl)getSession()).getParent().getIdentityMapAccessor().clearQueryCache(query);
    }

    /**
     * ADVANCED:
     * Clear the query cache associated with the named query on the session
     */
    public void clearQueryCache(String sessionQueryName) {
        ((UnitOfWorkImpl)getSession()).getParent().getIdentityMapAccessor().clearQueryCache((ReadQuery)session.getQuery(sessionQueryName));
    }

    /**
     * ADVANCED:
     * Clear the query cache associated with the named query on the descriptor for the given class
     */
    public void clearQueryCache(String descriptorQueryName, Class queryClass) {
        ((UnitOfWorkImpl)getSession()).getParent().getIdentityMapAccessor().clearQueryCache((ReadQuery)session.getDescriptor(queryClass).getQueryManager().getQuery(descriptorQueryName));
    }

    /**
     * INTERNAL:
     * Return if their is an object for the primary key.
     */
    public boolean containsObjectInIdentityMap(Vector primaryKey, Class theClass, ClassDescriptor descriptor) {
        if (getIdentityMapManager().containsKey(primaryKey, theClass, descriptor)) {
            return true;
        }
        return ((UnitOfWorkImpl)getSession()).getParent().getIdentityMapAccessorInstance().containsObjectInIdentityMap(primaryKey, theClass, descriptor);
    }

    /**
     * INTERNAL:
     * This method overrides the getAllFromIdentityMap method in Session.  Invalidated Objects
     * will always be returned from a UnitOfWork.
     */
    public Vector getAllFromIdentityMap(Expression selectionCriteria, Class theClass, AbstractRecord translationRow, int valueHolderPolicy, boolean shouldReturnInvalidatedObjects) throws QueryException {
        return super.getAllFromIdentityMap(selectionCriteria, theClass, translationRow, valueHolderPolicy, true);
    }

    /**
     * INTERNAL:
     * Override the getFromIdentityMapWithDeferredLock method on the session to ensure that
     * invalidated objects are always returned since this is a UnitOfWork
     */
    public Object getFromIdentityMapWithDeferredLock(Vector primaryKey, Class theClass, boolean shouldReturnInvalidatedObjects, ClassDescriptor descriptor) {
        return super.getFromIdentityMapWithDeferredLock(primaryKey, theClass, true, descriptor);
    }

    /**
     * INTERNAL:
     * Return the object from the identity map with the primary key and class.
     * The parent's cache must be checked after the child's,
     * if found in the parent, it must be registered/cloned (but must avoid looping).
     * Note: in a UnitOfWork, invalidated objects will always be returned from the identity map
     * In the parent session, only return the object if it has not been Invalidated
     */
    public Object getFromIdentityMap(Vector primaryKey, Class theClass, boolean shouldReturnInvalidatedObjects, ClassDescriptor descriptor) {
        Object objectFromCache = super.getFromIdentityMap(primaryKey, theClass, true, descriptor);

        if (objectFromCache != null) {
            return objectFromCache;
        }
        //Bug#4613774  In the parent session, only return the object if it has not been Invalidated
        return getAndCloneCacheKeyFromParent(primaryKey, theClass, shouldReturnInvalidatedObjects, descriptor);
    }

    /**
     * INTERNAL:
     * This method will return the object from the parent and clone it.
     */
    protected Object getAndCloneCacheKeyFromParent(Vector primaryKey, Class theClass, boolean shouldReturnInvalidatedObjects, ClassDescriptor descriptor) {
        // Note: Objects returned from the parent's identity map should include invalidated
        // objects. This is important because this internal method is used in the existence
        // check in the UnitOfWork.
        UnitOfWorkImpl unitOfWork = (UnitOfWorkImpl)getSession();
        org.eclipse.persistence.internal.sessions.IdentityMapAccessor parentIdentityMapAccessor = unitOfWork.getParent().getIdentityMapAccessorInstance();
        CacheKey cacheKey = parentIdentityMapAccessor.getCacheKeyForObject(primaryKey, theClass, descriptor);
        if ((cacheKey == null) && unitOfWork.getParent().isUnitOfWork()) {
            //for nested unit of work
            //make parent clone and register object
            ((UnitOfWorkIdentityMapAccessor)parentIdentityMapAccessor).getAndCloneCacheKeyFromParent(primaryKey, theClass, shouldReturnInvalidatedObjects, descriptor);
            //get the cachekey that was created in the parent.
            cacheKey = parentIdentityMapAccessor.getCacheKeyForObject(primaryKey, theClass, descriptor);
        }

        Object objectFromCache = null;
        // this check could be simplified to one line but would create a window
        // in which GC could remove the object and we would end up with a null pointer
        // as well we must inspect the cacheKey without locking on it.
        if ((cacheKey != null) && (shouldReturnInvalidatedObjects || !descriptor.getCacheInvalidationPolicy().isInvalidated(cacheKey))) {
            synchronized (cacheKey.getMutex()) {
                //if the object in the cachekey is null but the key is acquired then
                //someone must be rebuilding it or creating a new one.  Sleep until
                // it's finished. A plain wait here would be more efficient but we may not
                // get notified for quite some time (ie deadlock) if the other thread
                //is building the object.  Must wait and not sleep in order for the monitor to be released
                objectFromCache = cacheKey.getObject();
                try {
                    while (cacheKey.isAcquired() && (objectFromCache == null)) {
                        cacheKey.getMutex().wait(5);
                    }
                } catch (InterruptedException ex) {
                }
                if (objectFromCache == null) {
                    return null;
                }
            }
        } else {
            return null;
        }

        // Consider read-only class CR#4094
        if (getSession().isClassReadOnly(theClass, descriptor)) {
            // PERF: Just return the original object.
            return objectFromCache;
        }

        if(getSession() instanceof RepeatableWriteUnitOfWork ) {
            Object unregisteredDeletedClone = ((RepeatableWriteUnitOfWork)getSession()).getUnregisteredDeletedCloneForOriginal(objectFromCache);
            if(unregisteredDeletedClone != null) {
                return unregisteredDeletedClone;
            }
        }
        
        return unitOfWork.cloneAndRegisterObject(objectFromCache, cacheKey, descriptor);
    }

    /**
     * INTERNAL:
     * Get the cached results associated with a query.  Results are cached by the
     * values of the parameters to the query so different parameters will have
     * different cached results.
     *
     * results are only cached in the parent session for UnitOfWorks
     */
    public Object getQueryResult(ReadQuery query, Vector parameters, boolean checkExpiry) {
        return ((UnitOfWorkImpl)getSession()).getParent().getIdentityMapAccessorInstance().getQueryResult(query, parameters, checkExpiry);
    }

    /**
     *  INTERNAL:
     *  Set the results for a query.
     *  Query results are cached based on the parameter values provided to the query
     *  different parameter values access different caches.
     *
     *  Results are only cached in the parent session for UnitOfWorks
     */
    public void putQueryResult(ReadQuery query, Vector parameters, Object results) {
        ((UnitOfWorkImpl)getSession()).getParent().getIdentityMapAccessorInstance().putQueryResult(query, parameters, results);
    }

    /**
     * INTERNAL:
     * Reset the entire object cache,
     * ** be careful using this.
     * This method blows away both this session's and its parents caches,
     * this includes the server cache or any other cache.
     * This throws away any objects that have been read in.
     * Extreme caution should be used before doing this because object identity will no longer
     * be maintained for any objects currently read in.  This should only be called
     * if the application knows that it no longer has references to object held in the cache.
     */
    public void initializeAllIdentityMaps() {
        super.initializeAllIdentityMaps();
        ((UnitOfWorkImpl)getSession()).getParent().getIdentityMapAccessor().initializeAllIdentityMaps();
    }
}
