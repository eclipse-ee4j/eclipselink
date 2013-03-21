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

import org.eclipse.persistence.sessions.coordination.Command;
import org.eclipse.persistence.sessions.coordination.RemoteCommandManager;
import org.eclipse.persistence.internal.helper.Helper;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;

/**
 * <p>
 * <b>Purpose</b>: The implementation for the remote RMI object used for
 * transporting the remote command.
 * <p>
 * <b>Description</b>: This implementation class is the RMI remote object that is
 * wrapped by the conection abstraction. It is the first point of entry of the
 * command into the remote service. It simply hands the command to the RCM to be
 * processed.
 *
 * @see org.eclipse.persistence.sessions.coordination.Command
 * @see org.eclipse.persistence.sessions.coordination.RemoteCommandManager
 * @since OracleAS TopLink 10<i>g</i> (9.0.4)
 */
public class RMIRemoteCommandConnectionImpl extends UnicastRemoteObject implements RMIRemoteCommandConnection {
    RemoteCommandManager rcm;

    public RMIRemoteCommandConnectionImpl(RemoteCommandManager rcm) throws RemoteException {
        super();
        this.rcm = rcm;
    }

    /**
     * INTERNAL:
     * No support currently exists for returning the result of the command execution.
     * Currently only null is returned on success. On failure an error string is
     * returned (to avoid returning an object/exception that may not exist on the
     * sending side).
     */
    public Object executeCommand(Command command) throws RemoteException {
        try {
            rcm.processCommandFromRemoteConnection(command);
        } catch (Exception e) {
            // Log the problem
            Object[] args = { Helper.getShortClassName(command), Helper.printStackTraceToString(e) };
            rcm.logWarning("error_executing_remote_command", args);
            // Return the string in case the exception doesn't exist on the other side
            return e.toString();
        }

        // Success - return null
        return null;
    }
}
