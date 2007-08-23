/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.sessions.remote;

import java.util.Enumeration;
import org.eclipse.persistence.sessions.remote.*;
import org.eclipse.persistence.logging.SessionLog;

/**
 * <p>
 * <b>Purpose</b>: To provide TopLink with a thread to asynchronously propigate change sets
 * <p>
 * <b>Description</b>: It holds onto the CacheSynchronizationManager for connection information.
 * Then creates threads to connect to all other machines and propigate the changes.
 * <p>
 */
public class ChangeSetPropagator extends Thread {
    protected CacheSynchronizationManager cacheSynchronizationManager;
    protected RemoteCommand command;
    protected RemoteConnection connection;

    /**
     * This is the default constructor.  It takes the CacheSynchronization manager as a parameter
     */
    public ChangeSetPropagator(CacheSynchronizationManager cacheSynchronizationManager, RemoteCommand command) {
        this.cacheSynchronizationManager = cacheSynchronizationManager;
        this.command = command;
    }

    /**
     * This is the default constructor.  It takes the CacheSynchronization manager as a parameter
     */
    public ChangeSetPropagator(CacheSynchronizationManager cacheSynchronizationManager, RemoteCommand command, RemoteConnection connection) {
        this.cacheSynchronizationManager = cacheSynchronizationManager;
        this.command = command;
        this.connection = connection;
    }

    /**
     * INTERNAL:
     * Accesses the Synchronization manager used to propigate
     * @return org.eclipse.persistence.sessions.remote.CacheSynchronizationManager
     */
    protected org.eclipse.persistence.sessions.remote.CacheSynchronizationManager getCacheSynchronizationManager() {
        return cacheSynchronizationManager;
    }

    /**
     * INTERNAL:
     * Accesses the remoteCommand to be sent to the server
     */
    protected RemoteCommand getRemoteCommand() {
        return command;
    }

    /**
     * INTERNAL:
     * This is the execution of the thread.  It will create other threads, one each for the remote connections
     * to propigate the changeSets
     */
    public void run() {
        Enumeration distributedSessions = getCacheSynchronizationManager().getRemoteConnections().elements();
        while (distributedSessions.hasMoreElements()) {
            RemoteConnection connection = (RemoteConnection)distributedSessions.nextElement();

            ChangeSetPropagator propagator = new ChangeSetPropagator(this.getCacheSynchronizationManager(), command, connection) {
                public void run() {
                    try {
                        this.connection.processCommand(this.getRemoteCommand());
                    } catch (RuntimeException exception) {
                        if (this.getCacheSynchronizationManager().shouldRemoveConnectionOnError()) {
                            this.getCacheSynchronizationManager().removeRemoteConnection(this.connection);
                        }
                        try {
                            this.getCacheSynchronizationManager().getSession().handleException(exception);
                        } catch (RuntimeException exceptionNotHandled) {
                            ((org.eclipse.persistence.internal.sessions.AbstractSession)this.getCacheSynchronizationManager().getSession()).log(SessionLog.WARNING, SessionLog.PROPAGATION, "failed_to_propogate_to", this.connection.getServiceName(), exceptionNotHandled);
                        }
                    }
                }
            };
            propagator.start();

        }
    }

    /**
     * INTERNAL:
     * Sets the Synchronization Manager used to propigate the changes
     *
     * @param newSynchManager org.eclipse.persistence.sessions.remote.CacheSynchronizationManager
     */
    protected void setCacheSynchronizationManager(org.eclipse.persistence.sessions.remote.CacheSynchronizationManager cacheSynchronizationManager) {
        this.cacheSynchronizationManager = cacheSynchronizationManager;
    }

    /**
     * INTERNAL:
     * Sets the remoteCommand to be processed on the remote session
     *
     */
    protected void setRemoteCommand(RemoteCommand remoteCommand) {
        this.command = remoteCommand;
    }
}