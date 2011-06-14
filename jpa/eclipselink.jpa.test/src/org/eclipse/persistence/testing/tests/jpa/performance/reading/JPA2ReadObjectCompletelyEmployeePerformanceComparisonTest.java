/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 ******************************************************************************/  
 package org.eclipse.persistence.testing.tests.jpa.performance.reading;

import javax.persistence.*;

import org.eclipse.persistence.testing.models.jpa.performance2.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * This test compares the performance of read object Employee.
 */
public class JPA2ReadObjectCompletelyEmployeePerformanceComparisonTest extends PerformanceRegressionTestCase {
    protected long employeeId;

    public JPA2ReadObjectCompletelyEmployeePerformanceComparisonTest() {
        setDescription("This test compares the performance of read object Employee.");
    }

    /**
     * Get an employee id.
     */
    public void setup() {
        EntityManager manager = createEntityManager();
        employeeId = ((Employee)manager.createQuery("Select e from Employee e").getResultList().get(0)).getId();
        manager.close();
    }

    /**
     * Read object.
     */
    public void test() throws Exception {
        EntityManager manager = createEntityManager();
        manager.getTransaction().begin();
        Employee employee = manager.getReference(Employee.class, new Long(this.employeeId));
        employee.getAddress().toString();
        String.valueOf(employee.getManager());
        employee.getManagedEmployees().size();
        employee.getDegrees().size();
        employee.getEmailAddresses().size();
        employee.getPhoneNumbers().size();
        employee.getProjects().size();
        employee.getJobTitle().toString();
        employee.getResponsibilities().size();
        manager.getTransaction().commit();
        manager.close();
    }
}
