/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation
package org.eclipse.persistence.testing.tests.distributedservers.rcm;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.tests.distributedservers.DistributedServer;
import org.eclipse.persistence.testing.tests.distributedservers.DistributedServersModel;

/**
 * INVALIDATION BETWEEN SERVER SESSIONS GENERATE ADDITIONAL SQLS
 * Test to ensure a remote object is invalidated when updated and its
 * descriptor is configured to INVALIDATE_CHANGED_OBJECTS
 * @author dminsky
 */
public class UpdateObjectInvalidationTest extends ConfigurableCacheSyncDistributedTest {

    protected Employee employee;

    public UpdateObjectInvalidationTest() {
        super();
        setDescription("Ensure a remote object is invalidated when its descriptor is set to INVALIDATE_CHANGED_OBJECTS");
        cacheSyncConfigValues.put(Employee.class, ClassDescriptor.INVALIDATE_CHANGED_OBJECTS);
    }

    @Override
    public void setup() {
        super.setup();

        // Create an Employee
        employee = new Employee();
        employee.setFirstName("George");
        employee.setLastName("Alpha");

        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.registerObject(employee);
        uow.commit();

        // Ensure the employee exists in the Distributed Session
        ExpressionBuilder employees = new ExpressionBuilder();
        Expression expression = employees.get("firstName").equal(employee.getFirstName());
        expression = expression.and(employees.get("lastName").equal(employee.getLastName()));

        // Ensure our employee is in one of the distributed caches
        DistributedServer server = (DistributedServer)DistributedServersModel.getDistributedServers().get(0);
        Object result = server.getDistributedSession().readObject(Employee.class, expression);
        assertNotNull(result);
    }

    @Override
    public void test() {
        // Update the employee and commit (x2)
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Employee employeeClone = (Employee)uow.registerObject(employee);
        employeeClone.setLastName("Beta");
        uow.commit();

        uow = getSession().acquireUnitOfWork();
        employeeClone = (Employee)uow.registerObject(employee);
        employeeClone.setLastName("Gamma");
        uow.commit();
    }

    @Override
    public void verify() {
        // The employee should exist in the distributed cache, and it should be invalidated
        if (isObjectValidOnDistributedServer(employee) == true) {
            throw new TestErrorException("Employee should have been invalidated in the distributed cache, descriptor was set to: INVALIDATE_CHANGED_OBJECTS");
        }
    }

}
