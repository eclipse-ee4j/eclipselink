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
package org.eclipse.persistence.testing.tests.performance.concurrent;

import java.util.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.models.performance.toplink.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * This test compares the concurrency of updates.
 * This test must be run on a multi-CPU machine to be meaningful.
 */
public class UpdateAnyObjectCachedConcurrentTest extends ConcurrentPerformanceComparisonTest {
    protected int index;
    protected List allObjects;

    public UpdateAnyObjectCachedConcurrentTest() {
        setDescription("This tests the concurrency of updates.");
    }

    /**
     * Load all employees.
     */
    public void setup() {
        super.setup();
        this.allObjects = new ArrayList(getServerSession().acquireClientSession().readAllObjects(Employee.class));
        this.index = 0;
    }

    public synchronized int incrementIndex() {
        this.index++;
        if (this.index >= this.allObjects.size()) {
            this.index = 0;
        }
        return this.index;
    }

    /**
     * Update an object.
     */
    public void runTask() throws Exception {
        int currentIndex = incrementIndex();
        Employee employee = (Employee)this.allObjects.get(currentIndex);
        Session client = getServerSession().acquireClientSession();
        UnitOfWork uow = client.acquireUnitOfWork();
        employee = (Employee)uow.readObject(employee);
        employee.setSalary(employee.getSalary() + 1);
        try {
            uow.commit();
        } catch (OptimisticLockException exception) {
            System.out.println(exception);
        }
        client.release();
    }
}
