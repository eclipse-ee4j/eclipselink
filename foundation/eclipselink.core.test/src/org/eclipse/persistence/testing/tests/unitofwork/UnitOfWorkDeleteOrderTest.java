/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
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
//     John Vandale - initial API and implementation
package org.eclipse.persistence.testing.tests.unitofwork;

import java.util.List;
import java.util.Random;

import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.sessions.UnitOfWork.CommitOrderType;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.models.employee.domain.PhoneNumber;
import org.eclipse.persistence.testing.tests.clientserver.Server;



//bug 331064

/**
 *  Test to ensure objects are deleted in order of PK when should order is set to avoid deadlock.
 */
public class UnitOfWorkDeleteOrderTest extends TestCase {
    protected DatabaseLogin login;
    protected UpdateDeadlockClientDeleteOrder[] clients;
    protected Server server;
    public static int NUM_CLIENTS = 20;

    protected class UpdateDeadlockClientDeleteOrder extends Thread {
        protected int index;
        protected Server server;
        protected Session clientSession;
        protected Session session;
        public Throwable exception;

        public UpdateDeadlockClientDeleteOrder(Server server, Session session, String name, int index) {
            super(name);
            this.index = index;
            this.server = server;
            this.session = session;
            this.clientSession = this.server.serverSession.acquireClientSession();
        }

        public void release() {
            this.clientSession.release();
        }

        @Override
        public void run() {
            try {
                for (int index = 0; index < 5; index++) {
                    UnitOfWork uow = this.clientSession.acquireUnitOfWork();
                    uow.setCommitOrder(CommitOrderType.ID);
                    List<PhoneNumber> phoneNumbers = uow.readAllObjects(PhoneNumber.class);
                    Random random = new Random();
                    for (PhoneNumber phoneNumber : phoneNumbers) {
                        if (random.nextBoolean()) {
                            uow.deleteObject(phoneNumber);
                        }
                    }
                    uow.writeChanges();
                    Thread.sleep(1000);
                    uow.commit();
                }
            } catch (Throwable t) {
                t.printStackTrace();
                exception = t;
            }
        }
    }

    public UnitOfWorkDeleteOrderTest() {
        this.clients = new UpdateDeadlockClientDeleteOrder[NUM_CLIENTS];
        setDescription("Test to ensure objects are deleted in order of PK when should order is set to avoid deadlock.");
    }

    @Override
    public void setup() {
        this.login = (DatabaseLogin)getSession().getLogin().clone();
        this.server = new Server(this.login);
        this.server.serverSession.setSessionLog(getSession().getSessionLog());
        this.server.login();
        this.server.copyDescriptors(getSession());
        for (int index = 0; index < NUM_CLIENTS; index++) {
            clients[index] = new UpdateDeadlockClientDeleteOrder(this.server, getSession(), "Client" + index, index);
        }
    }

    @Override
    public void reset() {
        for (int i = 0; i < NUM_CLIENTS; i++) {
            this.clients[i].release();
        }
        this.server.logout();
    }

    @Override
    public void test() {
        for (int i = 0; i < NUM_CLIENTS; i++) {
            this.clients[i].start();
        }
        try {
            for (int i = 0; i < NUM_CLIENTS; i++) {
                this.clients[i].join();
            }
        } catch (InterruptedException ex) {
        }
    }

    @Override
    public void verify() {
        for (int i = 0; i < NUM_CLIENTS; i++) {
            if (this.clients[i].exception != null) {
                throwError("Deleting objects caused deadlock.");
            }
        }
    }
}
