/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.simultaneous;

import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import java.util.*;

/**
 * Ensure that a Query cache can be accessed by many threads at once
 */
public class QueryCacheMultithreadedTest extends MultithreadTestCase {
    public static final String CACHING_QUERY_NAME = "multithreadedCachedResultsQuery";

    public QueryCacheMultithreadedTest() {
        super();
        setDescription("Ensure query caching works with multithreading turned on.");
        Vector tests = new Vector();
        tests.add(new QueryCacheTest(1));
        tests.add(new QueryCacheTest(2));
        tests.add(new QueryCacheTest(3));
        tests.add(new QueryCacheTest(1));
        tests.add(new QueryCacheTest(2));
        tests.add(new QueryCacheTest(3));
        tests.add(new QueryCacheTest(1));
        tests.add(new QueryCacheTest(2));
        tests.add(new QueryCacheTest(3));
        tests.add(new QueryCacheTest(1));
        tests.add(new QueryCacheTest(2));
        tests.add(new QueryCacheTest(3));

        setTests(tests);
    }

    public void setup() {
        super.setup();
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        // Add the query all the tests will access
        ReadAllQuery testQuery = new ReadAllQuery(Employee.class);
        ExpressionBuilder employees = new ExpressionBuilder();
        Expression exp = employees.get("firstName").like(employees.getParameter("firstName"));
        testQuery.setSelectionCriteria(exp);
        testQuery.addArgument("firstName");
        testQuery.setQueryResultsCachePolicy(new QueryResultsCachePolicy());
        getSession().addQuery(CACHING_QUERY_NAME, testQuery);
    }

    public void reset() {
        super.reset();
        getSession().removeQuery(CACHING_QUERY_NAME);
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }
}
