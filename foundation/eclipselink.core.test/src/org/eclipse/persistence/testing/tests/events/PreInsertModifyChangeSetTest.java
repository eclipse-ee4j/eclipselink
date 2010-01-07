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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.events;

import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.descriptors.DescriptorEventAdapter;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.testing.framework.*;

/**
 * Added for Bug 2916836
 * Test to ensure changes made to change sets on in preWrite events
 * for Insert queries are reflected in the cache.
 */
public class PreInsertModifyChangeSetTest extends AutoVerifyTestCase {
    protected static int callCount = 0;
    protected Employee employee = null;

    // The following is an anonymous class which is used for event listening
    // uses the preInsert event to change a change set.
    protected DescriptorEventAdapter eventAdapter = new DescriptorEventAdapter() {
        public void preInsert(DescriptorEvent event) {
            if (event.getQuery().getDescriptor() != null) {
                event.updateAttributeWithObject("salary", new Integer(callCount));
                ++callCount;
            }
        }
    };

    public PreInsertModifyChangeSetTest() {
        setDescription("Test to ensure change sets modified by events actually affect the cache.");
    }

    public void setup() {
        getSession().getDescriptor(Employee.class).getEventManager().addListener(eventAdapter);
        beginTransaction();
    }

    public void test() {
        // Create an employee.  This employee's last name will be modifed in a preWrite evetn
        employee = new Employee();
        employee.setFirstName("Homer");
        employee.setLastName("Sampson");
        employee.setSalary(200000);
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.registerObject(employee);
        uow.commit();
    }

    public void verify() {
        ExpressionBuilder emp = new ExpressionBuilder();
        Expression exp = emp.get("id").equal(employee.getId());
        Employee result = (Employee)getSession().readObject(Employee.class, exp);

        // verify the modified salary appears.
        if (result.getSalary() == 200000) {
            throw new TestErrorException("Update of change set in preInsert event did not correctly change the cache.");
        }
        if (result.getSalary() != 0) {
            throw new TestErrorException("preInsert Called multiple times for same object");
        }
        callCount = 0;
    }

    public void reset() {
        rollbackTransaction();
        getSession().getDescriptor(Employee.class).getEventManager().removeListener(eventAdapter);
    }
}
