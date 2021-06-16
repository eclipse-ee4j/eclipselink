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
public class AggregateInheritanceTestCase extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    private int transportId = 0;

    public void reset() {
        // Remove the transport added for this test
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

        UnitOfWork uow = session.acquireUnitOfWork();

        // add a new transport
        Transport transport = Transport.example1();
        Transport transportClone = (Transport)uow.registerNewObject(transport);
        uow.commitAndResume();

        transportId = transportClone.getId();
        // change the aggregate 'vehicle' to a new value which is a different subclass of vehicle
        transportClone.setVehicle(Bicycle.example1());
        uow.commit();
    }

    public void verify() {
        DatabaseSession session = (DatabaseSession)getSession();
        ExpressionBuilder transport = new ExpressionBuilder();
        Expression expression = transport.get("id").equal(transportId);
        Transport testTransport = Transport.example1();

        // get transport we inserted and compare it's vehicle to the one we added to the database.
        Transport transportFromTL = (Transport)session.readObject(Transport.class, expression);
        if (testTransport.getVehicle().getColour().equals(transportFromTL.getVehicle().getColour()) && (testTransport.getVehicle().getCapacity() == transportFromTL.getVehicle().getCapacity())) {
            throw new TestErrorException("The Aggregate was changed, but the database was not updated.");
        }
    }
}
