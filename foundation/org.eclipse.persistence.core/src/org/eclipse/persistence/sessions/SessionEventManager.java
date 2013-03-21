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
package org.eclipse.persistence.sessions;

import java.util.*;
import java.io.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.core.sessions.CoreSessionEventManager;
import org.eclipse.persistence.internal.databaseaccess.*;
import org.eclipse.persistence.internal.sessions.*;
import org.eclipse.persistence.sessions.broker.SessionBroker;
import org.eclipse.persistence.sessions.server.ClientSession;
import org.eclipse.persistence.sessions.SessionProfiler;

/**
 * <p><b>Purpose</b>: Used to support session events.
 * To register for events notification an event listener must be registered with the session.
 *
 * @see Session#getEventManager()
 * @see SessionEvent
 */
public class SessionEventManager extends CoreSessionEventManager<SessionEventListener> implements Cloneable, Serializable {
    protected List<SessionEventListener> listeners;
    protected Session session;

    /**
     * INTERNAL:
     * Default constructor.
     */
    public SessionEventManager() {
        this.listeners = new ArrayList<SessionEventListener>();
    }

    /**
     * PUBLIC:
     * Create a new session event manager for a session
     */
    public SessionEventManager(Session session) {
        this.listeners = new ArrayList<SessionEventListener>();
        this.session = session;
    }

    /**
     * PUBLIC:
     * Add the event listener to the session.
     * The listener will receive all events raised by this session.
     * Also unit of works acquire from this session will inherit the listeners.
     * If session is a broker then its members add the listener, too.
     */
    public void addListener(SessionEventListener listener) {
        if (this.session != null) {
            if (this.session.isConnected()) {
                synchronized(this) {
                    if (this.listeners != null) {
                        ArrayList<SessionEventListener> listenersCopy = new ArrayList(listeners);
                        listenersCopy.add(listener);
                        this.listeners = listenersCopy;
                    } else {
                        this.listeners = new ArrayList();
                        this.listeners.add(listener);
                    }
                }
            } else {
                getListeners().add(listener);
            }
            if (this.session.isSessionBroker()) {
                // add listener to member sessions
                Iterator<AbstractSession> memberSessions = ((SessionBroker)this.session).getSessionsByName().values().iterator();
                while (memberSessions.hasNext()) {
                    AbstractSession memberSession = memberSessions.next();
                    memberSession.getEventManager().addListener(listener);
                }
            }
        } else {
            getListeners().add(listener);
        }
    }

    /**
     * INTERNAL:
     * Shallow clone the event manager.
     */
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException exception) {
            return new InternalError(exception.toString());
        }
    }

    /**
     * INTERNAL:
     * Clone the event manager for the new session.
     */
    public SessionEventManager clone(Session newSession) {
        SessionEventManager newManager = (SessionEventManager)clone();
        newManager.setSession(newSession);
        if (this.listeners != null) {
            newManager.setListeners(new ArrayList<SessionEventListener>(this.listeners));
        }
        return newManager;
    }

    /**
     * PUBLIC:
     * The event listeners will receive all events raised by this session.
     * Also unit of works acquire from this session will inherit the listeners.
     */
    public List<SessionEventListener> getListeners() {
        if (listeners == null) {
            listeners = new ArrayList<SessionEventListener>();
        }
        return listeners;
    }

    /**
     * INTERNAL:
     * Get the session for this session event manager
     */
    public Session getSession() {
        return session;
    }

    /**
     * PUBLIC:
     * Check if there are any event listeners.
     */
    public boolean hasListeners() {
        return (this.listeners != null) && (!this.listeners.isEmpty());
    }

    /**
     * INTERNAL:
     * Raised for missing descriptors for lazy registration.
     */
    public void missingDescriptor(Class missingClass) {
        if (!hasListeners()) {
            return;
        }
        startOperationProfile();
        SessionEvent event = new SessionEvent(SessionEvent.MissingDescriptor, getSession());
        event.setResult(missingClass);
        List<SessionEventListener> listeners = this.listeners;
        int size = listeners.size();
        for (int index = 0; index < size; index++) {
            listeners.get(index).missingDescriptor(event);
        }
        endOperationProfile();
    }

    /**
     * INTERNAL:
     * Raised for stored proc output parameters.
     */
    public void moreRowsDetected(DatabaseCall call) {
        if (!hasListeners()) {
            return;
        }
        startOperationProfile();
        SessionEvent event = new SessionEvent(SessionEvent.MoreRowsDetected, getSession());
        event.setResult(call);
        List<SessionEventListener> listeners = this.listeners;
        int size = listeners.size();
        for (int index = 0; index < size; index++) {
            listeners.get(index).moreRowsDetected(event);
        }
        endOperationProfile();
    }

    /**
     * INTERNAL:
     * Raised for stored proc output parameters.
     */
    public void noRowsModified(ModifyQuery query, Object object) {
        if (!hasListeners()) {
            return;
        }
        startOperationProfile();
        SessionEvent event = new SessionEvent(SessionEvent.NoRowsModified, getSession());
        event.setQuery(query);
        event.setResult(object);
        List<SessionEventListener> listeners = this.listeners;
        int size = listeners.size();
        for (int index = 0; index < size; index++) {
            listeners.get(index).noRowsModified(event);
        }
        endOperationProfile();
    }

    /**
     * INTERNAL:
     * Raised for stored proc output parameters.
     */
    public void outputParametersDetected(Record outputRow, DatasourceCall call) {
        if (!hasListeners()) {
            return;
        }
        startOperationProfile();
        SessionEvent event = new SessionEvent(SessionEvent.OutputParametersDetected, getSession());
        event.setResult(outputRow);
        event.setProperty("call", call);
        event.setQuery(call.getQuery());
        List<SessionEventListener> listeners = this.listeners;
        int size = listeners.size();
        for (int index = 0; index < size; index++) {
            listeners.get(index).outputParametersDetected(event);
        }
        endOperationProfile();
    }

    /**
     * INTERNAL:
     * Post acquire client session.
     */
    public void postAcquireClientSession() {
        if (!hasListeners()) {
            return;
        }
        startOperationProfile();
        SessionEvent event = new SessionEvent(SessionEvent.PostAcquireClientSession, getSession());
        List<SessionEventListener> listeners = this.listeners;
        int size = listeners.size();
        for (int index = 0; index < size; index++) {
            listeners.get(index).postAcquireClientSession(event);
        }
        endOperationProfile();
    }

    /**
     * INTERNAL:
     * Raised after acquire a connection from a connection pool.
     */
    public void postAcquireConnection(Accessor accessor) {
        if (!hasListeners()) {
            return;
        }
        startOperationProfile();
        SessionEvent event = new SessionEvent(SessionEvent.PostAcquireConnection, getSession());
        event.setResult(accessor);
        List<SessionEventListener> listeners = this.listeners;
        int size = listeners.size();
        for (int index = 0; index < size; index++) {
            listeners.get(index).postAcquireConnection(event);
        }
        endOperationProfile();
    }

    /**
     * INTERNAL:
     * Raised after acquire a connection from a connection pool.
     */
    public void postAcquireExclusiveConnection(ClientSession clientSession, Accessor accessor) {
        if (!hasListeners()) {
            return;
        }

        SessionEvent event = new SessionEvent(SessionEvent.PostAcquireExclusiveConnection, clientSession);
        event.setResult(accessor);
        List<SessionEventListener> listeners = this.listeners;
        int size = listeners.size();
        for (int index = 0; index < size; index++) {
            listeners.get(index).postAcquireExclusiveConnection(event);
        }
    }

    /**
     * INTERNAL:
     * Post acquire unit of work.
     */
    public void postAcquireUnitOfWork() {
        if (!hasListeners()) {
            return;
        }
        startOperationProfile();
        SessionEvent event = new SessionEvent(SessionEvent.PostAcquireUnitOfWork, getSession());
        List<SessionEventListener> listeners = this.listeners;
        int size = listeners.size();
        for (int index = 0; index < size; index++) {
            listeners.get(index).postAcquireUnitOfWork(event);
        }
        endOperationProfile();
    }

    /**
     * INTERNAL:
     * Post begin transaction.
     */
    public void postBeginTransaction() {
        if (!hasListeners()) {
            return;
        }
        startOperationProfile();
        SessionEvent event = new SessionEvent(SessionEvent.PostBeginTransaction, getSession());
        List<SessionEventListener> listeners = this.listeners;
        int size = listeners.size();
        for (int index = 0; index < size; index++) {
            listeners.get(index).postBeginTransaction(event);
        }
        endOperationProfile();
    }

    /**
     * INTERNAL:
     * Post commit transaction.
     */
    public void postCommitTransaction() {
        if (!hasListeners()) {
            return;
        }
        startOperationProfile();
        SessionEvent event = new SessionEvent(SessionEvent.PostCommitTransaction, getSession());
        List<SessionEventListener> listeners = this.listeners;
        int size = listeners.size();
        for (int index = 0; index < size; index++) {
            listeners.get(index).postCommitTransaction(event);
        }
        endOperationProfile();
    }

    /**
     * INTERNAL:
     * Post commit unit of work.
     */
    public void postCommitUnitOfWork() {
        if (!hasListeners()) {
            return;
        }
        startOperationProfile();
        SessionEvent event = new SessionEvent(SessionEvent.PostCommitUnitOfWork, getSession());
        List<SessionEventListener> listeners = this.listeners;
        int size = listeners.size();
        for (int index = 0; index < size; index++) {
            listeners.get(index).postCommitUnitOfWork(event);
        }
        endOperationProfile();
    }

    /**
     * INTERNAL:
     * Raised after connecting.
     */
    public void postConnect(Accessor accessor) {
        if (!hasListeners()) {
            return;
        }
        startOperationProfile();
        SessionEvent event = new SessionEvent(SessionEvent.PostConnect, getSession());
        event.setResult(accessor);
        List<SessionEventListener> listeners = this.listeners;
        int size = listeners.size();
        for (int index = 0; index < size; index++) {
            listeners.get(index).postConnect(event);
        }
        endOperationProfile();
    }

    /**
     * INTERNAL:
     * Post execute query.
     */
    public void postExecuteQuery(DatabaseQuery query, Object result) {
        if (!hasListeners()) {
            return;
        }
        startOperationProfile();
        SessionEvent event = new SessionEvent(SessionEvent.PostExecuteQuery, getSession());
        event.setQuery(query);
        event.setResult(result);
        List<SessionEventListener> listeners = this.listeners;
        int size = listeners.size();
        for (int index = 0; index < size; index++) {
            this.listeners.get(index).postExecuteQuery(event);
        }
        endOperationProfile();
    }

    /**
     * INTERNAL:
     * Post release client session.
     */
    public void postReleaseClientSession() {
        if (!hasListeners()) {
            return;
        }
        startOperationProfile();
        SessionEvent event = new SessionEvent(SessionEvent.PostReleaseClientSession, getSession());
        List<SessionEventListener> listeners = this.listeners;
        int size = listeners.size();
        for (int index = 0; index < size; index++) {
            listeners.get(index).postReleaseClientSession(event);
        }
        endOperationProfile();
    }

    /**
     * INTERNAL:
     * Post release unit of work.
     */
    public void postReleaseUnitOfWork() {
        if (!hasListeners()) {
            return;
        }
        startOperationProfile();
        SessionEvent event = new SessionEvent(SessionEvent.PostReleaseUnitOfWork, getSession());
        List<SessionEventListener> listeners = this.listeners;
        int size = listeners.size();
        for (int index = 0; index < size; index++) {
            listeners.get(index).postReleaseUnitOfWork(event);
        }
        endOperationProfile();
    }

    /**
     * INTERNAL:
     * Post resume unit of work.
     */
    public void postResumeUnitOfWork() {
        if (!hasListeners()) {
            return;
        }
        startOperationProfile();
        SessionEvent event = new SessionEvent(SessionEvent.PostResumeUnitOfWork, getSession());
        List<SessionEventListener> listeners = this.listeners;
        int size = listeners.size();
        for (int index = 0; index < size; index++) {
            listeners.get(index).postResumeUnitOfWork(event);
        }
        endOperationProfile();
    }

    /**
     * INTERNAL:
     * Post rollback transaction.
     */
    public void postRollbackTransaction() {
        if (!hasListeners()) {
            return;
        }
        startOperationProfile();
        SessionEvent event = new SessionEvent(SessionEvent.PostRollbackTransaction, getSession());
        List<SessionEventListener> listeners = this.listeners;
        int size = listeners.size();
        for (int index = 0; index < size; index++) {
            listeners.get(index).postRollbackTransaction(event);
        }
        endOperationProfile();
    }

    /**
     * INTERNAL:
     * Pre execute query.
     */
    public void postDistributedMergeUnitOfWorkChangeSet(UnitOfWorkChangeSet changeSet) {
        if (!hasListeners()) {
            return;
        }
        startOperationProfile();
        SessionEvent event = new SessionEvent(SessionEvent.PostDistributedMergeUnitOfWorkChangeSet, getSession());
        event.setProperty("UnitOfWorkChangeSet", changeSet);
        List<SessionEventListener> listeners = this.listeners;
        int size = listeners.size();
        for (int index = 0; index < size; index++) {
            listeners.get(index).postDistributedMergeUnitOfWorkChangeSet(event);
        }
        endOperationProfile();
    }

    /**
     * INTERNAL:
     * Pre execute query.
     */
    public void postMergeUnitOfWorkChangeSet(UnitOfWorkChangeSet changeSet) {
        if (!hasListeners()) {
            return;
        }
        startOperationProfile();
        SessionEvent event = new SessionEvent(SessionEvent.PostMergeUnitOfWorkChangeSet, getSession());
        event.setProperty("UnitOfWorkChangeSet", changeSet);
        List<SessionEventListener> listeners = this.listeners;
        int size = listeners.size();
        for (int index = 0; index < size; index++) {
            listeners.get(index).postMergeUnitOfWorkChangeSet(event);
        }
        endOperationProfile();
    }

    /**
     * INTERNAL:
     * Pre begin transaction.
     */
    public void preBeginTransaction() {
        if (!hasListeners()) {
            return;
        }
        startOperationProfile();
        SessionEvent event = new SessionEvent(SessionEvent.PreBeginTransaction, getSession());
        List<SessionEventListener> listeners = this.listeners;
        int size = listeners.size();
        for (int index = 0; index < size; index++) {
            listeners.get(index).preBeginTransaction(event);
        }
        endOperationProfile();
    }

    /**
     * INTERNAL:
     * Pre calculate UnitOfWork Change Set.
     */
    public void preCalculateUnitOfWorkChangeSet() {
        if (!hasListeners()) {
            return;
        }
        startOperationProfile();
        SessionEvent event = new SessionEvent(SessionEvent.PreCalculateUnitOfWorkChangeSet, getSession());
        List<SessionEventListener> listeners = this.listeners;
        int size = listeners.size();
        for (int index = 0; index < size; index++) {
            listeners.get(index).preCalculateUnitOfWorkChangeSet(event);
        }
        endOperationProfile();
    }

    /**
     * INTERNAL:
     * Post calculate UnitOfWork Change Set.
     */
    public void postCalculateUnitOfWorkChangeSet(UnitOfWorkChangeSet changeSet) {
        if (!hasListeners()) {
            return;
        }
        startOperationProfile();
        SessionEvent event = new SessionEvent(SessionEvent.PostCalculateUnitOfWorkChangeSet, getSession());
        event.setProperty("UnitOfWorkChangeSet", changeSet);
        List<SessionEventListener> listeners = this.listeners;
        int size = listeners.size();
        for (int index = 0; index < size; index++) {
            listeners.get(index).postCalculateUnitOfWorkChangeSet(event);
        }
        endOperationProfile();
    }

    /**
       * INTERNAL:
       * Pre commit transaction.
       */
    public void preCommitTransaction() {
        if (!hasListeners()) {
            return;
        }
        startOperationProfile();
        SessionEvent event = new SessionEvent(SessionEvent.PreCommitTransaction, getSession());
        List<SessionEventListener> listeners = this.listeners;
        int size = listeners.size();
        for (int index = 0; index < size; index++) {
            listeners.get(index).preCommitTransaction(event);
        }
        endOperationProfile();
    }

    /**
     * INTERNAL:
     * Pre commit unit of work.
     */
    public void preCommitUnitOfWork() {
        if (!hasListeners()) {
            return;
        }
        startOperationProfile();
        SessionEvent event = new SessionEvent(SessionEvent.PreCommitUnitOfWork, getSession());
        List<SessionEventListener> listeners = this.listeners;
        int size = listeners.size();
        for (int index = 0; index < size; index++) {
            listeners.get(index).preCommitUnitOfWork(event);
        }
        endOperationProfile();
    }

    /**
     * INTERNAL:
     * Pre execute query.
     */
    public void preExecuteQuery(DatabaseQuery query) {
        if (!hasListeners()) {
            return;
        }
        startOperationProfile();
        SessionEvent event = new SessionEvent(SessionEvent.PreExecuteQuery, getSession());
        event.setQuery(query);
        List<SessionEventListener> listeners = this.listeners;
        int size = listeners.size();
        for (int index = 0; index < size; index++) {
            listeners.get(index).preExecuteQuery(event);
        }
        endOperationProfile();
    }

    /**
     * INTERNAL:
     * Pre login to the session.
     */
    public void preLogin(Session session) {
        if (!hasListeners()) {
            return;
        }
        startOperationProfile();
        SessionEvent event = new SessionEvent(SessionEvent.PreLogin, session);
        List<SessionEventListener> listeners = this.listeners;
        int size = listeners.size();
        for (int index = 0; index < size; index++) {
            listeners.get(index).preLogin(event);
        }
        endOperationProfile();
    }

    /**
     * INTERNAL:
     * post login to the session.
     */
    public void postLogin(Session session) {
        if (!hasListeners()) {
            return;
        }
        startOperationProfile();
        SessionEvent event = new SessionEvent(SessionEvent.PostLogin, session);
        List<SessionEventListener> listeners = this.listeners;
        int size = listeners.size();
        for (int index = 0; index < size; index++) {
            listeners.get(index).postLogin(event);
        }
        endOperationProfile();
    }

    /**
     * INTERNAL:
     * Pre logout to the session.
     */
    public void preLogout(Session session) {
        if (!hasListeners()) {
            return;
        }
        startOperationProfile();
        SessionEvent event = new SessionEvent(SessionEvent.PreLogout, session);
        List<SessionEventListener> listeners = this.listeners;
        int size = listeners.size();
        for (int index = 0; index < size; index++) {
            listeners.get(index).preLogout(event);
        }
        endOperationProfile();
    }

    /**
     * INTERNAL:
     * post logout to the session.
     */
    public void postLogout(Session session) {
        if (!hasListeners()) {
            return;
        }
        startOperationProfile();
        SessionEvent event = new SessionEvent(SessionEvent.PostLogout, session);
        List<SessionEventListener> listeners = this.listeners;
        int size = listeners.size();
        for (int index = 0; index < size; index++) {
            listeners.get(index).postLogout(event);
        }
        endOperationProfile();
    }

    /**
     * INTERNAL:
     * Prepare unit of work.
     */
    public void prepareUnitOfWork() {
        if (!hasListeners()) {
            return;
        }
        startOperationProfile();
        SessionEvent event = new SessionEvent(SessionEvent.PrepareUnitOfWork, getSession());
        List<SessionEventListener> listeners = this.listeners;
        int size = listeners.size();
        for (int index = 0; index < size; index++) {
            listeners.get(index).prepareUnitOfWork(event);
        }
        endOperationProfile();
    }

    /**
     * INTERNAL:
     * Pre release client session.
     */
    public void preReleaseClientSession() {
        if (!hasListeners()) {
            return;
        }
        startOperationProfile();
        SessionEvent event = new SessionEvent(SessionEvent.PreReleaseClientSession, getSession());
        List<SessionEventListener> listeners = this.listeners;
        int size = listeners.size();
        for (int index = 0; index < size; index++) {
            listeners.get(index).preReleaseClientSession(event);
        }
        endOperationProfile();
    }

    /**
     * INTERNAL:
     * Raised before release a connection to a connection pool.
     */
    public void preReleaseConnection(Accessor accessor) {
        if (!hasListeners()) {
            return;
        }
        startOperationProfile();
        SessionEvent event = new SessionEvent(SessionEvent.PreReleaseConnection, getSession());
        event.setResult(accessor);
        List<SessionEventListener> listeners = this.listeners;
        int size = listeners.size();
        for (int index = 0; index < size; index++) {
            listeners.get(index).preReleaseConnection(event);
        }
        endOperationProfile();
    }

    /**
     * INTERNAL:
     * This event is fired just before a Client Session, with isolated data,
     * releases its Exclusive Connection
     */
    public void preReleaseExclusiveConnection(ClientSession clientSession, Accessor accessor) {
        if (!hasListeners()) {
            return;
        }

        SessionEvent event = new SessionEvent(SessionEvent.PreReleaseExclusiveConnection, clientSession);
        event.setResult(accessor);
        List<SessionEventListener> listeners = this.listeners;
        int size = listeners.size();
        for (int index = 0; index < size; index++) {
            listeners.get(index).preReleaseExclusiveConnection(event);
        }
    }

    /**
     * INTERNAL:
     * Pre release unit of work.
     */
    public void preReleaseUnitOfWork() {
        if (!hasListeners()) {
            return;
        }
        startOperationProfile();
        SessionEvent event = new SessionEvent(SessionEvent.PreReleaseUnitOfWork, getSession());
        List<SessionEventListener> listeners = this.listeners;
        int size = listeners.size();
        for (int index = 0; index < size; index++) {
            listeners.get(index).preReleaseUnitOfWork(event);
        }
        endOperationProfile();
    }

    /**
     * INTERNAL:
     * Pre rollback transaction.
     */
    public void preRollbackTransaction() {
        if (!hasListeners()) {
            return;
        }
        startOperationProfile();
        SessionEvent event = new SessionEvent(SessionEvent.PreRollbackTransaction, getSession());
        List<SessionEventListener> listeners = this.listeners;
        int size = listeners.size();
        for (int index = 0; index < size; index++) {
            listeners.get(index).preRollbackTransaction(event);
        }
        endOperationProfile();
    }

    /**
     * INTERNAL:
     * Pre merge Distributed UnitOfWorkChangeSet
     */
    public void preDistributedMergeUnitOfWorkChangeSet(UnitOfWorkChangeSet changeSet) {
        if (!hasListeners()) {
            return;
        }
        startOperationProfile();
        SessionEvent event = new SessionEvent(SessionEvent.PreDistributedMergeUnitOfWorkChangeSet, getSession());
        event.setProperty("UnitOfWorkChangeSet", changeSet);
        List<SessionEventListener> listeners = this.listeners;
        int size = listeners.size();
        for (int index = 0; index < size; index++) {
            listeners.get(index).preDistributedMergeUnitOfWorkChangeSet(event);
        }
        endOperationProfile();
    }

    /**
     * INTERNAL:
     * Pre merge UnitOfWorkChangeSet
     */
    public void preMergeUnitOfWorkChangeSet(UnitOfWorkChangeSet changeSet) {
        if (!hasListeners()) {
            return;
        }
        startOperationProfile();
        SessionEvent event = new SessionEvent(SessionEvent.PreMergeUnitOfWorkChangeSet, getSession());
        event.setProperty("UnitOfWorkChangeSet", changeSet);
        List<SessionEventListener> listeners = this.listeners;
        int size = listeners.size();
        for (int index = 0; index < size; index++) {
            listeners.get(index).preMergeUnitOfWorkChangeSet(event);
        }
        endOperationProfile();
    }

    /**
     * PUBLIC:
     * Remove the event listener from the session.
     * If session is a broker and the listener was in its list, then its members remove the listener, too.
     */
    public void removeListener(SessionEventListener listener) {
        if (this.session != null) {
            boolean isRemoved = false;
            if (this.session.isConnected()) {
                synchronized(this) {
                    if (this.listeners != null) {
                        ArrayList<SessionEventListener> listenersCopy = new ArrayList(listeners);
                        isRemoved = listenersCopy.remove(listener); 
                        if (isRemoved) {
                            this.listeners = listenersCopy;
                        }
                    }
                }
            } else {
                isRemoved = getListeners().remove(listener);
            }
            if (this.session.isSessionBroker() && isRemoved) {
                // remove listener from member sessions
                Iterator<AbstractSession> memberSessions = ((SessionBroker)this.session).getSessionsByName().values().iterator();
                while (memberSessions.hasNext()) {
                    AbstractSession memberSession = memberSessions.next();
                    memberSession.getEventManager().removeListener(listener);
                }
            }
        } else {
            getListeners().remove(listener);
        }
    }

    /**
     * The event listeners will receive all events raised by this session.
     * Also unit of works acquire from this session will inherit the listeners.
     */
    protected void setListeners(List<SessionEventListener> listeners) {
        this.listeners = listeners;
    }

    /**
     * INTERNAL:
     * Set the session for this session event manager
     */
    public void setSession(Session session) {
        this.session = session;
    }

    /**
     * INTERNAL:
     * Start call
     */
    protected void startOperationProfile() {
        if (getSession().isInProfile()) {
            getSession().getProfiler().startOperationProfile(SessionProfiler.SessionEvent);
        }
    }

    /**
     * INTERNAL:
     * End call
     */
    protected void endOperationProfile() {
        if (getSession().isInProfile()) {
            getSession().getProfiler().endOperationProfile(SessionProfiler.SessionEvent);
        }
    }
}
