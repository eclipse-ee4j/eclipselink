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

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.sessions.server.ClientSession;
import org.eclipse.persistence.testing.models.employee.domain.*;

public class EmployeeSeqDeadlockClient2 extends EmployeeSeqDeadlockClient {
    protected FIFO fifoIn;

    public EmployeeSeqDeadlockClient2(Server server, Session session, String name, int maximumLoops, boolean createEmployeesFirst, FIFO fifoOut, FIFO fifoIn) {
        super(server, session, name, maximumLoops, createEmployeesFirst, fifoOut);
        this.fifoIn = fifoIn;
    }

    public void run() {
        int i = 0;

        ((ClientSession)clientSession).beginTransaction();
        while (getKeepRunning()) {
            i++;
            if (i > getMaximumLoops()) {
                pleaseStop();
            } else {
                try {
                    if (shouldCreateEmployee(i)) {
                        ((ClientSession)clientSession).getSequencing().getNextValue(Employee.class);
                    } else {
                        ((ClientSession)clientSession).getSequencing().getNextValue(SmallProject.class);
                    }
                    if (fifoOut != null) {
                        fifoOut.insertTail(new Integer(i));
                    }

                    //			    System.out.println(getName() + " " + i);
                    if ((i == (getMaximumLoops() / 2)) && (fifoIn != null)) {
                        //    			    System.out.println(getName() + " " +  i + " Waiting");
                        while ((fifoIn.removeHead() == null) && getKeepRunning()) {
                            try {
                                sleep(100);
                            } catch (java.lang.InterruptedException e) {
                            }
                        }
                    }
                } catch (Exception e) {
                    pleaseStop();
                    setErrorOccurred(true);
                    setTestException(e);
                }
            }
        }
        if (!anErrorOccurred()) {
            try {
                ((ClientSession)clientSession).commitTransaction();
            } catch (Exception e) {
                pleaseStop();
                setErrorOccurred(true);
                setTestException(e);
            }
        } else {
            ((ClientSession)clientSession).rollbackTransaction();
        }

        // Cleanup before leaving...
        //	this.server.serverSession.releaseClientSession(this.clientSession);
        this.session = null;
        this.server = null;

        if (fifoOut != null) {
            fifoOut.insertTail(new Integer(-1));
        }
    }

    protected boolean shouldCreateEmployee(int i) {
        boolean firstHalf = i <= (getMaximumLoops() / 2);
        if (firstHalf) {
            return createEmployees;
        } else {
            return !createEmployees;
        }
    }
}
