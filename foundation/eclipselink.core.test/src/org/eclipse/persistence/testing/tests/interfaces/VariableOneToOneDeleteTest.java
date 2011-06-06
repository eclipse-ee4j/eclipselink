/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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
