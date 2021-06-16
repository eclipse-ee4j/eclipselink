/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
 package org.eclipse.persistence.testing.tests.jpa.performance.reading;

import java.util.*;
import jakarta.persistence.*;
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
