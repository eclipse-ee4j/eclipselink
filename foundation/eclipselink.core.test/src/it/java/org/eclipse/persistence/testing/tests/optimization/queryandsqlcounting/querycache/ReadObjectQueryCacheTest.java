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
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;

/**
 * Ensure results of a ReadObjectQuery can be cached.
 */
public class ReadObjectQueryCacheTest extends NamedQueryQueryCacheTest {
    public ReadObjectQueryCacheTest() {
        setDescription("Ensure query caches work for ReadObject queries.");
    }

    public ReadQuery getQueryForTest() {
        ReadObjectQuery testQuery = new ReadObjectQuery(Employee.class);
        ExpressionBuilder employees = new ExpressionBuilder();
        Expression exp = employees.get("firstName").like("B%");
        testQuery.setSelectionCriteria(exp);
        testQuery.cacheQueryResults();
        return testQuery;
    }

    public void verify() {
        if (tracker.getSqlStatements().size() != expectedSQLCount) {
            throw new TestErrorException("An incorrect number of SQL statements were generated: " + tracker.getSqlStatements().size() + ". This likely indicates a problem with the query cache.");
        }
        if (!((Employee)results).getFirstName().startsWith("B")) {
            throw new TestErrorException("The cached query did not yeild the correct result. " + results);
        }
    }
}
