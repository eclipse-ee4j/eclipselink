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

    public void test() {
        ReadQuery query = (ReadQuery)getSessionForQueryTest().getQuery(CACHING_QUERY_NAME);
        ExpressionBuilder employees = new ExpressionBuilder();
        Expression exp = employees.get("firstName").like("J%");

        // calling setSelectionCriteria should unprepare the query
        query.setSelectionCriteria(exp);
        results = getSessionForQueryTest().executeQuery(query);
    }

    public void verify() {
        super.verify();
        Iterator employees = ((Vector)results).iterator();
        while (employees.hasNext()) {
            if (!((Employee)employees.next()).getFirstName().startsWith("J")) {
                throw new TestErrorException("Query results were not registered in the UOW " + " after being returned from a query with cached results");
            }
        }
    }
}
