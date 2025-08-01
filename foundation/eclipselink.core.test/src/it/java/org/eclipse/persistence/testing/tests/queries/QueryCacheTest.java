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
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

import java.util.Iterator;
import java.util.Vector;

/**
 * Ensure the results of a cached query are correct.
 * Note: This test does not ensure the correct number of SQL statement are generated.
 */
public class QueryCacheTest extends TestCase {
    protected ReadAllQuery query = null;
    protected Vector initialResults = null;
    protected Vector secondResults = null;

    public QueryCacheTest() {
        setDescription("Ensure the results of a cached query are correct.");
    }

    @Override
    public void setup() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        query = new ReadAllQuery(Employee.class);
        query.cacheQueryResults();
        ExpressionBuilder employees = new ExpressionBuilder();
        Expression exp = employees.get("firstName").like(employees.getParameter("name"));
        query.setSelectionCriteria(exp);
        query.addArgument("name");
    }

    @Override
    public void test() {
        Vector arguments = new Vector();
        arguments.add("J%");
        initialResults = (Vector)getSession().executeQuery(query, arguments);
        secondResults = (Vector)getSession().executeQuery(query, arguments);
    }

    @Override
    public void verify() {
        if ((initialResults.size() != 3) || (secondResults.size() != 3)) {
            throw new TestErrorException("The results sizes do not match.");
        }
        Iterator i1 = initialResults.iterator();
        Iterator i2 = secondResults.iterator();
        while (i1.hasNext()) {
            Employee emp = (Employee)i1.next();
            if (!emp.getFirstName().startsWith("J")) {
                throw new TestErrorException("An incorrect employee was returned.");
            }
            if (!emp.equals(i2.next())) {
                throw new TestErrorException("The cached query did not return the same result as the original query.");
            }
        }
    }

    @Override
    public void reset() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }
}
