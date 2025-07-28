/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.queries;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.CursoredStream;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

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

    @Override
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

    @Override
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

    @Override
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

    @Override
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
