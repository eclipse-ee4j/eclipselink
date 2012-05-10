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
package org.eclipse.persistence.testing.tests.performance.java;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.performance.Employee;

/**
 * This test tries to recreate concurrency issues in lazy initialization,
 * and also measure their performance.
 */
public class SynchronizedLazyInitConcurrentTest extends ConcurrentPerformanceComparisonTest {
    public SynchronizedLazyInitConcurrentTest() {
        setDescription("This test tries to recreate concurrency issues in lazy initialization and also measure their performance.");
    }
    
    protected Employee employee;

    public void runTask() throws Exception {
        Employee employee = getEmployee();
    }
    
    public synchronized Employee getEmployee() {
        if (employee == null) {
            employee = new Employee();
        }
        return employee;
    }
}
