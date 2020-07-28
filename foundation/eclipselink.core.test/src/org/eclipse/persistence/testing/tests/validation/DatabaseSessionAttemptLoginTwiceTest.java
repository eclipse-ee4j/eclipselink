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
package org.eclipse.persistence.testing.tests.validation;

import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.models.employee.relational.EmployeeSystem;


public class DatabaseSessionAttemptLoginTwiceTest extends ExceptionTest {

    public DatabaseSessionAttemptLoginTwiceTest() {
        super();
        setDescription("This test tests if the approprite exception is thrown on attempt to login more than once.");
    }

    public void setup() {
        this.expectedException = org.eclipse.persistence.exceptions.ValidationException.alreadyLoggedIn("");
    }

    public void test() {
        EmployeeSystem employeeSystem = new EmployeeSystem();
        DatabaseLogin databaseLogin = (DatabaseLogin)getSession().getLogin().clone();
        employeeSystem.project.setLogin(databaseLogin);
        DatabaseSession newSession = employeeSystem.project.createDatabaseSession();
        newSession.login();
        try {
            newSession.login();
        } catch (org.eclipse.persistence.exceptions.EclipseLinkException e) {
            this.caughtException = e;
        } finally {
            newSession.logout();
        }
    }
}
