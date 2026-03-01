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
package org.eclipse.persistence.testing.tests.interfaces;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TransactionalTestCase;
import org.eclipse.persistence.testing.models.interfaces.Employee;
import org.eclipse.persistence.testing.models.interfaces.Phone;

public class VariableOneToOneShallowWriteTest extends TransactionalTestCase {
    public Employee emp;

    @Override
    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        this.emp = Employee.example1();
        Employee e = (Employee)uow.registerObject(this.emp);
        e.setName("Fred Walker");
        Phone p =  (Phone)uow.readObject(Phone.class);
        e.setContact(p);
        p.employee = e;
        uow.commit();
    }

    @Override
    public void verify() {
        if (!compareObjects(this.emp, getSession().readObject(this.emp))) {
            throw new TestErrorException("The employee was not written correctly to the database");
        }
    }
}
