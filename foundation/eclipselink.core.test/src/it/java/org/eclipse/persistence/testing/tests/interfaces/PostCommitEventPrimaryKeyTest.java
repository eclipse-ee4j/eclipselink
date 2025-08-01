/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.interfaces;

import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.sessions.changesets.ObjectChangeSet;
import org.eclipse.persistence.sessions.changesets.UnitOfWorkChangeSet;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.interfaces.Asset;
import org.eclipse.persistence.testing.models.interfaces.Computer;
import org.eclipse.persistence.testing.models.interfaces.Email;
import org.eclipse.persistence.testing.models.interfaces.Employee;

/**
 * Bug 2612571 - The primary key was not returned as part of the cache key in the
 * post-commit event for certain Varible One-One mappings
 */
public class PostCommitEventPrimaryKeyTest extends AutoVerifyTestCase {

    private boolean emptyKey = false; // flag to determine if the test passes

    // The following is an anonymous class which is used for event listenign
    // it simply calls the commitOccurred() method.
    private SessionEventAdapter eventAdapter = new SessionEventAdapter() {
            @Override
            public void postCommitUnitOfWork(SessionEvent event) {
                commitOccurred(event);
            }
        };

    public PostCommitEventPrimaryKeyTest() {
        super();
        setDescription("Test that the primary key is correctly included with the Post Commit Event");
    }


    /**
     *  This method will be called by our event listener
     *  It will check to ensure there are primary keys for all the items in the change set.
     */
    public void commitOccurred(SessionEvent event) {
        org.eclipse.persistence.sessions.UnitOfWork uow = (org.eclipse.persistence.sessions.UnitOfWork)event.getSession();
        UnitOfWorkChangeSet uowChangeSet = uow.getUnitOfWorkChangeSet();
        for (Object o : uowChangeSet.getAllChangeSets().keySet()) {
            ObjectChangeSet objChangeSet = (ObjectChangeSet) o;
            Object objKey = objChangeSet.getId();

            if (objKey == null) {
                emptyKey = true;
            }
        }
    }

    @Override
    public void setup() {
        getSession().getEventManager().addListener(eventAdapter);
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        beginTransaction();
    }

    @Override
    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Employee employee = (Employee)uow.registerObject(new Employee());
        employee.asset1 = new Asset();
        employee.asset1.setAsset(Computer.example1());
        employee.asset2 = new Asset();
        employee.asset2.setAsset(Computer.example2());
        Email email = (Email)uow.registerObject(new Email());
        employee.setContact(email);
        email.setHolder(employee);
        uow.commit();
    }

    @Override
    public void verify() {
        if (emptyKey) {
            throw new TestErrorException("The change set for a Variable One-to-one mapping retured with a Null Primary Key.");
        }
    }

    @Override
    public void reset() {
        rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        getSession().getEventManager().removeListener(eventAdapter);
    }
}
