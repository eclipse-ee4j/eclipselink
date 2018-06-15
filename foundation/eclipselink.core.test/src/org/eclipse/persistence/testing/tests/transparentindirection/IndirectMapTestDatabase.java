/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.transparentindirection;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.models.transparentindirection.AbstractOrderLine;
import org.eclipse.persistence.testing.models.transparentindirection.AbstractSalesRep;
import org.eclipse.persistence.testing.models.transparentindirection.IndirectMapProject;
import org.eclipse.persistence.testing.models.transparentindirection.AbstractOrder;
import org.eclipse.persistence.testing.models.transparentindirection.MappedOrderLine;
import org.eclipse.persistence.testing.models.transparentindirection.MappedSalesRep;
import org.eclipse.persistence.testing.models.transparentindirection.MappedOrder;

/**
 * Test the IndirectMap with assorted DatabaseSessions and UnitsOfWork.
 * @author: Big Country
 */
public class IndirectMapTestDatabase extends IndirectContainerTestDatabase {
    public IndirectMapTestDatabase(String name) {
        super(name);
    }

    protected AbstractOrder buildOrderShell() {
        return new MappedOrder();
    }

    protected AbstractOrder buildTestOrderShell(String customerName) {
        return new MappedOrder(customerName);
    }

    protected AbstractOrderLine newOrderLine(String item, int quanity) {
        return new MappedOrderLine(item, quanity);
    }

    protected AbstractSalesRep newSalesRep(String name) {
        return new MappedSalesRep(name);
    }

    /**
     * build the TopLink project
     */
    public Project setUpProjectFromCode() {
        return new IndirectMapProject();
    }

    public void testKeysAndValues() {
        AbstractOrder key = this.buildOrderShell();
        key.id = originalID;
        MappedOrder orderFromDB = (MappedOrder)getSession().readObject(key);

        AbstractSalesRep tempSalesRep = (AbstractSalesRep)((MappedOrder)this.buildTestOrder1()).salesReps.values().iterator().next();
        this.assertEquals("Invalid sales rep key/value pair.", tempSalesRep.name, ((AbstractSalesRep)orderFromDB.salesReps.get(tempSalesRep.getKey())).name);

        AbstractOrderLine tempLine = (AbstractOrderLine)((MappedOrder)this.buildTestOrder1()).lines.values().iterator().next();
        this.assertEquals("Invalid order line key/value pair.", tempLine.itemName, ((AbstractOrderLine)orderFromDB.lines.get(tempLine.getKey())).itemName);
    }
}
