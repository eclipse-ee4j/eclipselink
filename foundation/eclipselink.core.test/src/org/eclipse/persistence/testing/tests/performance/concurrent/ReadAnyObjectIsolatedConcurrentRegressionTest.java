/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.performance.concurrent;

import java.util.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.models.performance.toplink.*;

/**
 * This test compares the concurrency of refresh object.
 * This test must be run on a multi-CPU machine to be meaningful.
 */
public class ReadAnyObjectIsolatedConcurrentRegressionTest extends IsolatedConcurrentRegressionTest {
    protected int index;
    protected List allObjects;

    public ReadAnyObjectIsolatedConcurrentRegressionTest() {
        setDescription("This tests the concurrency of refresh object.");
    }

    /**
     * Find all employees.
     */
    public void setup() {
        super.setup();
        allObjects = getServerSession().acquireClientSession().readAllObjects(Employee.class);
    }

    /**
     * Refresh object.
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
