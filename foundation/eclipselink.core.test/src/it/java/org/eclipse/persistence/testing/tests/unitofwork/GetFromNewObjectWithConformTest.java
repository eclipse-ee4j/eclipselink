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
