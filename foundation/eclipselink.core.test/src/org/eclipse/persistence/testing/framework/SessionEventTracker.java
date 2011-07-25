/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     July 13, 2011 - Andrei Ilitchev (Oracle) - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.testing.framework;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventListener;

/**
 * <p><b>Purpose</b>: Used to test handling of session events.
 * 
 * Create several instances of the class;
 * name them to distinguish from each other;
 * add them to different sessions' EventManagers.
 * 
 * In the test:
 *   // clear the previously logged event handlings
 *   SessionEventTracker.clearLog(); 
 *   // define which events should be tracked, for instance:
 *   // first clear all the previously set events,
 *   SessionEventTracker.noneEvents();
 *   // then add events to be tracked:
 *   SessionEventTracker.addEvent(SessionEvent.PreLogin);
 *   SessionEventTracker.addEvent(SessionEvent.PostLogin);
 *   // start tracking
 *   SessionEventTracker.startTracking();
 *   
 *   // do something
 *
 *   SessionEventTracker.stopTracking();
 *   // analyze listeners and events Lists.
 *   
 *   clean up (optional)
 *   SessionEventTracker.clearLog();
 *   // return back to the default setting - handling all events
 *   SessionEventTracker.allEvents();
 *   
 *   In static handlings list contains instances of Handling class -
 *   each holds an event and a listener that has handled it.
 *   
 *   It's possible to set error on a Handling (see examples preLogin and postLogin) -
 *   that sends the erroneous Handling to errors list (still kept on handlings list, too).
 *
 * @see SessionEventManager#addListener(SessionEventListener)
 * @see Session#getEventManager()
 * @see SessionEvent
 */
public class SessionEventTracker implements SessionEventListener {
    
    // in order from 1 to (currently) 35 (no holes!).
    // should be kept in sync with SessionEvent codes.
    public static final String[] eventNames = {
        "PreExecuteQuery ",
        "PostExecuteQuery",
        "PreBeginTransaction",
        "PostBeginTransaction",
        "PreCommitTransaction",
        "PostCommitTransaction",
        "PreRollbackTransaction",
        "PostRollbackTransaction",
        "PostAcquireUnitOfWork",
        "PreCommitUnitOfWork",
        "PostCommitUnitOfWork",
        "PreReleaseUnitOfWork",
        "PostReleaseUnitOfWork",
        "PrepareUnitOfWork",
        "PostResumeUnitOfWork",
        "PostAcquireClientSession",
        "PreReleaseClientSession",
        "PostReleaseClientSession",
        "OutputParametersDetected",
        "MoreRowsDetected",
        "PostConnect",
        "PostAcquireConnection",
        "PreReleaseConnection",
        "PreLogin",
        "PostLogin",
        "PreMergeUnitOfWorkChangeSet",
        "PreDistributedMergeUnitOfWorkChangeSet",
        "PostMergeUnitOfWorkChangeSet",
        "PostDistributedMergeUnitOfWorkChangeSet",
        "PreCalculateUnitOfWorkChangeSet",
        "PostCalculateUnitOfWorkChangeSet",
        "MissingDescriptor",
        "PostAcquireExclusiveConnection",
        "PreReleaseExclusiveConnection",
        "NoRowsModified"
    };
    
    public static int nEvents = eventNames.length;
    
    public static boolean isTracking;
    public static boolean[] shouldTrackEvent = new boolean[nEvents];
    static {
        allEvents();
    }
    
    public static class Handling {
        public Handling(SessionEventTracker listener, SessionEvent event) {
            this.time = System.currentTimeMillis();
            this.listener = listener;
            this.event = event;
        }
        long time;
        SessionEventTracker listener;
        SessionEvent event;
        String error = "";
        public String toString() {
            return listener.name + " -> " + eventToString(event) + (error.length()==0 ? "" : " Error: " + error);
        }
        public void setError(String error) {
            this.error = error;
            if (error.length() > 0) {
                synchronized (errors) {
                    errors.add(this);
                }
            } else {
                synchronized (errors) {
                    errors.remove(this);
                }
            }
        }
        public String getError() {
            return error;
        }
        public SessionEvent getEvent() {
            return event;
        }
        public SessionEventTracker getListener() {
            return listener;
        }
    }
    
    protected static List<Handling> handlings = new ArrayList();
    protected static List<Handling> errors = new ArrayList();
    
    protected String name = "";
    
    public SessionEventTracker() {
        super();
    }
    
    public SessionEventTracker(String name) {
        this.name = name;
    }
    
    public static void startTracking() {
        isTracking = true;
    }

    public static void stopTracking() {
        isTracking = false;
    }
    
    public static boolean isTracking() {
        return isTracking;
    }
    
    public static boolean isTrackingEvent(SessionEvent event) {
        if (isTracking) {
            return shouldTrackEvent[event.getEventCode()];
        } else {
            return false;
        }
    }
    
    public static int size() {
        return handlings.size();
    }
    
    public static void clearLog() {
        handlings = new ArrayList();
        errors = new ArrayList();
    }

    public static void allEvents() {
        for (int i=1; i<nEvents; i++) {
            shouldTrackEvent[i] = true;
        }
    }
    
    public static void noneEvents() {
        for (int i=1; i<nEvents; i++) {
            shouldTrackEvent[i] = false;
        }
    }
    
    public static void addEvent(int eventCode) {
        if (eventCode <= 0) {
            throw new TestErrorException("event code " + eventCode + " is wrong - should be a positive number");
        } else if (eventCode > eventNames.length) {
            throw new TestErrorException("event code " + eventCode + " is unknown - add event name for it to SessionEventTracker.eventNames array");
        }
        shouldTrackEvent[eventCode] = true;
    }
    
    public static void removeEvent(int eventCode) {
        if (eventCode <= 0) {
            throw new TestErrorException("event code " + eventCode + " is wrong - should be a positive number");
        } else if (eventCode > eventNames.length) {
            throw new TestErrorException("event code " + eventCode + " is unknown - add event name for it to SessionEventTracker.eventNames array");
        }
        shouldTrackEvent[eventCode] = false;
    }
    
    public static List<Handling> getHandlings() {
        return handlings;
    }
    
    public static List<Handling> getErrors() {
        return errors;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * PUBLIC:
     * This event is raised on the session if a descriptor is missing for a class being persisted.
     * This can be used to lazy register the descriptor or set of descriptors.
     */
    public void missingDescriptor(SessionEvent event) {
        if (!isTrackingEvent(event)) {
            return;
        }
        log(event);
    }

    /**
     * PUBLIC:
     * This event is raised on the session after read object query detected more than a single row back from the database.
     * The "result" of the event will be the call.  Some applications may want to interpret this as an error or warning condition.
     */
    public void moreRowsDetected(SessionEvent event) {
        if (!isTrackingEvent(event)) {
            return;
        }
        log(event);
    }

    /**
     * PUBLIC:
     * This event is raised on the session after update or delete SQL has been sent to the database
     * but a row count of zero was returned.
     */
    public void noRowsModified(SessionEvent event) {
        if (!isTrackingEvent(event)) {
            return;
        }
        log(event);
    }

    /**
     * PUBLIC:
     * This event is raised on the session after a stored procedure call has been executed that had output parameters.
     * If the proc was used to override an insert/update/delete operation then EclipseLink will not be expecting any return value.
     * This event mechanism allows for a listener to be registered before the proc is call to process the output values.
     * The event "result" will contain a Record of the output values, and property "call" will be the StoredProcedureCall.
     */
    public void outputParametersDetected(SessionEvent event) {
        if (!isTrackingEvent(event)) {
            return;
        }
        log(event);
    }

    /**
     * PUBLIC:
     * This event is raised on the client session after creation/acquiring.
     */
    public void postAcquireClientSession(SessionEvent event) {
        if (!isTrackingEvent(event)) {
            return;
        }
        log(event);
    }

    /**
     * PUBLIC:
     * This event is raised on when using the server/client sessions.
     * This event is raised after a connection is acquired from a connection pool.
     */
    public void postAcquireConnection(SessionEvent event) { 
        if (!isTrackingEvent(event)) {
            return;
        }
        log(event);
    }

    /**
     * PUBLIC:
     * This event is raised when a ClientSession, with Isolated data, acquires
     * an exclusive connection.  The event will contain the ClientSession that
     * is being acquired.  Users can set properties within the ConnectionPolicy
     * of that ClientSession for access within this event.
     */
    public void postAcquireExclusiveConnection(SessionEvent event) {
        if (!isTrackingEvent(event)) {
            return;
        }
        log(event);
    }

    /**
     * PUBLIC:
     * This event is raised on the unit of work after creation/acquiring.
     * This will be raised on nest units of work.
     */
    public void postAcquireUnitOfWork(SessionEvent event) {
        if (!isTrackingEvent(event)) {
            return;
        }
        log(event);
    }

    /**
     * PUBLIC:
     * This event is raised after a database transaction is started.
     * It is not raised for nested transactions.
     */
    public void postBeginTransaction(SessionEvent event) {
        if (!isTrackingEvent(event)) {
            return;
        }
        log(event);
    }

    /**
     * PUBLIC:
     * This event is raised after the commit has begun on the UnitOfWork but before
     * the changes are calculated.
     */
    public void preCalculateUnitOfWorkChangeSet(SessionEvent event) {
        if (!isTrackingEvent(event)) {
            return;
        }
        log(event);
    }

    /**
     * PUBLIC:
     * This event is raised after the commit has begun on the UnitOfWork and
     * after the changes are calculated.  The UnitOfWorkChangeSet, at this point,
     * will contain changeSets without the version fields updated and without
     * IdentityField type primary keys.  These will be updated after the insert, or
     * update, of the object
     */
    public void postCalculateUnitOfWorkChangeSet(SessionEvent event) {
        if (!isTrackingEvent(event)) {
            return;
        }
        log(event);
    }

    /**
     * PUBLIC:
     * This event is raised after a database transaction is commited.
     * It is not raised for nested transactions.
     */
    public void postCommitTransaction(SessionEvent event) {
        if (!isTrackingEvent(event)) {
            return;
        }
        log(event);
    }

    /**
     * PUBLIC:
     * This event is raised on the unit of work after commit.
     * This will be raised on nest units of work.
     */
    public void postCommitUnitOfWork(SessionEvent event) {
        if (!isTrackingEvent(event)) {
            return;
        }
        log(event);
    }

    /**
     * PUBLIC:
     * This event is raised after the session connects to the database.
     * In a server session this event is raised on every new connection established.
     */
    public void postConnect(SessionEvent event) {
        if (!isTrackingEvent(event)) {
            return;
        }
        log(event);
    }

    /**
     * PUBLIC:
     * This event is raised after the execution of every query against the session.
     * The event contains the query and query result.
     */
    public void postExecuteQuery(SessionEvent event) {
        if (!isTrackingEvent(event)) {
            return;
        }
        log(event);
    }

    /**
     * PUBLIC:
     * This event is raised on the client session after releasing.
     */
    public void postReleaseClientSession(SessionEvent event) {
        if (!isTrackingEvent(event)) {
            return;
        }
        log(event);
    }

    /**
     * PUBLIC:
     * This event is raised on the unit of work after release.
     * This will be raised on nest units of work.
     */
    public void postReleaseUnitOfWork(SessionEvent event) {
        if (!isTrackingEvent(event)) {
            return;
        }
        log(event);
    }

    /**
     * PUBLIC:
     * This event is raised on the unit of work after resuming.
     * This occurs after pre/postCommit.
     */
    public void postResumeUnitOfWork(SessionEvent event) {
        if (!isTrackingEvent(event)) {
            return;
        }
        log(event);
    }

    /**
     * PUBLIC:
     * This event is raised after a database transaction is rolledback.
     * It is not raised for nested transactions.
     */
    public void postRollbackTransaction(SessionEvent event) {
        if (!isTrackingEvent(event)) {
            return;
        }
        log(event);
    }

    /**
     * PUBLIC:
     * This even will be raised after a UnitOfWorkChangeSet has been merged
     * When that changeSet has been received from a distributed session
     */
    public void postDistributedMergeUnitOfWorkChangeSet(SessionEvent event) {
        if (!isTrackingEvent(event)) {
            return;
        }
        log(event);
    }

    /**
     * PUBLIC:
     * This even will be raised after a UnitOfWorkChangeSet has been merged
     */
    public void postMergeUnitOfWorkChangeSet(SessionEvent event) {
        if (!isTrackingEvent(event)) {
            return;
        }
        log(event);
    }

    /**
     * PUBLIC:
     * This event is raised before a database transaction is started.
     * It is not raised for nested transactions.
     */
    public void preBeginTransaction(SessionEvent event) {
        if (!isTrackingEvent(event)) {
            return;
        }
        log(event);
    }

    /**
     * PUBLIC:
     * This event is raised before a database transaction is commited.
     * It is not raised for nested transactions.
     */
    public void preCommitTransaction(SessionEvent event) {
        if (!isTrackingEvent(event)) {
            return;
        }
        log(event);
    }

    /**
     * PUBLIC:
     * This event is raised on the unit of work before commit.
     * This will be raised on nest units of work.
     */
    public void preCommitUnitOfWork(SessionEvent event) {
        if (!isTrackingEvent(event)) {
            return;
        }
        log(event);
    }

    /**
     * PUBLIC:
     * This event is raised before the execution of every query against the session.
     * The event contains the query to be executed.
     */
    public void preExecuteQuery(SessionEvent event) {
        if (!isTrackingEvent(event)) {
            return;
        }
        log(event);
    }

    /**
     * PUBLIC:
     * This event is raised on the unit of work after the SQL has been flushed, but the commit transaction has not been executed.
     * It is similar to the JTS prepare phase.
     */
    public void prepareUnitOfWork(SessionEvent event) {
        if (!isTrackingEvent(event)) {
            return;
        }
        log(event);
    }

    /**
     * PUBLIC:
     * This event is raised on the client session before releasing.
     */
    public void preReleaseClientSession(SessionEvent event) {
        if (!isTrackingEvent(event)) {
            return;
        }
        log(event);
    }

    /**
     * PUBLIC:
     * This event is raised on when using the server/client sessions.
     * This event is raised before a connection is released into a connection pool.
     */
    public void preReleaseConnection(SessionEvent event) {
        if (!isTrackingEvent(event)) {
            return;
        }
        log(event);
    }

    /**
     * PUBLIC:
     * This event is fired just before a Client Session, with isolated data,
     * releases its Exclusive Connection
     */
    public void preReleaseExclusiveConnection(SessionEvent event) {
        if (!isTrackingEvent(event)) {
            return;
        }
        log(event);
    }

    /**
     * PUBLIC:
     * This event is raised on the unit of work before release.
     * This will be raised on nest units of work.
     */
    public void preReleaseUnitOfWork(SessionEvent event) {
        if (!isTrackingEvent(event)) {
            return;
        }
        log(event);
    }

    /**
     * PUBLIC:
     * This event is raised before a database transaction is rolledback.
     * It is not raised for nested transactions.
     */
    public void preRollbackTransaction(SessionEvent event) {
        if (!isTrackingEvent(event)) {
            return;
        }
        log(event);
    }

    /**
     * PUBLIC:
     * This even will be raised before a UnitOfWorkChangeSet has been merged
     * When that changeSet has been received from a distributed session
     */
    public void preDistributedMergeUnitOfWorkChangeSet(SessionEvent event) {
        if (!isTrackingEvent(event)) {
            return;
        }
        log(event);
    }

    /**
     * PUBLIC:
     * This even will be raised before a UnitOfWorkChangeSet has been merged
     */
    public void preMergeUnitOfWorkChangeSet(SessionEvent event) {
        if (!isTrackingEvent(event)) {
            return;
        }
        log(event);
    }

    /**
     * PUBLIC:
     * This Event is raised before the session logs in.
     */
    public void preLogin(SessionEvent event) {
        if (!isTrackingEvent(event)) {
            return;
        }
        Handling handling = log(event);
        if (((DatabaseSessionImpl)event.getSession()).isLoggedIn()) {
            handling.setError("session is already logged in");
        }        
    }

    /**
     * PUBLIC:
     * This Event is raised after the session logs out.
     */
    public void preLogout(SessionEvent event) {
        if (!isTrackingEvent(event)) {
            return;
        }
        log(event);
    }

    /**
     * PUBLIC:
     * This Event is raised after the session logs out.
     */
    public void postLogout(SessionEvent event) {
        if (!isTrackingEvent(event)) {
            return;
        }
        log(event);
    }

    /**
     * PUBLIC:
     * This Event is raised after the session logs in.
     */
    public void postLogin(SessionEvent event) {
        if (!isTrackingEvent(event)) {
            return;
        }
        Handling handling = log(event);
        String errorMsg = "";
        Iterator<ClassDescriptor> it = event.getSession().getDescriptors().values().iterator();
        while (it.hasNext()) {
            ClassDescriptor descriptor = it.next();
            if (!descriptor.isAggregateDescriptor()) {
                if (!descriptor.isFullyInitialized()) {
                    errorMsg += descriptor.getJavaClass().getName() + "; ";
                }
            }
        }
        if (errorMsg.length() > 0) {
            errorMsg = "Some descriptors are not initialized: " + errorMsg;
            handling.setError(errorMsg);
        }
    }
    
    protected Handling log(SessionEvent event) {
        Handling handling = new Handling(this, event);
        synchronized (handlings) {
            handlings.add(handling);
        }
        return handling;
    }
    
    public String toString() {
        return Helper.getShortClassName(this) + "(" + (name != null ? name : "") + ")";
    }
    
    public static String eventToString(SessionEvent event) {
        return getEventName(event.getEventCode()) + "[" + sessionToString(event.getSession()) + "]";
    }

    public static String getEventName(int eventCode) {
        if (eventCode <= 0) {
            throw new TestErrorException("event code " + eventCode + " is wrong - should be a positive number");
        } else if (eventCode > eventNames.length) {
            throw new TestErrorException("event code " + eventCode + " is unknown - add event name for it to SessionEventTracker.eventNames array");
        }
        return eventNames[eventCode - 1];
    }
    
    public static String sessionToString(Session session) {
        return Helper.getShortClassName(session) + "(" + session.getName() + ")";
    }    
}
