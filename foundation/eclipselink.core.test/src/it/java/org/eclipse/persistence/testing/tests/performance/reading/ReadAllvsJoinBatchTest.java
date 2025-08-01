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

import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.testing.framework.PerformanceComparisonTestCase;
import org.eclipse.persistence.testing.models.performance.Address;
import org.eclipse.persistence.testing.models.performance.toplink.Employee;

import java.util.List;

/**
 * This test compares the performance of read all and access to a 1-1 vs joining and batching.
 */
public class ReadAllvsJoinBatchTest extends PerformanceComparisonTestCase {
    public ReadAllvsJoinBatchTest() {
        setDescription("This test compares the performance of read all and access to a 1-1 vs joining and batching.");
        addReadAllJoinTest();
        addReadAllBatchTest();
    }

    /**
     * Read all employees and access their addresses.
     */
    @Override
    public void test() throws Exception {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        List results = getSession().readAllObjects(Employee.class);
        for (Object result : results) {
            Employee employee = (Employee) result;
            Address address = employee.getAddress();
        }
    }

    /**
     * Read all employees and join their addresses.
     */
    public void addReadAllJoinTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            @Override
            public void test() {
                getSession().getIdentityMapAccessor().initializeIdentityMaps();
                ReadAllQuery query = new ReadAllQuery(Employee.class);
                query.addJoinedAttribute("address");
                List results = (List)getSession().executeQuery(query);
                for (Object result : results) {
                    Employee employee = (Employee) result;
                    Address address = employee.getAddress();
                }
            }
        };
        test.setName("ReadAllJoinTest");
        test.setAllowableDecrease(200);
        addTest(test);
    }

    /**
     * Read all employees and batch read their addresses.
     */
    public void addReadAllBatchTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            @Override
            public void test() {
                getSession().getIdentityMapAccessor().initializeIdentityMaps();
                ReadAllQuery query = new ReadAllQuery(Employee.class);
                query.addBatchReadAttribute("address");
                List results = (List)getSession().executeQuery(query);
                for (Object result : results) {
                    Employee employee = (Employee) result;
                    Address address = employee.getAddress();
                }
            }
        };
        test.setName("ReadAllBatchTest");
        test.setAllowableDecrease(200);
        addTest(test);
    }
}
