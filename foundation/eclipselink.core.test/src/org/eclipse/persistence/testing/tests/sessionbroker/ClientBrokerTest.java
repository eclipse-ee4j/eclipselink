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
package org.eclipse.persistence.testing.tests.sessionbroker;

import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;


public class ClientBrokerTest extends AutoVerifyTestCase {
    protected DatabaseLogin login;
    protected Client[] clients;
    protected Server server;

    public ClientBrokerTest() {
        clients = new Client[3];
        setDescription("The test simulates the session broker by spawning clients in the thread");
    }

    public void reset() {
        this.clients[0].release();
        this.clients[1].release();
        this.clients[2].release();

        this.server.logout();

        ((DatabaseSession)getSession()).logout();
        ((DatabaseSession)getSession()).login();
    }

    public void setup() {
        this.server = new Server();
        this.server.sSessionBroker.setLogLevel(getSession().getLogLevel());
        this.server.sSessionBroker.setLog(getSession().getLog());
        this.server.login();

        this.clients[0] = new Client(this.server, "Smith", getSession());
        this.clients[1] = new Client(this.server, "Way", getSession());
        this.clients[2] = new Client(this.server, "Chanley", getSession());

    }

    public void test() {
        this.clients[0].start();
        this.clients[1].start();
        this.clients[2].start();

        try {
            this.clients[0].join();
            this.clients[1].join();
            this.clients[2].join();

        } catch (InterruptedException exception) {
            TestErrorException testException = new TestErrorException("Client threads are interrupted");
            testException.setInternalException(exception);
            throw testException;
        }
    }
}
