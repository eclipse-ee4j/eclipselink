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

@Override
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

@Override
public void setup() throws Exception {
    Session session = new DatabaseSessionImpl();
    session.setProperty("TransporterGenerator", generator);
    RMIServerManagerController.start(session, getNameToBind(), "org.eclipse.persistence.testing.tests.remote.rmi.IIOP.RMIRemoteSessionControllerDispatcherForTestingExceptions");
    InitialContext initialNamingContext = new InitialContext();
    org.eclipse.persistence.testing.tests.remotecorba.rmi.IIOP.RMIServerManager serverManager = (RMIServerManager) initialNamingContext.lookup(getNameToBind());
    RMIConnection rmiConnection = new RMIConnection(serverManager.createRemoteSessionController());
    setRemoteConnection(rmiConnection);
}

@Override
public void reset() throws Exception {
    InitialContext initialNamingContext = new InitialContext();
    initialNamingContext.unbind(getNameToBind());
}

}
