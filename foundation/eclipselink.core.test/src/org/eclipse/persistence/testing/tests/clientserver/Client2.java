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

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

public class Client2 extends Thread {
    protected Server server;
    protected Session clientSession;
    protected Session session;
    public Throwable exception;

    public Client2(Server server, Session session, String name) {
        super(name);
        this.server = server;
        this.session = session;
        this.clientSession = this.server.serverSession.acquireClientSession();
    }

    public void release() {
        this.clientSession.release();
    }

    public void run() {
        try {
            Employee readEmployee;
            Employee newManagedEmployee;
            SmallProject newProject;
            UnitOfWork uow;
            ExpressionBuilder exb = new ExpressionBuilder();
            for (int i = 0; i < 5; i++) {
                uow = this.clientSession.acquireUnitOfWork();
                readEmployee = (Employee)uow.readObject(Employee.class, exb.get("lastName").equal("MacIvor"));
                newManagedEmployee = new org.eclipse.persistence.testing.models.employee.domain.Employee();
                newManagedEmployee.setFirstName(this.getName() + " Employee" + i);
                readEmployee.addManagedEmployee(newManagedEmployee);
                newProject = new org.eclipse.persistence.testing.models.employee.domain.SmallProject();
                newProject.setName(this.getName() + " Project" + i);
                readEmployee.addProject(newProject);
                uow.commit();
            }
        } catch (Throwable t) {
            t.printStackTrace();
            exception = t;
        }
    }
}
