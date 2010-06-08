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
package org.eclipse.persistence.testing.tests.remote.rmi.IIOP;

import javax.naming.InitialContext;
import java.lang.reflect.Method;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.sessions.remote.rmi.iiop.RMIConnection;
import org.eclipse.persistence.testing.tests.remote.*;

public class RMIConnectionExceptionsTest extends RemoteConnectionExceptionsTest {

public RMIConnectionExceptionsTest(int mode) {
    super(mode, RMIConnection.class);
}

protected void setKnownBugs() {
    super.setKnownBugs();
    if(generator.getMode() == TransporterGenerator.SET_EXCEPTION_INTO_TRANSPORTER) {
        for (int i=0; i<methods.size(); i++) {
            Method method = (Method)methods.elementAt(i);
            Object[] params = (Object[]) args.elementAt(i);
            String name = method.getName();
            if(name.equals("getFromIdentityMap") || name.equals("containsObjectInIdentityMap")) {
                if(params.length == 2) {
                    knownBugs.put(method, "bug 2906391");
                }
            }
        }
    }
}

public void setup() throws Exception {
    Session session = new DatabaseSessionImpl();
    session.setProperty("TransporterGenerator", generator);
	RMIServerManagerController.start(session, getNameToBind(), "org.eclipse.persistence.testing.tests.remote.rmi.IIOP.RMIRemoteSessionControllerDispatcherForTestingExceptions");
	InitialContext initialNamingContext = new InitialContext();
	RMIServerManager serverManager = (RMIServerManager) initialNamingContext.lookup(getNameToBind());
	RMIConnection rmiConnection = new RMIConnection(serverManager.createRemoteSessionController());
    setRemoteConnection(rmiConnection);
}

public void reset() throws Exception {
	InitialContext initialNamingContext = new InitialContext();
	initialNamingContext.unbind(getNameToBind());
}

}
