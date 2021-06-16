/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.unitofwork.changeflag;

import java.util.Vector;

import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventAdapter;
import org.eclipse.persistence.descriptors.changetracking.ObjectChangePolicy;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


/**
 *   This test will read all of the employees, change one and add a new one, and ensure that preWrite and preInsert events get fired.
 *   @author Edwin Tang
 */
public class ChangeEventTest extends AutoVerifyTestCase {
    protected boolean extraChangeComparison = false;
    protected boolean writeOccurred = false;
    protected boolean insertOccurred = false;
    protected Employee clone;
    protected Employee employeeToBeInserted = new Employee();
    protected ObjectChangePolicy changePolicy;

    // The following is an anonymous class which is used for event listening
    private DescriptorEventAdapter eventAdapter = new DescriptorEventAdapter() {
            public void preWrite(DescriptorEvent event) {
                writeOccurred(event);
            }

            public void preInsert(DescriptorEvent event) {
                insertOccurred(event);
            }
        };

    public ChangeEventTest() {
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
        employeeToBeInserted.setFirstName("John");
        employeeToBeInserted.setLastName("Smith");
        employeeToBeInserted.setMale();
        uow.registerNewObject(employeeToBeInserted);
        uow.commit();
    }

    public void reset() {
        getAbstractSession().rollbackTransaction();
        getSession().getDescriptor(Employee.class).getEventManager().removeListener(eventAdapter);
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    public void verify() {
        if (!writeOccurred) {
            throw new TestErrorException("There was no preWrite event thrown.");
        }
        if (!insertOccurred) {
            throw new TestErrorException("There was no preInsert event thrown.");
        }
    }

    public void writeOccurred(DescriptorEvent event) {
        writeOccurred = true;
    }

    public void insertOccurred(DescriptorEvent event) {
        insertOccurred = true;
    }
}
