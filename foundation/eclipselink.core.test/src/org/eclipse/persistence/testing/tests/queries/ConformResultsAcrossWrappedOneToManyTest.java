/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.queries;

import java.util.Vector;
import java.util.Enumeration;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

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

    public void verify() {
        int resultSize = ((Vector)result).size();
        if (resultSize != expectedResult.size()) {
            throw new TestErrorException("Result probably was not unwrapped while conforming.");
        }
    }
}
