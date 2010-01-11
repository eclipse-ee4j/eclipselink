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
package org.eclipse.persistence.testing.tests.transparentindirection;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.models.transparentindirection.IndirectMapProject;
import org.eclipse.persistence.testing.models.transparentindirection.AbstractOrder;
import org.eclipse.persistence.testing.models.transparentindirection.SalesRep;
import org.eclipse.persistence.testing.models.transparentindirection.MappedOrder;
import org.eclipse.persistence.testing.models.transparentindirection.OrderLine;

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

        SalesRep tempSalesRep = (SalesRep)((MappedOrder)this.buildTestOrder1()).salesReps.values().iterator().next();
        this.assertEquals("Invalid sales rep key/value pair.", tempSalesRep.name, ((SalesRep)orderFromDB.salesReps.get(tempSalesRep.getKey())).name);

        OrderLine tempLine = (OrderLine)((MappedOrder)this.buildTestOrder1()).lines.values().iterator().next();
        this.assertEquals("Invalid order line key/value pair.", tempLine.itemName, ((OrderLine)orderFromDB.lines.get(tempLine.getKey())).itemName);
    }
}
