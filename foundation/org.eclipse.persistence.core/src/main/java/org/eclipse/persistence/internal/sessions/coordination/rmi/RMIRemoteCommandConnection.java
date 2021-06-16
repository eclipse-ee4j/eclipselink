/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
    Object executeCommand(Command command) throws RemoteException;
    Object executeCommand(byte[] command) throws RemoteException;
}
