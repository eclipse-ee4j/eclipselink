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
public class UpdateEmployeeUnitOfWorkTest extends PerformanceTest {
    protected Employee employee;
    protected boolean incrementSalary;

    public UpdateEmployeeUnitOfWorkTest() {
        setDescription("This tests the performance of unit of work updates.");
    }

    /**
     * Find any employee.
     */
    public void setup() {
        super.setup();
        Expression expression = new ExpressionBuilder().get("firstName").equal("Bob");
        employee = (Employee)getSession().readObject(Employee.class, expression);
    }

    /**
     * Read employee and clear the cache, test database read.
     */
    public void test() throws Exception {
        super.test();
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Employee employee = (Employee)uow.readObject(this.employee);
        if (incrementSalary) {
            employee.setSalary(employee.getSalary() + 100);
        } else {
            employee.setSalary(employee.getSalary() - 100);
        }
        uow.commit();
    }
}
