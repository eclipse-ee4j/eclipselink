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

import org.eclipse.persistence.queries.*;

/**
 * Ensure parameterized queries will only store the maximum number of results.
 */
public class QueryCacheMaxResultsTest extends QueryCacheParameterizedResultsTest {
    public QueryCacheMaxResultsTest() {
        setDescription("Ensure max results in the query cache limits the number of results in the cache.");
        expectedQuery1SQLStatementCount = 1;
    }

    public ReadAllQuery getReadAllQuery() {
        ReadAllQuery testQuery = super.getReadAllQuery();
        testQuery.setQueryResultsCachePolicy(new QueryResultsCachePolicy(1));
        return testQuery;
    }
}
