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

import java.util.ConcurrentModificationException;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.domain.PhoneNumber;


/**
 * Test that the refresh in a unit of work does not needlessly commit changes.
 */
public class UnitOfWorkConcurrentRevertTest extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    public Employee baseEmp;
    public boolean exception = false;

    public UnitOfWorkConcurrentRevertTest() {
        setDescription("Test that a concurrent modification exception can be avoided by a reverting UnitOfWork.");
    }

    public void reset() {
        getAbstractSession().rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void setup() {
        getAbstractSession().beginTransaction();
    }

    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        UnitOfWork uow2 = getSession().acquireUnitOfWork();

        baseEmp = (Employee)uow.readObject(Employee.class);
        for (int count = 20; count > 0; --count) {
            baseEmp.addPhoneNumber(new PhoneNumber(String.valueOf(count), String.valueOf(count), "5555555"));
        }
        uow.commit();

        Runnable runnable1 = new Runnable() {
                public void run() {
                }
            };

        Runnable runnable2 = new Runnable() {
                public void run() {
                    try {
                        for (int count = 20; count > 0; --count) {
                            UnitOfWork uow = getSession().acquireUnitOfWork();
                            Employee emp = (Employee)uow.readObject(baseEmp);
                            emp.getPhoneNumbers();
                            uow.revertObject(emp);
                        }
                    } catch (ConcurrentModificationException ex) {
                        exception = true;
                    }
                }
            };
        Thread thread1 = new Thread(runnable2);
        thread1.start();
        try {
            for (int count = 20; count > 0; --count) {
                uow = getSession().acquireUnitOfWork();
                Employee emp = (Employee)uow.readObject(baseEmp);
                emp.getPhoneNumbers().remove(0);
                uow.commit();
            }
        } catch (ConcurrentModificationException ex) {
            exception = true;
        }
        try {
            thread1.join();
        } catch (InterruptedException ex) {
        }
    }

    public void verify() {
        if (this.exception) {
            this.exception = false;
            throw new TestErrorException("Concurrent Modification exception was thrown durring a revert");
        }
    }
}
