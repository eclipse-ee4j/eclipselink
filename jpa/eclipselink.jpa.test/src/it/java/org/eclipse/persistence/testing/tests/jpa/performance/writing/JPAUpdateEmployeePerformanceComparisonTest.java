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
 package org.eclipse.persistence.testing.tests.jpa.performance.writing;

import jakarta.persistence.*;

import org.eclipse.persistence.testing.models.jpa.performance.Employee;
import org.eclipse.persistence.testing.framework.*;

/**
 * This test compares the performance of updating Employee.
 */
public class JPAUpdateEmployeePerformanceComparisonTest extends PerformanceRegressionTestCase {
    protected long employeeId;
    protected String firstName;
    protected long count;

    public JPAUpdateEmployeePerformanceComparisonTest() {
        setDescription("This test compares the performance of update Employee.");
    }

    /**
     * Get an employee id.
     */
    public void setup() {
        EntityManager manager = createEntityManager();
        Employee employee = (Employee)manager.createQuery("Select e from Employee e").getResultList().get(0);
        this.employeeId = employee.getId();
        this.firstName = employee.getFirstName();
        this.count = 0;
        manager.close();
    }

    /**
     * Update employee.
     */
    public void test() throws Exception {
        EntityManager manager = createEntityManager();
        manager.getTransaction().begin();
        Employee employee = manager.getReference(Employee.class, new Long(this.employeeId));
        count++;
        employee.setFirstName(this.firstName + count);
        try {
            manager.getTransaction().commit();
        } catch (Exception exception) {
            employee = manager.getReference(Employee.class, new Long(this.employeeId));
            manager.refresh(employee);
        }
        manager.close();
    }
}
