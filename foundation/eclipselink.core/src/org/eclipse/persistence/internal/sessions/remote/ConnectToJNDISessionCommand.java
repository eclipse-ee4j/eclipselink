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
import org.eclipse.persistence.sessions.remote.*;

/**
 * INTERNAL:
 * <p>
 * <b>Purpose</b>: This class provides an implementation of a RemoteSessionCommand
 * <p>
 * <b>Description</b>: This command allows information necessary to make a connection
 * to be passed to remote servers.  Those servers can connect back to the sender.
 * <p>
 */
public class ConnectToJNDISessionCommand implements RemoteCommand {
    protected String sessionId;// The unique identifier for the session to be connected to
    protected String jndiURL;// The URL for the service providing JNDI

    /**
     * INTERNAL:
     * Execute this command.  Recreate the remote connection and add it to the connections list.
     */
    public void execute(AbstractSession session, RemoteSessionController remoteSessionController) {
        AbstractJNDIClusteringService service = (AbstractJNDIClusteringService)session.getCacheSynchronizationManager().getClusteringService();
        session.getCacheSynchronizationManager().getRemoteConnections().put(sessionId, service.createRemoteConnection(sessionId, jndiURL));
        session.logDebug("Received Remote Connection from " + sessionId);
    }

    public String getJndiURL() {
        return jndiURL;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setJndiURL(String jndiURL) {
        this.jndiURL = jndiURL;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}