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
