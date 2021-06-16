/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.clientserver;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.exceptions.*;

public class ClientServerReadingDeadlockTest extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    protected DatabaseLogin login;
    protected Client2[] policyClients;
    protected Client2[] policyHolderClients;
    protected Server server;
    private static final int CLIENT_NUM = 200;

    public ClientServerReadingDeadlockTest() {
        policyClients = new PolicyClientLock[CLIENT_NUM];
        policyHolderClients = new PolicyHolderClient[CLIENT_NUM];

        setDescription("The test simulates the deadlock situation when reading the bidirectional objects among multi-thread");
    }

    public void reset() {
        try {
            for (int index = 0; index < CLIENT_NUM; index++) {
                policyHolderClients[index].release();
                policyClients[index].release();
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
            this.server = new Server(this.login);
            this.server.serverSession.setSessionLog(getSession().getSessionLog());
            this.server.login();
            this.server.copyDescriptors(getSession());
            for (int index = 0; index < CLIENT_NUM; index++) {
                policyClients[index] = new PolicyClientLock(this.server, getSession(), "PolicyClientLock" + index);
                policyHolderClients[index] = new PolicyHolderClient(this.server, getSession(), "PolicyHolderClient" + index);
            }
        } catch (Exception ex) {
            if (ex instanceof ValidationException) {
                this.verify();
            }
        }
    }

    public void test() {
        try {
            for (int index = 0; index < CLIENT_NUM; index++) {
                policyClients[index].start();
                policyHolderClients[index].start();
            }

            try {
                for (int index = 0; index < CLIENT_NUM; index++) {
                    policyClients[index].join();
                    policyHolderClients[index].join();
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
        } catch (Exception ex) {
            if ((ex instanceof ValidationException) && (((ValidationException)ex).getErrorCode() == 7090)) {
            }
        }
    }
}
