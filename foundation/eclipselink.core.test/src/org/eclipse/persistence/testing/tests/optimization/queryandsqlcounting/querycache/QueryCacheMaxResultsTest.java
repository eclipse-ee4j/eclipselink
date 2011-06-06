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
