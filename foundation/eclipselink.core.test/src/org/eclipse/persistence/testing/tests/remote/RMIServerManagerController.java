/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.remote;

import java.lang.reflect.Constructor;

import java.rmi.*;
import java.rmi.server.*;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.remote.rmi.*;

public class RMIServerManagerController extends UnicastRemoteObject implements RMIServerManager {
    protected Session session;
    protected String controllerClassName;

    public RMIServerManagerController(Session session) throws RemoteException {
        super();
        this.session = session;
    }

    public RMIServerManagerController(Session session, String controllerClassName) throws RemoteException {
        this(session);
        this.controllerClassName = controllerClassName;
    }

    public RMIRemoteSessionController createRemoteSessionController() {
        RMIRemoteSessionController controller = null;

        if (controllerClassName == null) {
            try {
                controller = new RMIRemoteSessionControllerDispatcher((getSession()));
            } catch (RemoteException exception) {
                System.out.println("Error in invocation " + exception.toString());
            }
        } else {
            try {
                Class cls = Class.forName(controllerClassName);
                Class[] parameterTypes = { org.eclipse.persistence.sessions.Session.class };
                Constructor constructor = cls.getConstructor(parameterTypes);
                Object[] params = { getSession() };
                controller = (RMIRemoteSessionController)constructor.newInstance(params);
            } catch (Exception exception) {
                System.out.println("Error instantiating  " + controllerClassName + " " + exception.toString());
            }
        }

        return controller;
    }

    protected Session getSession() {
        return session;
    }

    protected void setSession(Session session) {
        this.session = session;
    }

    public static void start(Session session) {
        start(session, "SERVER-MANAGER");
    }

    public static void start(Session session, String nameToBind) {
        start(session, nameToBind, null);
    }

    public static void start(Session session, String nameToBind, String controllerClassName) {
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
            manager = new RMIServerManagerController(session, controllerClassName);
        } catch (RemoteException exception) {
            throw new TestProblemException(exception.toString());
        }

        // Put the local instance into the Registry
        try {
            Naming.unbind(nameToBind);
        } catch (Exception exception) {
            System.out.println("Security violation " + exception.toString());
        }

        // Put the local instance into the Registry
        try {
            Naming.rebind(nameToBind, manager);
        } catch (Exception exception) {
            throw new TestProblemException(exception.toString());
        }
    }
}
