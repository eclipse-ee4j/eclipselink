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
package org.eclipse.persistence.testing.tests.optimization.queryandsqlcounting.querycache;

import org.eclipse.persistence.testing.framework.*;

/**
 * Test suite that tests that Query Cache
 */
public class QueryCacheTestSuite extends TestSuite {
    public QueryCacheTestSuite() {
        setDescription("Test the functionality of our Query Cache");
    }

    public void addTests() {
        addTest(new NamedQueryQueryCacheTest());
        addTest(new UnitOfWorkQueryCacheTest());
        addTest(new ReadObjectQueryCacheTest());
        addTest(new PartialAttributeQueryCacheTest());
        //addTest(new ReportQueryCacheTest());
        addTest(new QueryCacheParameterizedResultsTest());
        addTest(new QueryCacheMaxResultsTest());
        addTest(new QueryCacheChangedParameterTest());
        addTest(new ClearQueryResultsQueryCacheTest(ClearQueryResultsQueryCacheTest.CLEAR_WHOLE_CACHE));
        addTest(new ClearQueryResultsQueryCacheTest(ClearQueryResultsQueryCacheTest.CLEAR_CACHE_BY_QUERY));
        addTest(new ClearQueryResultsQueryCacheTest(ClearQueryResultsQueryCacheTest.CLEAR_CACHE_BY_NAME));
        addTest(new ClearQueryResultsQueryCacheTest(ClearQueryResultsQueryCacheTest.CLEAR_CACHE_BY_DESCRIPTOR));
        addTest(new QueryCacheInvalidationTest(QueryCacheInvalidationTest.NO_INVALIDATION, false));
        addTest(new QueryCacheInvalidationTest(QueryCacheInvalidationTest.TIME_TO_LIVE_INVALIDATION, false));
        addTest(new QueryCacheInvalidationTest(QueryCacheInvalidationTest.TIME_TO_LIVE_INVALIDATION, true));
        addTest(new QueryCacheInvalidationTest(QueryCacheInvalidationTest.DAILY_INVALIDATION, false));
        addTest(new QueryCacheInvalidationTest(QueryCacheInvalidationTest.DAILY_INVALIDATION, true));
        // bug6138532 & bug6135563
        addTest(new QueryCacheHitTest(QueryCacheHitTest.NO_RESULTS_CACHED_READALL));
        addTest(new QueryCacheHitTest(QueryCacheHitTest.VALID_RESULTS_CACHED_READALL));
        addTest(new QueryCacheHitTest(QueryCacheHitTest.NO_RESULTS_CACHED_READOBJECT));
        addTest(new QueryCacheHitTest(QueryCacheHitTest.VALID_RESULTS_CACHED_READOBJECT));
        addTest(new QueryCacheHitTest(QueryCacheHitTest.NO_RESULTS_CACHED_DATAREAD));
        addTest(new QueryCacheHitTest(QueryCacheHitTest.VALID_RESULTS_CACHED_DATAREAD));
    }
}
