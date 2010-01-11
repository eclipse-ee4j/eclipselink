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

import java.util.*;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 *  This test is designed to be run in the multithreaded test model.
 *
 *  It runs a cached query three times
 *  1. Populate the cache
 *  2. Use the populated cache
 *  3. After clearing the cache
 *
 * This sequence of steps is designed to ensure all of these three scenarios can be run
 * in a multithreaded environment.
 *
 * In addition, it can be set up to run the query with 3 different sets of parameters
 * in order to test caching of results for different queries
 */
public class QueryCacheTest extends TestCase {
    protected Vector args = null;
    protected int results1 = 0;
    protected int results2 = 0;
    protected int results3 = 0;
    protected int expectedResults = 0;

    public QueryCacheTest(int parameterType) {
        setDescription("A single test run as part of the multithreaded query caching test.");
        args = new Vector();
        if (parameterType == 1) {
            args.add("B%");
            expectedResults = 2;
        } else if (parameterType == 2) {
            args.add("J%");
            expectedResults = 3;
        } else {
            args.add("%");
            expectedResults = 12;
        }
    }

    public void test() {
        Vector results = (Vector)getSession().executeQuery(QueryCacheMultithreadedTest.CACHING_QUERY_NAME, args);
        results1 = results.size();
        results = (Vector)getSession().executeQuery(QueryCacheMultithreadedTest.CACHING_QUERY_NAME, args);
        results2 = results.size();

        ReadQuery query = (ReadQuery)getSession().getQuery(QueryCacheMultithreadedTest.CACHING_QUERY_NAME);
        query.clearQueryResults((AbstractSession)getSession());
        results = (Vector)getSession().executeQuery(query, args);
        results3 = results.size();
    }

    public void verify() {
        if (results1 != expectedResults) {
            throw new TestErrorException("Initial Query did not yield the expected number of results: " + results1);
        }
        if (results2 != expectedResults) {
            throw new TestErrorException("Second Query did not yield the expected number of results: " + results2);
        }
        if (results3 != expectedResults) {
            throw new TestErrorException("Third Query did not yield the expected number of results: " + results3);
        }
    }
}
