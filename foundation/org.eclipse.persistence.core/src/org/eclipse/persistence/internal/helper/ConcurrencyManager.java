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
            if (shouldTrackStack){
                this.stack = new Exception();
            }
        }
        this.lockedByMergeManager = forMerge;
        this.depth++;
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
            if ((this.activeThread == null && this.numberOfReaders == 0) || (this.activeThread == Thread.currentThread())){
                acquire(forMerge);
                return true;
            }
            return false;
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
                    AbstractSessionLog.getLog().log(SessionLog.FINER, SessionLog.CACHE, "acquiring_deferred_lock", ((CacheKey)this).getObject(), currentThread.getName());
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
    
}
