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
package org.eclipse.persistence.testing.tests.distributedservers.rcm.broadcast;

import org.eclipse.persistence.exceptions.ExceptionHandler;
import org.eclipse.persistence.exceptions.RemoteCommandManagerException;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;
import org.eclipse.persistence.testing.framework.TestProblemException;

// This is a helper class used by TestWrapper.
// In case uow merge happened on source, it allows to wait until 
// either the distributed merge is completed on target 
// or something goes wrong.
// lock could be triggered by
//   merge into uow event in the source session.
//   direct call to lock method.
// If locked, waitUntilUnlocked method waits until unlocked, which could be triggered by:
//   error propagating remote command on the source session;
//   lack of localConnection on the source session (happens if connection removed on error); 
//   distributed merge into uow event in the target session;
//   direct call to unlock method.
// Used by TestWrapper in the following pattern: 
//   eventLock.initialize();
//   internalTest.test();
//   eventLock.waitUntilUnlocked();
// After ventLock.waitUntilUnlocked() returns, the reason for unlocking could be obtained from
//   eventLock.getState()
public class BroadcastEventLock {
    // states
    // locked states are negative ints
    // postMergeEvent with changes on the source
    public static final int LOCKED_BY_SOURCE_LISTENER = -2;
    // states required to be uninterrupted sequence of ints, minState equals the lowest state value
    protected static final int minState = LOCKED_BY_SOURCE_LISTENER;
    // lock() method was called
    public static final int LOCKED_DIRECTLY = -1;
    // unlocked states are zero and positive ints
    // initial state - set by initialize()
    public static final int UNLOCKED_INITIAL = 0;
    // unlock() method was called
    public static final int UNLOCKED_DIRECTLY = 1;
    // specified time ran out
    public static final int UNLOCKED_BY_TIMER = 2;
    // postDistributedMergeEvent on the target
    public static final int UNLOCKED_BY_TARGET_LISTENER = 3;
    // error propagating command on the source
    public static final int UNLOCKED_BY_SOURCE_EXCEPTION_HANDLER = 4;
    // source session doesn't have remote connection
    public static final int UNLOCKED_BY_SOURCE_SESSION = 5;
    // states required to be uninterrupted sequence of ints, minState equals the lowest state value
    protected static final int maxState = UNLOCKED_BY_SOURCE_SESSION;
    // num ber of tests
    protected static final int numberOfStates = maxState - minState + 1;

    // inner classes
    // base class for SourceListener and TargetListener

    class Listener extends SessionEventAdapter {
        public void attach(AbstractSession session) {
            session.getEventManager().addListener(this);
        }

        public void detach(AbstractSession session) {
            session.getEventManager().removeListener(this);
        }
    }
    // locks the lock after source uow merges

    class SourceListener extends Listener {
        public void postMergeUnitOfWorkChangeSet(SessionEvent event) {
            UnitOfWorkChangeSet uowChangeSet = (UnitOfWorkChangeSet)event.getProperty("UnitOfWorkChangeSet");
            if (uowChangeSet.hasChanges()) {
                lock(LOCKED_BY_SOURCE_LISTENER);
            }
        }
    }
    // unlocks the lock after target uow distributely merges

    class TargetListener extends Listener {
        public void postDistributedMergeUnitOfWorkChangeSet(SessionEvent event) {
            unlock(UNLOCKED_BY_TARGET_LISTENER);
        }
    }
    // unlocks the lock when remote command propagation fails on source

    class SourceExceptionHandler implements ExceptionHandler {
        ExceptionHandler originalHandler;

        public void attach(AbstractSession session) {
            originalHandler = session.getExceptionHandler();
            session.setExceptionHandler(this);
        }

        public void detach(AbstractSession session) {
            session.setExceptionHandler(originalHandler);
        }

        public Object handleException(RuntimeException exception) {
            if (exception instanceof RemoteCommandManagerException) {
                if (((RemoteCommandManagerException)exception).getErrorCode() == RemoteCommandManagerException.ERROR_PROPAGATING_COMMAND) {
                    unlock(UNLOCKED_BY_SOURCE_EXCEPTION_HANDLER);
                }
            }
            if (originalHandler != null) {
                return originalHandler.handleException(exception);
            } else {
                throw exception;
            }
        }
    }

    // attributes
    private int state;
    private boolean[] enabled;
    SourceListener sourceListener;
    TargetListener targetListener;
    SourceExceptionHandler sourceExceptionHandler;
    AbstractSession sourceSession;

    public BroadcastEventLock() {
        sourceListener = new SourceListener();
        targetListener = new TargetListener();
        sourceExceptionHandler = new SourceExceptionHandler();
        enabled = new boolean[numberOfStates];
        enableAllStates();
    }

    // public setup methods - used by BroadcastSetupHelper
    // attach - detach

    public boolean isAttached() {
        return sourceSession != null;
    }

    public void attach(AbstractSession session, boolean isSource) {
        if (isSource) {
            attachSource(session);
        } else {
            attachTarget(session);
        }
    }

    public void detach(AbstractSession session) {
        if (session == sourceSession) {
            detachSource();
        } else {
            detachTarget(session);
        }
    }

    // public methods used TestWrapper

    public void initialize() {
        state = UNLOCKED_INITIAL;
    }

    public void waitUntilUnlocked(long timeToWaitBeforeVerify) throws InterruptedException {
        if (isLocked()) {
            long n = timeToWaitBeforeVerify / 100;
            for (int i = 0; i < n && isLocked(); i++) {
                if (isStateEnabled(UNLOCKED_BY_SOURCE_SESSION)) {
                    // source session remote connection has been removed
                    if (sourceSession.getCommandManager().getTransportManager().getConnectionsToExternalServices().isEmpty()) {
                        // the following line unlocks, unless UNLOCKED_BY_SOURCE_SESSION state is disabled
                        unlock(UNLOCKED_BY_SOURCE_SESSION);
                    }
                }
                if (isLocked()) {
                    Thread.sleep(100);
                }
            }
            if (isLocked()) {
                unlock(UNLOCKED_BY_TIMER);
            } else if (state == UNLOCKED_BY_TARGET_LISTENER) {
                Thread.sleep(100);
            }
        }
    }

    // enable/disable state

    public void enableAllStates() {
        for (int i = 0; i < numberOfStates; i++) {
            enabled[i] = true;
        }
    }
    // return index for the passed state in enabled array

    protected int getStateIndex(int state) {
        return state - minState;
    }

    public boolean isStateEnabled(int state) {
        return enabled[getStateIndex(state)];
    }

    public void enableState(int state) {
        enabled[getStateIndex(state)] = true;
    }

    public void disableState(int state) {
        enabled[getStateIndex(state)] = false;
    }

    // returns the state

    public int getState() {
        return state;
    }

    // other public methods: setting and checking states

    public boolean isLocked() {
        return isLockedState(state);
    }

    public void lock() {
        lock(LOCKED_DIRECTLY);
    }

    public void unlock() {
        unlock(UNLOCKED_DIRECTLY);
    }

    protected void attachSource(AbstractSession session) {
        sourceListener.attach(session);
        sourceExceptionHandler.attach(session);
        sourceSession = session;
    }

    protected void attachTarget(AbstractSession session) {
        targetListener.attach(session);
    }

    protected void detachSource() {
        sourceListener.attach(sourceSession);
        sourceExceptionHandler.detach(sourceSession);
        sourceSession = null;
    }

    protected void detachTarget(AbstractSession session) {
        targetListener.detach(session);
    }

    protected static boolean isLockedState(int stateToExamine) {
        return stateToExamine < 0;
    }

    protected void verifyState(boolean isNewStateSupposedToBeLocked, int newState) {
        if (isNewStateSupposedToBeLocked != isLockedState(newState)) {
            throw new TestProblemException("Locked states should be negative; unlocked - non-negative");
        }
    }

    protected void lock(int newState) {
        verifyState(true, newState);
        changeState(newState);
    }

    protected void unlock(int newState) {
        verifyState(false, newState);
        changeState(newState);
    }
    // changes from locked to unlocked state or vise versa only.
    // would not change from one locked state to another.

    protected void changeState(int newState) {
        if ((isLocked() != isLockedState(newState)) && isStateEnabled(newState)) {
            state = newState;
        }
    }
}
