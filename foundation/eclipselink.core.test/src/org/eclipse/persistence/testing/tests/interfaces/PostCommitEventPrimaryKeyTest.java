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
package org.eclipse.persistence.testing.tests.interfaces;

import java.util.*;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.sessions.changesets.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.interfaces.*;

/**
 * Bug 2612571 - The primary key was not returned as part of the cache key in the
 * post-commit event for certain Varible One-One mappings
 */
public class PostCommitEventPrimaryKeyTest extends AutoVerifyTestCase {

    private boolean emptyKey = false; // flag to determine if the test passes

    // The following is an anonymous class which is used for event listenign
    // it simply calls the commitOccurred() method.
    private SessionEventAdapter eventAdapter = new SessionEventAdapter() {
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
        for (Iterator changes = uowChangeSet.getAllChangeSets().keySet().iterator(); changes.hasNext(); ) {
            ObjectChangeSet objChangeSet = (ObjectChangeSet)changes.next();
            Object objKey = objChangeSet.getId();

            if (objKey == null) {
                emptyKey = true;
            }
        }
    }

    public void setup() {
        getSession().getEventManager().addListener(eventAdapter);
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        beginTransaction();
    }

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

    public void verify() {
        if (emptyKey) {
            throw new TestErrorException("The change set for a Variable One-to-one mapping retured with a Null Primary Key.");
        }
    }

    public void reset() {
        rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        getSession().getEventManager().removeListener(eventAdapter);
    }
}
