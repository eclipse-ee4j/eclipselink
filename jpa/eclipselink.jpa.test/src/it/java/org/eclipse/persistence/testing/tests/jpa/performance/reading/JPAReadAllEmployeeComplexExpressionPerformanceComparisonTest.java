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
 * This test compares the performance of read all Employee.
 */
public class JPAReadAllEmployeeComplexExpressionPerformanceComparisonTest extends JPAReadPerformanceComparisonTest {
    public JPAReadAllEmployeeComplexExpressionPerformanceComparisonTest(boolean isReadOnly) {
        super(isReadOnly);
        setName("JPAReadAllEmployeeComplexExpressionPerformanceComparisonTest-readonly:" + isReadOnly);
        setDescription("This test compares the performance of read all Employee in a criteria.");
    }

    /**
     * Read all.
     */
    public void test() throws Exception {
        EntityManager manager = createEntityManager();
        Query query = manager.createQuery("Select e from Employee e join e.phoneNumbers p where e.firstName = :firstName and e.lastName like :lastName and e.address.city = :city and p.areaCode <> :areaCode");
        query.setParameter("firstName", "Bob");
        query.setParameter("lastName", "Smith%");
        query.setParameter("city", "Toronto");
        query.setParameter("areaCode", "123");
        List result = list(query, manager);
        result.size();
        manager.close();
    }
}
