/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.performance.concurrent;

import java.util.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.models.performance.toplink.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * This test compares the concurrency of read all.
 * This test must be run on a multi-CPU machine to be meaningful.
 */
public class ReadAllObjectsConcurrentTest extends ConcurrentPerformanceComparisonTest {
    protected List allObjects;

    public ReadAllObjectsConcurrentTest() {
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