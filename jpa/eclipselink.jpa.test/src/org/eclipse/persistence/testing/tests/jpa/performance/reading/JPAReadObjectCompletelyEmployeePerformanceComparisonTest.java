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

import javax.persistence.*;

import org.eclipse.persistence.testing.models.jpa.performance.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * This test compares the performance of read object Employee.
 */
public class JPAReadObjectCompletelyEmployeePerformanceComparisonTest extends PerformanceRegressionTestCase {
    protected long employeeId;

    public JPAReadObjectCompletelyEmployeePerformanceComparisonTest() {
        setDescription("This test compares the performance of read object Employee.");
    }

    /**
     * Get an employee id.
     */
    public void setup() {
        EntityManager manager = createEntityManager();
        employeeId = ((Employee)manager.createQuery("Select e from Employee e").getResultList().get(0)).getId();
        manager.close();
    }

    /**
     * Read object.
     */
    public void test() throws Exception {
        EntityManager manager = createEntityManager();
        manager.getTransaction().begin();
        Employee employee = manager.getReference(Employee.class, new Long(this.employeeId));
        employee.getAddress().toString();
        String.valueOf(employee.getManager());
        employee.getManagedEmployees().size();
        employee.getPhoneNumbers().size();
        employee.getProjects().size();
        manager.getTransaction().commit();
        manager.close();
    }
}
