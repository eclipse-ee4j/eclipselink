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
package org.eclipse.persistence.testing.tests.unitofwork;

import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.tests.queries.ConformResultsInUnitOfWorkTest;

import java.util.Enumeration;
import java.util.Vector;


public class NestedUnitOfWorkQuery extends ConformResultsInUnitOfWorkTest {

    public UnitOfWork nestedUnitOfWork;
    public Employee employee;

    @Override
    public void buildConformQuery() {
        conformedQuery = new ReadAllQuery();
        conformedQuery.setReferenceClass(Employee.class);
        conformedQuery.conformResultsInUnitOfWork();
    }

    @Override
    public void prepareTest() {
        employee = (Employee)getSession().readObject(Employee.class);
        unitOfWork.registerObject(employee);

        nestedUnitOfWork = unitOfWork.acquireUnitOfWork();


    }

    @Override
    public void test() {
        result = nestedUnitOfWork.executeQuery(conformedQuery);
        unitOfWork.release();
    }

    @Override
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
