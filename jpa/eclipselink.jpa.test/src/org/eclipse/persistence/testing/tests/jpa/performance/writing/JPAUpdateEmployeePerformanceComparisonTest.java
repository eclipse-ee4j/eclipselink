/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
 package org.eclipse.persistence.testing.tests.jpa.performance.writing;

import javax.persistence.*;

import org.eclipse.persistence.testing.models.jpa.performance.Employee;
import org.eclipse.persistence.testing.framework.*;

/**
 * This test compares the performance of updating Employee.
 */
public class JPAUpdateEmployeePerformanceComparisonTest extends PerformanceRegressionTestCase {
    protected long employeeId;
    protected String firstName;
    protected long count;

    public JPAUpdateEmployeePerformanceComparisonTest() {
        setDescription("This test compares the performance of update Employee.");
    }

    /**
     * Get an employee id.
     */
    public void setup() {
        EntityManager manager = createEntityManager();
        Employee employee = (Employee)manager.createQuery("Select e from Employee e").getResultList().get(0);
        this.employeeId = employee.getId();
        this.firstName = employee.getFirstName();
        this.count = 0;
        manager.close();        
    }

    /**
     * Update employee.
     */
    public void test() throws Exception {
        EntityManager manager = createEntityManager();
        manager.getTransaction().begin();
        Employee employee = manager.getReference(Employee.class, new Long(this.employeeId));
        count++;
        employee.setFirstName(this.firstName + count);
        try {
            manager.getTransaction().commit();
        } catch (Exception exception) {
            employee = manager.getReference(Employee.class, new Long(this.employeeId));
            manager.refresh(employee);
        }
        manager.close();
    }
}
