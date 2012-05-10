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
package org.eclipse.persistence.testing.tests.distributedservers.rcm;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.tests.distributedservers.DistributedServer;
import org.eclipse.persistence.testing.tests.distributedservers.DistributedServersModel;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


public class RelatedNewObjectCacheSyncTest extends ConfigurableCacheSyncDistributedTest {

    protected Employee employee = null;
    protected Employee manager = null;
    protected Expression expression = null;

    public RelatedNewObjectCacheSyncTest() {
        cacheSyncConfigValues.put(Employee.class, new Integer(ClassDescriptor.SEND_NEW_OBJECTS_WITH_CHANGES));
    }

    public void setup() {
        super.setup();
        ExpressionBuilder employees = new ExpressionBuilder();
        expression = employees.get("firstName").equal("Charles");
        expression = expression.and(employees.get("lastName").equal("Chanley"));
        // ensure our employee is in one of the distributed caches
        DistributedServer server = (DistributedServer)DistributedServersModel.getDistributedServers().get(0);
        Object result = server.getDistributedSession().readObject(Employee.class, expression);
        ((Employee)result).getManagedEmployees();
        ((Employee)result).getPhoneNumbers();
        ((Employee)result).getAddress();
        ((Employee)result).getManager();
        ((Employee)result).getProjects();
        ((Employee)result).getResponsibilitiesList();
    }

    /**
     * Create a new employee and commit.
     */
    public void test() {
        employee = (Employee)getSession().readObject(Employee.class, expression);

        UnitOfWork uow = getSession().acquireUnitOfWork();
        Employee employeeClone = (Employee)uow.registerObject(employee);
        employeeClone.setSalary(employeeClone.getSalary() + 1);
        manager = new Employee();
        manager.setFirstName("Fred");
        manager.setLastName("Fenster");
        Employee managerClone = (Employee)uow.registerObject(manager);
        employeeClone.setManager(managerClone);
        uow.commit();
    }

    public void verify() {
        if (getObjectFromDistributedCache(manager) == null) {
            throw new TestErrorException("New employee was not added to distributed cache with " + " SEND_NEW_OBJECTS_WITH_CHANGES descriptor CacheSynchronizationTypeSetting.");
        }
        Employee distributedEmployee = (Employee)getObjectFromDistributedCache(employee);
        Employee distributedManager = (Employee)getObjectFromDistributedCache(manager);
        if (!(distributedEmployee.getSalary() == employee.getSalary())) {
            throw new TestErrorException("Changes for existing employee were not sent to the " + "distributed cache.");
        }
        if (!((AbstractSession)getSession()).compareObjects(distributedEmployee.getManager(), manager)) {
            throw new TestErrorException("Relationship between employee and manager was not " + "properly maintained in distributed cache.");
        }
    }

}
