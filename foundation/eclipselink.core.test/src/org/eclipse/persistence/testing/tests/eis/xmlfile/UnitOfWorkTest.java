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

import java.util.*;
import java.math.BigDecimal;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.eis.interactions.*;
import org.eclipse.persistence.internal.eis.adapters.xmlfile.*;
import org.eclipse.persistence.testing.models.order.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * Testing unit of work. Testing unit of work with the XML file adapter.
 */
public class UnitOfWorkTest extends TestCase {
    public UnitOfWorkTest() {
        setName("UnitOfWorkTest");
        setDescription("Testing unit of work with the XML file adapter.");
    }

    public void setup() {
        // Reset the databse (delete xml file).
        XMLInteraction deleteCall = new XMLInteraction();
        deleteCall.setFunctionName("delete-file");
        XMLFileInteractionSpec spec = new XMLFileInteractionSpec();
        spec.setInteractionType(XMLFileInteractionSpec.DELETE);
        spec.setFileName("orders.xml");
        deleteCall.setInteractionSpec(spec);
        getSession().executeNonSelectingCall(deleteCall);
    }

    public void test() throws Exception {
        Session session = getSession();
        session.getIdentityMapAccessor().initializeIdentityMaps();

        UnitOfWork uow = session.acquireUnitOfWork();

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
        order.id = 456;
        order.orderedBy = "Tom Jones";
        order.address = address;
        order.lineItems = lineItems;
        session.logMessage(String.valueOf(order));

        order = (Order)uow.registerObject(order);
        session.logMessage(String.valueOf(order));
        uow.commit();

        session.getIdentityMapAccessor().initializeIdentityMaps();
        uow = session.acquireUnitOfWork();
        order = (Order)uow.readObject(order);
        session.logMessage(String.valueOf(order));
        uow.commit();

        session.getIdentityMapAccessor().initializeIdentityMaps();
        uow = session.acquireUnitOfWork();
        order = (Order)uow.readObject(order);
        order.orderedBy = "John";
        line = new LineItem();
        line.lineNumber = 3;
        line.itemName = "Wheels";
        line.itemPrice = new BigDecimal("35.99");
        line.quantity = 50;
        order.lineItems.add(line);
        session.logMessage(String.valueOf(order));
        uow.commit();

        order = (Order)session.readObject(order);
        session.logMessage(String.valueOf(order));
        if ((order == null) || (order.lineItems.size() != 3) || (!order.orderedBy.equals("John"))) {
            throw new TestErrorException("merge did not occur correctly");
        }

        session.getIdentityMapAccessor().initializeIdentityMaps();
        uow = session.acquireUnitOfWork();
        order = (Order)uow.readObject(order);
        if ((order == null) || (order.lineItems.size() != 3) || (!order.orderedBy.equals("John"))) {
            throw new TestErrorException("update did not occur correctly");
        }
        uow.deleteObject(order);
        uow.commit();
    }
}
