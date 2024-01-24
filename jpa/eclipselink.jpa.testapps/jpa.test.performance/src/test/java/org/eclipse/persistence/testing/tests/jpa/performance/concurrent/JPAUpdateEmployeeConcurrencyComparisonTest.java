/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     James Sutherland - initial impl
 package org.eclipse.persistence.testing.tests.jpa.performance.concurrent;

import jakarta.persistence.EntityManager;
import org.eclipse.persistence.testing.framework.ConcurrentPerformanceComparisonTest;
import org.eclipse.persistence.testing.models.jpa.performance.Employee;

import java.util.List;

/**
 * This test compares the concurrency of updating Employee.
 */
public class JPAUpdateEmployeeConcurrencyComparisonTest extends ConcurrentPerformanceComparisonTest {
    protected List<Employee> employees;
    protected String lastName;
    protected int index;
    protected long count;
    protected int errors;

    public JPAUpdateEmployeeConcurrencyComparisonTest() {
        setDescription("This test compares the concurrency of update Employee.");
    }

    public synchronized int incrementIndex() {
        this.index++;
        if (this.index >= this.employees.size()) {
            this.index = 0;
        }
        return this.index;
    }

    /**
     * Get list of employees.
     */
    @Override
    public void setup() {
        super.setup();
        EntityManager manager = createEntityManager();
        this.employees = manager.createQuery("Select e from Employee e", Employee.class).getResultList();
        this.lastName = this.employees.get(0).getLastName();
        manager.close();
        this.index = 0;
        this.count = 0;
    }

    /**
     * Update employee.
     */
    @Override
    public void runTask() {
        EntityManager manager = createEntityManager();
        manager.getTransaction().begin();
        Employee employee = manager.find(Employee.class, this.employees.get(incrementIndex()).getId());
        this.count++;
        employee.setLastName(this.lastName + this.count);
        try {
            manager.getTransaction().commit();
        } catch (Exception exception) {
            this.errors++;
            System.out.println(this.errors + ":" + exception);
        }
        manager.close();
    }
}
