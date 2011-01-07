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

import java.util.*;

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

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

    public void setup() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        query = new ReadAllQuery(Employee.class);
        query.cacheQueryResults();
        ExpressionBuilder employees = new ExpressionBuilder();
        Expression exp = employees.get("firstName").like(employees.getParameter("name"));
        query.setSelectionCriteria(exp);
        query.addArgument("name");
    }

    public void test() {
        Vector arguments = new Vector();
        arguments.add("J%");
        initialResults = (Vector)getSession().executeQuery(query, arguments);
        secondResults = (Vector)getSession().executeQuery(query, arguments);
    }

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

    public void reset() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }
}
