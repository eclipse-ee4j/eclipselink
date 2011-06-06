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

import java.util.*;

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * Ensure results of a report query can be cached.
 */
public class ReportQueryCacheTest extends NamedQueryQueryCacheTest {
    public ReportQueryCacheTest() {
        setDescription("Ensure report queries can use query caching.");
    }

    public ReadQuery getQueryForTest() {
        ReportQuery testQuery = new ReportQuery();
        testQuery.setReferenceClass(Employee.class);
        ExpressionBuilder employees = new ExpressionBuilder();
        Expression exp = employees.get("firstName").like("B%");
        testQuery.setSelectionCriteria(exp);
        testQuery.cacheQueryResults();
        testQuery.addAttribute("firstName");
        return testQuery;
    }

    public void verify() {
        super.verify();
        Iterator reportResults = ((Vector)results).iterator();
        while (reportResults.hasNext()) {
            ReportQueryResult result = (ReportQueryResult)reportResults.next();
            if (!((String)result.get("firstName")).startsWith("B")) {
                throw new TestErrorException("Incorrect Report Query result returned from the cache.");
            }
        }
    }
}
