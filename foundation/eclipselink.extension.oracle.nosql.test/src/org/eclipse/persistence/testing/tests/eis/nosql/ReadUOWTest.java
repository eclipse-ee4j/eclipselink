/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.eis.nosql;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.models.order.Address;
import org.eclipse.persistence.testing.models.order.LineItem;
import org.eclipse.persistence.testing.models.order.Order;

/**
 * Testing reading with request and response and uow transaction.
 */
public class ReadUOWTest extends TestCase {
    public boolean done;
    public boolean received;
    public Order order;

    public ReadUOWTest() {
        setDescription("Testing reading with request and response and uow transaction.");
    }

    public void test() throws Exception {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        
        Address address = new Address();
        address.addressee = "Bob Jones";
        address.street = "123 lane";
        address.city = "Toronto";
        address.state = "Ont";
        address.country = "Canada";
        address.zipCode = "K2C4A4";

        List lineItems = new ArrayList();

        LineItem line = new LineItem();
        line.lineNumber = 1;
        line.itemName = "Wheels";
        line.itemPrice = new BigDecimal("35.99");
        line.quantity = 50;
        lineItems.add(line);

        line = new LineItem();
        line.lineNumber = 2;
        line.itemName = "Axles";
        line.itemPrice = new BigDecimal("135.99");
        line.quantity = 25;
        lineItems.add(line);

        order = new Order();
        order.id = 1234;
        order.orderedBy = "Tom Jones";
        order.address = address;
        order.lineItems = lineItems;

        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.registerObject(order);
        uow.commit();

        getSession().getIdentityMapAccessor().initializeIdentityMaps();

        Order dbOrder = (Order)getSession().readObject(order);
        if ((dbOrder == null) || !dbOrder.address.city.equals(order.address.city)) {
            fail("Order not returned properly: " + dbOrder);
        }
    }
}
