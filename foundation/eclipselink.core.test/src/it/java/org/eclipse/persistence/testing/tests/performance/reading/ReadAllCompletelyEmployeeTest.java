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
package org.eclipse.persistence.testing.tests.performance.reading;

import org.eclipse.persistence.testing.models.performance.toplink.Employee;
import org.eclipse.persistence.testing.tests.performance.PerformanceTest;

/**
 * This tests the performance of read-all queries.
 * Its purpose is to compare the test result with previous release/label results.
 * It also provides a useful test for profiling performance.
 */
public class ReadAllCompletelyEmployeeTest extends PerformanceTest {
    public ReadAllCompletelyEmployeeTest() {
        setDescription("This tests the performance of read-all queries.");
    }

    /**
     * Read employee and clear the cache, test database read.
     */
    @Override
    public void test() throws Exception {
        super.test();
        allObjects = getSession().readAllObjects(Employee.class);
        for (Object allObject : allObjects) {
            Employee employee = (Employee) allObject;
            employee.getAddress();
            employee.getManager();
            employee.getManagedEmployees().size();
            employee.getProjects().size();
            employee.getPhoneNumbers().size();
        }
    }
}
