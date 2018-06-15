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
package org.eclipse.persistence.testing.tests.performance.concurrent;

import java.util.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.models.performance.toplink.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * This test compares the concurrency of read all.
 * This test must be run on a multi-CPU machine to be meaningful.
 */
public class ReadAllObjectsConcurrentRegressionTest extends ConcurrentPerformanceRegressionTest {
    protected List allObjects;

    public ReadAllObjectsConcurrentRegressionTest() {
        setDescription("This tests the concurrency of read-all.");
    }

    /**
     * Find all employees.
     */
    public void setup() {
        super.setup();
        allObjects = getServerSession().acquireClientSession().readAllObjects(Employee.class);
    }

    /**
     * Find all employees
     */
    public void runTask() throws Exception {
        Session client = getServerSession().acquireClientSession();
        client.readAllObjects(Employee.class);
        client.release();
    }
}
