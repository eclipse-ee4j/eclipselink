/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.performance.reading;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.PerformanceComparisonTestCase;

/**
 * This test compares the performance of prepared queries vs dynamic queries.
 */
public class EmulatedReadObjectPreparedvsDynamicTest extends ReadObjectPreparedvsDynamicTest {

    public void setup() {
        Session session = buildEmulatedSession();

        /*EmulatedConnection connection = (EmulatedConnection)((org.eclipse.persistence.internal.sessions.AbstractSession)session).getAccessor().getConnection();

        ReadAllQuery query = new ReadAllQuery(Employee.class);
        query.setShouldIncludeData(true);
        query.setSelectionCriteria(query.getExpressionBuilder().get("firstName").equal("Bob"));
        ComplexQueryResult result = (ComplexQueryResult)getSession().executeQuery(query);
        Vector rows = (Vector)result.getData();
        String sql = query.getSQLString();
        connection.putRows(sql, rows);*/

        getExecutor().swapSession(session);
        super.setup();
    }

    public void reset() throws Throwable {
        getExecutor().resetSession();
        super.reset();
    }

    public PerformanceComparisonTestCase buildPreparedTest() {
        PerformanceComparisonTestCase test = super.buildPreparedTest();
        test.setAllowableDecrease(100);
        return test;
    }

    public PerformanceComparisonTestCase buildPreparedEJBQLTest() {
        PerformanceComparisonTestCase test = super.buildPreparedEJBQLTest();
        test.setAllowableDecrease(100);
        return test;
    }

    public PerformanceComparisonTestCase buildDynamicEJBQLTest() {
        PerformanceComparisonTestCase test = super.buildDynamicEJBQLTest();
        test.setAllowableDecrease(100);
        return test;
    }

    public PerformanceComparisonTestCase buildDynamicNoParseCacheEJBQLTest() {
        PerformanceComparisonTestCase test = super.buildDynamicNoParseCacheEJBQLTest();
        test.setAllowableDecrease(-200);
        return test;
    }

    public PerformanceComparisonTestCase buildDynamicExpressionCachedExpressionTest() {
        PerformanceComparisonTestCase test = super.buildDynamicExpressionCachedExpressionTest();
        test.setAllowableDecrease(100);
        return test;
    }
}
