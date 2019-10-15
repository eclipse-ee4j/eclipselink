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
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.relational.EmployeeProject;

public class PreBeginTransactionFailureTest extends AutoVerifyTestCase {
    protected org.eclipse.persistence.sessions.server.Server serverSession;
    protected UnitOfWork uow;
    static protected TestErrorException expectedException = new TestErrorException("Expected exception");
    protected Exception exception;

    class SessionListener extends SessionEventAdapter {
        public void preBeginTransaction(SessionEvent event) {
            throw expectedException;
        }
    }

    public void reset() {
        if (serverSession != null) {
            serverSession.logout();
            serverSession = null;
            exception = null;
        }
    }

    public void setup() {
        Project project = new EmployeeProject();
        project.setLogin((DatabaseLogin)getSession().getLogin().clone());
        serverSession = project.createServerSession(1, 1);
        serverSession.setSessionLog(getSession().getSessionLog());
        serverSession.login();

        PreBeginTransactionFailureTest.SessionListener listener = new PreBeginTransactionFailureTest.SessionListener();
        Session clientSession = serverSession.acquireClientSession();
        clientSession.getEventManager().addListener(listener);
        uow = clientSession.acquireUnitOfWork();
    }

    public void test() {
        Employee emp = new Employee();
        Employee empCopy = (Employee)uow.registerObject(emp);
        empCopy.setFirstName("A");
        try {
            uow.commit();
        } catch (Exception ex) {
            exception = ex;
        }
    }

    public void verify() {
        if (!expectedException.equals(exception)) {
            throw new TestErrorException("A wrong exception has been thrown.");
        }
    }
}
