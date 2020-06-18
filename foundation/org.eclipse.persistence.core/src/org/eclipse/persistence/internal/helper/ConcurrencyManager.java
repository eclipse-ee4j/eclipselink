/*******************************************************************************
 * Copyright (c) 1998, 2020 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.helper;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.persistence.config.SystemProperties;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.localization.*;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.logging.*;

/**
 * INTERNAL:
 * <p>
 * <b>Purpose</b>: To maintain concurrency for a particular task.
 * It is a wrappers of a semaphore that allows recursive waits by a single thread.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Keep track of the active thread.
 * <li> Wait all other threads until the first thread is done.
 * <li> Maintain the depth of the active thread.
 * </ul>
 */
public class ConcurrencyManager implements Serializable {
    
    protected int numberOfReaders;
    protected int depth;
    protected int numberOfWritersWaiting;
    protected volatile transient Thread activeThread;
    public static Map<Thread, DeferredLockManager> deferredLockManagers = initializeDeferredLockManagers();
    protected boolean lockedByMergeManager;
    protected long maxAllowedSleepTime = Long.parseLong(System.getProperty(SystemProperties.CONCURRENCY_MANAGER_SLEEP_TIME, "0"));

    protected static boolean shouldTrackStack = System.getProperty(SystemProperties.RECORD_STACK_ON_LOCK) != null;
    protected Exception stack;

    /**
     * Initialize the newly allocated instance of this class.
     * Set the depth to zero.
     */
    public ConcurrencyManager() {
        this.depth = 0;
        this.numberOfReaders = 0;
        this.numberOfWritersWaiting = 0;
    }

    /**
     * Wait for all threads except the active thread.
     * If the active thread just increment the depth.
     * This should be called before entering a critical section.
     */
    public void acquire() throws ConcurrencyException {
        this.acquire(false);
    }

    /**
     * Wait for all threads except the active thread.
     * If the active thread just increment the depth.
     * This should be called before entering a critical section.
     * called with true from the merge process, if true then the refresh will not refresh the object
     */
    public synchronized void acquire(boolean forMerge) throws ConcurrencyException {
        if (maxAllowedSleepTime == 0L) {
            while (((this.activeThread != null) || (this.numberOfReaders > 0)) && (this.activeThread != Thread.currentThread())) {
                // This must be in a while as multiple threads may be released, or another thread may rush the acquire after one is released.
                try {
                    this.numberOfWritersWaiting++;
                    wait();
                    this.numberOfWritersWaiting--;
                } catch (InterruptedException exception) {
                    throw ConcurrencyException.waitWasInterrupted(exception.getMessage());
                }
            }
            if (this.activeThread == null) {
                this.activeThread = Thread.currentThread();
                if (shouldTrackStack) {
                    this.stack = new Exception();
                }
            }
            this.lockedByMergeManager = forMerge;
            this.depth++;
        } else {
            //FIX - BUG-559307 - Flag the time when we start the while loop
            final Date whileStartDate = new Date();
            Thread currentThread = Thread.currentThread();
            DeferredLockManager lockManager = getDeferredLockManager(currentThread);

            while (((this.activeThread != null) || (this.numberOfReaders > 0)) && (this.activeThread != Thread.currentThread())) {
                // This must be in a while as multiple threads may be released, or another thread may rush the acquire after one is released.
                try {
                    this.numberOfWritersWaiting++;
                    // FIX - BUG-559307 - Do not wait forever - max allowed time to wait is 10 second and then check conditions again
                    wait(maxAllowedSleepTime);
                    // FIX - BUG-559307 -
                    // Run a method that will fire up an exception if we  having been sleeping for too long
                    determineIfReleaseDeferredLockAppearsToBeDeadLocked(this, whileStartDate, lockManager);
                } catch (InterruptedException exception) {
                    // FIX - BUG-559307 - If the thread is interrupted we want to make sure we release all of the locks the thread was owning
                    releaseAllLocksAquiredByThread(lockManager);
                    throw ConcurrencyException.waitWasInterrupted(exception.getMessage());
                } finally {
                    // FIX - BUG-559307 -
                    // Since above we incremente the number of writers
                    // whether or not the thread is exploded by an interrupt
                    // we need to make sure we decrement the number of writer to not allow the code to be corrupted
                    this.numberOfWritersWaiting--;
                }
            }
            if (this.activeThread == null) {
                this.activeThread = Thread.currentThread();
                if (shouldTrackStack){
                    this.stack = new Exception();
                }
            }
            this.lockedByMergeManager = forMerge;
            this.depth++;
        }
    }

    /**
     * If the lock is not acquired already acquire it and return true.
     * If it has been acquired already return false
     * Added for CR 2317
     */
    public boolean acquireNoWait() throws ConcurrencyException {
        return acquireNoWait(false);
    }

    /**
     * If the lock is not acquired already acquire it and return true.
     * If it has been acquired already return false
     * Added for CR 2317
     * called with true from the merge process, if true then the refresh will not refresh the object
     */
    public synchronized boolean acquireNoWait(boolean forMerge) throws ConcurrencyException {
        if ((this.activeThread == null && this.numberOfReaders == 0) || (this.activeThread == Thread.currentThread())) {
            //if I own the lock increment depth
            acquire(forMerge);
            return true;
        } else {
            return false;
        }
    }

    /**
     * If the lock is not acquired already acquire it and return true.
     * If it has been acquired already return false
     * Added for CR 2317
     * called with true from the merge process, if true then the refresh will not refresh the object
     */
    public synchronized boolean acquireWithWait(boolean forMerge, int wait) throws ConcurrencyException {
        if (maxAllowedSleepTime == 0L) {
            if ((this.activeThread == null && this.numberOfReaders == 0) || (this.activeThread == Thread.currentThread())) {
                //if I own the lock increment depth
                acquire(forMerge);
                return true;
            } else {
                try {
                    wait(wait);
                } catch (InterruptedException e) {
                    return false;
                }
                if ((this.activeThread == null && this.numberOfReaders == 0) || (this.activeThread == Thread.currentThread())) {
                    acquire(forMerge);
                    return true;
                }
                return false;
            }
        } else {
            if ((this.activeThread == null && this.numberOfReaders == 0) || (this.activeThread == Thread.currentThread())) {
                //if I own the lock increment depth
                acquire(forMerge);
                return true;
            } else {
                try {
                    wait(maxAllowedSleepTime);
                } catch (InterruptedException e) {
                    return false;
                }
                if ((this.activeThread == null && this.numberOfReaders == 0) || (this.activeThread == Thread.currentThread())){
                    acquire(forMerge);
                    return true;
                }
                return false;
            }
        }
    }

    /**
     * If the activeThread is not set, acquire it and return true.
     * If the activeThread is set, it has been acquired already, return false.
     * Added for Bug 5840635
     * Call with true from the merge process, if true then the refresh will not refresh the object.
     */
    public synchronized boolean acquireIfUnownedNoWait(boolean forMerge) throws ConcurrencyException {
        // Only acquire lock if active thread is null. Do not check current thread. 
        if (this.activeThread == null && this.numberOfReaders == 0) {
             // if lock is unowned increment depth
            acquire(forMerge);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Add deferred lock into a hashtable to avoid deadlock
     */
    public void acquireDeferredLock() throws ConcurrencyException {
        if (maxAllowedSleepTime == 0L) {
            Thread currentThread = Thread.currentThread();
            DeferredLockManager lockManager = getDeferredLockManager(currentThread);
            if (lockManager == null) {
                lockManager = new DeferredLockManager();
                putDeferredLock(currentThread, lockManager);
            }
            lockManager.incrementDepth();
            synchronized (this) {
                while (this.numberOfReaders != 0) {
                    // There are readers of this object, wait until they are done before determining if
                    //there are any other writers.  If not we will wait on the readers for acquire.  If another
                    //thread is also waiting on the acquire then a deadlock could occur.  See bug 3049635
                    //We could release all active locks before releasing deferred but the object may not be finished building
                    //we could make the readers get a hard lock, but then we would just build a deferred lock even though
                    //the object is not being built.
                    try {
                        this.numberOfWritersWaiting++;
                        wait();
                        this.numberOfWritersWaiting--;
                    } catch (InterruptedException exception) {
                        throw ConcurrencyException.waitWasInterrupted(exception.getMessage());
                    }
                }
                if ((this.activeThread == currentThread) || (!isAcquired())) {
                    lockManager.addActiveLock(this);
                    acquire();
                } else {
                    lockManager.addDeferredLock(this);
                    if (AbstractSessionLog.getLog().shouldLog(SessionLog.FINER) && this instanceof CacheKey) {
                        AbstractSessionLog.getLog().log(SessionLog.FINER, SessionLog.CACHE, "acquiring_deferred_lock", ((CacheKey) this).getObject(), currentThread.getName());
                    }
                }
            }
        } else {
            Thread currentThread = Thread.currentThread();
            DeferredLockManager lockManager = getDeferredLockManager(currentThread);
            if (lockManager == null) {
                lockManager = new DeferredLockManager();
                putDeferredLock(currentThread, lockManager);
            }
            lockManager.incrementDepth();
            synchronized (this) {

                // FIX - BUG-559307 - Flag the time when we start the while loop
                final Date whileStartDate = new Date();

                while (this.numberOfReaders != 0) {
                    // There are readers of this object, wait until they are done before determining if
                    //there are any other writers.  If not we will wait on the readers for acquire.  If another
                    //thread is also waiting on the acquire then a deadlock could occur.  See bug 3049635
                    //We could release all active locks before releasing deferred but the object may not be finished building
                    //we could make the readers get a hard lock, but then we would just build a deferred lock even though
                    //the object is not being built.
                    try {
                        this.numberOfWritersWaiting++;

                        // FIX - BUG-559307 - Do not wait forever - max allowed time to wait is 10 second and then check conditions again
                        // wait();
                        wait(maxAllowedSleepTime);

                        // FIX - BUG-559307 -  Moved to finally block to ensure it always runs
                        // this.numberOfWritersWaiting--;


                        // FIX - BUG-559307 -
                        // Run a method that will fire up an exception if we  having been sleeping for too long
                        determineIfReleaseDeferredLockAppearsToBeDeadLocked(this, whileStartDate, lockManager);
                    } catch (InterruptedException exception) {
                        // FIX - BUG-559307 - If the thread is interrupted we want to make sure we release all of the locks the thread was owning
                        releaseAllLocksAquiredByThread(lockManager);


                        throw ConcurrencyException.waitWasInterrupted(exception.getMessage());
                    } finally {
                        // FIX - BUG-559307 -
                        // Since above we incremente the number of writers
                        // whether or not the thread is exploded by an interrupt
                        // we need to make sure we decrement the number of writer to not allow the code to be corrupeted
                        this.numberOfWritersWaiting--;
                    }
                }
                if ((this.activeThread == currentThread) || (!isAcquired())) {
                    lockManager.addActiveLock(this);
                    acquire();
                } else {
                    lockManager.addDeferredLock(this);
                    if (AbstractSessionLog.getLog().shouldLog(SessionLog.FINER) && this instanceof CacheKey) {
                        AbstractSessionLog.getLog().log(SessionLog.FINER, SessionLog.CACHE, "acquiring_deferred_lock", ((CacheKey)this).getObject(), currentThread.getName());
                    }
                }
            }
        }
    }
        
    /**
     * Check the lock state, if locked, acquire and release a deferred lock.
     * This optimizes out the normal deferred-lock check if not locked.
     */
    public void checkDeferredLock() throws ConcurrencyException {
        // If it is not locked, then just return.
        if (this.activeThread == null) {
            return;
        }
        acquireDeferredLock();
        releaseDeferredLock();
    }
    
    /**
     * Check the lock state, if locked, acquire and release a read lock.
     * This optimizes out the normal read-lock check if not locked.
     */
    public void checkReadLock() throws ConcurrencyException {
        // If it is not locked, then just return.
        if (this.activeThread == null) {
            return;
        }
        acquireReadLock();
        releaseReadLock();
    }
    
    /**
     * Wait on any writer.
     * Allow concurrent reads.
     */
    public synchronized void acquireReadLock() throws ConcurrencyException {
        // Cannot check for starving writers as will lead to deadlocks.
        while ((this.activeThread != null) && (this.activeThread != Thread.currentThread())) {
            try {
                wait();
            } catch (InterruptedException exception) {
                throw ConcurrencyException.waitWasInterrupted(exception.getMessage());
            }
        }
        this.numberOfReaders++;
    }

    /**
     * If this is acquired return false otherwise acquire readlock and return true
     */
    public synchronized boolean acquireReadLockNoWait() {
        if ((this.activeThread == null) || (this.activeThread == Thread.currentThread())) {
            acquireReadLock();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Return the active thread.
     */
    public Thread getActiveThread() {
        return activeThread;
    }

    /**
     * Return the deferred lock manager from the thread
     */
    public static DeferredLockManager getDeferredLockManager(Thread thread) {
        return getDeferredLockManagers().get(thread);
    }

    /**
     * Return the deferred lock manager hashtable (thread - DeferredLockManager).
     */
    protected static Map<Thread, DeferredLockManager> getDeferredLockManagers() {
        return deferredLockManagers;
    }
    
    /**
     * Init the deferred lock managers (thread - DeferredLockManager).
     */
    protected static Map initializeDeferredLockManagers() {
        return new ConcurrentHashMap();
    }

    /**
     * Return the current depth of the active thread.
     */
    public int getDepth() {
        return depth;
    }

    /**
     * Number of writer that want the lock.
     * This is used to ensure that a writer is not starved.
     */
    public int getNumberOfReaders() {
        return numberOfReaders;
    }

    /**
     * Number of writers that want the lock.
     * This is used to ensure that a writer is not starved.
     */
    public int getNumberOfWritersWaiting() {
        return numberOfWritersWaiting;
    }
    
    /**
     * Return if a thread has acquire this manager.
     */
    public boolean isAcquired() {
        return depth > 0;
    }

    /**
     * INTERNAL:
     * Used byt the refresh process to determine if this concurrency manager is locked by
     * the merge process.  If it is then the refresh should not refresh the object
     */
    public boolean isLockedByMergeManager() {
        return this.lockedByMergeManager;
    }

    /**
     * Check if the deferred locks of a thread are all released
     */
    public static boolean isBuildObjectOnThreadComplete(Thread thread, Map recursiveSet) {
        if (recursiveSet.containsKey(thread)) {
            return true;
        }
        recursiveSet.put(thread, thread);

        DeferredLockManager lockManager = getDeferredLockManager(thread);
        if (lockManager == null) {
            return true;
        }

        Vector deferredLocks = lockManager.getDeferredLocks();
        for (Enumeration deferredLocksEnum = deferredLocks.elements();
                 deferredLocksEnum.hasMoreElements();) {
            ConcurrencyManager deferedLock = (ConcurrencyManager)deferredLocksEnum.nextElement();
            Thread activeThread = null;
            if (deferedLock.isAcquired()) {
                activeThread = deferedLock.getActiveThread();

                // the active thread may be set to null at anypoint
                // if added for CR 2330 
                if (activeThread != null) {
                    DeferredLockManager currentLockManager = getDeferredLockManager(activeThread);
                    if (currentLockManager == null) {
                        return false;
                    } else if (currentLockManager.isThreadComplete()) {
                        activeThread = deferedLock.getActiveThread();
                        // The lock may suddenly finish and no longer have an active thread.
                        if (activeThread != null) {
                            if (!isBuildObjectOnThreadComplete(activeThread, recursiveSet)) {
                                return false;
                            }
                        }
                    } else {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Return if this manager is within a nested acquire.
     */
    public boolean isNested() {
        return depth > 1;
    }

    public void putDeferredLock(Thread thread, DeferredLockManager lockManager) {
        getDeferredLockManagers().put(thread, lockManager);
    }

    /**
     * Decrement the depth for the active thread.
     * Assume the current thread is the active one.
     * Raise an error if the depth become < 0.
     * The notify will release the first thread waiting on the object,
     * if no threads are waiting it will do nothing.
     */
    public synchronized void release() throws ConcurrencyException {
        if (this.depth == 0) {
            throw ConcurrencyException.signalAttemptedBeforeWait();
        } else {
            this.depth--;
        }
        if (this.depth == 0) {
            this.activeThread = null;
            if (shouldTrackStack){
                this.stack = null;
            }
            this.lockedByMergeManager = false;
            notifyAll();
        }
    }

    /**
     * Release the deferred lock.
     * This uses a deadlock detection and resolution algorithm to avoid cache deadlocks.
     * The deferred lock manager keeps track of the lock for a thread, so that other
     * thread know when a deadlock has occurred and can resolve it.
     */
    public void releaseDeferredLock() throws ConcurrencyException {
        if (maxAllowedSleepTime == 0L) {
            Thread currentThread = Thread.currentThread();
            DeferredLockManager lockManager = getDeferredLockManager(currentThread);
            if (lockManager == null) {
                return;
            }
            int depth = lockManager.getThreadDepth();

            if (depth > 1) {
                lockManager.decrementDepth();
                return;
            }

            // If the set is null or empty, means there is no deferred lock for this thread, return.
            if (!lockManager.hasDeferredLock()) {
                lockManager.releaseActiveLocksOnThread();
                removeDeferredLockManager(currentThread);
                return;
            }

            lockManager.setIsThreadComplete(true);

            // Thread have three stages, one where they are doing work (i.e. building objects)
            // two where they are done their own work but may be waiting on other threads to finish their work,
            // and a third when they and all the threads they are waiting on are done.
            // This is essentially a busy wait to determine if all the other threads are done.
            while (true) {
                try {
                    // 2612538 - the default size of Map (32) is appropriate
                    Map recursiveSet = new IdentityHashMap();
                    if (isBuildObjectOnThreadComplete(currentThread, recursiveSet)) {// Thread job done.
                        lockManager.releaseActiveLocksOnThread();
                        removeDeferredLockManager(currentThread);
                        AbstractSessionLog.getLog().log(SessionLog.FINER, SessionLog.CACHE, "deferred_locks_released", currentThread.getName());
                        return;
                    } else {// Not done yet, wait and check again.
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException interrupted) {
                            AbstractSessionLog.getLog().logThrowable(SessionLog.SEVERE, SessionLog.CACHE, interrupted);
                            lockManager.releaseActiveLocksOnThread();
                            removeDeferredLockManager(currentThread);
                            throw ConcurrencyException.waitWasInterrupted(interrupted.getMessage());
                        }
                    }
                } catch (Error error) {
                    AbstractSessionLog.getLog().logThrowable(SessionLog.SEVERE, SessionLog.CACHE, error);
                    lockManager.releaseActiveLocksOnThread();
                    removeDeferredLockManager(currentThread);
                    throw error;
                }
            }
        } else {
            Thread currentThread = Thread.currentThread();
            DeferredLockManager lockManager = getDeferredLockManager(currentThread);
            if (lockManager == null) {
                return;
            }
            int depth = lockManager.getThreadDepth();

            if (depth > 1) {
                lockManager.decrementDepth();
                return;
            }

            // If the set is null or empty, means there is no deferred lock for this thread, return.
            if (!lockManager.hasDeferredLock()) {
                lockManager.releaseActiveLocksOnThread();
                removeDeferredLockManager(currentThread);
                return;
            }

            lockManager.setIsThreadComplete(true);

            // FIX - BUG-559307 - start a date time
            // Thread have three stages, one where they are doing work (i.e. building objects)
            // two where they are done their own work but may be waiting on other threads to finish their work,
            // and a third when they and all the threads they are waiting on are done.
            // This is essentially a busy wait to determine if all the other threads are done.
            final Date whileStartDate = new Date();

            // Thread have three stages, one where they are doing work (i.e. building objects)
            // two where they are done their own work but may be waiting on other threads to finish their work,
            // and a third when they and all the threads they are waiting on are done.
            // This is essentially a busy wait to determine if all the other threads are done.
            while (true) {
                try{
                    // 2612538 - the default size of Map (32) is appropriate
                    Map recursiveSet = new IdentityHashMap();
                    if (isBuildObjectOnThreadComplete(currentThread, recursiveSet)) {// Thread job done.
                        lockManager.releaseActiveLocksOnThread();
                        removeDeferredLockManager(currentThread);
                        AbstractSessionLog.getLog().log(SessionLog.FINER, SessionLog.CACHE, "deferred_locks_released", currentThread.getName());
                        return;
                    } else {// Not done yet, wait and check again.
                        try {
                            Thread.sleep(1);

                            // FIX - BUG-559307 -
                            // Run a method that will fire up an exception if we  having been sleeping for too long
                            determineIfReleaseDeferredLockAppearsToBeDeadLocked(this, whileStartDate, lockManager);
                        } catch (InterruptedException interrupted) {
                            AbstractSessionLog.getLog().logThrowable(SessionLog.SEVERE, SessionLog.CACHE, interrupted);
                            lockManager.releaseActiveLocksOnThread();
                            removeDeferredLockManager(currentThread);
                            throw ConcurrencyException.waitWasInterrupted(interrupted.getMessage());
                        }
                    }
                } catch (Error error) {
                    AbstractSessionLog.getLog().logThrowable(SessionLog.SEVERE, SessionLog.CACHE, error);
                    lockManager.releaseActiveLocksOnThread();
                    removeDeferredLockManager(currentThread);
                    throw error;
                }
            }
        }
    }

    /**
     * Decrement the number of readers.
     * Used to allow concurrent reads.
     */
    public synchronized void releaseReadLock() throws ConcurrencyException {
        if (this.numberOfReaders == 0) {
            throw ConcurrencyException.signalAttemptedBeforeWait();
        } else {
            this.numberOfReaders--;
        }
        if (this.numberOfReaders == 0) {
            notifyAll();
        }
    }

    /**
     * Remove the deferred lock manager for the thread
     */
    public static DeferredLockManager removeDeferredLockManager(Thread thread) {
        return getDeferredLockManagers().remove(thread);
    }

    /**
     * Set the active thread.
     */
    public void setActiveThread(Thread activeThread) {
        this.activeThread = activeThread;
    }

    /**
     * Set the current depth of the active thread.
     */
    protected void setDepth(int depth) {
        this.depth = depth;
    }

    /**
     * INTERNAL:
     * Used by the mergemanager to let the read know not to refresh this object as it is being
     * loaded by the merge process.
     */
    public void setIsLockedByMergeManager(boolean state) {
        this.lockedByMergeManager = state;
    }

    /**
     * Track the number of readers.
     */
    protected void setNumberOfReaders(int numberOfReaders) {
        this.numberOfReaders = numberOfReaders;
    }

    /**
     * Number of writers that want the lock.
     * This is used to ensure that a writer is not starved.
     */
    protected void setNumberOfWritersWaiting(int numberOfWritersWaiting) {
        this.numberOfWritersWaiting = numberOfWritersWaiting;
    }
    
    public synchronized void transitionToDeferredLock() {
        Thread currentThread = Thread.currentThread();
        DeferredLockManager lockManager = getDeferredLockManager(currentThread);
        if (lockManager == null) {
            lockManager = new DeferredLockManager();
            putDeferredLock(currentThread, lockManager);
        }
        lockManager.incrementDepth();
        lockManager.addActiveLock(this);
    }

    /**
     * Print the nested depth.
     */
    public String toString() {
        Object[] args = { Integer.valueOf(getDepth()) };
        return Helper.getShortClassName(getClass()) + ToStringLocalization.buildMessage("nest_level", args);
    }

    public Exception getStack() {
        return stack;
    }

    public void setStack(Exception stack) {
        this.stack = stack;
    }

    public static boolean shouldTrackStack() {
        return shouldTrackStack;
    }

    /**
     * INTERNAL:
     * This can be set during debugging to record the stacktrace when a lock is acquired.
     * Then once IdentityMapAccessor.printIdentityMapLocks() is called the stack call for each
     * lock will be printed as well.  Because locking issues are usually quite time sensitive setting 
     * this flag may inadvertently remove the deadlock because of the change in timings.
     * 
     * There is also a system level property for this setting. "eclipselink.cache.record-stack-on-lock"
     * @param shouldTrackStack
     */
    public static void setShouldTrackStack(boolean shouldTrackStack) {
        ConcurrencyManager.shouldTrackStack = shouldTrackStack;
    }

    public long getMaxAllowedSleepTime() {
        return maxAllowedSleepTime;
    }

    /**
     * It control how long thread will wait for the another thread. If value is greater than zero thread will
     * continue after specified amount of miliseconds.
     */
    public void setMaxAllowedSleepTime(long maxAllowedSleepTime) {
        this.maxAllowedSleepTime = maxAllowedSleepTime;
    }


// FIX - BUG-559307 -
    /**
     * For the thread to release all of its locks.
     * @param lockManager
     *      the deferred lock manager
     */
    // FIX - BUG-559307 - Put in one place the code that frees up the locks acuired by the current thread
    protected void releaseAllLocksAquiredByThread(DeferredLockManager lockManager) {
        Thread currentThread = Thread.currentThread();
        // (a) Log some information in the LOG
        String cacheKeyToString = createToStringExplainingOwnedCacheKey(this);
        String errMsg = "releaseAllLocksAquiredByThread has been invoked. On  " + cacheKeyToString;
        AbstractSessionLog.getLog().log(SessionLog.SEVERE, SessionLog.CACHE, errMsg, currentThread.getName());

        // (b) When this method is invoked during an aquire lock sometimes there is no lock manager
        if(lockManager == null) {
            AbstractSessionLog.getLog().log(SessionLog.SEVERE, SessionLog.CACHE, "Lock manager is null. "
                    + "This might be an aquire operation. So not possible to lockManager.releaseActiveLocksOnThread(). "
                    + " Cache Key" + cacheKeyToString, currentThread.getName());
            return;
        }

        // (c) Release the active locks on the thread
        lockManager.releaseActiveLocksOnThread();
        removeDeferredLockManager(currentThread);

        // Question:
        // Would it be safe to send a NOTIFY ALL?
        // probably not. But there might be threads waiting for the release of the lock
        // Normally the Notifyall is necessary when locks are released. But in this case we are even dealing with not one but ALL
        // of hte locks owned by thread so there would be multiple objects notify - we probably cannot do this
    }


    /**
     * Create a print of the ACTIVE locks associated to the DeferredLockManager
     */
    public String createStringWithSummaryOfActiveLocksOnThread(DeferredLockManager lockManager) {
        // (a) Make sure the lock manager being passed is not null
        if(lockManager == null) {
            return "DeferredLockManager - Listing of all Deferred Locks. Not currently created for current thread and cache key";
        }

        // (b) Try to build a string that lists all of the acitve locks on the thread
        StringBuilder sb = new StringBuilder();
        sb.append("DeferredLockManager - Listing of all Deferred Locks.");
        sb.append("\n\n");
        sb.append("Thread Name: ").append(Thread.currentThread().getName());
        sb.append("\n\n");

        // Loop over al of the active locks and print them
        long activeLock = 0;
        Vector activeLocks = lockManager.getActiveLocks();
        if (!activeLocks.isEmpty()) {
            for (Enumeration activeLocksEnum = activeLocks.elements();
                 activeLocksEnum.hasMoreElements();) {
                activeLock++;
                ConcurrencyManager manager = (ConcurrencyManager)activeLocksEnum.nextElement();
                String concurrencyManagerActiveLock = createToStringExplainingOwnedCacheKey(manager);
                sb.append("ACTIVE LOCK NR: ").append("" + activeLock).append("ConcurrencyManager: ").append(concurrencyManagerActiveLock);
                sb.append("\n\n");
            }
        }
        return sb.toString();
    }

    /**
     *
     * @return A to string of the owned cache key
     */
    public String createToStringExplainingOwnedCacheKey(ConcurrencyManager concurrencyManager) {
        if(concurrencyManager instanceof CacheKey) {
            CacheKey cacheKey = (CacheKey) concurrencyManager;
            Object cacheKeykey = cacheKey.getKey();
            Object cacheKeyObject = cacheKey.getObject();
            String canonicalName = cacheKeyObject != null? cacheKeyObject.getClass().getCanonicalName():null;
            return String.format("ConcurrencyManager-CacheKey: %1$s ownerCacheKey (key,object, canonicalName) = (%2$s, %3$s, %4$s).", this, cacheKeykey, cacheKeyObject, canonicalName);
        } else {
            return String.format("ConcurrencyManager: %1$s. .", this);
        }

    }

    /**
     * Throw an interrupted exception if appears that eclipse link code is taking too long to release a deferred lock.
     * @param whileStartDate
     *      the start date of the while tru loop for releasing a deferred lock
     * @throws InterruptedException
     *  we fire an interupted exception to ensure that the code  blows up and releases all of the locks it had.
     */
    public void determineIfReleaseDeferredLockAppearsToBeDeadLocked(ConcurrencyManager concurrencyManager, final Date whileStartDate, DeferredLockManager lockManager) throws InterruptedException {
        // Determine if we believe to be dealing with a dead lock
        Thread currentThread = Thread.currentThread();
        final long maxAllowedSleepTime40ThousandMs = maxAllowedSleepTime;
        Date whileCurrentDate = new Date();
        long elpasedTime = whileCurrentDate.getTime() - whileStartDate.getTime();
        if (elpasedTime > maxAllowedSleepTime40ThousandMs) {
            // We believe this is a dead lock so now we will log some information
            String ownedCacheKey = createToStringExplainingOwnedCacheKey(concurrencyManager);

            String errorMessageBase = String.format(
                    "RELEASE DEFERRED LOCK PROBLEM:  The release deffered log process has not managed to finish in: %1$s ms.%n"
                            + " (ownerCacheKey) = (%2$s). Current thread: %3$s. %n",
                    elpasedTime, ownedCacheKey, currentThread.getName());
            String errorMessageExplainingActiveLocksOnThread = createStringWithSummaryOfActiveLocksOnThread(lockManager);
            String errorMessage = errorMessageBase +  errorMessageExplainingActiveLocksOnThread;

            AbstractSessionLog.getLog().log(SessionLog.SEVERE, SessionLog.CACHE, errorMessage,
                    currentThread.getName());

            throw new InterruptedException(errorMessage);
        }
    }
}
