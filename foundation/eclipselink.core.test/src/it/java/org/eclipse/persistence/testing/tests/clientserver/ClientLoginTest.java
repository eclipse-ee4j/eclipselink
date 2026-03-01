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
package org.eclipse.persistence.testing.tests.clientserver;

import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.server.ClientSession;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;

public class ClientLoginTest extends TestCase {
    protected DatabaseLogin login;
    protected Server server;
    protected ClientSession clientSession;

    public ClientLoginTest() {
    }

    @Override
    public void reset() {
        this.server.logout();
        getDatabaseSession().logout();
        getDatabaseSession().login();
    }

    @Override
    public void setup() {
        this.login = (DatabaseLogin)getSession().getLogin().clone();
        this.server = new Server(this.login);
        this.server.serverSession.setSessionLog(getSession().getSessionLog());
        this.server.login();
    }

    @Override
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

    @Override
    public void verify() {
        if ((clientSession != null) && clientSession.isConnected()) {
            throw new TestErrorException("The client's login value was ignored.  Test failed");
        }
    }
}
