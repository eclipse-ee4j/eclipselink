/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.remote.rmi;

import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.remote.rmi.RMIConnection;
import org.eclipse.persistence.testing.tests.remote.RMIServerManager;
import org.eclipse.persistence.testing.tests.remote.RMIServerManagerController;
import org.eclipse.persistence.testing.tests.remote.RemoteConnectionExceptionsTest;

import java.rmi.Naming;

public class RMIConnectionExceptionsTest extends RemoteConnectionExceptionsTest {

public RMIConnectionExceptionsTest(int mode) {
    super(mode, RMIConnection.class);
}

@Override
public void setup() throws Exception {
    Session session = new org.eclipse.persistence.internal.sessions.DatabaseSessionImpl();
    session.setProperty("TransporterGenerator", generator);
    RMIServerManagerController.start(session, getNameToBind(), "org.eclipse.persistence.testing.tests.remote.rmi.RMIRemoteSessionControllerDispatcherForTestingExceptions");
    RMIServerManager serverManager = (RMIServerManager) Naming.lookup(getNameToBind());
    RMIConnection rmiConnection = new RMIConnection(serverManager.createRemoteSessionController());
    setRemoteConnection(rmiConnection);
}

@Override
public void reset() throws Exception {
    Naming.unbind(getNameToBind());
}

}
