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
import org.eclipse.persistence.testing.models.performance.toplink.Employee;

import java.util.List;

/**
 * This test compares the performance of read all and access to 2 1-m vs batching.
 */
public class ReadAllvsBatch21mTest extends PerformanceComparisonTestCase {
    public ReadAllvsBatch21mTest() {
        setDescription("This test compares the performance of read all and access to 2 1-m vs batching and joining.");
        addReadAllBatchTest();
        addReadAllJoinTest();
    }

    /**
     * Read all employees and access phones, cleared cache.
     */
    @Override
    public void test() throws Exception {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        List results = getSession().readAllObjects(Employee.class);
        for (Object result : results) {
            Employee employee = (Employee) result;
            employee.getPhoneNumbers().size();
            employee.getManagedEmployees().size();
        }
    }

    /**
     * Read all employees and batch read their phones.
     */
    public void addReadAllBatchTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            @Override
            public void test() {
                getSession().getIdentityMapAccessor().initializeIdentityMaps();
                ReadAllQuery query = new ReadAllQuery(Employee.class);
                query.addBatchReadAttribute("phoneNumbers");
                query.addBatchReadAttribute("managedEmployees");
                List results = (List)getSession().executeQuery(query);
                for (Object result : results) {
                    Employee employee = (Employee) result;
                    employee.getPhoneNumbers().size();
                    employee.getManagedEmployees().size();
                }
            }
        };
        test.setName("ReadAllBatchTest");
        test.setAllowableDecrease(500);
        addTest(test);
    }

    /**
     * Read all employees and join read their phones.
     */
    public void addReadAllJoinTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            @Override
            public void test() {
                getSession().getIdentityMapAccessor().initializeIdentityMaps();
                ReadAllQuery query = new ReadAllQuery(Employee.class);
                query.addJoinedAttribute(query.getExpressionBuilder().anyOf("phoneNumbers"));
                query.addJoinedAttribute(query.getExpressionBuilder().anyOfAllowingNone("managedEmployees"));
                List results = (List)getSession().executeQuery(query);
                for (Object result : results) {
                    Employee employee = (Employee) result;
                    employee.getPhoneNumbers().size();
                    employee.getManagedEmployees().size();
                }
            }
        };
        test.setName("ReadAllJoinTest");
        test.setAllowableDecrease(200);
        addTest(test);
    }
}
