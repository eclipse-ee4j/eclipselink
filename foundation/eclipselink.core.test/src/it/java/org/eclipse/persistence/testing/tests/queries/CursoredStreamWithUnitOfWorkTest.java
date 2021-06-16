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
package org.eclipse.persistence.testing.tests.queries;

import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.framework.*;

public class CursoredStreamWithUnitOfWorkTest extends TestCase {
    public String firstName;
    public String lastName;
    public java.math.BigDecimal employeeId;

    public java.math.BigDecimal getEmployeeId() {
        return employeeId;
    }

    private String getFirstName() {
        return firstName;
    }

    private String getLastName() {
        return lastName;
    }

    public void reset() {
        getAbstractSession().rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    public void setEmployeeId(java.math.BigDecimal newValue) {
        this.employeeId = newValue;
    }

    private void setFirstName(String newValue) {
        this.firstName = newValue;
    }

    private void setLastName(String newValue) {
        this.lastName = newValue;
    }

    public void setup() {
        getAbstractSession().beginTransaction();

    }

    private Employee swapNames(Employee employee) {
        // Trigger a change by swapping the first and last names...
        String firstName = employee.getFirstName();
        String lastName = employee.getLastName();
        employee.setFirstName(lastName);
        employee.setLastName(firstName);

        return employee;
    }

    public void test() {

        UnitOfWork uow = getSession().acquireUnitOfWork();

        ReadAllQuery query = new ReadAllQuery(Employee.class);

        query.useCursoredStream(2, 1);
        // Make sure the query goes to the database
        //    query.dontMaintainCache();
        //    query.dontCheckCache();

        CursoredStream stream = (CursoredStream)uow.executeQuery(query);

        Employee employee = (Employee)stream.nextElement();
        setFirstName(employee.getFirstName());
        setLastName(employee.getLastName());
        setEmployeeId(employee.getId());
        employee = swapNames(employee);

        stream.close();

        uow.commit();

    }

    public void verify() {

        ReadObjectQuery query = new ReadObjectQuery();
        query.setReferenceClass(Employee.class);
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression exp = builder.get("id").equal(getEmployeeId());
        query.setSelectionCriteria(exp);

        // Make sure the query goes to the database
        query.dontMaintainCache();
        query.dontCheckCache();

        Employee newEmployee = (Employee)getSession().executeQuery(query);
        if (newEmployee.getFirstName().equals(getFirstName())) {
            throw new TestErrorException("first Name was not changed");
        } else if (newEmployee.getLastName().equals(getLastName())) {
            throw new TestErrorException("last Name was not changed");
        }
    }
}
