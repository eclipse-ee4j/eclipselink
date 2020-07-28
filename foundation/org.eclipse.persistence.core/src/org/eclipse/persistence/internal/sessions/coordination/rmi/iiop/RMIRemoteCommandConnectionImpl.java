/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.sessions.coordination.rmi.iiop;

import org.eclipse.persistence.sessions.coordination.Command;
import org.eclipse.persistence.sessions.coordination.RemoteCommandManager;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.sessions.coordination.rmi.*;
import java.rmi.*;
import javax.rmi.PortableRemoteObject;

/**
 * <p>
 * <b>Purpose</b>: The implementation for the remote RMI-IIOP object used for
 * transporting the remote command.
 * <p>
 * <b>Description</b>: This implementation class is the RMI-IIOP remote object that is
 * wrapped by the conection abstraction. It is the first point of entry of the
 * command into the remote service. It simply hands the command to the RCM to be
 * processed.
 *
 * @see org.eclipse.persistence.sessions.coordination.Command
 * @see org.eclipse.persistence.sessions.coordination.RemoteCommandManager
 * @since TopLink 10i
 */
public class RMIRemoteCommandConnectionImpl extends PortableRemoteObject implements RMIRemoteCommandConnection {
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
            rcm.logDebug("error_executing_remote_command", args);
            // Return the string in case the exception doesn't exist on the other side
            return e.toString();
        }

        // Success - return null
        return null;
    }

    /**
     * INTERNAL:
     * No support currently exists for returning the result of the command execution.
     * Currently only null is returned on success. On failure an error string is
     * returned (to avoid returning an object/exception that may not exist on the
     * sending side).
     */
    public Object executeCommand(byte[] command) throws RemoteException {
        try {
            rcm.processCommandFromRemoteConnection(command);
        } catch (Exception e) {
            // Log the problem
            Object[] args = { Helper.getShortClassName(command), Helper.printStackTraceToString(e) };
            rcm.logDebug("error_executing_remote_command", args);
            // Return the string in case the exception doesn't exist on the other side
            return e.toString();
        }

        // Success - return null
        return null;
    }
}
