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

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.aggregate.AggregateUpdateDescriptorListener;
import org.eclipse.persistence.testing.models.aggregate.Transport;

/**
 * Test to ensure that the type field in an aggregate with inheritance is not updated when it is
 * not changed
 * @author: Tom Ware
 */
public class AggregateInheritanceTypeFieldTestCase extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    public AggregateUpdateDescriptorListener listener = null;
    private int transportId = 0;

    public void reset() {
        // remove the Transport that was added for this test.
        DatabaseSession session = (DatabaseSession)getSession();
        UnitOfWork uow = session.acquireUnitOfWork();
        ExpressionBuilder transport = new ExpressionBuilder();
        Expression expression = transport.get("id").equal(transportId);
        uow.deleteAllObjects(uow.readAllObjects(Transport.class, expression));
        uow.commit();
    }

    public void setup() {
    }

    public void test() {
        DatabaseSession session = (DatabaseSession)getSession();

        // create a listener which will listen for updates
        this.listener = new AggregateUpdateDescriptorListener();
        UnitOfWork uow = session.acquireUnitOfWork();
        Transport transport = Transport.example1();
        Transport transportClone = (Transport)uow.registerNewObject(transport);
        uow.commitAndResume();
        (uow.getProject().getDescriptors().get(Transport.class)).getEventManager().addListener(this.listener);
        transportId = transportClone.getId();
        uow.commit();
        // An update should not occur since we did not change the transport
        if (listener.didUpdateOccur()) {
            throw new TestErrorException("Unexpected update occurred to type field for Aggregate Inheritance");
        }
    }

    public void verify() {
    }
}
