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
 package org.eclipse.persistence.testing.tests.jpa.performance.writing;

import javax.persistence.*;
import org.eclipse.persistence.testing.models.jpa.performance2.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * This test compares the performance of updating Employee.
 */
public class JPA2ComplexUpdateEmployeePerformanceComparisonTest extends PerformanceRegressionTestCase {
    protected Employee originalEmployee;
    protected long count;

    public JPA2ComplexUpdateEmployeePerformanceComparisonTest() {
        setDescription("This test compares the performance of update Employee.");
    }

    /**
     * Get an employee id.
     */
    public void setup() {
        EntityManager manager = createEntityManager();
        this.originalEmployee = (Employee)manager.createQuery("Select e from Employee e").getResultList().get(0);
        this.originalEmployee.getAddress().getCity();
        this.originalEmployee.getPhoneNumbers().size();
        this.count = 0;
        manager.close();
    }

    /**
     * Update employee.
     */
    public void test() throws Exception {
        EntityManager manager = createEntityManager();
        manager.getTransaction().begin();
        Employee employee = manager.find(Employee.class, new Long(originalEmployee.getId()));
        count++;
        employee.setFirstName(originalEmployee.getFirstName() + count);
        employee.setLastName(originalEmployee.getLastName() + count);
        employee.getAddress().setStreet(originalEmployee.getAddress().getStreet() + count);
        employee.getAddress().setCity(originalEmployee.getAddress().getCity() + count);
        employee.getAddress().setCity(originalEmployee.getAddress().getCity() + count);
        try {
            // Phone
            PhoneNumber workFax = null;
            for (PhoneNumber phone : employee.getPhoneNumbers()) {
                if (phone.getType().equals("work-fax")) {
                    workFax = phone;
                    break;
                }
            }
            if (workFax == null) {
                PhoneNumber phone = new PhoneNumber();
                phone.setType("work-fax");
                phone.setAreaCode("613");
                phone.setNumber("9991111");
                employee.addPhoneNumber(phone);
            } else {
                employee.removePhoneNumber(workFax);
            }
            // Degree
            Degree bComp = null;
            for (Degree degree : employee.getDegrees()) {
                if (degree.getName().equals("BComp")) {
                    bComp = degree;
                    break;
                }
            }
            if (bComp == null) {
                employee.addDegree("BComp");
            } else {
                employee.removeDegree(bComp);
            }
            // Email
            if (employee.getEmailAddresses().containsKey("work")) {
                employee.removeEmailAddress("work");
            } else {
                employee.addEmailAddress("work", "joe@acme.com");
            }
            // Responsibility
            if (employee.getResponsibilities().contains("write code")) {
                employee.removeResponsibility("write code");
            } else {
                employee.addResponsibility("write code");
            }
            manager.getTransaction().commit();
        } catch (Exception exception) {
            // Cache can get stale from TopLink run, so force refresh.
            employee = manager.getReference(Employee.class, new Long(originalEmployee.getId()));
            manager.refresh(employee);
            employee.getPhoneNumbers();
        }
        manager.close();
    }
}
