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
package org.eclipse.persistence.testing.tests.clientserver;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

public class ClientServerOptimisticLockingTest extends AutoVerifyTestCase {
    protected DatabaseLogin login;
    protected Client2[] clients;
    protected AddressClient aClient;
    protected Server server;
    public static int NUM_CLIENTS = 15;

    /**
     * This method was created in VisualAge.
     */
    public ClientServerOptimisticLockingTest() {
        clients = new Client2[NUM_CLIENTS];
        setDescription("Tests running a number of threads while using optomistic locking");
    }

    /**
     * This method was created in VisualAge.
     */
    public void reset() {
        try {
            for (int i = 0; i < NUM_CLIENTS; i++) {
                clients[i].release();
            }
            aClient.release();
            this.server.logout();
            getDatabaseSession().logout();
            getDatabaseSession().login();
        } catch (Exception ex) {
            if (ex instanceof ValidationException) {
                this.verify();
            }
        }
    }

    /**
     * This method was created in VisualAge.
     */
    public void setup() {
        try {
            this.login = (DatabaseLogin)getSession().getLogin().clone();
            this.server = new Server(this.login);
            this.server.serverSession.setSessionLog(getSession().getSessionLog());
            this.server.login();
            this.server.copyDescriptors(getSession());
            for (int i = 0; i < NUM_CLIENTS; i++) {
                clients[i] = new Client2(this.server, getSession(), "Client" + i);
            }
            aClient = new AddressClient(this.server, getSession(), "aClient");
        } catch (Exception ex) {
            if (ex instanceof ValidationException) {
                this.verify();
            }
        }
    }

    /**
     * This method was created in VisualAge.
     */
    public void test() {
        try {
            Session cs = this.server.serverSession.acquireClientSession();
            Employee newEmp = new org.eclipse.persistence.testing.models.employee.domain.Employee();
            newEmp.setFirstName("Matthew");
            newEmp.setLastName("MacIvor");
            UnitOfWork uow = cs.acquireUnitOfWork();
            uow.registerObject(newEmp);
            uow.commit();
            cs.release();
            for (int i = 0; i < NUM_CLIENTS; i++) {
                this.clients[i].start();
            }
            this.aClient.start();
            try {
                for (int i = 0; i < NUM_CLIENTS; i++) {
                    this.clients[i].join();
                }
                this.aClient.join();
            } catch (InterruptedException ex) {
            }
        } catch (Exception ex) {
            if (ex instanceof ValidationException) {
                this.verify();
            }
        }
    }

    /**
     * Check if any errors occured.
     */
    public void verify() {
        try {
            for (int i = 0; i < NUM_CLIENTS; i++) {
                if (this.clients[i].exception != null) {
                    throw new TestErrorException("Error occurred, check system.out");
                }
            }
        } catch (Exception ex) {
            if ((ex instanceof ValidationException) && (((ValidationException)ex).getErrorCode() == 7090)) {
            }
        }
    }
}
