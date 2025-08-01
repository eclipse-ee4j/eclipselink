/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.ConcurrentPerformanceComparisonTest;
import org.eclipse.persistence.testing.models.performance.toplink.Employee;
import org.eclipse.persistence.testing.models.performance.toplink.EmployeeProject;

import java.util.List;

/**
 * This test compares the concurrency of read all.
 * This test must be run on a multi-CPU machine to be meaningful.
 */
public class ReadAllObjectsSharedDatabaseSessionConcurrentTest extends ConcurrentPerformanceComparisonTest {
    protected List allObjects;
    protected DatabaseSession session;

    public ReadAllObjectsSharedDatabaseSessionConcurrentTest() {
        setDescription("This tests the concurrency of read-all.");
    }

    /**
     * Create the database session.
     */
    @Override
    public void setup() {
        super.setup();
        session = new EmployeeProject().createDatabaseSession();
        session.setLogin(getSession().getLogin());
        session.login();
        allObjects = session.readAllObjects(Employee.class);
    }

    /**
     * Find all employees
     */
    @Override
    public void runTask() throws Exception {
        session.readAllObjects(Employee.class);
    }

    /**
     * Switch the descriptors back.
     */
    @Override
    public void reset() {
        super.reset();
        session.logout();
        session = null;
    }
}
