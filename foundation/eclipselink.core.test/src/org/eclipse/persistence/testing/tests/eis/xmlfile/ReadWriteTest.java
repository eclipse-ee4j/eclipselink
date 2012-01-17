/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.eis.xmlfile;

import java.math.BigDecimal;
import java.util.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.eis.interactions.*;
import org.eclipse.persistence.internal.eis.adapters.xmlfile.*;
import org.eclipse.persistence.testing.models.order.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * Testing reading and writing.
 */
public class ReadWriteTest extends AutoVerifyTestCase {
    public ReadWriteTest() {
        setName("ReadWriteTest");
        setDescription("Testing reading and writing the order model.");
    }

    public void setup() {
        // Reset the databse (delete xml file).
        XMLInteraction deleteCall = new XMLInteraction();
        deleteCall.setFunctionName("delete-file");
        XMLFileInteractionSpec spec = new XMLFileInteractionSpec();
        spec.setInteractionType(XMLFileInteractionSpec.DELETE);
        spec.setFileName("order.xml");
        deleteCall.setInteractionSpec(spec);
        getSession().executeNonSelectingCall(deleteCall);
    }

    public void test() throws Exception {
        DatabaseSession session = (DatabaseSession)getSession();

        Address address = new Address();
        address.addressee = "Bob Jones";
        address.street = "123 lane";
        address.city = "Ottawa";
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

        Order order = new Order();
        order.id = 123;
        order.orderedBy = "Tom Jones";
        order.address = address;
        order.lineItems = lineItems;

        session.insertObject(order);

        session.getIdentityMapAccessor().initializeIdentityMaps();
        order = (Order)session.readObject(order);
        session.logMessage(String.valueOf(order));
        if ((order == null) || (order.id != 123)) {
            throw new TestErrorException("order not read back properly");
        }

        List orders = session.readAllObjects(org.eclipse.persistence.testing.models.order.Order.class);
        session.logMessage(String.valueOf(orders));
        if (orders.size() != 1) {
            throw new TestErrorException("should be 1 orders");
        }

        session.deleteObject(order);
        orders = session.readAllObjects(org.eclipse.persistence.testing.models.order.Order.class);
        session.logMessage(String.valueOf(orders));
        if (orders.size() != 0) {
            throw new TestErrorException("should be 0 orders");
        }

        session.insertObject(order);

        order = new Order();
        order.id = 456;
        order.orderedBy = "Bob Baggins";

        session.insertObject(order);

        orders = session.readAllObjects(org.eclipse.persistence.testing.models.order.Order.class);
        session.logMessage(String.valueOf(orders));
        if (orders.size() != 2) {
            throw new TestErrorException("should be 2 orders");
        }

        order.orderedBy = "Frodo Baggins";
        order.lineItems = new ArrayList();

        line = new LineItem();
        line.lineNumber = 1;
        line.itemName = "Wheels";
        line.itemPrice = new BigDecimal("35.99");
        line.quantity = 50;
        order.lineItems.add(line);

        session.updateObject(order);

        session.getIdentityMapAccessor().initializeIdentityMaps();
        order = (Order)session.readObject(order);
        session.logMessage(String.valueOf(order));
        if ((order == null) || (order.lineItems.size() != 1) || (!order.orderedBy.equals("Frodo Baggins"))) {
            throw new TestErrorException("order orderedBy should be 'Frodo Baggins' and should have 1 line item:" + order);
        }
    }
}
