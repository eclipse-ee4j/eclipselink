/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 1998, 2018 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
//     02/11/2009-1.1 Michael O'Brien
//        - 259993: As part 2) During mergeClonesAfterCompletion()
//           If the the acquire and release threads are different
//           switch back to the stored acquire thread stored on the mergeManager.
//      tware, David Mulligan - fix performance issue with releasing locks
//     11/07/2017 - Dalia Abo Sheasha
//       - 526957 : Split the logging and trace messages
package org.eclipse.persistence.internal.helper;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.FetchGroupManager;
import org.eclipse.persistence.exceptions.ConcurrencyException;
import org.eclipse.persistence.internal.helper.linkedlist.ExposedNodeLinkedList;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.localization.LoggingLocalization;
import org.eclipse.persistence.internal.localization.TraceLocalization;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.MergeManager;
import org.eclipse.persistence.internal.sessions.ObjectChangeSet;
import org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.mappings.DatabaseMapping;

import static java.util.Collections.unmodifiableMap;

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

    /**
     * The code spots where we use this constant are code spots where we afraid the thread might be trying to run a
     * commit. Blowing up the thread with an interrupted exception might be too dangerous. We are not certain the
     * eclipselink code is able to cope with it and release all resources appropriately.
     *
     */
    private static final Boolean ALLOW_INTERRUPTED_EXCEPTION_TO_BE_FIRED_UP_FALSE = false;

    /**
     * This flag we use if the write lock manager is stuck building clones of objects. Because we are not in the code
     * area of commit to the db anything.
     */
    private static final Boolean ALLOW_INTERRUPTED_EXCEPTION_TO_BE_FIRED_UP_TRUE = true;

    /**
     * This a map from a thread to cache keys the thread is finding itself not being able to acquire. This map is
     * important to explain why a thread might be stuck in a stack trace of the form: {@code
     * at java.lang.Class.getEnclosingMethod0(Native Method)
    at java.lang.Class.getEnclosingMethodInfo(Class.java:1072)
    at java.lang.Class.getEnclosingClass(Class.java:1272)
    at java.lang.Class.getSimpleBinaryName(Class.java:1443)
    at java.lang.Class.getSimpleName(Class.java:1309)
    at org.eclipse.persistence.internal.identitymaps.IdentityMapManager.acquireLockNoWait(IdentityMapManager.java:205)
    at org.eclipse.persistence.internal.sessions.IdentityMapAccessor.acquireLockNoWait(IdentityMapAccessor.java:108)
    at org.eclipse.persistence.internal.helper.WriteLockManager.attemptToAcquireLock(WriteLockManager.java:431)
    at org.eclipse.persistence.internal.helper.WriteLockManager.acquireRequiredLocks(WriteLockManager.java:280)
     * }
     *
     * We want to be able to trace these dead lock situations. To put them our on the massive log dump as to do the dead
     * lock detection.
     *
     */
    private static final Map<Thread, Set<ConcurrencyManager>> THREAD_TO_FAIL_TO_ACQUIRE_CACHE_KEYS = new ConcurrentHashMap<>();

    /**
     * We want to have traceability of what objects where changed by thread that is in the middle of a commit. This
     * information can be useful when a massive dump is performed to explain the situation of any thread that might
     * eventually be stuck inside of the write lock manager to tells us what exactly are the objects it has changed and
     * wants to commit or merge into the shared cache. Relates to the
     * {@link #THREAD_TO_FAIL_TO_ACQUIRE_CACHE_KEYS} but this map does not tells us about any
     * specific problem such as a cache key that could not be acquired just tells us what objects were modified.
     *
     */
    private static final Map<Thread, Set<Object>> MAP_WRITE_LOCK_MANAGER_THREAD_TO_OBJECT_IDS_WITH_CHANGE_SET = new ConcurrentHashMap<>();

    /** Semaphore related properties */
    private static final transient ThreadLocal<Boolean> SEMAPHORE_THREAD_LOCAL_VAR = new ThreadLocal<>();
    private static final transient int SEMAPHORE_MAX_NUMBER_THREADS = ConcurrencyUtil.SINGLETON.getNoOfThreadsAllowedToDoWriteLockManagerAcquireRequiredLocksInParallel();
    private static final transient Semaphore SEMAPHORE_LIMIT_MAX_NUMBER_OF_THREADS_WRITE_LOCK_MANAGER = new Semaphore(SEMAPHORE_MAX_NUMBER_THREADS);
    private transient ConcurrencySemaphore writeLockManagerSemaphore = new ConcurrencySemaphore(SEMAPHORE_THREAD_LOCAL_VAR, SEMAPHORE_MAX_NUMBER_THREADS, SEMAPHORE_LIMIT_MAX_NUMBER_OF_THREADS_WRITE_LOCK_MANAGER, this,"write_lock_manager_semaphore_acquired_01");

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
        // determineIfReleaseDeferredLockAppearsToBeDeadLocked
        final long whileStartTimeMillis = System.currentTimeMillis();
        final Thread currentThread = Thread.currentThread();
        DeferredLockManager lockManager = ConcurrencyManager.getDeferredLockManager(currentThread);
        ReadLockManager readLockManager = ConcurrencyManager.getReadLockManager(currentThread);

        boolean successful = false;
        IdentityHashMap lockedObjects = new IdentityHashMap();
        IdentityHashMap refreshedObjects = new IdentityHashMap();

        CacheKey lastCacheKeyWeNeededToWaitToAcquire = null;

        try {
            // if the descriptor has indirection for all mappings then wait as there will be no deadlock risks
            CacheKey toWaitOn = acquireLockAndRelatedLocks(objectForClone, lockedObjects, refreshedObjects, cacheKey, descriptor, cloningSession);
            int tries = 0;
            while (toWaitOn != null) {// loop until we've tried too many times.
                for (Iterator lockedList = lockedObjects.values().iterator(); lockedList.hasNext();) {
                    ((CacheKey)lockedList.next()).releaseReadLock();
                    lockedList.remove();
                }

                // of the concurrency manager that we use for creating the massive log dump
                // to indicate that the current thread is now stuck trying to acquire some arbitrary
                // cache key for writing
                StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[1];
                lastCacheKeyWeNeededToWaitToAcquire = toWaitOn;
                lastCacheKeyWeNeededToWaitToAcquire.putThreadAsWaitingToAcquireLockForWriting(currentThread, stackTraceElement.getClassName() + "." + stackTraceElement.getMethodName() + "(...)");

                // Since we know this one of those methods that can appear in the dead locks
                // we threads frozen here forever inside of the wait that used to have no timeout
                // we will now always check for how long the current thread is stuck in this while loop going nowhere
                // using the exact same approach we have been adding to the concurrency manager
                ConcurrencyUtil.SINGLETON.determineIfReleaseDeferredLockAppearsToBeDeadLocked(toWaitOn, whileStartTimeMillis, lockManager, readLockManager, ALLOW_INTERRUPTED_EXCEPTION_TO_BE_FIRED_UP_TRUE);

                synchronized (toWaitOn) {
                    try {
                        if (toWaitOn.isAcquired()) {//last minute check to insure it is still locked.
                            toWaitOn.wait(ConcurrencyUtil.SINGLETON.getAcquireWaitTime());// wait for lock on object to be released
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
        } catch (InterruptedException exception) {
            throw ConcurrencyException.maxTriesLockOnCloneExceded(objectForClone);
        } finally {
            if (lastCacheKeyWeNeededToWaitToAcquire != null) {
                lastCacheKeyWeNeededToWaitToAcquire.removeThreadNoLongerWaitingToAcquireLockForWriting(currentThread);
            }
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
            if (mergeManager.isTransitionedToDeferredLocks()) {
                return;
            }
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
     * This is wrapper method with semaphore logic.
     */
    public void acquireRequiredLocks(MergeManager mergeManager, UnitOfWorkChangeSet changeSet) {
        boolean semaphoreWasAcquired = false;
        boolean useSemaphore = ConcurrencyUtil.SINGLETON.isUseSemaphoreToLimitConcurrencyOnWriteLockManagerAcquireRequiredLocks();
        try {
            semaphoreWasAcquired = writeLockManagerSemaphore.acquireSemaphoreIfAppropriate(useSemaphore);
            acquireRequiredLocksInternal(mergeManager, changeSet);
        } finally {
            writeLockManagerSemaphore.releaseSemaphoreAllowOtherThreadsToStartDoingObjectBuilding(semaphoreWasAcquired);
        }
    }

    /**
     * INTERNAL:
     * This method will be the entry point for threads attempting to acquire locks for all objects that have
     * a changeset.  This method will hand off the processing of the deadlock algorithm to other member
     * methods.  The mergeManager must be the active mergemanager for the calling thread.
     * Returns true if all required locks were acquired
     */
    private void acquireRequiredLocksInternal(MergeManager mergeManager, UnitOfWorkChangeSet changeSet) {
        if (!MergeManager.LOCK_ON_MERGE) {//lockOnMerge is a backdoor and not public
            return;
        }
        boolean locksToAcquire = true;

        final Thread currentThread = Thread.currentThread();
        final long timeWhenLocksToAcquireLoopStarted = System.currentTimeMillis();
        populateMapThreadToObjectIdsWithChagenSet(currentThread, changeSet.getAllChangeSets().values());
        clearMapWriteLockManagerToCacheKeysThatCouldNotBeAcquired(currentThread);

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
                            // see bug 483478
                            activeCacheKey = waitOnObjectLock(descriptor, objectChangeSet.getId(),
                                    targetSession, (int) Math.round(((0.001d + Math.random()) * 500)));
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
                                        if (activeCacheKey.isAcquired() && (activeCacheKey.getActiveThread() != Thread.currentThread())) {                                                Thread thread = activeCacheKey.getActiveThread();
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
                            // we want to record this information so that we have traceability over this sort of problems
                            addCacheKeyToMapWriteLockManagerToCacheKeysThatCouldNotBeAcquired(currentThread, activeCacheKey, timeWhenLocksToAcquireLoopStarted);
                            // failed to acquire, exit this loop to restart all over again.
                            locksToAcquire = true;
                            break;
                        }else{
                            removeCacheKeyFromMapWriteLockManagerToCacheKeysThatCouldNotBeAcquired(currentThread, activeCacheKey);
                            objectChangeSet.setActiveCacheKey(activeCacheKey);
                            mergeManager.getAcquiredLocks().add(activeCacheKey);
                        }
                    } else {
                        removeCacheKeyFromMapWriteLockManagerToCacheKeysThatCouldNotBeAcquired(currentThread, activeCacheKey);
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
        } catch (InterruptedException exception) {
            releaseAllAcquiredLocks(mergeManager);
            throw ConcurrencyException.waitFailureOnClientSession(exception);
        } catch (Error error){
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
            clearMapWriteLockManagerToCacheKeysThatCouldNotBeAcquired(currentThread);
            clearMapThreadToObjectIdsWithChagenSet(currentThread);
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

    // Helper data structures to have tracebility about object ids with change sets and cache keys we are sturggling to acquire

    /** Getter for {@link #THREAD_TO_FAIL_TO_ACQUIRE_CACHE_KEYS} */
    public static Map<Thread, Set<ConcurrencyManager>> getThreadToFailToAcquireCacheKeys() {
        return unmodifiableMap(THREAD_TO_FAIL_TO_ACQUIRE_CACHE_KEYS);
    }

    /** Getter for {@link #MAP_WRITE_LOCK_MANAGER_THREAD_TO_OBJECT_IDS_WITH_CHANGE_SET} */
    public static Map<Thread, Set<Object>> getMapWriteLockManagerThreadToObjectIdsWithChangeSet() {
        return unmodifiableMap(MAP_WRITE_LOCK_MANAGER_THREAD_TO_OBJECT_IDS_WITH_CHANGE_SET);
    }

    /**
     * Remove the current thread from the map of object ids with change sets that are about to bec ommited
     *
     * @param thread
     *            the thread that is clearing itself out of the map of change sets it needs to merge into the shared
     *            cache
     */
    public static void clearMapThreadToObjectIdsWithChagenSet(Thread thread) {
        MAP_WRITE_LOCK_MANAGER_THREAD_TO_OBJECT_IDS_WITH_CHANGE_SET.remove(thread);
    }
    /**
     * Before a thread starts long wait loop to acquire write locks during a commit transaction the thread will record
     * in this map the object ids it holds with chance sets. It will be useful information if a dead lock is taking
     * place.
     *
     * @param thread
     *            the thread that is in the middle of merge to the shared cache trying to acquire write locks to do this
     *            merge
     * @param objectChangeSets
     *            the object change sets it has in its hands and that it would like to merge into the cache
     */
    public static void populateMapThreadToObjectIdsWithChagenSet(Thread thread,
                                                                 Collection<ObjectChangeSet> objectChangeSets) {
        // (a) make sure the map has an entry for the the thread
        boolean hasKey = MAP_WRITE_LOCK_MANAGER_THREAD_TO_OBJECT_IDS_WITH_CHANGE_SET.containsKey(thread);
        if (!hasKey) {
            Set value = MAP_WRITE_LOCK_MANAGER_THREAD_TO_OBJECT_IDS_WITH_CHANGE_SET.get(thread);
            if (value == null) {
                MAP_WRITE_LOCK_MANAGER_THREAD_TO_OBJECT_IDS_WITH_CHANGE_SET.put(thread, new HashSet<>());
            }
        }

        // (b) The ids of the objects with change sets
        Set<Object> primarykeys = MAP_WRITE_LOCK_MANAGER_THREAD_TO_OBJECT_IDS_WITH_CHANGE_SET.get(thread);
        primarykeys.clear();
        for (ObjectChangeSet objectChangeSet : objectChangeSets) {
            Object primaryKey = objectChangeSet.getId();
            primarykeys.add(primaryKey);
        }
    }

    /**
     * Before the problematic while loop starts we should always clear for this thread the set of cache keys it could
     * not acquire.
     *
     * @param thread
     *            the thread that what clear his set of cache keys it is struggling to acquire.
     */
    public static void clearMapWriteLockManagerToCacheKeysThatCouldNotBeAcquired(Thread thread) {
        THREAD_TO_FAIL_TO_ACQUIRE_CACHE_KEYS.remove(thread);
    }

    /**
     * The thread was doing its while loop to acquire all required locks to proceed with the commmit and it realized
     * there was one cache key it is unable to acquire
     *
     * @param thread
     *            thread the thread working on updating the shared cache
     * @param cacheKeyThatCouldNotBeAcquired
     *            the cache key it is not managing to acquire
     * @throws InterruptedException
     *             Should be fired because we are passing a flag into the
     *             determineIfReleaseDeferredLockAppearsToBeDeadLocked to say we do not want the thread to be blown up
     *             (e.g. we are afraid of breaking threads in the middle of a commit process could be quite dangerous).
     *             See
     *             {@link #ALLOW_INTERRUPTED_EXCEPTION_TO_BE_FIRED_UP_FALSE}
     */
    public static void addCacheKeyToMapWriteLockManagerToCacheKeysThatCouldNotBeAcquired(Thread thread, ConcurrencyManager cacheKeyThatCouldNotBeAcquired, long whileStartDate) throws InterruptedException {

        // sanity check, make sure the cacheKeyThatCouldNotBeAcquired is not null
        // should never happen because when the write lock manager fails to acquire the cache key both with acquire no
        // wait and acquire with wait
        // then the code will just grab the cache key fro loggging puprposes using the
        // see the code getCacheKeyForObjectForLock
        // this is why we believe this is never null. But the sanity check does not hurt us.
        if (cacheKeyThatCouldNotBeAcquired == null) {
            return;
        }

        // (b) add the cache key to the set if absent
        Set<ConcurrencyManager> cacheKeysWeAreHavingDifficultyAcquiring = getCacheKeysThatCouldNotBeAcquiredByThread(thread);
        if(!cacheKeysWeAreHavingDifficultyAcquiring.contains(cacheKeyThatCouldNotBeAcquired)) {
            cacheKeysWeAreHavingDifficultyAcquiring.add(cacheKeyThatCouldNotBeAcquired);
        }

        // (c) If a write lock fails to be acquired and goes into the basked of cache keys that could not be acquired
        // it could be an indication this thread is stuck for a long while
        // NOTE:
        // it might be best to not even give the possibility for an exception to be fired
        // for code that is in the lock manager
        final Thread currentThread = Thread.currentThread();
        DeferredLockManager lockManager = ConcurrencyManager.getDeferredLockManager(currentThread);
        ReadLockManager readLockManager = ConcurrencyManager.getReadLockManager(currentThread);
        ConcurrencyUtil.SINGLETON.determineIfReleaseDeferredLockAppearsToBeDeadLocked(
                cacheKeyThatCouldNotBeAcquired, whileStartDate, lockManager, readLockManager,
                ALLOW_INTERRUPTED_EXCEPTION_TO_BE_FIRED_UP_FALSE);
    }

    /**
     * A cache keys was successfully acquired we want to make sure it is not recorded in the map of cache keys that
     * could not be acquired. The situation theoretically can change. Failing to acquire a write lock can be a temporary
     * situation. The lock might become available eventually. Otherwise there would be no point for the while loop that
     * is trying to acquire these locks.
     *
     * @param thread
     *            the thread that just managed to grab a write lock
     * @param cacheKeyThatCouldNotBeAcquired
     *            the cache key it managed to acquire for writing.
     */
    public static void removeCacheKeyFromMapWriteLockManagerToCacheKeysThatCouldNotBeAcquired(Thread thread,
                                                                                              ConcurrencyManager cacheKeyThatCouldNotBeAcquired) {
        Set<ConcurrencyManager> cacheKeysWeAreHavingDifficultyAcquiring = getCacheKeysThatCouldNotBeAcquiredByThread(
                thread);
        cacheKeysWeAreHavingDifficultyAcquiring.remove(cacheKeyThatCouldNotBeAcquired);
    }

    /**
     * If the thread is not yet registered in the map it will get registered with an empty map.
     *
     * @param thread
     *            the thread that wants to get its set of cache keys it is not managing to acquire.
     * @return the set of cache keys the thrad is struggling to acquire
     */
    private static Set<ConcurrencyManager> getCacheKeysThatCouldNotBeAcquiredByThread(Thread thread) {
        // (a) make sure the map has an entry for the the thread
        boolean hasKey = THREAD_TO_FAIL_TO_ACQUIRE_CACHE_KEYS.containsKey(thread);
        if (!hasKey) {
            Set value = THREAD_TO_FAIL_TO_ACQUIRE_CACHE_KEYS.get(thread);
            if (value == null) {
                THREAD_TO_FAIL_TO_ACQUIRE_CACHE_KEYS.put(thread, new HashSet<ConcurrencyManager>());
            }
        }
        // (b) We are certain the map is not empty anymore return the set
        return THREAD_TO_FAIL_TO_ACQUIRE_CACHE_KEYS.get(thread);
    }
}