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
package org.eclipse.persistence.testing.tests.clientserver;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.server.*;

public class ClientLoginTest extends TestCase {
    protected DatabaseLogin login;
    protected Server server;
    protected ClientSession clientSession;

    public ClientLoginTest() {
    }

    public void reset() {
        this.server.logout();
        getDatabaseSession().logout();
        getDatabaseSession().login();
    }

    public void setup() {
        this.login = (DatabaseLogin)getSession().getLogin().clone();
        this.server = new Server(this.login);
        this.server.serverSession.setSessionLog(getSession().getSessionLog());
        this.server.login();
    }

    public void test() {
        DatabaseLogin newLogin = (DatabaseLogin)this.login.clone();
        newLogin.setUserName("xFredyFlinstonez");//Nonsense username and password
        newLogin.setPassword("abcWindex");

        try {
            //Make sure Client Session logs in using the newLogin, NOT the server's login.
            this.clientSession = this.server.serverSession.acquireClientSession(newLogin);
        } catch (Exception e) {/* Ignore, test passed */
        }
    }

    public void verify() {
        if ((clientSession != null) && clientSession.isConnected()) {
            throw new TestErrorException("The client's login value was ignored.  Test failed");
        }
    }
}
