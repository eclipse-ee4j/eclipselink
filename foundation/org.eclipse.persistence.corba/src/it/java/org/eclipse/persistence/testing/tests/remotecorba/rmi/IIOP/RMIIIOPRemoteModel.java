/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.remotecorba.rmi.IIOP;

import java.rmi.*;

import javax.naming.*;

import org.eclipse.persistence.testing.tests.remote.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.sessions.remote.rmi.iiop.*;
import org.eclipse.persistence.testing.framework.TestProblemException;
import org.eclipse.persistence.testing.framework.TestSuite;

public class RMIIIOPRemoteModel extends RemoteModel {

    public RMIIIOPRemoteModel() {
        super();
    }

    public RMIConnection createConnection() {
        RMIServerManager serverManager = null;
        Context initialNamingContext = null;
        try {
            initialNamingContext = new InitialContext();
        } catch (NamingException exception) {
            System.out.println("Naming Exception " + exception.toString());
        }
        ;
        // Set the client security manager
        try {
            System.setSecurityManager(new RMISecurityManager());
        } catch (Exception exception) {
            System.out.println("Security violation " + exception.toString());
        }

        // Get the remote factory object from the Registry
        try {
            serverManager = (RMIServerManager)initialNamingContext.lookup("SERVER-MANAGER");
        } catch (Exception exception) {
            throw new TestProblemException(exception.toString());
        }

        RMIConnection rmiConnection = null;
        try {
            rmiConnection = new RMIConnection(serverManager.createRemoteSessionController());
        } catch (RemoteException exception) {
            System.out.println("Error in invocation " + exception.toString());
        }

        return rmiConnection;
    }

    @Override
    public void setup() {
        RemoteModel.originalSession = getSession();
        RMIServerManagerController.start(buildServerSession());
        RMIConnection connection = createConnection();
        Session remoteSession = connection.createRemoteSession();
        remoteSession.setLog(RemoteModel.originalSession.getLog());
        remoteSession.setLogLevel(RemoteModel.originalSession.getLogLevel());

        getExecutor().setSession(remoteSession);
    }

    @Override
    public void addTests() {
        super.addTests();
        addTest(getExceptionsTestSuite());
    }

    public static TestSuite getExceptionsTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("ExceptionsTestSuite");
        suite.setDescription("Verifies whether the right exceptions are thrown");

        suite.addTest(new RMIConnectionExceptionsTest(TransporterGenerator.THROW_REMOTE_EXCEPTION));
        suite.addTest(new RMIConnectionExceptionsTest(TransporterGenerator.SET_EXCEPTION_INTO_TRANSPORTER));

        return suite;
    }
}
