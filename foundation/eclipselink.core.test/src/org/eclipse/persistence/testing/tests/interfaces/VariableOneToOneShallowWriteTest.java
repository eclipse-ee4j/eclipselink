/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.models.interfaces.*;

public class VariableOneToOneShallowWriteTest extends TransactionalTestCase {
    public Employee emp;

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

    public void verify() {
        if (!compareObjects(this.emp, getSession().readObject(this.emp))) {
            throw new TestErrorException("The employee was not written correctly to the database");
        }
    }
}
