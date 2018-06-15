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
package org.eclipse.persistence.testing.tests.feature;

import org.eclipse.persistence.testing.framework.TestErrorException;

/**
 * Bug 214910:  Add query timeout support to batched update queries (Oracle DB 9.0.1+)</p>
 * Test the query timeout feature in batch queries.
 * For data queries , a queryTimeout on the largest DatabaseQuery of the batch will be used.
 * For object queries, a queryTimeout on the largest DescriptorQueryManager (parent) or DatabaseQuery
 * of the batch will be used.
 */
public class QueryTimeoutBatchParameterizedDatabaseQueryTest extends QueryTimeoutBatchDatabaseQueryTest {

    protected boolean shouldBindAllParameters() { return true; }
    protected boolean shouldCacheAllStatements() { return true; }
    protected  int getNumberOfInserts() { return 1; }

    protected String getQuerySQLPrefix() {
        return "insert into employee (f_name, emp_id, version) SELECT 'first', ";
    }

    public void test() {
        super.test();
        if(!limitExceeded) {
            System.out.println("test completed without timeout.");
        }
    }

    public QueryTimeoutBatchParameterizedDatabaseQueryTest() {
        super();
        setDescription("Test that the query timeout setting is passed to the JDBC layer in batch queries in parameterized SQL mode.");
    }

    public void verify() {
        if (!limitExceeded || (verifyErrorCode() && getExpectedErrorCode() != vendorErrorCodeEncountered))  {
            if(unsupportedPlatform) {
                System.out.println("QueryTimeoutBatch test failed as expected on unsupported Platform");
            } else {
                throw new TestErrorException("Batch queryTimeout was not passed to the Statement.");
            }
        }
        // Make flag reentrant
        limitExceeded = false;
    }

}
