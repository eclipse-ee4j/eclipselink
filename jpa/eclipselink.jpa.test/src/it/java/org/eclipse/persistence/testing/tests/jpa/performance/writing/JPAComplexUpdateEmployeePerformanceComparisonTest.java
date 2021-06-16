/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
 package org.eclipse.persistence.testing.tests.jpa.performance.writing;

import java.util.*;
import jakarta.persistence.*;
import org.eclipse.persistence.testing.models.jpa.performance.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * This test compares the performance of updating Employee.
 */
public class JPAComplexUpdateEmployeePerformanceComparisonTest extends PerformanceRegressionTestCase {
    protected Employee originalEmployee;
    protected long count;

    public JPAComplexUpdateEmployeePerformanceComparisonTest() {
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
        PhoneNumber workFax = null;
        try {
            for (Iterator iterator = employee.getPhoneNumbers().iterator(); iterator.hasNext();) {
                PhoneNumber phone = (PhoneNumber)iterator.next();
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
                manager.remove(workFax);
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
