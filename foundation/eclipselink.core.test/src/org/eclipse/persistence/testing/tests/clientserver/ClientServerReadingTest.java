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
package org.eclipse.persistence.testing.tests.clientserver;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.exceptions.*;

public abstract class ClientServerReadingTest extends TestCase {
    protected DatabaseLogin login;
    protected Reader[] reader;
    protected Server1 server;
    private static final int NUM_THREADS = 50;
    int type;
    protected boolean shouldReadFromStreams;

    public ClientServerReadingTest(boolean useStreamReader, int theType) {
        reader = new Reader[NUM_THREADS];
        setDescription("The test simulates the client/serve by spawning clients in the thread");
        this.shouldReadFromStreams = useStreamReader;
        this.type = theType;
    }

    public void reset() {
        try {
            for (int index = 0; index < NUM_THREADS; index++) {
                (this.reader[index]).release();
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
            this.server = new Server1(this);
            this.server.serverSession.setSessionLog(getSession().getSessionLog());
            this.server.login();
            this.server.copyDescriptors(getSession());

            if(shouldReadFromStreams) {
                for (int index = 0; index < NUM_THREADS; index++) {
                    this.reader[index] = new StreamReader(server,getSession());
                }
            } else {
                for (int index = 0; index < NUM_THREADS; index++) {
                    this.reader[index] = new Reader(server,getSession());
                }
            }
        } catch (Exception ex) {
            if (ex instanceof ValidationException) {
                this.verify();
            }
        }
    }

    public void test() {
        try {
            for (int index = 0; index < NUM_THREADS; index++) {
                (this.reader[index]).start();
            }

            try {
                for (int index = 0; index < NUM_THREADS; index++) {
                    (this.reader[index]).join();
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
