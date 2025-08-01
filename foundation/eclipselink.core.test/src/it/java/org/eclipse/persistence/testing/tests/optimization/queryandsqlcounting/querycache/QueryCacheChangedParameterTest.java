/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.optimization.queryandsqlcounting.querycache;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReadQuery;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

import java.util.Vector;

/**
 * Ensure if a setting is changed that causes the query to reprepare, the cache will
 * be cleared.
 */
public class QueryCacheChangedParameterTest extends NamedQueryQueryCacheTest {
    public QueryCacheChangedParameterTest() {
        setDescription("Ensure the query cache is cleared when a setting is changed.");
        expectedSQLCount = 1;
        expectedResults = 3;
    }

    @Override
    public void test() {
        ReadQuery query = (ReadQuery)getSessionForQueryTest().getQuery(CACHING_QUERY_NAME);
        ExpressionBuilder employees = new ExpressionBuilder();
        Expression exp = employees.get("firstName").like("J%");

        // calling setSelectionCriteria should unprepare the query
        query.setSelectionCriteria(exp);
        results = getSessionForQueryTest().executeQuery(query);
    }

    @Override
    public void verify() {
        super.verify();
        for (Object o : (Vector) results) {
            if (!((Employee) o).getFirstName().startsWith("J")) {
                throw new TestErrorException("Query results were not registered in the UOW " + " after being returned from a query with cached results");
            }
        }
    }
}
