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

import org.eclipse.persistence.sessions.DatabaseLogin;

/**
 * The following test realises the following concurrent scenario:
 * Client0 creates in a single transaction 5 Employees, than 5 Projects;
 * Client1 creates in a single transaction 5 Projects, than 5 Employees.
 *
 * this.server.serverSession.getSequencingControl().setShouldUseSeparateConnection(true)
 * line in setup insures that a separate sequencing connection pool is used - which
 * allows to avoid deadlock.
 * Note that if the line is changed to:
 * this.server.serverSession.getSequencingControl().setShouldUseSeparateConnection(false);
 * the deadlock will occur.
 * Some dbs (Oracle) use deadlock-avoidance strategies. To defy those, Client0 waits after creating
 * 5 Employees until Client1 will creates its 5 Projects - this would insure a deadlock in "false" case
 */
@SuppressWarnings("deprecation")
public class ClientServerSequenceDeadlockTest2 extends ClientServerSequenceDeadlockTest {
    protected FIFO[] fifoInArray;
    protected boolean[] firstHalfDone;

    /**
     *
     */
    public ClientServerSequenceDeadlockTest2() {
        super();
        fifoInArray = new FIFO[NUM_CLIENTS];
        firstHalfDone = new boolean[NUM_CLIENTS];
        numObjects = 10;
        setDescription("Tests concurrent writing of objects of two types for sequencing deadlock - one big transaction per thread");
    }

    /**
     *
     */
    public void setup() {
        this.login = (DatabaseLogin)getSession().getLogin().clone();
        this.server = new Server(this.login);
        this.server.serverSession.setSessionLog(getSession().getSessionLog());
        this.server.serverSession.getLogin().getDefaultSequence().setPreallocationSize(numObjects / 2);
        this.server.serverSession.getSequencingControl().setShouldUseSeparateConnection(true);
        this.server.login();
        this.server.copyDescriptors(getSession());
        for (int i = 0; i < NUM_CLIENTS; i++) {
            boolean createEmployeesFirst = (i % 2) == 0;
            fifoArray[i] = new FIFO(numObjects);
            fifoInArray[i] = new FIFO(numObjects);
            getClients().addElement(new EmployeeSeqDeadlockClient2(this.server, getSession(), "Client " + i, numObjects, createEmployeesFirst, fifoArray[i], fifoInArray[i]));
            clientStateArray[i] = THREAD_UNDEFINED;
            clientLastActionTimeArray[i] = 0;
            firstHalfDone[i] = false;
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

                        //	                System.out.println("Client# = " + i + " object# =  " + objectNumber);
                        if (objectNumber == (numObjects / 2)) {
                            firstHalfDone[i] = true;
                            boolean firstHalfDoneForAll = true;
                            for (int j = 0; j < NUM_CLIENTS; j++) {
                                firstHalfDoneForAll = firstHalfDoneForAll && firstHalfDone[j];
                            }
                            if (firstHalfDoneForAll) {
                                for (int j = 0; j < NUM_CLIENTS; j++) {
                                    fifoInArray[j].insertTail(new Boolean(true));
                                }
                            }
                        }
                        if ((objectNumber == numObjects) || (objectNumber == -1)) {
                            clientStateArray[i] = THREAD_EXITED;
                            doneClients++;
                            //    	                System.out.println("Client# = " + i + " EXITED");
                        }
                    } else {
                        if (clientLastActionTimeArray[i] == 0) {
                            clientLastActionTimeArray[i] = System.currentTimeMillis();
                        } else if ((currentTime - clientLastActionTimeArray[i]) >= TIME_TO_PRESUME_DEADLOCK) {
                            clientStateArray[i] = THREAD_LOCKED;
                            deadlock = true;
                            doneClients++;
                            ((Thread)getClients().elementAt(i)).stop();
                            //    	                System.out.println("Client# = " + i + " DEADLOCK");
                        }
                    }
                }
            }
        }
    }
}
