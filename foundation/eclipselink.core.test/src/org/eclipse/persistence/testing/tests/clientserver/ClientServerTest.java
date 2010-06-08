/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

public class ClientServerTest extends TestCase {
    protected DatabaseLogin login;
    protected Client[] clients;
    protected AnotherClient[] moreClients;
    protected Server server;

    public ClientServerTest() {
        clients = new Client[3];
        moreClients = new AnotherClient[15];
        setDescription("The test simulates the client/serve by spawning clients in the thread");
    }

    public void reset() {
        try {
            this.clients[0].release();
            this.clients[1].release();
            this.clients[2].release();

            for (int index = 0; index < 15; index++) {
                (this.moreClients[index]).release();
            }

            this.server.logout();

            getDatabaseSession().logout();
            getDatabaseSession().login();

        } catch (ValidationException ex) {
            this.verify();
        }
    }

    public void setup() {
        try {
            this.login = (DatabaseLogin)getSession().getLogin().clone();
            this.server = new Server(this.login);
            this.server.serverSession.setSessionLog(getSession().getSessionLog());
            this.server.login();
            this.server.copyDescriptors(getSession());

            this.clients[0] = new Client(this.server, "Smith", getSession());
            this.clients[1] = new Client(this.server, "Way", getSession());
            this.clients[2] = new Client(this.server, "Chanley", getSession());

            for (int index = 0; index < 15; index++) {
                this.moreClients[index] = new AnotherClient(this.server, index, getSession());
            }
        } catch (ValidationException ex) {
            this.verify();
        }
    }

    public void test() {
        try {
            this.clients[0].start();
            this.clients[1].start();
            this.clients[2].start();

            for (int index = 0; index < 15; index++) {
                (this.moreClients[index]).start();
            }

            try {
                this.clients[0].join();
                this.clients[1].join();
                this.clients[2].join();

                for (int index = 0; index < 15; index++) {
                    (this.moreClients[index]).join();
                }
            } catch (InterruptedException exception) {
                TestErrorException testException = new TestErrorException("Client threads are interrupted");
                testException.setInternalException(exception);
                throw testException;
            }
        } catch (ValidationException ex) {
            this.verify();
        }
    }

    public void verify() {
        try {
            if (this.server.errorOccured) {
                throw new TestErrorException("An error occurred on one of the clients, check System.out.");
            }

            getExecutor().getSession().getIdentityMapAccessor().initializeIdentityMaps();

            Expression exp = new ExpressionBuilder().get("lastName").equal("Smith");
            Object objectToVerify = getSession().readObject(Employee.class, exp);

            if (!compareObjects(objectToVerify, this.clients[0].objectToBeWritten)) {
                throw new TestErrorException("Object was not written properly to the database");
            }

            exp = new ExpressionBuilder().get("lastName").equal("Way");
            objectToVerify = getSession().readObject(Employee.class, exp);

            if (!compareObjects(objectToVerify, this.clients[1].objectToBeWritten)) {
                throw new TestErrorException("Object was not written properly to the database");
            }

            exp = new ExpressionBuilder().get("lastName").equal("Chanley");
            objectToVerify = getSession().readObject(Employee.class, exp);

            if (!compareObjects(objectToVerify, this.clients[2].objectToBeWritten)) {
                throw new TestErrorException("Object was not written properly to the database");
            }
        } catch (ValidationException ex) {
            if ((ex.getErrorCode() != 7090)) {
                throw ex;
            }
        }
    }
}
