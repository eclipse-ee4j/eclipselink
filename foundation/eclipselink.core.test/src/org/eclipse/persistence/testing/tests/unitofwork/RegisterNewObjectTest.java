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
package org.eclipse.persistence.testing.tests.unitofwork;

import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.models.employee.domain.Address;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


/**
 * This test is in responce to a support question.  There was a problem with
 * The stability of the cache when an object was registered with the Unit Of
 * Work as a new object.
 */
public class RegisterNewObjectTest extends TestCase {
    public RegisterNewObjectTest() {
        setDescription("This test verifies the the UOW cache and the Parent Session's cache when registering a new object");
    }

    public void reset() {
        getAbstractSession().rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void setup() {
        getAbstractSession().beginTransaction();
    }

    public void test() {
        Session session = getSession();
        UnitOfWork uow = session.acquireUnitOfWork();
        Address add = (Address)session.readObject(Address.class);
        org.eclipse.persistence.testing.models.employee.domain.EmployeePopulator emps = 
            new org.eclipse.persistence.testing.models.employee.domain.EmployeePopulator();
        for (int i = 0; i < 10; ++i) {
            Employee emp = (Employee)uow.registerNewObject(emps.basicEmployeeExample1());
            emp.setAddress((Address)uow.registerObject(add));
            uow.commitAndResume();
        }
    }
}
