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
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.models.performance.Address;
import org.eclipse.persistence.testing.models.performance.toplink.*;
import org.eclipse.persistence.testing.framework.*;

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
    public void test() throws Exception {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        List results = getSession().readAllObjects(Employee.class);
        for (int index = 0; index < results.size(); index++) {
            Employee employee = (Employee)results.get(index);
            Address address = employee.getAddress();
        }
    }

    /**
     * Read all employees and join their addresses.
     */
    public void addReadAllJoinTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public void test() {
                getSession().getIdentityMapAccessor().initializeIdentityMaps();
                ReadAllQuery query = new ReadAllQuery(Employee.class);
                query.addJoinedAttribute("address");
                List results = (List)getSession().executeQuery(query);
                for (int index = 0; index < results.size(); index++) {
                    Employee employee = (Employee)results.get(index);
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
            public void test() {
                getSession().getIdentityMapAccessor().initializeIdentityMaps();
                ReadAllQuery query = new ReadAllQuery(Employee.class);
                query.addBatchReadAttribute("address");
                List results = (List)getSession().executeQuery(query);
                for (int index = 0; index < results.size(); index++) {
                    Employee employee = (Employee)results.get(index);
                    Address address = employee.getAddress();
                }
            }
        };
        test.setName("ReadAllBatchTest");
        test.setAllowableDecrease(200);
        addTest(test);
    }
}
