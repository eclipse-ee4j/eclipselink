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
package org.eclipse.persistence.internal.sessions.coordination.corba.sun;

import org.eclipse.persistence.sessions.coordination.RemoteCommandManager;
import org.eclipse.persistence.internal.sessions.coordination.corba.CORBAConnection;
import org.eclipse.persistence.sessions.coordination.corba.CORBATransportManager;

/**
 * <p>
 * <b>Purpose</b>: The implementation is for the remote Sun CORBA object that is used for
 * transporting the remote command.
 * <p>
 * <b>Description</b>: This implementation class is the CORBA remote object that is
 * wrapped by the connection abstraction. It is the first point of entry of the
 * command into the remote service. It simply hands the command to the RCM to be
 * processed.
 *
 * @see org.eclipse.persistence.sessions.coordination.Command
 * @see org.eclipse.persistence.sessions.coordination.RemoteCommandManager
 *
 * @author Steven Vo
 * @since OracleAS TopLink 10.0.3
 */
public class SunCORBAConnectionImpl extends _SunCORBAConnectionImplBase implements CORBAConnection {
    protected RemoteCommandManager rcm;

    public SunCORBAConnectionImpl(RemoteCommandManager rcm) {
        super();
        this.rcm = rcm;
    }

    @Override
    public byte[] executeCommand(byte[] command) {
        return CORBATransportManager.processCommand(command, rcm);
    }
}
