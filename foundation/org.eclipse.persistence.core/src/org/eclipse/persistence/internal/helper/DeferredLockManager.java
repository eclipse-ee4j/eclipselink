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

import java.util.*;

/**
 * INTERNAL:
 * <p>
 * <b>Purpose</b>: Be used for deadlock avoidance through allowing detection and resolution.
 *
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Keep track of all deferred locks of each thread.
 * <li> Keep track of all active locks of each thread..
 * <li> Maintain the depth of the each thread.
 * </ul>
 */
public class DeferredLockManager {
    protected Vector deferredLocks;
    protected Vector activeLocks;
    protected int threadDepth;
    protected boolean isThreadComplete;
    
    public static boolean SHOULD_USE_DEFERRED_LOCKS = true;

    /**
     * DeferredLockManager constructor comment.
     */
    public DeferredLockManager() {
        super();
        this.deferredLocks = new Vector(1);
        this.activeLocks = new Vector(1);
        this.threadDepth = 0;
        this.isThreadComplete = false;
    }

    /**
     * add a concurrency manager as active locks to the DLM
     */
    public void addActiveLock(Object manager) {
        getActiveLocks().addElement(manager);
    }

    /**
     * add a concurrency manager as deferred locks to the DLM
     */
    public void addDeferredLock(Object manager) {
        getDeferredLocks().addElement(manager);
    }

    /**
     * decrement the depth of the thread
     */
    public void decrementDepth() {
        threadDepth--;
    }

    /**
     * Return a set of the active locks from the DLM
     */
    public Vector getActiveLocks() {
        return activeLocks;
    }

    /**
     * Return a set of the deferred locks
     */
    public Vector getDeferredLocks() {
        return deferredLocks;
    }

    /**
     * Return the depth of the thread associated with the DLM, being used to release the lock
     */
    public int getThreadDepth() {
        return threadDepth;
    }

    /**
     * Return if there are any deferred locks.
     */
    public boolean hasDeferredLock() {
        return !getDeferredLocks().isEmpty();
    }

    /**
     * increment the depth of the thread
     */
    public void incrementDepth() {
        threadDepth++;
    }

    /**
     * Return if the thread is complete
     */
    public boolean isThreadComplete() {
        return isThreadComplete;
    }

    /**
     * Release the active lock on the DLM
     */
    public void releaseActiveLocksOnThread() {
        Vector activeLocks = getActiveLocks();
        if (!activeLocks.isEmpty()) {
            for (Enumeration activeLocksEnum = activeLocks.elements();
                     activeLocksEnum.hasMoreElements();) {
                ConcurrencyManager manager = (ConcurrencyManager)activeLocksEnum.nextElement();
                manager.release();
            }
        }
        setIsThreadComplete(true);
    }

    /**
     * set a set of the active locks to the DLM
     */
    public void setActiveLocks(Vector activeLocks) {
        this.activeLocks = activeLocks;
    }

    /**
     * set a set of the deferred locks to the DLM
     */
    public void setDeferredLocks(Vector deferredLocks) {
        this.deferredLocks = deferredLocks;
    }

    /**
     * set if the thread is complete in the given DLM
     */
    public void setIsThreadComplete(boolean isThreadComplete) {
        this.isThreadComplete = isThreadComplete;
    }
}
