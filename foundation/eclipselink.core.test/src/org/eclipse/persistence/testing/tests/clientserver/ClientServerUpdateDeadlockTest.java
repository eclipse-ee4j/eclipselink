/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     James Sutherland - initial implementation
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.clientserver;

import java.util.List;
import java.util.Random;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

/**
 * This test attempts to cause a database deadlock by updates the same
 * set of objects in a transaction on multiple threads.
 * If the updates are ordered randomly, this can cause a deadlock.
 * @author James Sutherland
 */
public class ClientServerUpdateDeadlockTest extends TestCase {
    protected DatabaseLogin login;
    protected UpdateDeadlockClient[] clients;
    protected Server server;
    public static int NUM_CLIENTS = 15;
    
    protected class UpdateDeadlockClient extends Thread {
        protected int index;
        protected Server server;
        protected Session clientSession;
        protected Session session;
        public Throwable exception;

        public UpdateDeadlockClient(Server server, Session session, String name, int index) {
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
                    uow.setShouldOrderUpdates(true);
                    List<Address> addresses = uow.readAllObjects(Address.class);
                    Random random = new Random();
                    for (Address address : addresses) {
                        if (random.nextBoolean()) {
                            address.setCity(getName() + index);
                        }
                    }
                    uow.commit();
                }
            } catch (Throwable t) {
                t.printStackTrace();
                exception = t;
            }
        }
    }

    public ClientServerUpdateDeadlockTest() {
        this.clients = new UpdateDeadlockClient[NUM_CLIENTS];
        setDescription("Tests running a number of threads updating the same employees.");
    }

    @Override
    public void setup() {
        this.login = (DatabaseLogin)getSession().getLogin().clone();
        this.server = new Server(this.login);
        this.server.serverSession.setSessionLog(getSession().getSessionLog());
        this.server.login();
        this.server.copyDescriptors(getSession());
        for (int index = 0; index < NUM_CLIENTS; index++) {
            clients[index] = new UpdateDeadlockClient(this.server, getSession(), "Client" + index, index);
        }
    }

    @Override
    public void reset() {
        for (int i = 0; i < NUM_CLIENTS; i++) {
            this.clients[i].release();
        }
        this.server.logout();
        getDatabaseSession().logout();
        getDatabaseSession().login();
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

    /**
     * Check if any errors occurred.
     */
    @Override
    public void verify() {
        for (int i = 0; i < NUM_CLIENTS; i++) {
            if (this.clients[i].exception != null) {
                throw new TestErrorException("Error occurred, check system.out");
            }
        }
    }
}
