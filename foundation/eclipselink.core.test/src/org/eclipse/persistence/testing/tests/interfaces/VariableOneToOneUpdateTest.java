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

public class VariableOneToOneUpdateTest extends TransactionalTestCase {

    public void setup() {
        super.setup();

        UnitOfWork uow = getSession().acquireUnitOfWork();
        Employee emp = (Employee)uow.registerObject(Employee.example1());
        emp.setName("Guy");
        uow.commit();
    }

    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Employee e = 
            (Employee)uow.registerObject(uow.readObject(Employee.class, new ExpressionBuilder().get("name").equal("Guy")));
        e.setName("Guy Pelletier");
        Phone p = Phone.example3();
        p.setNumber("6138236262");
        e.setContact(p);
        p.setEmp(e);
        uow.commit();
    }

    public void verify() {
        Employee empFromDb = 
            (Employee)getSession().readObject(Employee.class, new ExpressionBuilder().get("name").equal("Guy Pelletier"));

        if (empFromDb == null) {
            throw new TestErrorException("The employee NAME was not updated correctly in the database");
        }

        if (!((Phone)empFromDb.getContact()).getNumber().equals("6138236262")) {
            throw new TestErrorException("The employee PHONE was not updated correctly in the database");
        }
    }
}
