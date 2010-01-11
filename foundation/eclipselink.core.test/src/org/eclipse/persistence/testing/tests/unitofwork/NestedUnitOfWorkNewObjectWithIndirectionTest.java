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

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.domain.EmployeePopulator;


public class NestedUnitOfWorkNewObjectWithIndirectionTest extends AutoVerifyTestCase {
    Employee original;

    public void setup() {
        getAbstractSession().beginTransaction();
        Employee emp = (Employee)new EmployeePopulator().basicEmployeeExample1();
        original = (Employee)new EmployeePopulator().basicEmployeeExample2();
        original.getManagedEmployees().add(emp);
        emp.setManager(original);
    }

    public void test() {

        UnitOfWork uow = getSession().acquireUnitOfWork();
        UnitOfWork nestedUow1 = uow.acquireUnitOfWork();
        Employee employeeClone = (Employee)uow.registerObject(original);
        Employee employeeNestedClone = (Employee)nestedUow1.registerObject(employeeClone);
        if (((Employee)employeeNestedClone.getManagedEmployees().firstElement()).getManager() != 
            employeeNestedClone) {
            throw new TestErrorException("Object Identity Lost when triggering indirection in nested UOW see BUG # 3228185");
        }

    }

    public void reset() {
        getAbstractSession().commitTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }
}
