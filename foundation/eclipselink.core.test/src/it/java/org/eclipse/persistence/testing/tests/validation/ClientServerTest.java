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
package org.eclipse.persistence.testing.tests.validation;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.server.ConnectionPool;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;

import java.util.List;


public class ClientServerTest extends AutoVerifyTestCase {
    protected DatabaseLogin login;
    protected Client[] clients;
    protected Server server;
    protected int numberOfClients;
    protected int minimumConnections;
    protected int maximumConnections;

    public ClientServerTest(int number) {
        this(number, 2, 4);
    }

    public ClientServerTest(int number, int min, int max) {
        numberOfClients = number;
        minimumConnections = min;
        maximumConnections = max;
        clients = new Client[numberOfClients];

        setName(getName() + "(" + numberOfClients + " clients, min: " + min + " max: " + max + ")");
        setDescription("This test spawns clients and releases the client sessions many times" + " to ensure that too many connections are not being disconnected.");
    }

    @Override
    public void reset() throws Exception {
        try {
            for (int i = 0; i < numberOfClients; i++) {
                this.clients[i].release();
            }
            this.server.logout();

            ((DatabaseSession)getSession()).logout();
            ((DatabaseSession)getSession()).login();

        } catch (Exception ex) {
            if ((ex instanceof ValidationException)) {
                this.verify();
            }
        }

    }

    @Override
    public void setup() throws Exception {
        try {
            this.login = (DatabaseLogin)getSession().getLogin().clone();
            this.server = new Server(this.login, minimumConnections, maximumConnections);
            this.server.serverSession.setLogLevel(getSession().getLogLevel());
            this.server.serverSession.setLog(getSession().getLog());
            this.server.login();
            for (int i = 0; i < numberOfClients; i++) {
                this.clients[i] = new Client(this.server, "Client " + i, getSession());
            }
        } catch (Exception ex) {
            if (ex instanceof ValidationException) {
                this.verify();
            }
        }

    }

    @Override
    public void test() {
        try {

            for (int i = 0; i < numberOfClients; i++) {
                this.clients[i].run();
            }

            try {
                for (int i = 0; i < numberOfClients; i++) {
                    this.clients[i].join();
                }
            } catch (InterruptedException exception) {
                TestErrorException testException = new TestErrorException("Client threads are interrupted");
                testException.setInternalException(exception);
                throw testException;
            }
        } catch (Exception ex) {
            if (ex instanceof ValidationException) {
                this.verify();
            }
        }


    }

    @Override
    public void verify() {
        try {
            int counter = 0;
            ConnectionPool pool = server.serverSession.getConnectionPools().get("default");
            List<Accessor> connections = pool.getConnectionsAvailable();
            for (Accessor connection : connections) {
                if (connection.isConnected()) {
                    counter = counter + 1;
                }
            }

            if (counter < minimumConnections) {
                throw new TestErrorException("too many connections are disconnected!!");
            }
            if (counter > minimumConnections) {
                throw new TestErrorException("not enough connections are disconected!!");
            }
            if (connections.size() < minimumConnections) {
                throw new TestErrorException("too many connections are released!!");
            }
            if (connections.size() > minimumConnections) {
                throw new TestErrorException("not enough connections are released!!");
            }
        } catch (Exception ex) {
            if ((ex instanceof ValidationException) && (((ValidationException)ex).getErrorCode() == 7090)) {
            }
        }

    }
}
