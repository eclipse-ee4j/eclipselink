/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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

import java.util.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.DatabaseLogin;

/**
 * Tests that a deadlock in sequencing does not occur.
 */
@SuppressWarnings("deprecation")
public class ClientServerSequenceDeadlockTest extends ClientServerConcurrentWriteTest {
    protected FIFO[] fifoArray;
    protected int[] clientStateArray;
    protected long[] clientLastActionTimeArray;
    protected int numObjects = 25;
    protected int doneClients;
    protected boolean deadlock;
    protected static final int THREAD_UNDEFINED = 0;
    protected static final int THREAD_RUNNING = 0;
    protected static final int THREAD_LOCKED = 1;
    protected static final int THREAD_EXITED = 2;
    protected static final long TIME_TO_PRESUME_DEADLOCK = 300000;

    /**
     *
     */
    public ClientServerSequenceDeadlockTest() {
        super();
        numObjects = 25;
        setDescription("Tests concurrent writing of objects of two types for sequencing deadlock");
        fifoArray = new FIFO[NUM_CLIENTS];
        clientStateArray = new int[NUM_CLIENTS];
        clientLastActionTimeArray = new long[NUM_CLIENTS];
    }

    /**
     *
     */
    public void reset() {
        for (int i = 0; i < getClients().size(); i++) {
            EmployeeSeqDeadlockClient client = (EmployeeSeqDeadlockClient)getClients().elementAt(i);
            if (clientStateArray[i] == THREAD_RUNNING) {
                client.stop();
            }
        }
        this.server.logout();
        setClients(new Vector());
    }

    /**
     *
     */
    public void setup() {
        this.login = (DatabaseLogin)getSession().getLogin().clone();
        // Workaround for DB2 bug (or feature?) causing test deadlock, see bug 3550940.
        if (this.login.getPlatform().isDB2()) {
            this.login.setTransactionIsolation(DatabaseLogin.TRANSACTION_SERIALIZABLE);
        }
        this.server = new Server(this.login);
        this.server.serverSession.getLogin().getDefaultSequence().setPreallocationSize(1);
        this.server.serverSession.setSessionLog(getSession().getSessionLog());
        this.server.login();
        this.server.copyDescriptors(getSession());
        for (int i = 0; i < NUM_CLIENTS; i++) {
            boolean createEmployees = (i % 2) == 0;
            fifoArray[i] = new FIFO(numObjects);
            getClients().addElement(new EmployeeSeqDeadlockClient(this.server, getSession(), "Client " + i, numObjects, createEmployees, fifoArray[i]));
            clientStateArray[i] = THREAD_UNDEFINED;
            clientLastActionTimeArray[i] = 0;
        }
        doneClients = 0;
        deadlock = false;
    }

    /**
     *
     */
    public void test() {
        for (int i = 0; i < NUM_CLIENTS; i++) {
            EmployeeSeqDeadlockClient client = (EmployeeSeqDeadlockClient)clients.elementAt(i);
            client.start();
            clientStateArray[i] = THREAD_RUNNING;
        }

        while (doneClients < NUM_CLIENTS) {
            for (int i = 0; i < NUM_CLIENTS; i++) {
                if (clientStateArray[i] == THREAD_RUNNING) {
                    if ((NUM_CLIENTS - doneClients) == 1) {
                        ((EmployeeClient)getClients().elementAt(i)).pleaseStop();
                    }
                    long currentTime = System.currentTimeMillis();
                    if (!fifoArray[i].isEmpty()) {
                        clientLastActionTimeArray[i] = currentTime;
                        int objectNumber = ((Integer)fifoArray[i].removeHead()).intValue();

                        //                    System.out.println("Client# = " + i + " object# =  " + objectNumber);
                        if ((objectNumber == numObjects) || (objectNumber == -1)) {
                            clientStateArray[i] = THREAD_EXITED;
                            doneClients++;
                            //                        System.out.println("Client# = " + i + " EXITED");
                        }
                    } else {
                        if (clientLastActionTimeArray[i] == 0) {
                            clientLastActionTimeArray[i] = System.currentTimeMillis();
                        } else if ((currentTime - clientLastActionTimeArray[i]) >= TIME_TO_PRESUME_DEADLOCK) {
                            clientStateArray[i] = THREAD_LOCKED;
                            deadlock = true;
                            doneClients++;
                            ((Thread)getClients().elementAt(i)).stop();
                            //                        System.out.println("Client# = " + i + " DEADLOCK");
                        }
                    }
                }
            }
        }
    }

    /**
     *
     */
    public void verify() {
        for (int i = 0; i < NUM_CLIENTS; i++) {
            EmployeeSeqDeadlockClient client = (EmployeeSeqDeadlockClient)clients.elementAt(i);
            if (client.anErrorOccurred()) {
                throw new TestErrorException("An exception " + client.getTestException() + " occurred in client " + client.toString());
            }
        }
        if (deadlock) {
            throw new TestErrorException("Looks like it's a deadlock");
        }
    }
}
