/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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

import java.util.Properties;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.sessions.remote.corba.sun.CORBAConnection;
import org.eclipse.persistence.testing.tests.remote.*;

public class CORBAConnectionExceptionsTest extends RemoteConnectionExceptionsTest {

public CORBAConnectionExceptionsTest(int mode) {
    super(mode, CORBAConnection.class);
}

public void setup() throws Exception {
    Session session = new DatabaseSessionImpl();
    session.setProperty("TransporterGenerator", generator);
    CORBAServerManagerController.start(session, getNameToBind(), "org.eclipse.persistence.testing.tests.remote.suncorba.CORBARemoteSessionControllerDispatcherForTestingExceptions", false);

    // Initialize the ORB.
    ORB orb = ORB.init(new String[0], new Properties());
    org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
    NamingContext ncRef = NamingContextHelper.narrow(objRef);
    NameComponent nc = new NameComponent(getNameToBind(), "");
	NameComponent path[] = {nc};
    CORBAServerManager manager = CORBAServerManagerHelper.narrow(ncRef.resolve(path));
	CORBAConnection corbaConnection = new CORBAConnection(manager.createRemoteSessionController());
    setRemoteConnection(corbaConnection);
}

public void reset() throws Exception {
    ORB orb = ORB.init(new String[0], new Properties());
    org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
    NamingContext ncRef = NamingContextHelper.narrow(objRef);
    NameComponent nc = new NameComponent(getNameToBind(), "");
	NameComponent path[] = {nc};
    ncRef.unbind(path);
}

}
