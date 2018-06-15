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
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.exceptions.*;

public class ConcurrencyManagerTest extends AutoVerifyTestCase {
    protected DatabaseLogin login;
    protected CMClient[] clients;
    protected CMServer server;
    protected final int MaxThread;

    public ConcurrencyManagerTest() {
        this.MaxThread = 200;
        this.clients = new CMClient[MaxThread];
        setDescription("Tests Read concurrency with the Concurrency Manager");

    }

    public void reset() {
        try {
            for (int index = 0; index < MaxThread; index++) {
                (this.clients[index]).release();
            }

            this.server.logout();

            getDatabaseSession().logout();
            getDatabaseSession().login();

        } catch (Exception ex) {
            if (ex instanceof ValidationException) {
                this.verify();
            }
        }
    }

    public void setup() {
        try {
            this.login = (DatabaseLogin)getSession().getLogin().clone();
            this.server = new CMServer(this.login);
            this.server.serverSession.setSessionLog(getSession().getSessionLog());
            this.server.login();
            this.server.copyDescriptors(getSession());

            for (int index = 0; index < MaxThread; index++) {
                this.clients[index] = new CMClient(this.server, index, getSession());
            }
        } catch (Exception ex) {
            if (ex instanceof ValidationException) {
                this.verify();
            }
        }
    }

    public void test() {
        try {
            for (int index = 0; index < MaxThread; index++) {
                this.clients[index].start();
            }

            try {
                for (int index = 0; index < MaxThread; index++) {
                    (this.clients[index]).join();
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

    public void verify() {
        try {
            if (this.server.errorOccured) {
                throw new TestErrorException("An error occurred on one of the clients, check System.out.");
            }

            for (int index = 0; index < MaxThread; index++) {
                if (this.clients[0].objectRead != this.clients[index].objectRead) {
                    throw new TestErrorException("Objects are not the same!!!");
                }
            }
        } catch (Exception ex) {
            if ((ex instanceof ValidationException) && (((ValidationException)ex).getErrorCode() == 7090)) {
            }
        }
    }
}
