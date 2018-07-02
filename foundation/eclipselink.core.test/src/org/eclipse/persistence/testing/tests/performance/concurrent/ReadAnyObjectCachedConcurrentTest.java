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
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.models.performance.toplink.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * This test compares the concurrency of read object cache hits.
 * This test must be run on a multi-CPU machine to be meaningful.
 */
public class ReadAnyObjectCachedConcurrentTest extends ConcurrentPerformanceComparisonTest {
    protected int index;
    protected List allObjects;

    public ReadAnyObjectCachedConcurrentTest() {
        setDescription("This tests the concurrency of read-object cache hits.");
    }

    /**
     * Find all employees.
     */
    public void setup() {
        super.setup();
        allObjects = new ArrayList(getServerSession().acquireClientSession().readAllObjects(Employee.class));
    }

    /**
     * Cached read-object.
     */
    public void runTask() throws Exception {
        int currentIndex = index;
        if (currentIndex >= allObjects.size()) {
            index = 0;
            currentIndex = 0;
        }
        index++;
        Object employee = allObjects.get(currentIndex);
        Session client = getServerSession().acquireClientSession();
        client.readObject(employee);
        client.release();
    }
}
