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
 *     Gyorke - bug 284305 aggregate type change not merged
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.aggregate;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.aggregate.Bicycle;
import org.eclipse.persistence.testing.models.aggregate.Transport;

/**
 * Test to make sure that the appropriate update is made when an aggregate using inheriance is
 * changed from one subclass to another.
 * @author Tom Ware
 */
public class AggregateInheritanceInitAggregateTestCase extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    protected Transport workingCopy;

    public void reset() {
        // Remove the transport added for this test
        DatabaseSession session = (DatabaseSession)getSession();
        UnitOfWork uow = session.acquireUnitOfWork();
        ExpressionBuilder transport = new ExpressionBuilder();
        Expression expression = transport.get("id").equal(this.workingCopy.getId());
        uow.deleteAllObjects(uow.readAllObjects(Transport.class, expression));
        uow.commit();
    }

    public void setup() {
    }

    public void test() {
        DatabaseSession session = (DatabaseSession)getSession();

        UnitOfWork uow = session.acquireUnitOfWork();

        // add a new transport
        Transport transport = Transport.example1();
        this.workingCopy = (Transport)uow.registerObject(transport);
        this.workingCopy.setVehicle(Bicycle.example1());
        uow.commit();
    }

    public void verify() {
        DatabaseSession session = (DatabaseSession)getSession();
        ExpressionBuilder transport = new ExpressionBuilder();
        Expression expression = transport.get("id").equal(this.workingCopy.getId());

        // get transport we merged.
        Transport transportFromTL = (Transport)session.readObject(Transport.class, expression);
        if (! compareObjects(transportFromTL, workingCopy)) {
            throw new TestErrorException("The Aggregate was changed by type, but the cache was not updated.");
        }
    }
}
