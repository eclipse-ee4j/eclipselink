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

import java.util.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.models.performance.toplink.*;
import org.eclipse.persistence.testing.tests.performance.PerformanceTest;

/**
 * This tests the performance of read-all queries.
 * Its purpose is to compare the test result with previous release/label results.
 * It also provides a useful test for profiling performance.
 */
public class ReadAllInMemoryEmployeeTest extends PerformanceTest {
    public ReadAllInMemoryEmployeeTest() {
        setDescription("This tests the performance of in-memory read-all queries.");
    }

    public void setup() {
        super.setup();
        // Fully load the cache.
        allObjects = getSession().readAllObjects(Employee.class);
    }

    /**
     * Read employee and clear the cache, test database read.
     */
    public void test() throws Exception {
        ReadAllQuery query = new ReadAllQuery(Employee.class);
        ExpressionBuilder employee = new ExpressionBuilder();
        query.setSelectionCriteria(employee.get("firstName").equal("Brendan"));
        query.checkCacheOnly();
        List result = (List)getSession().executeQuery(query);
    }
}
