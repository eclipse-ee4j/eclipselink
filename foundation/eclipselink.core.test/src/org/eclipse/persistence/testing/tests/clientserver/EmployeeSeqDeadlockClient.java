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

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

public class EmployeeSeqDeadlockClient extends EmployeeClient {
    protected boolean createEmployees;
    protected FIFO fifoOut;

    public EmployeeSeqDeadlockClient(Server server, Session session, String name, int maximumLoops, boolean createEmployees, FIFO fifoOut) {
        super(server, session, name, maximumLoops);
        this.createEmployees = createEmployees;
        this.fifoOut = fifoOut;
    }

    public void run() {
        int i = 0;

        while (getKeepRunning()) {
            try {
                i++;
                if (i > getMaximumLoops()) {
                    pleaseStop();
                } else {
                    UnitOfWork uow = this.clientSession.acquireUnitOfWork();

                    if (createEmployees) {
                        uow.registerNewObject(new Employee());
                    } else {
                        uow.registerNewObject(new SmallProject());
                    }

                    uow.commit();

                    if (fifoOut != null) {
                        fifoOut.insertTail(new Integer(i));
                    }
                }
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

        if (fifoOut != null) {
            fifoOut.insertTail(new Integer(-1));
        }
    }
}
