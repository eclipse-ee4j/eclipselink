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
package org.eclipse.persistence.internal.sessions.coordination.rmi;

import org.eclipse.persistence.exceptions.CommunicationException;
import org.eclipse.persistence.sessions.coordination.Command;
import org.eclipse.persistence.internal.sessions.coordination.RemoteConnection;
import java.rmi.RemoteException;

/**
 * <p>
 * <b>Purpose</b>: Define an RMI implementation class for the remote object that
 * can execute a remote command.
 * <p>
 * <b>Description</b>: This implementation class is the RMI transport version of
 * the connection that is used by the remote command manager to send remote
 * commands. This object just wraps the RMIRemoteCommandConnection remote object
 *
 * @since OracleAS TopLink 10<i>g</i> (9.0.4)
 */
public class RMIRemoteConnection extends RemoteConnection {
    RMIRemoteCommandConnection connection;

    public RMIRemoteConnection(RMIRemoteCommandConnection connection) {
        this.connection = connection;
    }

    /**
     * INTERNAL:
     * This method invokes the remote object with the Command argument, and causes
     * it to execute the command in the remote VM. The result is currently assumed
     * to be either null if successful, or an exception string if an exception was
     * thrown during execution.
     *
     * If a RemoteException occurred then a communication problem occurred. In this
     * case the exception will be wrapped in a CommunicationException and re-thrown.
     */
    public Object executeCommand(Command command) throws CommunicationException {
        try {
            return connection.executeCommand(command);
        } catch (RemoteException exception) {
            throw CommunicationException.errorInInvocation(exception);
        }
    }
    
    /**
     * INTERNAL
     * Return the RemoteCommandConnection associated with this RemoteConnection
     * @return RMIRemoteCommandConnection connection
     */
    public RMIRemoteCommandConnection getConnection() {
    	return this.connection;
    }
    
}
