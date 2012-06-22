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

import org.eclipse.persistence.queries.*;

/**
 * Ensure an explicit call to clearQueryResults() actually clears the query cache.
 *
 * This test can be set to run any of the four cache clearing methods
 */
public class ClearQueryResultsQueryCacheTest extends NamedQueryQueryCacheTest {
    protected int apiType = 0;
    public static final int CLEAR_WHOLE_CACHE = 0;
    public static final int CLEAR_CACHE_BY_QUERY = 1;
    public static final int CLEAR_CACHE_BY_NAME = 2;
    public static final int CLEAR_CACHE_BY_DESCRIPTOR = 3;

    public ClearQueryResultsQueryCacheTest(int apiType) {
        if (apiType == CLEAR_WHOLE_CACHE) {
            setName("Clear Whole Query Cache Test");
        } else if (apiType == CLEAR_CACHE_BY_QUERY) {
            setName("Clear Query Cache By Query Test");
        } else if (apiType == CLEAR_CACHE_BY_NAME) {
            setName("Clear Query Cache By Name Test");
        } else {
            queryLocation = QUERY_ON_DESCRIPTOR;
            setName("Clear Query Cache By Descriptor Test");
        }
        setDescription("Test the clearQueryResults API on ReadQuery");
        this.apiType = apiType;
        expectedSQLCount = 1;
    }

    public void clearCache() {
        if (apiType == CLEAR_WHOLE_CACHE) {
            getSession().getIdentityMapAccessor().clearQueryCache();
        } else if (apiType == CLEAR_CACHE_BY_QUERY) {
            ReadQuery query = (ReadQuery)getSessionForQueryTest().getQuery(CACHING_QUERY_NAME);
            getSession().getIdentityMapAccessor().clearQueryCache(query);
        } else if (apiType == CLEAR_CACHE_BY_NAME) {
            getSession().getIdentityMapAccessor().clearQueryCache(NamedQueryQueryCacheTest.CACHING_QUERY_NAME);
        } else {
            getSession().getIdentityMapAccessor().clearQueryCache(NamedQueryQueryCacheTest.CACHING_QUERY_NAME, getQueryForTest().getReferenceClass());
        }
    }

    public void test() {
        clearCache();
        super.test();
    }
}
