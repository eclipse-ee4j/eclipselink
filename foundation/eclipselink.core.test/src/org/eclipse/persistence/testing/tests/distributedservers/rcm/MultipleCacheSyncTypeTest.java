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
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.tests.distributedservers.DistributedServer;
import org.eclipse.persistence.testing.tests.distributedservers.DistributedServersModel;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Address;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.domain.LargeProject;
import org.eclipse.persistence.testing.models.employee.domain.PhoneNumber;
import org.eclipse.persistence.testing.models.employee.domain.Project;
import org.eclipse.persistence.testing.models.employee.domain.SmallProject;

/**
 * Test the interaction of object with different cache synchronization types.  Ensure
 * cache synchronization works when multiple types are used.
 */
public class MultipleCacheSyncTypeTest extends ConfigurableCacheSyncDistributedTest {

    protected Expression expression = null;
    protected Project project = null;
    protected Address address = null;
    protected Employee verbal = null;

    public MultipleCacheSyncTypeTest() {
        super();
        cacheSyncConfigValues.put(Employee.class, new Integer(ClassDescriptor.SEND_NEW_OBJECTS_WITH_CHANGES));
        cacheSyncConfigValues.put(Project.class, new Integer(ClassDescriptor.INVALIDATE_CHANGED_OBJECTS));
        cacheSyncConfigValues.put(SmallProject.class, new Integer(ClassDescriptor.INVALIDATE_CHANGED_OBJECTS));
        cacheSyncConfigValues.put(LargeProject.class, new Integer(ClassDescriptor.INVALIDATE_CHANGED_OBJECTS));
        cacheSyncConfigValues.put(Address.class, new Integer(ClassDescriptor.DO_NOT_SEND_CHANGES));
        cacheSyncConfigValues.put(PhoneNumber.class, new Integer(ClassDescriptor.SEND_OBJECT_CHANGES));
    }

    /**
     * Setup the test by ensuring that the necessary employee is both in the local cache
     * and one distributed cache.
     */
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
     * Make one change to each type of object so that change sets are sent for all the objects
     */
    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Employee charles = (Employee)getSession().readObject(Employee.class, expression);
        Employee workingCharles = (Employee)uow.registerObject(charles);
        workingCharles.setSalary(workingCharles.getSalary() + 1);
        project = (Project)workingCharles.getProjects().firstElement();
        // Attempt to change the project name.  Limitations on the length of project name
        // mean that we have a minute risk of duplication here.
        project.setName(project.getName().substring(0, 2) + "-changed");
        address = workingCharles.getAddress();
        address.setCity(address.getCity() + "-changed");
        PhoneNumber phoneNumber = (PhoneNumber)workingCharles.getPhoneNumbers().firstElement();
        phoneNumber.setNumber("7654321");
        verbal = new Employee();
        verbal.setFirstName("Verbal");
        verbal.setLastName("Kint");
        uow.registerObject(verbal);
        uow.commit();
    }

    public void verify() {
        Employee chuck = (Employee)getSession().readObject(Employee.class, expression);
        Employee distributedChuck = (Employee)getObjectFromDistributedCache(chuck);
        if (!(distributedChuck.getSalary() == chuck.getSalary())) {
            throw new TestErrorException("Updated Employee Object not propogated to distributed cache.");
        }
        if (isObjectValidOnDistributedServer(project)) {
            throw new TestErrorException("Project was not invalidated in distributed cache.");
        }
        if (distributedChuck.getAddress().getCity().equals(chuck.getAddress().getCity())) {
            throw new TestErrorException("Address was propogated to distributed cache even though it was not set to.");
        }
        if (distributedChuck.getPhoneNumbers().firstElement().equals(chuck.getPhoneNumbers().firstElement())) {
            throw new TestErrorException("PhoneNumber was not propogated to distributed cache.");
        }
        if (getObjectFromDistributedCache(verbal) == null) {
            throw new TestErrorException("New employee was not propogated to distributed cache.");
        }
    }

}
