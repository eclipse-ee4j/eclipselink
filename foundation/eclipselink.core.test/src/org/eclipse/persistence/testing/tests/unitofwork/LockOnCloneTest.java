/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.unitofwork;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TestWarningException;


public class LockOnCloneTest extends AutoVerifyTestCase {
    //session for the threads
    public ConcurrentPerson person;
    public ConcurrentPerson cloned;
    public LockOnCloneListener listener = new LockOnCloneListener();

    public LockOnCloneTest() {
        this.setDescription("Tests that TopLink is correctly locking on cloning");
    }

    public void setup() {
        if (getSession().isDistributedSession()) {
            throw new TestWarningException("Test unavailable on Remote UnitOfWork because of timing issues");
        }
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        getSession().getDescriptor(ConcurrentAddress.class).getEventManager().addListener(listener);
        UnitOfWork uow = getSession().acquireUnitOfWork();
        this.person = ConcurrentPerson.example1();
        //get the UOW clone to ensure the PK will be set in the case of Isolate in UOW test model
        this.person = (ConcurrentPerson)uow.registerObject(this.person);
        uow.commit();
        ConcurrentAddress.RUNNING_TEST = ConcurrentAddress.LOCK_ON_CLONE_TEST;

    }

    public void test() {
        Thread thread1 = new Thread() {
                public void run() {
                    UnitOfWork uow = getSession().acquireUnitOfWork();
                    cloned = (ConcurrentPerson)uow.registerObject(person);
                }
            };
        Thread thread2 = new Thread() {
                public void run() {
                    getSession().refreshObject(person.address);
                }
            };
        thread1.start();
        thread2.start();
        try {
            thread1.join();
            thread2.join();
        } catch (Exception ex) {
            //just an inturrupt ignore
        }
        ConcurrentAddress.RUNNING_TEST = ConcurrentAddress.NONE;
        if (!(cloned.getAddress().getStreet().equals("Start") &&
              cloned.getAddress().getPostalCode().equals("H0H0H0"))) {
            if (!(cloned.getAddress().getStreet().equals("Corrupted") &&
                  cloned.getAddress().getPostalCode().equals("A1A1A1"))) {
                throw new TestErrorException("Failed to wholly clone the object");
            } else {
                getSession().logMessage("LockOnCloneTest :-> Clone blocked on Refresh");
            }
        } else {
            getSession().logMessage(" LockOnCloneTest :-> refresh blocked on clone");
        }
    }

    public void reset() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.deleteObject(this.person);
        uow.commit();
        getSession().getDescriptor(ConcurrentAddress.class).getEventManager().removeListener(listener);
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }
}
