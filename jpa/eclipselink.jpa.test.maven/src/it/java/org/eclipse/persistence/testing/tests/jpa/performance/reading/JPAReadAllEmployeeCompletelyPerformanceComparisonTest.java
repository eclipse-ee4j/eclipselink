/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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

import java.util.*;
import javax.persistence.*;
import org.eclipse.persistence.testing.models.jpa.performance.*;

/**
 * This test compares the performance of read all Employee.
 */
public class JPAReadAllEmployeeCompletelyPerformanceComparisonTest extends JPAReadPerformanceComparisonTest {

    public JPAReadAllEmployeeCompletelyPerformanceComparisonTest(boolean isReadOnly) {
        super(isReadOnly);
        setName("JPAReadAllEmployeeCompletelyPerformanceComparisonTest-readonly:" + isReadOnly);
        setDescription("This test compares the performance of read all Employee.");
    }

    /**
     * Read all employee.
     */
    public void test() throws Exception {
        EntityManager manager = createEntityManager();
        manager.getTransaction().begin();
        Query query = manager.createQuery("Select e from Employee e");
        query.setHint("org.hibernate.readOnly", new Boolean(isReadOnly()));
        query.setHint("eclipselink.read-only", new Boolean(isReadOnly()));
        query.setHint("toplink.return-shared", new Boolean(isReadOnly()));
        List result = query.getResultList();
        for (Iterator iterator = result.iterator(); iterator.hasNext();) {
            Employee employee = (Employee)iterator.next();
            employee.getAddress().toString();
            employee.getManagedEmployees().size();
        }
        manager.getTransaction().commit();
        manager.close();
    }
}
