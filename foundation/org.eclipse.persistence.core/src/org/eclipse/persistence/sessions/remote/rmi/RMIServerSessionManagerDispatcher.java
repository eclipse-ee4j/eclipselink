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
