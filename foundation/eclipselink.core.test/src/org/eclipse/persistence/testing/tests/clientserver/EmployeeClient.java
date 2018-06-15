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

import java.util.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.expressions.*;

public class EmployeeClient extends Thread {
    protected Server server;
    protected Session clientSession;
    protected Session session;
    public boolean keepRunning = true;
    public int maximumLoops = 0;
    public boolean errorOccurred = false;
    public Exception testException = null;
    public Vector sequenceNumbers = new Vector();

    public EmployeeClient() {
        super();
    }

    public EmployeeClient(Server server, Session session, String name, int maximumLoops) {
        super(name);
        this.server = server;
        this.session = session;
        setMaximumLoops(maximumLoops);
        this.clientSession = this.server.serverSession.acquireClientSession();
    }

    public boolean anErrorOccurred() {
        return getErrorOccurred();
    }

    private boolean getErrorOccurred() {
        return errorOccurred;
    }

    public boolean getKeepRunning() {
        return keepRunning;
    }

    protected int getMaximumLoops() {
        return maximumLoops;
    }

    public Vector getSequenceNumbers() {
        return sequenceNumbers;
    }

    public Exception getTestException() {
        return testException;
    }

    public void pleaseStop() {
        setKeepRunning(false);
    }

    public void run() {
        Employee newEmployee;
        SmallProject newProject;
        ExpressionBuilder exb = new ExpressionBuilder();
        int i = 0;

        while (getKeepRunning()) {
            try {
                UnitOfWork uow = this.clientSession.acquireUnitOfWork();
                Address address = new Address();
                uow.assignSequenceNumber(address);
                getSequenceNumbers().addElement(address.getId());
                i++;
                if (i > getMaximumLoops()) {
                    pleaseStop();
                }
                uow.commit();

            } catch (Exception e) {
                pleaseStop();
                setErrorOccurred(true);
                setTestException(e);
            }
        }

        // Cleanup before leaving...
        //    this.server.serverSession.releaseClientSession(this.clientSession);
        this.session = null;
        this.server = null;
    }

    protected void setErrorOccurred(boolean newValue) {
        this.errorOccurred = newValue;
    }

    public void setKeepRunning(boolean newValue) {
        this.keepRunning = newValue;
    }

    private void setMaximumLoops(int newValue) {
        this.maximumLoops = newValue;
    }

    protected void setTestException(Exception newValue) {
        this.testException = newValue;
    }

    /**
     * Returns a String that represents the value of this object.
     * @return a string representation of the receiver
     */
    public String toString() {
        // Insert code to print the receiver here.
        // This implementation forwards the message to super. You may replace or supplement this.
        return super.toString();
    }
}
