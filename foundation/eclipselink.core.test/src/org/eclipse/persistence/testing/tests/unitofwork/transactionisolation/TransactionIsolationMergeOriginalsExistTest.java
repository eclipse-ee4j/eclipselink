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
package org.eclipse.persistence.testing.tests.unitofwork.transactionisolation;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


/**
 * Tests the Session read refactoring / reading through the write connection
 * properly feature.
 * <p>
 * This test verifies that originals of objects read by a UnitOfWork in early
 * transaction are not placed in the shared cache until commit/merge time.
 * <p>
 * Test checks case where original already exists in the shared cache.  In this
 * case should correctly register based on that one.
 *  @author  smcritch
 */
public class TransactionIsolationMergeOriginalsExistTest extends TransactionIsolationMergeTest {
    public void test() {
        unitOfWork.beginEarlyTransaction();

        // load original into shared cache first, to make sure that if already in
        // cache just return it.
        original = (Employee)getSession().readObject(Employee.class);
        originalFirstName = original.getFirstName();

        // want to avoid cache hit here, so make it go to database...
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression expression =
            builder.get("firstName").equal(original.getFirstName()).and(builder.get("lastName").equal(original.getLastName()));

        Employee employeeClone = (Employee)unitOfWork.readAllObjects(Employee.class, expression).elementAt(0);
        employeeClone.setFirstName("elle");

        unitOfWork.commit();

        unitOfWork = null;

        ReadObjectQuery cacheQuery = new ReadObjectQuery(Employee.class);
        cacheQuery.checkCacheOnly();
        Employee newOriginal = (Employee)getSession().executeQuery(cacheQuery);

        strongAssert(newOriginal != null, "There should now be an original in the shared cache.");
        strongAssert(newOriginal == original,
                     "The original should in the shared cache should still have its identity.");
        strongAssert(newOriginal.getFirstName().equals("elle"), "Changes were not merged into the shared cache");
    }
}
