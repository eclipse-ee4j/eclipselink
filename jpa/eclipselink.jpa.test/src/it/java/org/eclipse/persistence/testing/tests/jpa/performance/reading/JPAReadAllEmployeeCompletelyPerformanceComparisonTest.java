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
