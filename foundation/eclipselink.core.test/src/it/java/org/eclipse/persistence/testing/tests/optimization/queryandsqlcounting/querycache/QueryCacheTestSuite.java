/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
