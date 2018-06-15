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
import org.eclipse.persistence.testing.models.jpa.performance2.*;

/**
 * This test compares the performance of read all Employee.
 */
public class JPA2ReadAllEmployeeCompletelyPerformanceComparisonTest extends JPAReadPerformanceComparisonTest {

    public JPA2ReadAllEmployeeCompletelyPerformanceComparisonTest() {
        super(false);
        setDescription("This test compares the performance of read all Employee.");
    }

    /**
     * Read all employee.
     */
    public void test() throws Exception {
        EntityManager manager = createEntityManager();
        manager.getTransaction().begin();
        Query query = manager.createQuery("Select e from Employee e");
        List result = query.getResultList();
        for (Iterator iterator = result.iterator(); iterator.hasNext();) {
            Employee employee = (Employee)iterator.next();
            employee.getAddress().toString();
            employee.getManagedEmployees().size();
            employee.getDegrees().size();
            employee.getEmailAddresses().size();
            employee.getPhoneNumbers().size();
            employee.getProjects().size();
            employee.getJobTitle().toString();
            employee.getResponsibilities().size();
        }
        manager.getTransaction().commit();
        manager.close();
    }
}
