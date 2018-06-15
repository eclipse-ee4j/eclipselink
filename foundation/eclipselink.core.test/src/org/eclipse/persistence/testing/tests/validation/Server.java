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
package org.eclipse.persistence.testing.tests.validation;

import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.server.ServerSession;


public class Server {
    protected ServerSession serverSession;

    public Server(DatabaseLogin login, int min, int max) {
        this.serverSession = new ServerSession(login);
        this.serverSession.addConnectionPool("default", login, min, max);
    }

    public void connect(Client client) {
        client.serverSession = serverSession;
        client.clientSession = serverSession.acquireClientSession("default");
        client.clientSession.getConnectionPolicy().dontUseLazyConnection();
    }

    public void login() {
        this.serverSession.login();
    }

    public void logout() {
        this.serverSession.logout();
    }
}
