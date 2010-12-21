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
package org.eclipse.persistence.testing.tests.optimization.queryandsqlcounting.querycache;

import java.util.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.tests.optimization.queryandsqlcounting.QuerySQLTracker;

/**
 * Base test for most Query Cache tests
 *
 * This test sets up by getting a query from the getQueryForTest() method, stores it on
 * either the session or the descriptor and executes it on the base session for this test
 * to populate the cache.
 *
 * In its test phase it executes it on the session provided in getSessionForQueryTest().
 *
 * It verifys itself by ensuring the correct number of SQL statements were executed and
 * the correct number of results were returned.
 *
 * Subclasses override various method in this class to achieve the test they are looking for.
 *
 * SQL Statements are counted between the end of the setup method and the beginning of the reset method.
 */
public class NamedQueryQueryCacheTest extends AutoVerifyTestCase {
    protected QuerySQLTracker tracker = null;
    protected Object results = null;
    protected int expectedSQLCount = 0;
    protected int expectedResults = 2;
    protected int queryLocation = QUERY_ON_SESSION;
    public static final int QUERY_ON_SESSION = 0;
    public static final int QUERY_ON_DESCRIPTOR = 1;
    public static final String CACHING_QUERY_NAME = "cachedResultsQuery";

    public NamedQueryQueryCacheTest() {
        setDescription("Test to ensure a query with caching enabled returns the cached results.");
    }

    public NamedQueryQueryCacheTest(int queryLocation) {
        this();
        this.queryLocation = queryLocation;
    }

    public ReadQuery getQueryForTest() {
        ReadAllQuery testQuery = new ReadAllQuery(Employee.class);
        ExpressionBuilder employees = new ExpressionBuilder();
        Expression exp = employees.get("firstName").like("B%");
        testQuery.setSelectionCriteria(exp);
        testQuery.setQueryResultsCachePolicy(new QueryResultsCachePolicy());
        return testQuery;
    }

    public Session getSessionForQueryTest() {
        return getSession();
    }

    public void setup() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();

        ReadQuery query = getQueryForTest();
        if (queryLocation == QUERY_ON_DESCRIPTOR) {
            getSession().getDescriptor(query.getReferenceClass()).getQueryManager().addQuery(CACHING_QUERY_NAME, query);
            getSession().executeQuery(CACHING_QUERY_NAME, getQueryForTest().getReferenceClass());
        } else {
            getSession().addQuery(CACHING_QUERY_NAME, query);
            getSession().executeQuery(CACHING_QUERY_NAME);
        }
        tracker = new QuerySQLTracker(getSession());
    }

    public void test() {
        if (queryLocation == QUERY_ON_DESCRIPTOR) {
            results = getSessionForQueryTest().executeQuery(CACHING_QUERY_NAME, getQueryForTest().getReferenceClass());
        } else {
            results = getSessionForQueryTest().executeQuery(CACHING_QUERY_NAME);
        }
    }

    public void verify() {
        if (tracker.getSqlStatements().size() != expectedSQLCount) {
            throw new TestErrorException("An incorrect number of SQL statements were generated: " + tracker.getSqlStatements().size() + ". This likely indicates a problem with the query cache.");
        }
        if (((Vector)results).size() != expectedResults) {
            throw new TestErrorException("The cached query did not yeild the correct number of results: " + ((Vector)results).size());
        }
    }

    public void reset() {
        tracker.remove();
        if (queryLocation == QUERY_ON_DESCRIPTOR) {
            getSession().getDescriptor(getQueryForTest().getReferenceClass()).getQueryManager().removeQuery(CACHING_QUERY_NAME);
        } else {
            getSession().removeQuery(CACHING_QUERY_NAME);
        }
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }
}
