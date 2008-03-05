/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
 package org.eclipse.persistence.testing.tests.jpa.performance;

import javax.persistence.*;
import org.eclipse.persistence.testing.models.performance.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * This test compares the performance of read object Employee.
 */
public class JPAReadObjectEmployeePerformanceComparisonTest extends PerformanceRegressionTestCase {
    protected long employeeId;

    public JPAReadObjectEmployeePerformanceComparisonTest() {
        setDescription("This test compares the performance of read object Employee.");
    }

    /**
     * Get an employee id.
     */
    public void setup() {
        employeeId = ((Employee)getSession().readObject(org.eclipse.persistence.testing.models.performance.toplink.Employee.class)).getId();
    }

    /**
     * Read object employee.
     */
    public void test() throws Exception {
        EntityManager manager = createEntityManager();
        manager.getTransaction().begin();
        Employee employee = manager.getReference(Employee.class, new Long(this.employeeId));
        employee.toString();
        manager.getTransaction().commit();
        manager.close();
    }
}