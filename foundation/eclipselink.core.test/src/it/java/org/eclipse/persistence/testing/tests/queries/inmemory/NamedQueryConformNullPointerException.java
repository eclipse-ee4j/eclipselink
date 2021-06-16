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
package org.eclipse.persistence.testing.tests.queries.inmemory;

import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.sessions.*;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

public class NamedQueryConformNullPointerException extends TestCase {

    public NamedQueryConformNullPointerException() {
        super();
    }

    public void reset() {
        //clear the cache.
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void test() {
        ExpressionBuilder emp = new ExpressionBuilder();
        Expression firstNameExpression = emp.get("firstName").equal(emp.getParameter("firstName"));
        Expression lastNameExpression = emp.get("lastName").equal(emp.getParameter("lastName"));

        // Create the appropriate query and add the arguments.
        ReadObjectQuery query = new ReadObjectQuery();
        query.setReferenceClass(Employee.class);
        query.setSelectionCriteria(firstNameExpression.and(lastNameExpression));
        query.addArgument("firstName");
        query.addArgument("lastName");
        query.conformResultsInUnitOfWork();

        getSession().removeQuery("getEmployeeWithName");
        // Add the query to the session.
        getSession().addQuery("getEmployeeWithName", query);

        UnitOfWork uow = getSession().acquireUnitOfWork();

        // The query can now be executed by referencing its name and providing a
        // first and last name argument.
        Employee employee = (Employee)uow.readObject(Employee.class, new ExpressionBuilder().get("firstName").equal("Bob"));

        Employee employeeClone = (Employee)uow.registerObject(employee);
        employeeClone.setFirstName("Bobby");
        Employee employee1 = (Employee)uow.executeQuery("getEmployeeWithName", "Bob", "Smith");
        if (employee1 != null) {
            throw new TestErrorException("employee is not null and should be");
        }
        employee1 = (Employee)uow.executeQuery("getEmployeeWithName", "Bobby", "Smith");
        if (employee1 == null) {
            throw new TestErrorException("employee is null and shouldn't be");
        }
    }
}
