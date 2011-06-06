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
package org.eclipse.persistence.testing.tests.eis.aq;

import java.math.BigDecimal;
import java.util.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.oxm.record.XMLRecord;
import org.eclipse.persistence.queries.InsertObjectQuery;
import org.eclipse.persistence.eis.interactions.*;
import org.eclipse.persistence.testing.models.order.*;
import org.eclipse.persistence.testing.framework.TestCase;

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

        order = new Order();
        order.id = 1234;
        order.orderedBy = "Tom Jones";
        order.address = address;
        order.lineItems = lineItems;

        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.registerObject(order);
        uow.commit();
        done = false;
        received = false;

        // now simulate the server, fork a processing thread.
        Thread thread = new Thread() {
            public void run() {
                DatabaseSession session = getSession().getProject().createDatabaseSession();
                session.setSessionLog(getSession().getSessionLog());
                session.login();
                while (!done) {
                    // read the insert or read request
                    XMLInteraction readCall = new XMLInteraction();
                    readCall.setProperty("queue", "raw_order_queue");
                    readCall.setProperty("schema", "aquser");
                    readCall.setProperty("operation", "dequeue");
                    XMLRecord record = (XMLRecord)session.executeSelectingCall(readCall).get(0);
                    getSession().logMessage(record.toString());
                    received = true;
                    getSession().logMessage(record.getDOM().getLocalName());
                    // check if it is a read
                    if (record.getDOM().getLocalName().equals("read-order")) {
                        // write the order back
                        XMLInteraction insertCall = new XMLInteraction();
                        insertCall.setProperty("queue", "raw_order_queue");
                        insertCall.setProperty("schema", "aquser");
                        insertCall.setProperty("operation", "enqueue");
                        insertCall.setInputRootElementName("response");
                        InsertObjectQuery insert = new InsertObjectQuery(order);
                        insert.setCall(insertCall);
                        session.executeQuery(insert);
                        done = true;
                    }
                    try {
                        Thread.sleep(10);
                    } catch (Exception ignore) {
                    }
                }
            }
        };
        thread.start();

        int count = 0;
        while (!received && count < 1000) {
            try {
                count++;
                Thread.sleep(10);
            } catch (Exception ignore) {
            }
        }

        getSession().getIdentityMapAccessor().initializeIdentityMaps();

        getSession().logMessage(getSession().readObject(order).toString());
        done = true;
    }
}
