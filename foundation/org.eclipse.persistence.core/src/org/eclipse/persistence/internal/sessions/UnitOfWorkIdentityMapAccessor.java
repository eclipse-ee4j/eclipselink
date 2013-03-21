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
package org.eclipse.persistence.internal.sessions;

import java.util.*;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.descriptors.PersistenceEntity;
import org.eclipse.persistence.internal.identitymaps.*;
import org.eclipse.persistence.internal.sessions.IdentityMapAccessor;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.sessions.Record;

/**
 * INTERNAL:
 * IdentityMapAccessor subclass for UnitOfWork
 * Overrides some initialization functionality and some behavior having to do with
 * getting objects from identity maps.
 */
public class UnitOfWorkIdentityMapAccessor extends IdentityMapAccessor {
    
    public UnitOfWorkIdentityMapAccessor() {
    }
    
    public UnitOfWorkIdentityMapAccessor(AbstractSession session, IdentityMapManager identityMapManager) {
        super(session, identityMapManager);
    }

    /**
     * ADVANCED:
     * Clear all the query caches
     */
    @Override
    public void clearQueryCache() {
        ((UnitOfWorkImpl)this.session).getParent().getIdentityMapAccessor().clearQueryCache();
    }

    /**
     * Invalidate/remove any results for the class from the query cache.
     * This is used to invalidate the query cache on any change.
     */
    public void invalidateQueryCache(Class classThatChanged) {
        ((UnitOfWorkImpl)this.session).getParent().getIdentityMapAccessor().invalidateQueryCache(classThatChanged);
    }

    /**
     * ADVANCED:
     * Clear the query class associated with the passed-in read query
     */
    @Override
    public void clearQueryCache(ReadQuery query) {
        ((UnitOfWorkImpl)this.session).getParent().getIdentityMapAccessor().clearQueryCache(query);
    }

    /**
     * ADVANCED:
     * Clear the query cache associated with the named query on the session
     */
    @Override
    public void clearQueryCache(String sessionQueryName) {
        ((UnitOfWorkImpl)this.session).getParent().getIdentityMapAccessor().clearQueryCache((ReadQuery)session.getQuery(sessionQueryName));
    }

    /**
     * ADVANCED:
     * Clear the query cache associated with the named query on the descriptor for the given class
     */
    @Override
    public void clearQueryCache(String descriptorQueryName, Class queryClass) {
        ((UnitOfWorkImpl)this.session).getParent().getIdentityMapAccessor().clearQueryCache((ReadQuery)session.getDescriptor(queryClass).getQueryManager().getQuery(descriptorQueryName));
    }

    /**
     * INTERNAL:
     * Return if their is an object for the primary key.
     */
    @Override
    public boolean containsObjectInIdentityMap(Object primaryKey, Class theClass, ClassDescriptor descriptor) {
        if (getIdentityMapManager().containsKey(primaryKey, theClass, descriptor)) {
            return true;
        }
        return ((UnitOfWorkImpl)this.session).getParent().getIdentityMapAccessorInstance().containsObjectInIdentityMap(primaryKey, theClass, descriptor);
    }

    /**
     * INTERNAL:
     * This method overrides the getAllFromIdentityMap method in Session.  Invalidated Objects
     * will always be returned from a UnitOfWork.
     */
    @Override
    public Vector getAllFromIdentityMap(Expression selectionCriteria, Class theClass, Record translationRow, int valueHolderPolicy, boolean shouldReturnInvalidatedObjects) throws QueryException {
        return super.getAllFromIdentityMap(selectionCriteria, theClass, translationRow, valueHolderPolicy, true);
    }

    /**
     * INTERNAL:
     * Override the getFromIdentityMapWithDeferredLock method on the session to ensure that
     * invalidated objects are always returned since this is a UnitOfWork
     */
    @Override
    public Object getFromIdentityMapWithDeferredLock(Object primaryKey, Class theClass, boolean shouldReturnInvalidatedObjects, ClassDescriptor descriptor) {
        Object objectFromCache = super.getFromIdentityMapWithDeferredLock(primaryKey, theClass, true, descriptor);
        if (objectFromCache != null){
            return objectFromCache;
        }
        return getAndCloneCacheKeyFromParent(primaryKey, null, theClass, shouldReturnInvalidatedObjects, descriptor);
    }

    /**
     * INTERNAL:
     * Return the object from the identity map with the primary key and class.
     * The parent's cache must be checked after the child's,
     * if found in the parent, it must be registered/cloned (but must avoid looping).
     * Note: in a UnitOfWork, invalidated objects will always be returned from the identity map
     * In the parent session, only return the object if it has not been Invalidated
     */
    @Override
    public Object getFromIdentityMap(Object primaryKey, Object object, Class theClass, boolean shouldReturnInvalidatedObjects, ClassDescriptor descriptor) {
        Object objectFromCache = super.getFromIdentityMap(primaryKey, object, theClass, true, descriptor);

        if (objectFromCache != null) {
            return objectFromCache;
        }
        //Bug#4613774  In the parent session, only return the object if it has not been Invalidated
        return getAndCloneCacheKeyFromParent(primaryKey, object, theClass, shouldReturnInvalidatedObjects, descriptor);
    }

    /**
     * INTERNAL:
     * This method will return the object from the parent and clone it.
     */
    protected Object getAndCloneCacheKeyFromParent(Object primaryKey, Object objectToClone, Class theClass, boolean shouldReturnInvalidatedObjects, ClassDescriptor descriptor) {
        // Note: Objects returned from the parent's identity map should include invalidated
        // objects. This is important because this internal method is used in the existence
        // check in the UnitOfWork.
        UnitOfWorkImpl unitOfWork = (UnitOfWorkImpl)this.session;
        CacheKey cacheKey = null;
        if (objectToClone != null && objectToClone instanceof PersistenceEntity){
            cacheKey = ((PersistenceEntity)objectToClone)._persistence_getCacheKey();
        }
            
        if (cacheKey == null || cacheKey.getOwningMap() == null){
            org.eclipse.persistence.internal.sessions.IdentityMapAccessor parentIdentityMapAccessor = unitOfWork.getParentIdentityMapSession(descriptor, false, false).getIdentityMapAccessorInstance();
            cacheKey = parentIdentityMapAccessor.getCacheKeyForObject(primaryKey, theClass, descriptor, false);
        }
        if ((cacheKey == null) && unitOfWork.getParentIdentityMapSession(descriptor, false, false).isUnitOfWork()) {
            //for nested unit of work
            //make parent clone and register object
            org.eclipse.persistence.internal.sessions.IdentityMapAccessor parentIdentityMapAccessor = unitOfWork.getParentIdentityMapSession(descriptor, false, false).getIdentityMapAccessorInstance();
            ((UnitOfWorkIdentityMapAccessor)parentIdentityMapAccessor).getAndCloneCacheKeyFromParent(primaryKey, null, theClass, shouldReturnInvalidatedObjects, descriptor);
            //get the cachekey that was created in the parent.
            cacheKey = parentIdentityMapAccessor.getCacheKeyForObject(primaryKey, theClass, descriptor, false);
        }

        Object objectFromCache = null;
        // this check could be simplified to one line but would create a window
        // in which GC could remove the object and we would end up with a null pointer
        // as well we must inspect the cacheKey without locking on it.
        if ((cacheKey != null) && (shouldReturnInvalidatedObjects || !descriptor.getCacheInvalidationPolicy().isInvalidated(cacheKey))) {
            synchronized (cacheKey) {
                //if the object in the cachekey is null but the key is acquired then
                //someone must be rebuilding it or creating a new one.  Sleep until
                // it's finished. A plain wait here would be more efficient but we may not
                // get notified for quite some time (ie deadlock) if the other thread
                //is building the object.  Must wait and not sleep in order for the monitor to be released
                objectFromCache = cacheKey.getObject();
                try {
                    while (cacheKey.isAcquired() && (objectFromCache == null)) {
                        cacheKey.wait(5);
                    }
                } catch (InterruptedException ex) {
                }
            }
            
            // check for inheritance.
            objectFromCache = checkForInheritance(objectFromCache, theClass, descriptor);
            if (objectFromCache == null) {
                return null;
            }
        } else {
            return null;
        }

        // Consider read-only class CR#4094
        if (this.session.isClassReadOnly(theClass, descriptor)) {
            // PERF: Just return the original object.
            return objectFromCache;
        }

        if (this.session instanceof RepeatableWriteUnitOfWork) {
            Object unregisteredDeletedClone = ((RepeatableWriteUnitOfWork)this.session).getUnregisteredDeletedCloneForOriginal(objectFromCache);
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
    @Override
    public Object getQueryResult(ReadQuery query, List parameters, boolean checkExpiry) {
        return ((UnitOfWorkImpl)this.session).getParent().getIdentityMapAccessorInstance().getQueryResult(query, parameters, checkExpiry);
    }

    /**
     *  INTERNAL:
     *  Set the results for a query.
     *  Query results are cached based on the parameter values provided to the query
     *  different parameter values access different caches.
     *
     *  Results are only cached in the parent session for UnitOfWorks
     */
    @Override
    public void putQueryResult(ReadQuery query, List parameters, Object results) {
        ((UnitOfWorkImpl)this.session).getParent().getIdentityMapAccessorInstance().putQueryResult(query, parameters, results);
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
    @Override
    public void initializeAllIdentityMaps() {
        super.initializeAllIdentityMaps();
        ((UnitOfWorkImpl)this.session).getParent().getIdentityMapAccessor().initializeAllIdentityMaps();
    }
    
    /**
     * This method is used to resolve the inheritance issues arise while trying to get object from 
     * identity map of parent session. Avoid reading the unintended subclass during in-memory query
     * (e.g. when querying on large project, do not want to check small project, both are inherited 
     * from the project, and stored in the same identity map).
     */
    protected Object checkForInheritance(Object domainObject, Class superClass, ClassDescriptor descriptor) {
        if ((domainObject != null) && ((domainObject.getClass() != superClass) && (!superClass.isInstance(domainObject)))) {
            if (descriptor.hasInheritance() && descriptor.getInheritancePolicy().getUseDescriptorsToValidateInheritedObjects()) {
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
