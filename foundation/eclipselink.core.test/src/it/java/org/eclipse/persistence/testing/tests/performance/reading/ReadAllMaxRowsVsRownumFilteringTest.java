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
package org.eclipse.persistence.testing.tests.performance.reading;

import org.eclipse.persistence.platform.database.OraclePlatform;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.testing.framework.PerformanceComparisonTestCase;
import org.eclipse.persistence.testing.models.performance.toplink.Employee;

/**
 * Compares  ReadAllQuery returning all rows to a Query using Oracle's Rownum support
 * to filter out all but the first row using MaxResults=1.
 */
public class ReadAllMaxRowsVsRownumFilteringTest extends PerformanceComparisonTestCase {
    public ReadAllMaxRowsVsRownumFilteringTest() {
        setDescription("This test compares the performance of Oracle rownum feature returning the first result"+
        " vs a ReadAllQuery.");
        addReadAllRownumMaxRowsTest();
        addReadAllMaxRowsTest();
    }

    public void setup() throws Throwable {
        super.setup();
    }


    /**
     * Read all employees with cursored stream.
     */
    public void test() throws Exception {
        ReadAllQuery query = new ReadAllQuery(Employee.class);
        getSession().executeQuery(query);
    }

    /**
     * Read all employees with scrollable cursor.
     */
    public void addReadAllRownumMaxRowsTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public void test() {
                ReadAllQuery query = new ReadAllQuery(Employee.class);
                query.setMaxRows(1);
                getSession().executeQuery(query);
            }

        };
        test.setName("ReadAllRownumMaxRowsTest");
        test.setAllowableDecrease(280);
        addTest(test);
    }

    public void addReadAllMaxRowsTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public void test() {
                ReadAllQuery query = new ReadAllQuery(Employee.class);
                query.setMaxRows(1);
                getSession().executeQuery(query);
            }
            public void startTest() {
                ((OraclePlatform)getSession().getPlatform()).setShouldUseRownumFiltering(false);
            }

            /**
             * Allows any test specific setup before starting the test run.
             */
            public void endTest() {
                ((OraclePlatform)getSession().getPlatform()).setShouldUseRownumFiltering(true);
            }

        };
        test.setName("addReadAllMaxRowsTest");
        test.setAllowableDecrease(325);
        addTest(test);
    }
}
