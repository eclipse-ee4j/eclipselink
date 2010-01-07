/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.models.performance.toplink.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * This test compares the concurrency of read object cache hits.
 * This test must be run on a multi-CPU machine to be meaningful.
 */
public class ReadObjectCachedConcurrentRegressionTest extends ConcurrentPerformanceRegressionTest {
    protected Employee employee;
    protected List allObjects;

    public ReadObjectCachedConcurrentRegressionTest() {
        setDescription("This tests the concurrency of read-object cache hits.");
    }

    /**
     * Find any employee.
     */
    public void setup() {
        super.setup();
        Expression expression = new ExpressionBuilder().get("firstName").equal("Bob");
        employee = (Employee)getServerSession().acquireClientSession().readObject(Employee.class, expression);
        // Fully load the cache.
        allObjects = getServerSession().acquireClientSession().readAllObjects(Employee.class);
    }

    /**
     * Cached read-object.
     */
    public void runTask() throws Exception {
        Session client = getServerSession().acquireClientSession();
        client.readObject(employee);
        client.release();
    }
}
