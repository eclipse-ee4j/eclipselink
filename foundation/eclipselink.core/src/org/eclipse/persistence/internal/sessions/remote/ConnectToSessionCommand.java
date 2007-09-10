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

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.logging.SessionLog;

/**
 * <p>
 * <b>Purpose</b>: This class provides an implementation of a RemoteSessionCommand
 * <p>
 * <b>Description</b>: This command provides the implementation for cache synchronization messages
 * <p>
 */
public class ConnectToSessionCommand implements RemoteCommand {

    /** This attribute holds the UnitOfWorkChangeSet that will be applied to the remote session */
    RemoteConnection remoteConnection;

    /**
     * INTERNAL:
     * Use this method to set the ChangeSet into the command
     */
    public RemoteConnection getRemoteConnection() {
        return this.remoteConnection;
    }

    /**
     * INTERNAL:
     * Use this method to set the ChangeSet into the command
     */
    public void setRemoteConnection(RemoteConnection remoteConnection) {
        this.remoteConnection = remoteConnection;
    }

    /**
     * INTERNAL:
     * This method is used bye the remote Session to execute the command
     */
    public void execute(AbstractSession session, RemoteSessionController remoteSessionController) {
        session.getCacheSynchronizationManager().getRemoteConnections().put(remoteConnection.getServiceName(), remoteConnection);
        session.log(SessionLog.FINEST, SessionLog.PROPAGATION, "received_remote_connection_from", remoteConnection.getServiceName());
    }
}