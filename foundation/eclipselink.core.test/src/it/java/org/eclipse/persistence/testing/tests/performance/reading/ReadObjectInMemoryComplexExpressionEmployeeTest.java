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

import java.util.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.models.performance.toplink.*;
import org.eclipse.persistence.testing.tests.performance.PerformanceTest;

/**
 * This tests the performance of read-object queries.
 * Its purpose is to compare the test result with previous release/label results.
 * It also provides a useful test for profiling performance.
 */
public class ReadObjectInMemoryComplexExpressionEmployeeTest extends PerformanceTest {
    public ReadObjectInMemoryComplexExpressionEmployeeTest() {
        setDescription("This tests the performance of in-memory read-object queries.");
    }

    public void setup() {
        super.setup();
        // Fully load the cache and fire indirection.
        allObjects = getSession().readAllObjects(Employee.class);
        for (Iterator iterator = allObjects.iterator(); iterator.hasNext();) {
            Employee employee = (Employee)iterator.next();
            employee.getAddress();
            employee.getPhoneNumbers().size();
        }
    }

    /**
     * Read employee and clear the cache, test database read.
     */
    public void test() throws Exception {
        ReadObjectQuery query = new ReadObjectQuery(Employee.class);
        ExpressionBuilder employee = new ExpressionBuilder();
        query.setSelectionCriteria(employee.get("firstName").equal("Brendan").and(employee.get("salary").equal(100000)).and(employee.get("address").get("city").like("%pean%")).and(employee.anyOf("phoneNumbers").get("type").equal("Home")));
        query.checkCacheOnly();
        Employee result = (Employee)getSession().executeQuery(query);
    }
}
