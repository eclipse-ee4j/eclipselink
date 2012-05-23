/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
import org.eclipse.persistence.testing.tests.optimization.queryandsqlcounting.QuerySQLTracker;

/**
 * Ensure running cached queries with different parameterized results will cache both results.
 */
public class QueryCacheParameterizedResultsTest extends AutoVerifyTestCase {
    protected QuerySQLTracker tracker = null;
    protected ReadAllQuery query = null;
    protected Vector args1 = null;
    protected Vector args2 = null;
    protected Vector results1 = null;
    protected Vector results2 = null;
    protected int expectedQuery1SQLStatementCount = 0;
    protected int expectedQuery2SQLStatementCount = 0;
    protected int query1SQLStatementCount = 0;
    protected int query2SQLStatementCount = 0;

    public QueryCacheParameterizedResultsTest() {
        setDescription("Ensure a query with multiple cached results based on parameters works.");
        args1 = new Vector();
        args1.addElement("Bob");
        args2 = new Vector();
        args2.addElement("Jill");
    }

    public ReadAllQuery getReadAllQuery() {
        ReadAllQuery testQuery = new ReadAllQuery(Employee.class);
        ExpressionBuilder employees = new ExpressionBuilder();
        Expression exp = employees.get("firstName").equal(employees.getParameter("name"));
        testQuery.setSelectionCriteria(exp);
        testQuery.addArgument("name");
        testQuery.cacheQueryResults();
        return testQuery;
    }

    public void setup() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        query = getReadAllQuery();
        getSession().executeQuery(query, args1);
        getSession().executeQuery(query, args2);
        tracker = new QuerySQLTracker(getSession());
    }

    public void test() {
        results2 = (Vector)getSession().executeQuery(query, args2);
        query2SQLStatementCount = tracker.getSqlStatements().size();
        results1 = (Vector)getSession().executeQuery(query, args1);
        query1SQLStatementCount = tracker.getSqlStatements().size();
    }

    public void verify() {
        if (query1SQLStatementCount != expectedQuery1SQLStatementCount) {
            throw new TestErrorException("An incorrect number of sql statements were executed in the first query " + ", the query cache was not used sucessfully: " + tracker.getSqlStatements().size());
        }
        if (query2SQLStatementCount != expectedQuery2SQLStatementCount) {
            throw new TestErrorException("An incorrect number of sql statements were executed in the second query " + ", the query cache was not used sucessfully: " + tracker.getSqlStatements().size());
        }
        Iterator iterator = results1.iterator();
        while (iterator.hasNext()) {
            Employee emp = (Employee)iterator.next();
            if (!emp.getFirstName().equals("Bob")) {
                throw new TestErrorException("The cached query returned the wrong results. " + emp);
            }
        }
        iterator = results2.iterator();
        while (iterator.hasNext()) {
            Employee emp = (Employee)iterator.next();
            if (!emp.getFirstName().equals("Jill")) {
                throw new TestErrorException("The cached query returned the wrong results. " + emp);
            }
        }
    }

    public void reset() {
        tracker.remove();
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }
}
