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
package org.eclipse.persistence.testing.tests.performance.writing;

import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.tests.performance.PerformanceTest;
import org.eclipse.persistence.sessions.server.*;
import org.eclipse.persistence.testing.models.performance.toplink.*;

/**
 * This tests the performance of unit of work with no changes.
 * It uses a client session to also test client session creation.
 * Its purpose is to compare the test result with previous release/label results.
 * It also provides a useful test for profiling performance.
 */
public class UnitOfWorkNoChangesClientSessionEmployeeTest extends PerformanceTest {
    protected Server server;
    protected Employee employee;

    public UnitOfWorkNoChangesClientSessionEmployeeTest() {
        setDescription("This tests the performance of unit of work with no changes.");
    }

    /**
     * Find any employee.
     */
    public void setup() {
        server = getSession().getProject().createServerSession();
        server.login();
        Expression expression = new ExpressionBuilder().get("firstName").equal("Bob");
        employee = (Employee)getSession().readObject(Employee.class, expression);
    }

    /**
     * Read employee and clear the cache, test database read.
     */
    public void test() throws Exception {
        Session client = server.acquireClientSession();
        UnitOfWork uow = client.acquireUnitOfWork();
        Employee employee = (Employee)uow.readObject(this.employee);
        uow.commit();
        client.release();
    }

    public void reset() {
        server.logout();
    }
}
