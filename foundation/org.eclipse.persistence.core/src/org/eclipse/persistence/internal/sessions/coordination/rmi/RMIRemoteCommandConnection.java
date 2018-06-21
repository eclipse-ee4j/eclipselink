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
package org.eclipse.persistence.internal.sessions.coordination.rmi;

import org.eclipse.persistence.sessions.coordination.Command;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * <p>
 * <b>Purpose</b>: The interface for the remote RMI object used for transporting
 * the remote command.
 * <p>
 * <b>Description</b>: This interface is the RMI remote object interface that is
 * wrapped by the connection abstraction.
 *
 * @see org.eclipse.persistence.sessions.coordination.Command
 * @since OracleAS TopLink 10<i>g</i> (9.0.4)
 */
public interface RMIRemoteCommandConnection extends Remote {
    public Object executeCommand(Command command) throws RemoteException;
    public Object executeCommand(byte[] command) throws RemoteException;
}
