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
package org.eclipse.persistence.testing.tests.unitofwork;

import java.util.Iterator;
import java.util.Vector;

import org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TestProblemException;
import org.eclipse.persistence.testing.models.employee.domain.Address;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.domain.PhoneNumber;


// bug 4364283. Make sure that only change sets with changes are included in uowChangeSet.getAllChangeSets()
public class AllChangeSetsTest extends AutoVerifyTestCase {
    static class SessionListener extends SessionEventAdapter {
        public int changedObjectsCount;

        public void preMergeUnitOfWorkChangeSet(SessionEvent event) {
            UnitOfWorkChangeSet uowChangeSet = (UnitOfWorkChangeSet)event.getProperty("UnitOfWorkChangeSet");
            changedObjectsCount = uowChangeSet.getAllChangeSets().size();
        }
    }

    SessionListener listener = new SessionListener();
    int changedObjectsCount;

    public void reset() {
        if (getAbstractSession().isInTransaction()) {
            getAbstractSession().rollbackTransaction();
            getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
            getSession().getEventManager().removeListener(listener);
        }
    }

    public void setup() {
        getSession().getEventManager().addListener(listener);
        getAbstractSession().beginTransaction();
        changedObjectsCount = 0;
        listener.changedObjectsCount = -1;
    }

    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Vector employees = uow.readAllObjects(Employee.class);

        // We should end up with only 4 changedObjects.
        // To make it easy to ensure that not all registered objects are counted
        // let's require at least 5 object
        if (employees.size() <= 4) {
            throw new TestProblemException("only " + employees.size() + " employees were read, need at least 5");
        }

        // Existing object has changed - should be counted
        Employee emp = (Employee)employees.firstElement();
        emp.setFirstName("Changed");
        changedObjectsCount++;

        Address add = emp.getAddress();
        if (emp.getAddress() != null) {
            // Existing object has changed - should be counted
            add.setCity("Changed");
            changedObjectsCount++;
        }

        // New object - should be counted
        Employee newEmp = new Employee();
        Employee newEmpClone = (Employee)uow.registerObject(newEmp);
        newEmpClone.setFirstName("New");
        changedObjectsCount++;

        Iterator it = emp.getPhoneNumbers().iterator();
        while (it.hasNext()) {
            PhoneNumber phone = (PhoneNumber)it.next();

            // to force reading the phone in and therefore registering in uow
            // Registered but not changed - should NOT be counted
            String code = phone.getAreaCode();
        }

        // forcedUpdate with false - should NOT be counted (doesn't affect cache)
        Employee emp1 = (Employee)employees.elementAt(1);
        uow.forceUpdateToVersionField(emp1, false);

        // forcedUpdate with true - should be counted
        Employee emp2 = (Employee)employees.elementAt(2);
        uow.forceUpdateToVersionField(emp2, true);
        changedObjectsCount++;

        uow.commit();
    }

    public void verify() {
        if (listener.changedObjectsCount == -1) {
            throw new TestProblemException("postCalculateUnitOfWorkChangeSet was not handled");
        }
        if (listener.changedObjectsCount != changedObjectsCount) {
            throw new TestErrorException("uowChangeSet.getAllChangeSets().size() == " + 
                                         listener.changedObjectsCount + "; " + changedObjectsCount + 
                                         " is expected");
        }
    }
}
