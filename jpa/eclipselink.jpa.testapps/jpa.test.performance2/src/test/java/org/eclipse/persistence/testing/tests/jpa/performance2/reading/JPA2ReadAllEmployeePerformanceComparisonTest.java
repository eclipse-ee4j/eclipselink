/*
 * Copyright (c) 2011, 2022 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
 package org.eclipse.persistence.testing.tests.jpa.performance2.reading;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.eclipse.persistence.testing.tests.jpa.performance.reading.JPAReadPerformanceComparisonTest;

import java.util.List;

/**
 * This test compares the performance of read all Employee.
 */
public class JPA2ReadAllEmployeePerformanceComparisonTest extends JPAReadPerformanceComparisonTest {

    public JPA2ReadAllEmployeePerformanceComparisonTest() {
        super(false);
        setDescription("This test compares the performance of read all Employee.");
    }

    /**
     * Read all employee.
     */
    @Override
    public void test() {
        EntityManager manager = createEntityManager();
        Query query = manager.createQuery("Select e from Employee e");
        List<?> result = list(query, manager);
        result.size();
        manager.close();
    }
}
