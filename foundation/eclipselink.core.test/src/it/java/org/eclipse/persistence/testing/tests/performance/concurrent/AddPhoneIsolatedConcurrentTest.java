/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.performance.concurrent;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.models.performance.toplink.Employee;
import org.eclipse.persistence.testing.models.performance.toplink.PhoneNumber;

/**
 * This test compares the concurrency of updates.
 * This test must be run on a multi-CPU machine to be meaningful.
 */
public class AddPhoneIsolatedConcurrentTest extends IsolatedConcurrentTest {
    protected Employee employee;
    protected int index;

    public AddPhoneIsolatedConcurrentTest() {
        setDescription("This tests the concurrency of updates.");
    }

    /**
     * Return the next index to use as the phone type id.
     */
    public synchronized int incrementIndex() {
        return index++;
    }

    /**
     * Find any employee.
     */
    @Override
    public void setup() {
        super.setup();
        Expression expression = new ExpressionBuilder().get("firstName").equal("Bob");
        employee = (Employee)getServerSession().acquireClientSession().readObject(Employee.class, expression);
        index = 0;
    }

    /**
     * Add/remove a phone to/from the employee.
     */
    @Override
    public void runTask() throws Exception {
        Session client = getServerSession().acquireClientSession();
        UnitOfWork uow = client.acquireUnitOfWork();
        Employee employee = (Employee)uow.readObject(this.employee);
        PhoneNumber phone = new PhoneNumber();
        int currentIndex = incrementIndex();
        phone.setType("new" + currentIndex);
        employee.addPhoneNumber(phone);
        uow.commit();
        uow = client.acquireUnitOfWork();
        employee = (Employee)uow.readObject(employee);
        phone = (PhoneNumber)uow.readObject(phone);
        employee.removePhoneNumber(phone);
        uow.commit();
        client.release();
    }
}
