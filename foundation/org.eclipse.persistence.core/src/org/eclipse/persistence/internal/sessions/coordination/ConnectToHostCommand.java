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
package org.eclipse.persistence.internal.sessions.coordination;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.coordination.RemoteCommandManager;

/**
 * <p>
 * <b>Purpose</b>: This class provides an implementation of an internal RCM Command.
 * <p>
 * <b>Description</b>: This command is used by the RCM when it receives a connection
 * from a remote service. It adds the connection to its own list, and then sends this command 
 * back to the remote service  to establish the backward connection from the remote service to 
 * this service, accompanying the command with a reference to this own service id.  The 
 * remote service can then create a connection using the service id stored in the command and 
 * add the connection to its connection list.
 * <p>
 * @see org.eclipse.persistence.sessions.coordination.TransportManager
 * @author Steven Vo
 * @since OracleAS TopLink 10<i>g</i> (9.0.4)
 */
public class ConnectToHostCommand extends RCMCommand {

    /**
     * INTERNAL:
     * Executed on the RCM when received. Just add the remote object reference to
     * the connection list.
     */
    public void executeWithRCM(RemoteCommandManager rcm) {
        RemoteConnection connection = rcm.getTransportManager().createConnection(this.getServiceId());
        // null is returned from createConnection if connection cannot be created 
        if (connection != null) {
            rcm.getTransportManager().getConnectionsToExternalServices().put(connection.getServiceId().getId(), connection);
        }
    }

    public void executeWithSession(AbstractSession session) {
        // Internal RCM commands do not implement this method
    }
}
