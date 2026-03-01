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
package org.eclipse.persistence.testing.tests.aggregate;

import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventManager;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.aggregate.AddressDescription;
import org.eclipse.persistence.testing.models.aggregate.AggregateEventListener;
import org.eclipse.persistence.testing.models.aggregate.Employee;

import java.util.Vector;

/**
 * This is a test to verify that events are being thrown when appropriate in AggregateMapping.
 */
public class AggregateEventTestCase extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    public AggregateEventListener listener;

    @Override
    public void reset() {
        rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        (getSession().getProject().getDescriptors().get(Employee.class)).getEventManager().removeListener(this.listener);
    }

    @Override
    public void setup() {
        beginTransaction();
    }

    @Override
    public void test() {
        DatabaseSession session = (DatabaseSession)getSession();
        this.listener = new AggregateEventListener();
        (session.getProject().getDescriptors().get(AddressDescription.class)).getEventManager().addListener(this.listener);

        UnitOfWork uow = session.acquireUnitOfWork();
        Employee emp = Employee.example1();
        Employee empClone = (Employee)uow.registerObject(emp);
        uow.commitAndResume();
        Vector events = this.listener.getEvents();
        if (events.size() != 3) {// one for the preInsert and One for thePostInsert and postWrite
            throw new TestErrorException("The aggregate events failed to fire");
        } else {
            if ((!(((DescriptorEvent)events.get(0)).getEventCode() == DescriptorEventManager.PreInsertEvent)) || (!(((DescriptorEvent)events.get(2)).getEventCode() == DescriptorEventManager.PostWriteEvent)) || (!(((DescriptorEvent)events.get(1)).getEventCode() == DescriptorEventManager.PostInsertEvent))) {
                throw new TestErrorException("The appropriate aggregate events failed to fire");
            } else {
                if (((DescriptorEvent)events.get(0)).getSource().getClass() != AddressDescription.class) {
                    throw new TestErrorException("The wrong object was passed into the event");
                }
            }
        }
        events.clear();
        empClone.setFirstName("Douglas");
        empClone.getAddressDescription().getPeriodDescription().getPeriod().setEndDate(new java.sql.Date(System.currentTimeMillis()));
        uow.commit();
        events = this.listener.getEvents();
        if (events.size() != 3) {// one for the preUpdate and One for the PostUpdate and post Write
            throw new TestErrorException("The appropriate aggregate events failed to fire");
        } else {
            if ((!(((DescriptorEvent)events.get(0)).getEventCode() == DescriptorEventManager.PreUpdateEvent)) || (!(((DescriptorEvent)events.get(2)).getEventCode() == DescriptorEventManager.PostWriteEvent)) || (!(((DescriptorEvent)events.get(1)).getEventCode() == DescriptorEventManager.PostUpdateEvent))) {
                throw new TestErrorException("The aggregate events failed to fire");
            }
        }
        events.clear();
        session.deleteObject(emp);
        events = this.listener.getEvents();
        if (events.size() != 2) {// one for the preDelete and One for the Postdelete
            throw new TestErrorException("The appropriate aggregate events failed to fire");
        } else {
            if ((!(((DescriptorEvent)events.get(0)).getEventCode() == DescriptorEventManager.PreDeleteEvent)) || (!(((DescriptorEvent)events.get(1)).getEventCode() == DescriptorEventManager.PostDeleteEvent))) {
                throw new TestErrorException("The aggregate events failed to fire");
            }
        }
    }

    @Override
    public void verify() {
    }
}
