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
package org.eclipse.persistence.testing.tests.interfaces;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.interfaces.*;

/**
 * This test is all about code coverage and forcing the code to execute specific
 * code within VariableOneToOneMapping.
 * However, it still 'tests' the deletion of an employee which has
 * a VariableOneToOneMapping.
 */
public class VariableOneToOneDeleteTest extends TransactionalTestCase {
    private Employee employee;

    public void setup() {
        super.setup();

        UnitOfWork uow = getSession().acquireUnitOfWork();
        Employee emp = (Employee)uow.registerObject(Employee.example1());
        emp.setName("Guy");
        uow.commit();
    }

    public void test() {
        employee = (Employee)getSession().readObject(Employee.class, new ExpressionBuilder().get("name").equal("Guy"));

        UnitOfWork uow = getSession().acquireUnitOfWork();
        Employee e = (Employee)uow.registerObject(employee);
        e.setName("Guy Pelletier");
        Phone p = Phone.example3();
        p.setNumber("6138236262");
        e.setContact(p);
        p.setEmp(e);
        uow.deleteObject(employee);
        uow.commit();
    }

    protected void verify() {
        if (!verifyDelete(employee)) {
            throw new TestErrorException("The object '" + employee + "' was not completely deleted from the database.");
        }

        if (getSession().readObject(Employee.class, new ExpressionBuilder().get("name").equal("Guy Pelletier")) != null) {
            throw new TestErrorException("The object '" + employee + "' was not completely deleted from the database.");
        }
    }
}
