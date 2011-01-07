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
package org.eclipse.persistence.testing.tests.distributedservers.rcm;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.tests.distributedservers.DistributedServer;
import org.eclipse.persistence.testing.tests.distributedservers.DistributedServersModel;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Address;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


/**
 * Test to ensure deletes are not propogated when a delete occurs to an object who's
 * descriptor is set to DO_NOT_SEND_CHANGES
 */
public class DeleteObjectNotSentTest extends ConfigurableCacheSyncDistributedTest {

    protected Employee employee = null;
    protected Address address = null;
    protected Expression expression = null;

    public DeleteObjectNotSentTest() {
        super();
        cacheSyncConfigValues.put(Employee.class, new Integer(ClassDescriptor.DO_NOT_SEND_CHANGES));
    }

    public void setup() {
        super.setup();
        // Create an Employee
        employee = new Employee();
        employee.setFirstName("Dean");
        employee.setLastName("Keaton");
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.registerObject(employee);
        uow.commit();

        // Ensure the employee exists in the Distributed Session
        ExpressionBuilder employees = new ExpressionBuilder();
        expression = employees.get("firstName").equal("Dean");
        expression = expression.and(employees.get("lastName").equal("Keaton"));
        // ensure our employee is in one of the distributed caches
        DistributedServer server = (DistributedServer)DistributedServersModel.getDistributedServers().get(0);
        Object result = server.getDistributedSession().readObject(Employee.class, expression);
    }

    /**
     * Create a new employee and commit.
     */
    public void test() {
        // Delete the employee and commit
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Employee employeeClone = (Employee)uow.registerObject(employee);
        uow.deleteObject(employeeClone);
        uow.commit();
    }

    public void verify() {
        // The employee should exist in the distributed cache because the cache synchronization
        // setting did not allow the delete to be passed.
        if (getObjectFromDistributedCache(employee) == null) {
            throw new TestErrorException("Employee was deleted from distributed cache even through " + "descriptor was set to: DO_NOT_SEND_CHANGES");
        }
    }
}
