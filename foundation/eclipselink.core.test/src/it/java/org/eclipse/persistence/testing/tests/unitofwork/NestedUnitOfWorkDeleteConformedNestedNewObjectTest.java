/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
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

import java.math.BigDecimal;

import java.util.Collection;
import java.util.Vector;

import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.domain.EmployeePopulator;


public class NestedUnitOfWorkDeleteConformedNestedNewObjectTest extends AutoVerifyTestCase {
    public NestedUnitOfWorkDeleteConformedNestedNewObjectTest() {
        super();
        setDescription("Test designed to verify that a new object registered in a nested unit of work is unregistered when deleted in same unit of work.");
    }

    @Override
    public void setup() {
        getAbstractSession().beginTransaction();
    }

    @Override
    public void test() {

        UnitOfWork uow = getSession().acquireUnitOfWork();
        UnitOfWork nestedUow1 = uow.acquireUnitOfWork();

        Employee employee = (Employee)new EmployeePopulator().basicEmployeeExample1();
        employee.setId(new BigDecimal(15));
        nestedUow1.registerObject(employee);
        nestedUow1.commit();

        nestedUow1 = uow.acquireUnitOfWork();

        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(Employee.class);
        query.setSelectionCriteria(new org.eclipse.persistence.expressions.ExpressionBuilder().get("id").equal(new BigDecimal(15)));
        query.conformResultsInUnitOfWork();
        Vector results = (Vector)nestedUow1.executeQuery(query);
        if (results.size() > 1) {
            throw new TestErrorException("The read all query on primary key returned more than one object see bug # 3183379");
        }
        Employee nestedEmployee = (Employee)results.firstElement();

        nestedUow1.deleteObject(nestedEmployee);
        nestedUow1.commit();
        if (!((UnitOfWorkImpl)uow).getNewObjectsCloneToOriginal().isEmpty()) {
            throw new TestErrorException("Failed to unregister the Object in the nested unit of work");
        }

        if (!((UnitOfWorkImpl)uow).getPrimaryKeyToNewObjects().isEmpty()) {
            throw new TestErrorException("Failed to unregister the Object in the nested unit of work");
        }

    }

    @Override
    public void reset() {
        getAbstractSession().commitTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }
}
