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

import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.tests.optimization.queryandsqlcounting.*;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;

/**
 * Bug 6138532 - QUERY RESULTS CACHE SHOULD NOT STORE NULL FOR MULTIPLE RESULTS
 * Bug 6135563 - DATAREADQUERY QUERY RESULTS CACHE DOES NOT CACHE QUERY RESULTS
 * Responsibilities:
 * - Test that database queries do not go to the database, even if a "no results" query is cached
 * - Test that "no results" queries are cached for single and multiple value results
 * - Ensure that query results are cached, and no additional database hits are performed
 */
public class QueryCacheHitTest extends TestCase {

    private String testType;
    protected QuerySQLTracker tracker = null;
    
    public static final String NO_RESULTS_CACHED_READALL = "testNoResultsCachedReadAll";
    public static final String VALID_RESULTS_CACHED_READALL = "testValidResultsCachedReadAll";
    public static final String NO_RESULTS_CACHED_READOBJECT = "testNoResultsCachedReadObject";
    public static final String VALID_RESULTS_CACHED_READOBJECT = "testValidResultsCachedReadObject";
    public static final String NO_RESULTS_CACHED_DATAREAD = "testNoResultsCachedDataRead";
    public static final String VALID_RESULTS_CACHED_DATAREAD = "testValidResultsCachedDataRead";

    public QueryCacheHitTest(String testType) {
        super();
        this.testType = testType;
        setDescription("Test to ensure that the database is not hit for queries that are cached, including cached zero results.");
    }
    
    public void setup() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        tracker = new QuerySQLTracker(getSession());        
    }
    
    public void test() {
        if (testType == NO_RESULTS_CACHED_DATAREAD) {
            testNoResultsCachedDataRead();
        } else if (testType == NO_RESULTS_CACHED_READALL) {
            testNoResultsCachedReadAll();            
        } else if (testType == VALID_RESULTS_CACHED_READALL) {
            testValidResultsCachedReadAll();
        } else if (testType == NO_RESULTS_CACHED_READOBJECT) {
            testNoResultsCachedReadObject();
        } else if (testType == VALID_RESULTS_CACHED_READOBJECT) {
            testValidResultsCachedReadObject();
        } else if (testType == NO_RESULTS_CACHED_DATAREAD) {
            testNoResultsCachedDataRead();
        } else if (testType == VALID_RESULTS_CACHED_DATAREAD) {
            testValidResultsCachedDataRead();
        } else {
            throw new TestErrorException("Invalid test type (" + testType + ") passed");
        }
    }
    
    public void testNoResultsCachedReadAll() {
        ReadAllQuery query = new ReadAllQuery(Employee.class);
        ExpressionBuilder builder = query.getExpressionBuilder();
        Expression exp = builder.get("firstName").like(builder.getParameter("fName"));
        query.setSelectionCriteria(exp);
        query.addArgument("fName");
        query.setQueryResultsCachePolicy(new QueryResultsCachePolicy(2));
    
        // Test ReadAllQuery with zero expected results ("no results" cached)
        // Do not go to the database if "no results" is cached
        Vector params = new Vector(1);
        params.add("impossiblefirstname");

        for (int i = 0; i < 3; i++) {
            Vector results = (Vector) getSession().executeQuery(query, params);
            // assert that we do not get null back from the cache for "no results"
            assertNotNull(results);
            if (results != null) {
                // assert that we do not have any DB results (from cache)
                assertTrue(results.isEmpty());
            }
            // assert that the database was only hit once (important)
            assertEquals(tracker.getSqlStatements().size(), 1);
        }
    }
    
    public void testValidResultsCachedReadAll() {
        ReadAllQuery query = new ReadAllQuery(Employee.class);
        ExpressionBuilder builder = query.getExpressionBuilder();
        Expression exp = builder.get("firstName").like(builder.getParameter("fName"));
        query.setSelectionCriteria(exp);
        query.addArgument("fName");
        query.setQueryResultsCachePolicy(new QueryResultsCachePolicy(2));
    
        Vector params = new Vector(1);
        params.add("B%");
 
        for (int i = 0; i < 3; i++) {
            Vector results = (Vector) getSession().executeQuery(query, params);
            // assert that we do not get null back from the cache
            assertNotNull(results);
            if (results != null) {
                // assert that we get back DB results (from cache)
                assertTrue(!results.isEmpty());
            }
            // assert that the database was only hit once (important)
            assertEquals(tracker.getSqlStatements().size(), 1);
        }
    }
    
    public void testNoResultsCachedReadObject() {
        ReadObjectQuery query = new ReadObjectQuery(Employee.class);
        ExpressionBuilder builder2 = query.getExpressionBuilder();
        Expression exp2 = builder2.get("lastName").like(builder2.getParameter("lName"));
        query.setSelectionCriteria(exp2);
        query.addArgument("lName");
        query.setQueryResultsCachePolicy(new QueryResultsCachePolicy(2));
        
        // Do not go to the database if "no results" is cached
        Vector params = new Vector(1);
        params.add("impossiblelastname");

        for (int i = 0; i < 3; i++) {
            Object result = getSession().executeQuery(query, params);
            assertNull(result);
            // assert that the database was only hit once (important)
            assertEquals(tracker.getSqlStatements().size(), 1);
        }
    }
    
    public void testValidResultsCachedReadObject() {
        ReadObjectQuery query = new ReadObjectQuery(Employee.class);
        ExpressionBuilder builder2 = query.getExpressionBuilder();
        Expression exp2 = builder2.get("lastName").like(builder2.getParameter("lName"));
        query.setSelectionCriteria(exp2);
        query.addArgument("lName");
        query.setQueryResultsCachePolicy(new QueryResultsCachePolicy(2));
        
        Vector params = new Vector(1);
        params.add("Smith");
        
        for (int i = 0; i < 3; i++) {
            Object result = getSession().executeQuery(query, params);
            // assert that we do not get null back from the cache
            assertNotNull(result);
            // assert that the database was only hit once (important)
            assertEquals(tracker.getSqlStatements().size(), 1);
        }
    }
    
    public void testNoResultsCachedDataRead() {
        DataReadQuery dataReadQuery1 = new DataReadQuery();
        String sqlString1 = "SELECT F_NAME, L_NAME FROM EMPLOYEE WHERE F_NAME = 'impossible'";
        dataReadQuery1.setSQLString(sqlString1);
        dataReadQuery1.setQueryResultsCachePolicy(new QueryResultsCachePolicy(2));
        // Test DataReadQuery with zero expected results ("no results" cached)
        // Do not go to the database if "no results" is cached

        for (int i = 0; i < 3; i++) {
            Vector results = (Vector) getSession().executeQuery(dataReadQuery1);
            // assert that we do not get null back from the cache for "no results"
            assertNotNull(results);
            if (results != null) {
                // assert that we do not have any DB results (from cache)
                assertTrue(results.isEmpty());
            }
            // assert that the database was only hit once (important)
            assertEquals(tracker.getSqlStatements().size(), 1);
        }
    }
    
    public void testValidResultsCachedDataRead() {
        DataReadQuery dataReadQuery2 = new DataReadQuery();
        String sqlString2 = "SELECT F_NAME, L_NAME FROM EMPLOYEE";
        dataReadQuery2.setSQLString(sqlString2);
        dataReadQuery2.setQueryResultsCachePolicy(new QueryResultsCachePolicy(2));
        
        for (int i = 0; i < 3; i++) {
            Vector results = (Vector) getSession().executeQuery(dataReadQuery2);
            // assert that we do not get null back from the cache
            assertNotNull(results);
            if (results != null) {
                // assert that we get back DB results (from cache)
                assertTrue(!results.isEmpty());
            }
            // assert that the database was only hit once (important)
            assertEquals(tracker.getSqlStatements().size(), 1);
        }
    }
    
    public void reset() {
        tracker.remove();
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

}
