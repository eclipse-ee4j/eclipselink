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
 package org.eclipse.persistence.testing.tests.jpa.performance.reading;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import java.util.List;

/**
 * This test compares the performance of read all Project.
 */
public class JPAReadAllProjectPerformanceComparisonTest extends JPAReadPerformanceComparisonTest {

    public JPAReadAllProjectPerformanceComparisonTest(boolean isReadOnly) {
        super(isReadOnly);
        setName("JPAReadAllProjectPerformanceComparisonTest-readonly:" + isReadOnly);
        setDescription("This test compares the performance of read all Project.");
    }

    /**
     * Read all.
     */
    @Override
    public void test() {
        EntityManager manager = createEntityManager();
        Query query = manager.createQuery("Select p from Project p");
        List<?> result = list(query, manager);
        result.size();
        manager.close();
    }
}
