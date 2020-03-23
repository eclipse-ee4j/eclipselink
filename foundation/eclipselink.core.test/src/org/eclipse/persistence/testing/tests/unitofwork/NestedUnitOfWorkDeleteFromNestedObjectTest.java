/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.unitofwork;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.domain.PhoneNumber;

import java.util.Vector;


public class NestedUnitOfWorkDeleteFromNestedObjectTest extends AutoVerifyTestCase {

    public NestedUnitOfWorkDeleteFromNestedObjectTest() {
        super();
        setDescription("Test designed to verify that a object in relation deleted/removed in a nested unit of work are deleted/removed in main unit of work after commit of nested UOW.");
    }

    public void setup() {
        getAbstractSession().beginTransaction();
    }

    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        UnitOfWork nestedUow1 = uow.acquireUnitOfWork();

        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(Employee.class);
        ExpressionBuilder expressionBuilder = new ExpressionBuilder();
        query.setSelectionCriteria(expressionBuilder.get("firstName").equal("Bob").and(expressionBuilder.get("lastName").equal("Smith")));
        query.conformResultsInUnitOfWork();
        Vector<Employee> results = (Vector<Employee>)uow.executeQuery(query);
        Employee employee = results.firstElement();
        Employee employeeNested = (Employee)nestedUow1.registerObject(employee);

        assertTrue(employeeNested.getPhoneNumbers().size() > 0);
        for (PhoneNumber item: new Vector<PhoneNumber>(employeeNested.getPhoneNumbers())) {
            if (item != null) {
                nestedUow1.deleteObject(item);
                employeeNested.removePhoneNumber(item);
            }
        }
        nestedUow1.deleteObject(employeeNested);
        nestedUow1.commitAndResume();
        if (employee.getPhoneNumbers().size() != 0) {
            throw new TestErrorException("Objects removal from the nested unit of work is not merged into outer/main unit of work. Number of remaining objects is: " +  employee.getPhoneNumbers().size());
        }
    }

    public void reset() {
        getAbstractSession().rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }
}