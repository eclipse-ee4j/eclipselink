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
package org.eclipse.persistence.testing.tests.unitofwork.changeflag;

import java.util.Vector;

import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventAdapter;
import org.eclipse.persistence.descriptors.changetracking.ObjectChangePolicy;
import org.eclipse.persistence.queries.WriteObjectQuery;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


/**
 *   Test to ensure that minimal comparisons are done when an ObjectChangeTrackingPolicy
 *   is used for an object.
 *   This test will read all of the employees, change one, and ensure that only the changed employee
 *   is compared for change.
 *   @author Tom Ware
 */
public class ChangeFlagTest extends AutoVerifyTestCase {

    protected boolean extraChangeComparison = false;
    protected Employee clone;
    protected ObjectChangePolicy changePolicy;

    // The following is an anonymous class which is used for event listening
    // it simply calls the writeOccurred() method.
    private DescriptorEventAdapter eventAdapter = new DescriptorEventAdapter() {
            public void preWrite(DescriptorEvent event) {
                writeOccurred(event);
            }
        };


    public ChangeFlagTest() {
        super();
    }

    public void setup() {
        getSession().getDescriptor(Employee.class).getEventManager().addListener(eventAdapter);
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        getAbstractSession().beginTransaction();
    }

    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Vector employees = uow.readAllObjects(Employee.class);
        clone = (Employee)employees.firstElement();
        clone.setFirstName(clone.getFirstName() + "1");
        uow.commit();
    }

    public void reset() {
        getAbstractSession().rollbackTransaction();
        getSession().getDescriptor(Employee.class).getEventManager().removeListener(eventAdapter);
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    public void verify() {
        if (extraChangeComparison) {
            throw new TestErrorException("An extra change comparison occurred when commiting an employee to the database.");
        }
    }

    public void writeOccurred(DescriptorEvent event) {
        Object object = ((WriteObjectQuery)event.getQuery()).getObject();
        // if we ever get a preWrite event which uses an object we have not changed, 
        // we know we have done an extraneous comparison.
        if (object != clone) {
            extraChangeComparison = true;
        }
    }
}
