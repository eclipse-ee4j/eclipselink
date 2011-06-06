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
package org.eclipse.persistence.testing.tests.unitofwork;

import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TransactionalTestCase;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


public class GetFromNewObjectWithConformTest extends TransactionalTestCase {
    Employee emp;

    /**
     * Added for code coverage, this calss verifies that TopLink will find a new Object when conformed
     * on a read object with no selection criteria
     */
    public GetFromNewObjectWithConformTest() {
        setDescription("Verifies that TopLink will find a new Object when conformed");
    }

    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        emp = new Employee();
        emp.setFirstName("SomeName");
        emp.setLastName("Some Last Name");
        uow.registerObject(emp);
        ReadObjectQuery query = new ReadObjectQuery(Employee.class);
        query.conformResultsInUnitOfWork();
        uow.getIdentityMapAccessor().initializeAllIdentityMaps();
        emp = (Employee)uow.executeQuery(query);
        uow.release();
    }
    // end of test()

    public void verify() {
        if (emp == null) {
            throw new TestErrorException("New Object Not Found");
        }
    }
    // end of verify()
}
