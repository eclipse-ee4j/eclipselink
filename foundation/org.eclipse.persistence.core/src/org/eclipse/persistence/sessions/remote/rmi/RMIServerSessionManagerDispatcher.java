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
