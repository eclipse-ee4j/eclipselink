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
 package org.eclipse.persistence.testing.tests.jpa.performance.reading;

import java.util.*;
import jakarta.persistence.*;

/**
 * This test compares the performance of read all Address.
 */
public class JPAReadAllAddressSimpleExpressionPerformanceComparisonTest extends JPAReadPerformanceComparisonTest {
    public JPAReadAllAddressSimpleExpressionPerformanceComparisonTest(boolean isReadOnly) {
        super(isReadOnly);
        setName("JPAReadAllAddressSimpleExpressionPerformanceComparisonTest-readonly:" + isReadOnly);
        setDescription("This test compares the performance of read all Address in a city.");
    }

    /**
     * Read all address.
     */
    public void test() throws Exception {
        EntityManager manager = createEntityManager();
        Query query = manager.createQuery("Select a from Address a where a.city = :city");
        query.setParameter("city", "Ottawa");
        List result = list(query, manager);
        result.size();
        manager.close();
    }
}
