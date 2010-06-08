/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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

import java.rmi.*;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.sessions.remote.rmi.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.tests.remote.rmi.RMIConnectionExceptionsTest;

public class RMIRemoteModel extends RemoteModel {
    public RMIRemoteModel() {
        super();
    }

    public RMIConnection createConnection() {
        RMIServerManager serverManager = null;

        // Set the client security manager
        try {
            System.setSecurityManager(new RMISecurityManager());
        } catch (Exception exception) {
            throw new TestProblemException("Security manager set failed:", exception);
        }

        // Get the remote factory object from the Registry
        try {
            serverManager = (RMIServerManager)Naming.lookup("SERVER-MANAGER");
        } catch (Exception exception) {
            throw new TestProblemException("RMI Lookup failed:", exception);
        }

        RMIConnection rmiConnection = null;
        try {
            rmiConnection = new RMIConnection(serverManager.createRemoteSessionController());
        } catch (RemoteException exception) {
            throw new TestProblemException("Create remote session failed:", exception);
        }

        return rmiConnection;
    }

    public void setup() {
        originalSession = getSession();
        RMIServerManagerController.start(buildServerSession());
        RMIConnection connection = createConnection();
        Session remoteSession = connection.createRemoteSession();
        remoteSession.setSessionLog(originalSession.getSessionLog());

        getExecutor().setSession(remoteSession);
    }

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
