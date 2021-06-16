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
//     James Sutherland - initial impl
 package org.eclipse.persistence.testing.tests.jpa.performance.concurrent;

import java.util.Iterator;

import jakarta.persistence.*;

import org.eclipse.persistence.testing.models.jpa.performance.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * This test compares the concurrency of complex updating Employee.
 */
public class JPAAddPhoneConcurrencyComparisonTest extends ConcurrentPerformanceComparisonTest {
    protected Employee employee;
    protected int errors;

    public JPAAddPhoneConcurrencyComparisonTest() {
        setDescription("This test compares the concurrency of complex update Employee.");
    }

    /**
     * Get employees.
     */
    public void setup() {
        super.setup();
        EntityManager manager = createEntityManager();
        this.employee = (Employee)manager.createQuery("Select e from Employee e").getResultList().get(0);
        this.employee.getAddress().getCity();
        this.employee.getPhoneNumbers().size();
        manager.close();
    }

    /**
     * Update employee.
     */
    public void runTask() throws Exception {
        EntityManager manager = createEntityManager();
        manager.getTransaction().begin();
        Employee employee = manager.find(Employee.class, this.employee.getId());
        PhoneNumber workFax = null;
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
        try {
            manager.getTransaction().commit();
        } catch (Exception exception) {
            this.errors++;
            System.out.println("" + this.errors + ":" + exception);
        }
        manager.close();
    }
}
