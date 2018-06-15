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
package org.eclipse.persistence.testing.tests.remote.rmi;

import java.rmi.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.sessions.remote.rmi.RMIConnection;
import org.eclipse.persistence.testing.tests.remote.*;

public class RMIConnectionExceptionsTest extends RemoteConnectionExceptionsTest {

public RMIConnectionExceptionsTest(int mode) {
    super(mode, RMIConnection.class);
}

public void setup() throws Exception {
    Session session = new org.eclipse.persistence.internal.sessions.DatabaseSessionImpl();
    session.setProperty("TransporterGenerator", generator);
    RMIServerManagerController.start(session, getNameToBind(), "org.eclipse.persistence.testing.tests.remote.rmi.RMIRemoteSessionControllerDispatcherForTestingExceptions");
    RMIServerManager serverManager = (RMIServerManager) Naming.lookup(getNameToBind());
    RMIConnection rmiConnection = new RMIConnection(serverManager.createRemoteSessionController());
    setRemoteConnection(rmiConnection);
}

public void reset() throws Exception {
    Naming.unbind(getNameToBind());
}

}
