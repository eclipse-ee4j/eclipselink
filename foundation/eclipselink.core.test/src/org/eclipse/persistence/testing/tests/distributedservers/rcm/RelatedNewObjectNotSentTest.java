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

import java.util.Enumeration;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.tests.distributedservers.DistributedServer;
import org.eclipse.persistence.testing.tests.distributedservers.DistributedServersModel;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Address;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.domain.Project;
import org.eclipse.persistence.testing.models.employee.domain.SmallProject;


/**
 * Test to ensure an related new object that is not sent is properly read into the cache
 * on the distributed server.
 */
public class RelatedNewObjectNotSentTest extends ConfigurableCacheSyncDistributedTest {

    protected Employee employee = null;
    protected Address address = null;
    protected Project project = null;
    protected Expression expression = null;

    public RelatedNewObjectNotSentTest() {
        cacheSyncConfigValues.put(Employee.class, new Integer(ClassDescriptor.SEND_NEW_OBJECTS_WITH_CHANGES));
        cacheSyncConfigValues.put(Address.class, new Integer(ClassDescriptor.DO_NOT_SEND_CHANGES));
        cacheSyncConfigValues.put(SmallProject.class, new Integer(ClassDescriptor.DO_NOT_SEND_CHANGES));
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

    public void test() {
        employee = (Employee)getSession().readObject(Employee.class, expression);

        UnitOfWork uow = getSession().acquireUnitOfWork();
        Employee employeeClone = (Employee)uow.registerObject(employee);
        address = new Address();
        address.setCity("Minas Tirith");
        address.setStreet("1 Main Street");
        address.setProvince("Tirith");
        address.setPostalCode("ROH AN");
        address.setCountry("Rohan");
        Address addressClone = (Address)uow.registerObject(address);
        employeeClone.setAddress(addressClone);
        project = new SmallProject();
        project.setName("TopLink for your brain");
        project.setDescription("Improves your memory, but includes one heck of a connection pool.");
        SmallProject projectClone = (SmallProject)uow.registerObject(project);
        employeeClone.addProject(projectClone);
        uow.commit();
    }

    public void verify() {
        Employee distributedEmployee = (Employee)getObjectFromDistributedCache(employee);
        Address distributedAddress = (Address)getObjectFromDistributedCache(address);
        SmallProject distributedProject = (SmallProject)getObjectFromDistributedCache(project);
        if (!((AbstractSession)getSession()).compareObjects(distributedEmployee.getAddress(), employee.getAddress())) {
            throw new TestErrorException("The employee's address was not properly merged.  The new address " + "was not read into the distributed cache.");
        }
        distributedProject = null;
        boolean foundProject = false;
        Enumeration enumtr = distributedEmployee.getProjects().elements();
        while (enumtr.hasMoreElements() && !foundProject) {
            Project proj = (Project)enumtr.nextElement();
            if (((AbstractSession)getSession()).compareObjects(proj, project)) {
                foundProject = true;
            }
        }
        if (!foundProject) {
            throw new TestErrorException("The employee's small project was not properly merged.  The new small project " + "was not read into the distributed cache.");
        }
    }
}
