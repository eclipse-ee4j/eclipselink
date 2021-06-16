/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
