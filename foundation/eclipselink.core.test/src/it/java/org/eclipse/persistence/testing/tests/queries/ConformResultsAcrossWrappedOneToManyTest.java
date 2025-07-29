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
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

import java.util.Enumeration;
import java.util.Vector;

/**
 * This test added for bug 2766379: Expression.doesConform not unwrapping as
 * it traverses object relationships.
 * <p><b>Test description:</b>
 * <ul>
 * <li>A wrapping policy is used on Employee, and conforming is enabled.
 * <li>A query with expression emp.anyOf("managedEmployees").get("firstName").equal("Marcus") is called.
 * <li>The registered result then has its valueholders triggered, so that conform
 * result on the second query can work.
 * <li>The query is then repeated.  When the anyOf is traversed in doesConform,
 * the resulting collection should be correctly unwrapped, allowing the
 * second doesConform and the test to suceed.
 * </ul>
 * @author Stephen McRitchie
 */
public class ConformResultsAcrossWrappedOneToManyTest extends ConformResultsInUnitOfWorkTest {
    public Vector expectedResult;

    public ConformResultsAcrossWrappedOneToManyTest() {
        setShouldUseWrapperPolicy(true);
    }

    @Override
    public void buildConformQuery() {
        if (conformedQuery != null) {
            return;
        }
        conformedQuery = new ReadAllQuery(Employee.class);
        ExpressionBuilder emp = new ExpressionBuilder();
        Expression expression = emp.anyOf("managedEmployees").get("firstName").equal("Marcus");
        conformedQuery.setSelectionCriteria(expression);
        conformedQuery.conformResultsInUnitOfWork();
    }

    @Override
    public void prepareTest() {
        // The key to this test is already having a triggered valueholder in the
        // unit of work cache.
        buildConformQuery();
        ReadAllQuery conformedQuery = (ReadAllQuery)this.conformedQuery;
        Vector employees = (Vector)unitOfWork.executeQuery(conformedQuery);

        // Now unwrap the result to access clone.
        employees = conformedQuery.getContainerPolicy().vectorFor(employees, (UnitOfWorkImpl)unitOfWork);
        for (Enumeration enumtr = employees.elements(); enumtr.hasMoreElements();) {
            Employee employee = (Employee)enumtr.nextElement();

            // Now unwrap the value holders and trigger the indirection.
            conformedQuery.getContainerPolicy().vectorFor(employee.getManagedEmployees(), (UnitOfWorkImpl)unitOfWork);
        }
        expectedResult = employees;
    }

    @Override
    public void verify() {
        int resultSize = ((Vector)result).size();
        if (resultSize != expectedResult.size()) {
            throw new TestErrorException("Result probably was not unwrapped while conforming.");
        }
    }
}
