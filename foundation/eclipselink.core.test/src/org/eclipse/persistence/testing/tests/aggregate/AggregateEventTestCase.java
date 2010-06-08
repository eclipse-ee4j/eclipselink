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
package org.eclipse.persistence.testing.tests.aggregate;

import java.util.Vector;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventManager;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.aggregate.AddressDescription;
import org.eclipse.persistence.testing.models.aggregate.AggregateEventListener;
import org.eclipse.persistence.testing.models.aggregate.Employee;

/**
 * This is a test to verify that events are being thrown when appropriate in AggregateMapping.
 */
public class AggregateEventTestCase extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    public AggregateEventListener listener;

    public void reset() {
        rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        (getSession().getProject().getDescriptors().get(Employee.class)).getEventManager().removeListener(this.listener);
    }

    public void setup() {
        beginTransaction();
    }

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
            if ((!(((DescriptorEvent)events.firstElement()).getEventCode() == DescriptorEventManager.PreInsertEvent)) || (!(((DescriptorEvent)events.elementAt(2)).getEventCode() == DescriptorEventManager.PostWriteEvent)) || (!(((DescriptorEvent)events.elementAt(1)).getEventCode() == DescriptorEventManager.PostInsertEvent))) {
                throw new TestErrorException("The appropriate aggregate events failed to fire");
            } else {
                if (((DescriptorEvent)events.firstElement()).getSource().getClass() != AddressDescription.class) {
                    throw new TestErrorException("The wrong object was passed into the event");
                }
            }
        }
        events.removeAllElements();
        empClone.setFirstName("Douglas");
        empClone.getAddressDescription().getPeriodDescription().getPeriod().setEndDate(new java.sql.Date(System.currentTimeMillis()));
        uow.commit();
        events = this.listener.getEvents();
        if (events.size() != 3) {// one for the preUpdate and One for the PostUpdate and post Write
            throw new TestErrorException("The appropriate aggregate events failed to fire");
        } else {
            if ((!(((DescriptorEvent)events.firstElement()).getEventCode() == DescriptorEventManager.PreUpdateEvent)) || (!(((DescriptorEvent)events.elementAt(2)).getEventCode() == DescriptorEventManager.PostWriteEvent)) || (!(((DescriptorEvent)events.elementAt(1)).getEventCode() == DescriptorEventManager.PostUpdateEvent))) {
                throw new TestErrorException("The aggregate events failed to fire");
            }
        }
        events.removeAllElements();
        session.deleteObject(emp);
        events = this.listener.getEvents();
        if (events.size() != 2) {// one for the preDelete and One for the Postdelete
            throw new TestErrorException("The appropriate aggregate events failed to fire");
        } else {
            if ((!(((DescriptorEvent)events.firstElement()).getEventCode() == DescriptorEventManager.PreDeleteEvent)) || (!(((DescriptorEvent)events.elementAt(1)).getEventCode() == DescriptorEventManager.PostDeleteEvent))) {
                throw new TestErrorException("The aggregate events failed to fire");
            }
        }
    }

    public void verify() {
    }
}
