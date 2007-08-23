/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.queries.optimization;

import java.util.*;

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.legacy.*;

/**
 * This test checks the outcome of modifying a one to many mapping query to a custom user defined query
 * (that performs a batch read and does a join)
 */
public class OneToManyBatchReadingCustomSelectionQueryTest extends TestCase {
    public java.util.Vector shipments;

    public OneToManyBatchReadingCustomSelectionQueryTest() {
        setDescription("Tests a one to many mapping using a custom selction query to perform batch reading and a join ");
    }

    public void reset() {
        getAbstractSession().rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void setup() {

        getAbstractSession().beginTransaction();

    }

    public void test() {

        ReadAllQuery q = new ReadAllQuery();
        q.setReferenceClass(Shipment.class);
        q.addBatchReadAttribute("orders");
        UnitOfWork uow = getSession().acquireUnitOfWork();
        shipments = (java.util.Vector)uow.executeQuery(q);
    }

    public void verify() {
        Enumeration enumtr = shipments.elements();
        while (enumtr.hasMoreElements()) {
            Shipment shipment = (Shipment)enumtr.nextElement();
            if (shipment.orders.size() == 0) {
                throw new TestErrorException("Test failed. Batched objects were not read");
            }
        }
    }
}
