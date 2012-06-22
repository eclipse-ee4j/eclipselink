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
package org.eclipse.persistence.testing.tests.performance.writing;

import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.models.performance.toplink.*;
import org.eclipse.persistence.testing.tests.performance.PerformanceTest;

/**
 * This tests the performance of unit of work updates.
 * Its purpose is to compare the test result with previous release/label results.
 * It also provides a useful test for profiling performance.
 */
public class ComplexUpdateEmployeeUnitOfWorkTest extends PerformanceTest {
    protected Employee employee;
    protected Employee manager;
    protected PhoneNumber phone;
    protected boolean promotion = true;

    public ComplexUpdateEmployeeUnitOfWorkTest() {
        setDescription("This tests the performance of unit of work updates.");
    }

    /**
     * Find an employee with a manager.
     */
    public void setup() {
        super.setup();
        allObjects = getSession().readAllObjects(Employee.class);
        Expression expression = new ExpressionBuilder().get("firstName").equal("Bob");
        employee = (Employee)getSession().readObject(Employee.class, expression);
        manager = (Employee)employee.getManager();
        promotion = true;
    }

    /**
     * Read employee and clear the cache, test database read.
     * Tries to keep the test data the same by cycling the employee manager relationship.
     */
    public void test() throws Exception {
        super.test();
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Employee employee = (Employee)uow.readObject(this.employee);
        if (promotion) {
            Employee manager = (Employee)employee.getManager();
            employee.setManager(manager.getManager());
            employee.addManagedEmployee(manager);
            employee.getAddress().setCity("Toronto");
            this.phone = new PhoneNumber();
            phone.setType("Cellphone");
            phone.setAreaCode("416");
            phone.setNumber("7921771");
            employee.addPhoneNumber(phone);
            employee.setSalary(employee.getSalary() + 100);
        } else {
            Employee manager = (Employee)uow.readObject(this.manager);
            employee.removeManagedEmployee(manager);
            employee.setManager(manager);
            employee.getAddress().setCity("Ottawa");
            PhoneNumber phone = (PhoneNumber)uow.readObject(this.phone);
            employee.removePhoneNumber(phone);
            employee.setSalary(employee.getSalary() - 100);
        }
        uow.commit();
        promotion = !promotion;
    }

    /**
     * Ensure that the employee is reset to original state.
     */
    public void reset() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Employee employee = (Employee)uow.readObject(this.employee);
        if (!promotion) {
            Employee manager = (Employee)uow.readObject(this.manager);
            employee.removeManagedEmployee(manager);
            employee.setManager(manager);
            employee.getAddress().setCity("Ottawa");
            PhoneNumber phone = (PhoneNumber)uow.readObject(this.phone);
            employee.removePhoneNumber(phone);
            employee.setSalary(employee.getSalary() - 100);
        }
        uow.commit();
        promotion = true;
    }
}
