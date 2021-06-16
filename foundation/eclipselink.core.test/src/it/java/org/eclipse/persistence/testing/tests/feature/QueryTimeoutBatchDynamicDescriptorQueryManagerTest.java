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
package org.eclipse.persistence.testing.tests.feature;

import org.eclipse.persistence.testing.framework.TestErrorException;

/**
 * Bug 214910:  Add query timeout support to batched update queries (Oracle DB 9.0.1+)</p>
 * Test the query timeout feature in batch queries.
 * For data queries , a queryTimeout on the largest DatabaseQuery of the batch will be used.
 * For object queries, a queryTimeout on the largest DescriptorQueryManager (parent) or DatabaseQuery
 * of the batch will be used.
 */
public class QueryTimeoutBatchDynamicDescriptorQueryManagerTest extends QueryTimeoutBatchDescriptorQueryManagerTest {

    protected boolean shouldBindAllParameters() { return false; }
    protected boolean shouldCacheAllStatements() { return true; }
    protected  int getNumberOfInserts() { return 1; }

    protected String getQuerySQLPrefix() {
        return "insert into employee (emp_id, version) SELECT ";
    }

    public void test() {
        super.test();
        if(!limitExceeded) {
            System.out.println("test completed without timeout.");
        }
    }

    public QueryTimeoutBatchDynamicDescriptorQueryManagerTest() {
        super();
        setDescription("Test that the query timeout setting is passed to the JDBC layer in batch queries in dynamic object mode.");
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
        initialize();
    }

}
