/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.unitofwork;

import java.util.Enumeration;
import java.util.Vector;

import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.tests.queries.ConformResultsInUnitOfWorkTest;


public class NestedUnitOfWorkQuery extends ConformResultsInUnitOfWorkTest {

    public UnitOfWork nestedUnitOfWork;
    public Employee employee;

    public void buildConformQuery() {
        conformedQuery = new ReadAllQuery();
        conformedQuery.setReferenceClass(Employee.class);
        conformedQuery.conformResultsInUnitOfWork();
    }

    public void prepareTest() {
        employee = (Employee)getSession().readObject(Employee.class);
        unitOfWork.registerObject(employee);

        nestedUnitOfWork = unitOfWork.acquireUnitOfWork();


    }

    public void test() {
        result = nestedUnitOfWork.executeQuery(conformedQuery);
        unitOfWork.release();
    }

    public void verify() {
        boolean error = true;
        for (Enumeration enumtr = ((Vector)result).elements(); enumtr.hasMoreElements(); ) {
            if (((Employee)enumtr.nextElement()).getId() == employee.getId()) {
                error = false;
            }
        }
        if (error) {
            throw new TestErrorException("The object deleted from unit of work shows up in ReadAllQuery using conformResultsInUnitOfWork().");
        }
    }
}
