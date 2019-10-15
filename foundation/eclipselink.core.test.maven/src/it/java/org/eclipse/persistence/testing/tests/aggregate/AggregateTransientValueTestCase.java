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
package org.eclipse.persistence.testing.tests.aggregate;

import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.aggregate.*;

//Bug#4719341  Always obtain aggregate attribute value from the target object regardless of new or not
public class AggregateTransientValueTestCase extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    private int transportId = 0;
    Transport transport;

    public AggregateTransientValueTestCase() {
        setDescription("Test that transient value in the aggregate object is not reset");
    }

    public void reset() {
        // Remove the transport added for this test
        DatabaseSession session = (DatabaseSession) getSession();
        UnitOfWork uow = session.acquireUnitOfWork();
        ExpressionBuilder transport = new ExpressionBuilder();
        Expression expression = transport.get("id").equal(transportId);
        uow.deleteAllObjects(uow.readAllObjects(Transport.class, expression));
        uow.commit();
    }

    public void setup(){
    }

    public void test() {
        transport = Transport.example6();
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.registerObject(transport);
        uow.commit();

        transportId = transport.getId();

        DatabaseSession session = (DatabaseSession) getSession();
        ExpressionBuilder exp = new ExpressionBuilder();
        Expression expression = exp.get("id").equal(transportId);
        transport = (Transport) session.readObject(Transport.class, expression);
    }

    public void verify(){
        if (transport.getVehicle() == null) {
            throw new TestErrorException("Transport.getVehicle() was null.");
        }
        if (transport.getVehicle().getTransientValue() == null) {
            throw new TestErrorException("TransientValue should not be null.");
        }
    }
}
