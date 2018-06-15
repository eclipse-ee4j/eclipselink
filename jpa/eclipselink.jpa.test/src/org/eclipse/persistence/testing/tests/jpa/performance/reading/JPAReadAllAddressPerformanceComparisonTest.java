/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
 package org.eclipse.persistence.testing.tests.jpa.performance.reading;

import java.util.*;
import javax.persistence.*;

/**
 * This test compares the performance of read all Address.
 */
public class JPAReadAllAddressPerformanceComparisonTest extends JPAReadPerformanceComparisonTest {

    public JPAReadAllAddressPerformanceComparisonTest(boolean isReadOnly) {
        super(isReadOnly);
        setName("JPAReadAllAddressPerformanceComparisonTest-readonly:" + isReadOnly);
        setDescription("This test compares the performance of read all Address.");
    }

    /**
     * Read all address.
     */
    public void test() throws Exception {
        EntityManager manager = createEntityManager();
        Query query = manager.createQuery("Select a from Address a");
        List result = list(query, manager);
        result.size();
        manager.close();
    }
}
