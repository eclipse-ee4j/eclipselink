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
package org.eclipse.persistence.testing.tests.sessionbroker;

import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import org.eclipse.persistence.sessions.remote.rmi.RMIRemoteSessionController;
import org.eclipse.persistence.sessions.remote.rmi.RMIRemoteSessionControllerDispatcher;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.testing.framework.TestProblemException;


public class RMIServerManagerController extends UnicastRemoteObject implements RMISessionBrokerServerManager {
    protected Session session;
    public Session broker;

    public RMIServerManagerController(Session session) throws RemoteException {
        super();
        this.session = session;
    }

    public RMIRemoteSessionController createRemoteSessionController() {
        RMIRemoteSessionController controller = null;

        try {
            controller = new RMIRemoteSessionControllerDispatcher(this.session);
        } catch (RemoteException exception) {
            System.out.println("Error in invocation " + exception.toString());
        }

        return controller;
    }

    protected void setSession(Session session) {
        this.session = session;
    }

    public static void start(Session session) {
        RMIServerManagerController manager = null;

        // Set the security manager
        try {
            System.setSecurityManager(new RMISecurityManager());
        } catch (Exception exception) {
            System.out.println("Security violation " + exception.toString());
        }

        // Make sure RMI registry is started.
        try {
            java.rmi.registry.LocateRegistry.createRegistry(1099);
        } catch (Exception exception) {
            System.out.println("Security violation " + exception.toString());
        }

        // Create local instance of the factory
        try {
            manager = new RMIServerManagerController(session);
        } catch (RemoteException exception) {
            throw new TestProblemException(exception.toString());
        }

        // Put the local instance into the Registry
        //try {
        //    Naming.unbind("SERVER-BROKER-MANAGER");
        //} catch (Exception exception) {
        //    System.out.println("Security violation " + exception.toString());
        //}

        // Put the local instance into the Registry
        try {
            Naming.rebind("SERVER-BROKER-MANAGER", manager);
        } catch (Exception exception) {
            throw new TestProblemException(exception.toString());
        }
    }
}
