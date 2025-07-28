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
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.queries.ReportQueryResult;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

import java.util.Vector;

/**
 * Ensure results of a report query can be cached.
 */
public class ReportQueryCacheTest extends NamedQueryQueryCacheTest {
    public ReportQueryCacheTest() {
        setDescription("Ensure report queries can use query caching.");
    }

    @Override
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

    @Override
    public void verify() {
        super.verify();
        for (Object o : (Vector) results) {
            ReportQueryResult result = (ReportQueryResult) o;
            if (!((String) result.get("firstName")).startsWith("B")) {
                throw new TestErrorException("Incorrect Report Query result returned from the cache.");
            }
        }
    }
}
