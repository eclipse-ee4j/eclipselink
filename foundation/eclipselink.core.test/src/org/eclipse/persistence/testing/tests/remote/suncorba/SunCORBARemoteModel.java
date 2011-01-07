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
package org.eclipse.persistence.testing.tests.remote.suncorba;

import java.util.*;

import org.omg.CORBA.*;
import org.omg.CosNaming.*;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.sessions.remote.corba.sun.*;
import org.eclipse.persistence.testing.tests.remote.RemoteModel;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.tests.remote.TransporterGenerator;

public class SunCORBARemoteModel extends RemoteModel {

    public SunCORBARemoteModel() {
        super();
    }

    public CORBAConnection createConnection() {
        System.out.println("The following environment properties must be set to run this test.");
        System.out.println("-Djava.naming.factory.initial=com.sun.jndi.cosnaming.CNCtxFactory -Djava.naming.provider.url=iiop://localhost:900");
        System.out.println("The follwing corba naming server must also be started on the computer.");
        System.out.println("<java_home>/bin/tnameserv.exe");
        CORBAServerManager manager = null;
        try {
            // Initialize the ORB.
            ORB orb = ORB.init(new String[0], new Properties());
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContext ncRef = NamingContextHelper.narrow(objRef);
            NameComponent nc = new NameComponent("CORBAServer", "");
            NameComponent path[] = { nc };
            manager = CORBAServerManagerHelper.narrow(ncRef.resolve(path));
        } catch (Exception exception) {
            throw new TestErrorException("Corba not configured correctly, see system.out.", exception);
        }
        return new CORBAConnection(manager.createRemoteSessionController());

    }

    public void setup() {
        RemoteModel.originalSession = getSession();
        new CORBAServerRunner(buildServerSession()).start();
        // Must delay to let the stupid server start
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        CORBAConnection connection = createConnection();
        Session remoteSession = connection.createRemoteSession();
        remoteSession.setLog(RemoteModel.originalSession.getLog());
        remoteSession.setLogLevel(RemoteModel.originalSession.getLogLevel());

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

        suite.addTest(new CORBAConnectionExceptionsTest(TransporterGenerator.SET_EXCEPTION_INTO_TRANSPORTER));

        return suite;
    }
}
