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
 *     02/11/2009-1.1 Michael O'Brien 
 *        - 259993: As part 2) During mergeClonesAfterCompletion() 
 *           If the the acquire and release threads are different 
 *           switch back to the stored acquire thread stored on the mergeManager.
 *      tware, David Mulligan - fix performance issue with releasing locks
 ******************************************************************************/  
package org.eclipse.persistence.internal.helper;

import java.util.*;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.FetchGroupManager;
import org.eclipse.persistence.exceptions.ConcurrencyException;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.internal.sessions.*;
import org.eclipse.persistence.internal.identitymaps.*;
import org.eclipse.persistence.internal.localization.TraceLocalization;
import org.eclipse.persistence.internal.helper.linkedlist.*;
import org.eclipse.persistence.logging.SessionLog;

/**
 * INTERNAL:
 * <p>
 * <b>Purpose</b>: Acquires all required locks for a particular merge process.
 * Implements a deadlock avoidance algorithm to prevent concurrent merge conflicts.
 *
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Acquires locks for writing threads.
 * <li> Provides deadlock avoidance behavior.
 * <li> Releases locks for writing threads.
 * </ul>
 * @author Gordon Yorke
 * @since 10.0.3
 */
public class WriteLockManager {

    // this will allow us to prevent a readlock thread from looping forever.
    public static int MAXTRIES = 10000;
    
    public static int MAX_WAIT = 600000; //10 mins

    /* This attribute stores the list of threads that have had a problem acquiring locks */
    /*  the first element in this list will be the prevailing thread */
    protected ExposedNodeLinkedList prevailingQueue;

    public WriteLockManager() {
        this.prevailingQueue = new ExposedNodeLinkedList();
    }

    /**
     * INTERNAL:
     * This method will return once the object is locked and all non-indirect
     * related objects are also locked.
     */
    public Map acquireLocksForClone(Object objectForClone, ClassDescriptor descriptor, CacheKey cacheKey, AbstractSession cloningSession) {
        boolean successful = false;
        IdentityHashMap lockedObjects = new IdentityHashMap();
        IdentityHashMap refreshedObjects = new IdentityHashMap();
        try {
            // if the descriptor has indirection for all mappings then wait as there will be no deadlock risks
            CacheKey toWaitOn = acquireLockAndRelatedLocks(objectForClone, lockedObjects, refreshedObjects, cacheKey, descriptor, cloningSession);
            int tries = 0;
            while (toWaitOn != null) {// loop until we've tried too many times.
                for (Iterator lockedList = lockedObjects.values().iterator(); lockedList.hasNext();) {
                    ((CacheKey)lockedList.next()).releaseReadLock();
                    lockedList.remove();
                }
                synchronized (toWaitOn) {
                    try {
                        if (toWaitOn.isAcquired()) {//last minute check to insure it is still locked.
                            toWaitOn.wait();// wait for lock on object to be released
                        }
                    } catch (InterruptedException ex) {
                        // Ignore exception thread should continue.
                    }
                }
                Object waitObject = toWaitOn.getObject();
                // Object may be null for loss of identity.
                if (waitObject != null) {
                    cloningSession.checkAndRefreshInvalidObject(waitObject, toWaitOn, cloningSession.getDescriptor(waitObject));
                    refreshedObjects.put(waitObject, waitObject);
                }
                toWaitOn = acquireLockAndRelatedLocks(objectForClone, lockedObjects, refreshedObjects, cacheKey, descriptor, cloningSession);
                if ((toWaitOn != null) && ((++tries) > MAXTRIES)) {
                    // If we've tried too many times abort.
                    throw ConcurrencyException.maxTriesLockOnCloneExceded(objectForClone);
                }
            }
            successful = true;//successfully acquired all locks
        } finally {
            if (!successful) {//did not acquire locks but we are exiting
                for (Iterator lockedList = lockedObjects.values().iterator(); lockedList.hasNext();) {
                    ((CacheKey)lockedList.next()).releaseReadLock();
                    lockedList.remove();
                }
            }
        }
        return lockedObjects;
    }

    /**
     * INTERNAL:
     * This is a recursive method used to acquire read locks on all objects that
     * will be cloned.  These include all related objects for which there is no
     * indirection.
     * The returned object is the first object that the lock could not be acquired for.
     * The caller must try for exceptions and release locked objects in the case
     * of an exception.
     */
    public CacheKey acquireLockAndRelatedLocks(Object objectForClone, Map lockedObjects, Map refreshedObjects, CacheKey cacheKey, ClassDescriptor descriptor, AbstractSession cloningSession) {
        if (!refreshedObjects.containsKey(objectForClone) && cloningSession.isConsideredInvalid(objectForClone, cacheKey, descriptor)) {
            return cacheKey;
        }
        // Attempt to get a read-lock, null is returned if cannot be read-locked.
        if (cacheKey.acquireReadLockNoWait()) {
            if (cacheKey.getObject() == null) {
                // This will be the case for deleted objects, NoIdentityMap, and aggregates.
                lockedObjects.put(objectForClone, cacheKey);
            } else {
                objectForClone = cacheKey.getObject();
                if (lockedObjects.containsKey(objectForClone)) {
                    // This is a check for loss of identity, the original check in
                    // checkAndLockObject() will shortcircuit in the usual case.
                    cacheKey.releaseReadLock();
                    return null;
                }
                // Store locked cachekey for release later.
                lockedObjects.put(objectForClone, cacheKey);
            }
            return traverseRelatedLocks(objectForClone, lockedObjects, refreshedObjects, descriptor, cloningSession);
        } else {
            // Return the cache key that could not be locked.
            return cacheKey;
        }
    }
    
    /**
     * INTERNAL: 
     * This method will transition the previously acquired active
     * locks to deferred locks in the case a readlock could not be acquired for
     * a related object. Deferred locks must be employed to prevent deadlock
     * when waiting for the readlock while still protecting readers from
     * incomplete data.
     */
    public void transitionToDeferredLocks(MergeManager mergeManager){
        try{
            if (mergeManager.isTransitionedToDeferredLocks()) return;
            for (CacheKey cacheKey : mergeManager.getAcquiredLocks()){
                cacheKey.transitionToDeferredLock();
            }
            mergeManager.transitionToDeferredLocks();
        }catch (RuntimeException ex){
            for (CacheKey cacheKey : mergeManager.getAcquiredLocks()){
                cacheKey.release();
            }
            ConcurrencyManager.getDeferredLockManager(Thread.currentThread()).setIsThreadComplete(true);
            ConcurrencyManager.removeDeferredLockManager(Thread.currentThread());
            mergeManager.getAcquiredLocks().clear();
            throw ex;
        }
    }
    
    /**
     * INTERNAL:
     * Traverse the object and acquire locks on all related objects.
     */
    public CacheKey traverseRelatedLocks(Object objectForClone, Map lockedObjects, Map refreshedObjects, ClassDescriptor descriptor, AbstractSession cloningSession) {
        // If all mappings have indirection short-circuit.
        if (descriptor.shouldAcquireCascadedLocks()) {
            FetchGroupManager fetchGroupManager = descriptor.getFetchGroupManager();
            boolean isPartialObject = (fetchGroupManager != null) && fetchGroupManager.isPartialObject(objectForClone);
            for (Iterator mappings = descriptor.getLockableMappings().iterator();
                     mappings.hasNext();) {
                DatabaseMapping mapping = (DatabaseMapping)mappings.next();
                // Only cascade fetched mappings.
                if (!isPartialObject || (fetchGroupManager.isAttributeFetched(objectForClone, mapping.getAttributeName()))) {
                    // any mapping in this list must not have indirection.
                    Object objectToLock = mapping.getAttributeValueFromObject(objectForClone);
                    if (mapping.isCollectionMapping()) {
                        // Ignore null, means empty.
                        if (objectToLock != null) {
                            ContainerPolicy cp = mapping.getContainerPolicy();
                            Object iterator = cp.iteratorFor(objectToLock);
                            while (cp.hasNext(iterator)) {
                                Object object = cp.next(iterator, cloningSession);
                                if (mapping.getReferenceDescriptor().hasWrapperPolicy()) {
                                    object = mapping.getReferenceDescriptor().getWrapperPolicy().unwrapObject(object, cloningSession);
                                }
                            CacheKey toWaitOn = checkAndLockObject(object, lockedObjects, refreshedObjects, mapping, cloningSession);
                                if (toWaitOn != null) {
                                    return toWaitOn;
                                }
                            }
                        }
                    } else {
                        if (mapping.getReferenceDescriptor().hasWrapperPolicy()) {
                            objectToLock = mapping.getReferenceDescriptor().getWrapperPolicy().unwrapObject(objectToLock, cloningSession);
                        }
                    CacheKey toWaitOn = checkAndLockObject(objectToLock, lockedObjects, refreshedObjects, mapping, cloningSession);
                        if (toWaitOn != null) {
                            return toWaitOn;
                        }
                    }
                }
            }
        }
        return null;
    }
    
    /**
     * INTERNAL:
     * This method will be the entry point for threads attempting to acquire locks for all objects that have
     * a changeset.  This method will hand off the processing of the deadlock algorithm to other member
     * methods.  The mergeManager must be the active mergemanager for the calling thread.
     * Returns true if all required locks were acquired
     */
    public void acquireRequiredLocks(MergeManager mergeManager, UnitOfWorkChangeSet changeSet) {
        if (!MergeManager.LOCK_ON_MERGE) {//lockOnMerge is a backdoor and not public
            return;
        }
        boolean locksToAcquire = true;

        //while that thread has locks to acquire continue to loop.
        try {
            // initialize the MergeManager during this commit or merge for insert/updates only
            // this call is not required in acquireLocksForClone() or acquireLockAndRelatedLocks()
            mergeManager.setLockThread(Thread.currentThread());
            
            AbstractSession session = mergeManager.getSession();
            // If the session in the mergemanager is not a unit of work then the
            // merge is of a changeSet into a distributed session.
            if (session.isUnitOfWork()) {
                session = ((UnitOfWorkImpl)session).getParent();
            }
            while (locksToAcquire) {
                //lets assume all locks will be acquired
                locksToAcquire = false;
                //first access the changeSet and begin to acquire locks
                ClassDescriptor descriptor = null;
                for (ObjectChangeSet objectChangeSet : changeSet.getAllChangeSets().values()) {
                    // No Need to acquire locks for invalidated objects.
                    if ((mergeManager.shouldMergeChangesIntoDistributedCache() && (objectChangeSet.getSynchronizationType() == ClassDescriptor.INVALIDATE_CHANGED_OBJECTS))
                            || objectChangeSet.getId() == null) {
                        //skip this process as we will be unable to acquire the correct cachekey anyway
                        //this is a new object with identity after write sequencing, ? huh, all objects must have an id by merge?
                        continue;
                    }
                    descriptor = objectChangeSet.getDescriptor();
                    // Maybe null for distributed merge, initialize it.
                    if (descriptor == null) {
                        descriptor = session.getDescriptor(objectChangeSet.getClassType(session));
                        objectChangeSet.setDescriptor(descriptor);
                    }
                    // PERF: Do not merge nor lock into the session cache if descriptor set to unit of work isolated.
                    if (descriptor.getCachePolicy().shouldIsolateObjectsInUnitOfWork()) {
                        continue;
                    }
                    AbstractSession targetSession = session.getParentIdentityMapSession(descriptor, true, true);
                    CacheKey activeCacheKey = attemptToAcquireLock(descriptor, objectChangeSet.getId(), targetSession);
                    if (activeCacheKey == null) {
                        // if cacheKey is null then the lock was not available no need to synchronize this block,because if the
                        // check fails then this thread will just return to the queue until it gets woken up.
                        if (this.prevailingQueue.getFirst() == mergeManager) {
                            // wait on this object until it is free,  or until wait time expires because
                            // this thread is the prevailing thread
                            activeCacheKey = waitOnObjectLock(descriptor, objectChangeSet.getId(), targetSession, (int)Math.round((Math.random()*500)));
                        }
                        if (activeCacheKey == null) {
                            // failed to acquire lock, release all acquired
                            // locks and place thread on waiting list
                            releaseAllAcquiredLocks(mergeManager);
                            // get cacheKey
                            activeCacheKey = targetSession.getIdentityMapAccessorInstance().getCacheKeyForObjectForLock(objectChangeSet.getId(), descriptor.getJavaClass(), descriptor);
                            if (session.shouldLog(SessionLog.FINER, SessionLog.CACHE)) {
                                Object[] params = new Object[3];
                                params[0] = descriptor.getJavaClass();
                                params[1] = objectChangeSet.getId();
                                params[2] = Thread.currentThread().getName();
                                session.log(SessionLog.FINER, SessionLog.CACHE, "dead_lock_encountered_on_write_no_cachekey", params, null);
                            }
                            if (mergeManager.getWriteLockQueued() == null) {
                                // thread is entering the wait queue for the
                                // first time
                                // set the QueueNode to be the node from the
                                // linked list for quick removal upon
                                // acquiring all locks
                                synchronized (this.prevailingQueue) {
                                    mergeManager.setQueueNode(this.prevailingQueue.addLast(mergeManager));
                                }
                            }

                            // set the cache key on the merge manager for
                            // the object that could not be acquired
                            mergeManager.setWriteLockQueued(objectChangeSet.getId());
                            try {
                                if (activeCacheKey != null){
                                    //wait on the lock of the object that we couldn't get.
                                    synchronized (activeCacheKey) {
                                        // verify that the cache key is still locked before we wait on it, as
                                        //it may have been released since we tried to acquire it.
                                        if (activeCacheKey.isAcquired() && (activeCacheKey.getActiveThread() != Thread.currentThread())) {
                                                Thread thread = activeCacheKey.getActiveThread();
                                                if (thread.isAlive()){
                                                    long time = System.currentTimeMillis();
                                                    activeCacheKey.wait(MAX_WAIT);
                                                    if (System.currentTimeMillis() - time >= MAX_WAIT){
                                                        Object[] params = new Object[]{MAX_WAIT /1000, descriptor.getJavaClassName(), activeCacheKey.getKey(), thread.getName()};
                                                        StringBuilder buffer = new StringBuilder(TraceLocalization.buildMessage("max_time_exceeded_for_acquirerequiredlocks_wait", params));
                                                        StackTraceElement[] trace = thread.getStackTrace();
                                                        for (StackTraceElement element : trace){
                                                            buffer.append("\t\tat");
                                                            buffer.append(element.toString());
                                                            buffer.append("\n");
                                                        }
                                                        session.log(SessionLog.SEVERE, SessionLog.CACHE, buffer.toString());
                                                        session.getIdentityMapAccessor().printIdentityMapLocks();
                                                    }
                                                }else{
                                                    session.log(SessionLog.SEVERE, SessionLog.CACHE, "releasing_invalid_lock", new Object[] { thread.getName(),descriptor.getJavaClass(), objectChangeSet.getId()});
                                                    //thread that held lock is no longer alive.  Something bad has happened like
                                                    while (activeCacheKey.isAcquired()){
                                                        // could have a depth greater than one.
                                                        activeCacheKey.release();
                                                    }
                                                }
                                            }
                                        }
                                    }
                            } catch (InterruptedException exception) {
                                throw org.eclipse.persistence.exceptions.ConcurrencyException.waitWasInterrupted(exception.getMessage());
                            }
                            // failed to acquire, exit this loop to restart all over again. 
                            locksToAcquire = true;
                            break;
                        }else{
                            objectChangeSet.setActiveCacheKey(activeCacheKey);
                            mergeManager.getAcquiredLocks().add(activeCacheKey);
                        }
                    } else {
                        objectChangeSet.setActiveCacheKey(activeCacheKey);
                        mergeManager.getAcquiredLocks().add(activeCacheKey);
                    }
                }
            }
        } catch (RuntimeException exception) {
            // if there was an exception then release.
            //must not release in a finally block as release only occurs in this method
            // if there is a problem or all of the locks can not be acquired.
            releaseAllAcquiredLocks(mergeManager);
            throw exception;
        }catch (Error error){
            releaseAllAcquiredLocks(mergeManager);
            mergeManager.getSession().logThrowable(SessionLog.SEVERE, SessionLog.TRANSACTION, error);
            throw error;
        }finally {
            if (mergeManager.getWriteLockQueued() != null) {
                //the merge manager entered the wait queue and must be cleaned up
                synchronized(this.prevailingQueue) {
                    this.prevailingQueue.remove(mergeManager.getQueueNode());
                }
                mergeManager.setWriteLockQueued(null);
            }
        }
    }

    /**
     * INTERNAL:
     * This method will be called by a merging thread that is attempting to lock
     * a new object that was not locked previously.  Unlike the other methods
     * within this class this method will lock only this object.
     */
    public CacheKey appendLock(Object primaryKey, Object objectToLock, ClassDescriptor descriptor, MergeManager mergeManager, AbstractSession session) {
        CacheKey lockedCacheKey = session.getIdentityMapAccessorInstance().acquireLockNoWait(primaryKey, descriptor.getJavaClass(), false, descriptor);
        if (lockedCacheKey == null) {
            session.getIdentityMapAccessorInstance().getWriteLockManager().transitionToDeferredLocks(mergeManager);
            lockedCacheKey = session.getIdentityMapAccessorInstance().acquireDeferredLock(primaryKey, descriptor.getJavaClass(), descriptor, true);
            Object cachedObject = lockedCacheKey.getObject();
            if (cachedObject == null) {
                if (lockedCacheKey.getActiveThread() == Thread.currentThread()) {
                    lockedCacheKey.setObject(objectToLock);
                } else {
                    cachedObject = lockedCacheKey.waitForObject();
                }
            }
            lockedCacheKey.releaseDeferredLock();
            return lockedCacheKey;
        } else {
            if (lockedCacheKey.getObject() == null) {
                lockedCacheKey.setObject(objectToLock); // set the object in the
                // cachekey
                // for others to find an prevent cycles
            }
            if (mergeManager.isTransitionedToDeferredLocks()){
                lockedCacheKey.getDeferredLockManager(Thread.currentThread()).getActiveLocks().add(lockedCacheKey);
            }else{
                 mergeManager.getAcquiredLocks().add(lockedCacheKey);
            }
            return lockedCacheKey;
        }
    }
	
    /**
     * INTERNAL:
     * This method performs the operations of finding the cacheKey and locking it if possible.
     * Returns True if the lock was acquired, false otherwise
     */
    protected CacheKey attemptToAcquireLock(ClassDescriptor descriptor, Object primaryKey, AbstractSession session) {
        return session.getIdentityMapAccessorInstance().acquireLockNoWait(primaryKey, descriptor.getJavaClass(), true, descriptor);
    }

    /**
     * INTERNAL:
     * Simply check that the object is not already locked then pass it on to the locking method
     */
    protected CacheKey checkAndLockObject(Object objectToLock, Map lockedObjects, Map refreshedObjects, DatabaseMapping mapping, AbstractSession cloningSession) {
        //the cachekey should always reference an object otherwise what would we be cloning.
        if ((objectToLock != null) && !lockedObjects.containsKey(objectToLock)) {
            Object primaryKeyToLock = null;
            ClassDescriptor referenceDescriptor = null;
            if (mapping.getReferenceDescriptor().hasInheritance() || mapping.getReferenceDescriptor().isDescriptorForInterface()) {
                referenceDescriptor = cloningSession.getDescriptor(objectToLock);
            } else {
                referenceDescriptor = mapping.getReferenceDescriptor();
            }
            // Need to traverse aggregates, but not lock aggregates directly.
            if (referenceDescriptor.isDescriptorTypeAggregate()) {
                traverseRelatedLocks(objectToLock, lockedObjects, refreshedObjects, referenceDescriptor, cloningSession);
            } else {
                primaryKeyToLock = referenceDescriptor.getObjectBuilder().extractPrimaryKeyFromObject(objectToLock, cloningSession);
                CacheKey cacheKey = cloningSession.getIdentityMapAccessorInstance().getCacheKeyForObjectForLock(primaryKeyToLock, objectToLock.getClass(), referenceDescriptor);
                if (cacheKey == null) {
                    // Cache key may be null for no-identity map, missing or deleted object, just create a new one to be locked.
                    cacheKey = new CacheKey(primaryKeyToLock);
                    cacheKey.setReadTime(System.currentTimeMillis());
                }
                CacheKey toWaitOn = acquireLockAndRelatedLocks(objectToLock, lockedObjects, refreshedObjects, cacheKey, referenceDescriptor, cloningSession);
                if (toWaitOn != null) {
                    return toWaitOn;
                }
            }
        }
        return null;
    }

    /**
     * INTERNAL:
     * This method will release all acquired locks
     */
    public void releaseAllAcquiredLocks(MergeManager mergeManager) {
        if (!MergeManager.LOCK_ON_MERGE) {//lockOnMerge is a backdoor and not public
            return;
        }
        List acquiredLocks = mergeManager.getAcquiredLocks(); 
        Iterator locks = acquiredLocks.iterator();
        RuntimeException exception = null;
        while (locks.hasNext()) {
            try {
                CacheKey cacheKeyToRemove = (CacheKey) locks.next();
                if (cacheKeyToRemove.getObject() == null) {
                    cacheKeyToRemove.removeFromOwningMap();
                }
                if (mergeManager.isTransitionedToDeferredLocks()) {
                    cacheKeyToRemove.releaseDeferredLock();
                } else {
                    cacheKeyToRemove.release();
                }
            } catch (RuntimeException e){
                if (exception == null){
                    exception = e;
                }
            }
        }
        acquiredLocks.clear();
        if (exception != null){
            throw exception;
        }
    }

    /**
     * INTERNAL:
     * This method performs the operations of finding the cacheKey and locking it if possible.
     * Waits until the lock can be acquired
     */
    protected CacheKey waitOnObjectLock(ClassDescriptor descriptor, Object primaryKey, AbstractSession session, int waitTime) {
        return session.getIdentityMapAccessorInstance().acquireLockWithWait(primaryKey, descriptor.getJavaClass(), true, descriptor, waitTime);
    }
}
