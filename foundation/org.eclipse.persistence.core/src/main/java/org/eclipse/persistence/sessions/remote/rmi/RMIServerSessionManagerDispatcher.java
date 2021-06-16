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
package org.eclipse.persistence.sessions.remote.rmi;

import java.rmi.*;
import java.rmi.server.*;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.sessions.server.ServerSession;

public class RMIServerSessionManagerDispatcher extends UnicastRemoteObject implements RMIServerSessionManager {
    protected Session session;
    protected String controllerClassName;

    public RMIServerSessionManagerDispatcher(Session session) throws RemoteException {
        super();
        this.session = session;
    }

    @Override
    public RMIRemoteSessionController createRemoteSessionController() {
        RMIRemoteSessionController controller = null;
        try {
            if (getSession().isServerSession()) {
                controller = new RMIRemoteSessionControllerDispatcher((((ServerSession)getSession()).acquireClientSession()));
            } else {
                controller = new RMIRemoteSessionControllerDispatcher((getSession()));
            }
        } catch (RemoteException exception) {
            System.out.println("Error in invocation " + exception.toString());
        }

        return controller;
    }

    protected Session getSession() {
        return session;
    }

    protected void setSession(Session session) {
        this.session = session;
    }

}
