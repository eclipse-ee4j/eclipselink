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
 *     James Sutherland - initial impl
 ******************************************************************************/  
 package org.eclipse.persistence.testing.tests.jpa.performance.concurrent;

import java.util.Iterator;
import java.util.List;

import javax.persistence.*;

import org.eclipse.persistence.testing.models.jpa.performance.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * This test compares the concurrency of complex updating Employee.
 */
public class JPAComplexUpdateEmployeeConcurrencyComparisonTest extends ConcurrentPerformanceComparisonTest {
    protected List<Employee> employees;
    protected int index;
    protected long count;
    protected int errors;

    public JPAComplexUpdateEmployeeConcurrencyComparisonTest() {
        setDescription("This test compares the concurrency of complex update Employee.");
    }

    public synchronized int incrementIndex() {
        this.index++;
        if (this.index >= this.employees.size()) {
            this.index = 0;
        }
        return this.index;
    }

    /**
     * Get list of employees.
     */
    public void setup() {
        super.setup();
        EntityManager manager = createEntityManager();
        this.employees = manager.createQuery("Select e from Employee e").getResultList();
        for (Employee employee : this.employees) {
            employee.getAddress().getCity();
            employee.getPhoneNumbers().size();
        }
        manager.close();
        this.index = 0;
        this.count = 0;
    }

    /**
     * Update employee.
     */
    public void runTask() throws Exception {
        EntityManager manager = createEntityManager();
        manager.getTransaction().begin();
        Employee originalEmployee = this.employees.get(incrementIndex());
        Employee employee = manager.find(Employee.class, originalEmployee.getId());
        this.count++;
        employee.setFirstName(originalEmployee.getFirstName() + count);
        employee.setLastName(originalEmployee.getLastName() + count);
        employee.getAddress().setStreet(originalEmployee.getAddress().getStreet() + count);
        employee.getAddress().setCity(originalEmployee.getAddress().getCity() + count);
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
