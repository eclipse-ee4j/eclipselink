/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
 package org.eclipse.persistence.testing.tests.jpa.performance.reading;

import java.util.*;
import javax.persistence.*;

/**
 * This test compares the performance of read all Project.
 */
public class JPA2ReadAllProjectPerformanceComparisonTest extends JPAReadPerformanceComparisonTest {

    public JPA2ReadAllProjectPerformanceComparisonTest() {
        super(false);
        setDescription("This test compares the performance of read all Project.");
    }

    /**
     * Read all.
     */
    public void test() throws Exception {
        EntityManager manager = createEntityManager();
        Query query = manager.createQuery("Select p from Project p");
        List result = list(query, manager);
        result.size();
        manager.close();
    }
}
