/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.remote.suncorba;

import java.lang.reflect.Constructor;
import java.util.*;

import org.omg.CORBA.*;
import org.omg.CosNaming.*;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.sessions.remote.corba.sun.*;

public class CORBAServerManagerController extends _CORBAServerManagerImplBase {
    protected Session session;
    protected String controllerClassName;

    public CORBAServerManagerController(Session session) {
        super();
        this.session = session;
    }

    public CORBAServerManagerController(Session session, String controllerClassName) {
        this(session);
        this.controllerClassName = controllerClassName;
    }

    public CORBARemoteSessionController createRemoteSessionController() {
        CORBARemoteSessionController controller = null;

        if (controllerClassName == null) {
            controller = new CORBARemoteSessionControllerDispatcher((getSession()));
        } else {
            try {
                Class cls = Class.forName(controllerClassName);
                Class[] parameterTypes = { org.eclipse.persistence.sessions.Session.class };
                Constructor constructor = cls.getConstructor(parameterTypes);
                java.lang.Object[] params = { getSession() };
                controller = (CORBARemoteSessionController)constructor.newInstance(params);
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
        start(session, "CORBAServer", null, true);
    }

    public static NameComponent start(Session session, String nameToBind, String controllerClassName,
                                      boolean wait) {
        try {
            // Initialize the ORB.
            ORB orb = ORB.init(new String[0], new Properties());

            CORBAServerManagerController server = new CORBAServerManagerController(session, controllerClassName);
            orb.connect(server);
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContext ncRef = NamingContextHelper.narrow(objRef);
            NameComponent nc = new NameComponent(nameToBind, "");
            NameComponent path[] = { nc };
            ncRef.rebind(path, server);
            java.lang.Object sync = new java.lang.Object();
            if (wait) {
                synchronized (sync) {
                    sync.wait();
                }
            }
            System.out.println(server + " is ready.");
            return nc;
        } catch (Exception exception) {
            System.out.println("CORBA Error");
            exception.printStackTrace();
        }
        return null;
    }
}
