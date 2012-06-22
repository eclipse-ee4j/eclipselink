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
 *     Matt MacIvor - 2.4 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmljoinnode.xmlvalue;

import java.util.ArrayList;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class XmlJoinNodesWithValueTestCases extends JAXBTestCases {

    private static final String XML_RESOURCE="org/eclipse/persistence/testing/jaxb/annotations/xmljoinnode/xmlvalue/root.xml";
    public XmlJoinNodesWithValueTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[]{Root.class, Order.class, Customer.class});
        setControlDocument(XML_RESOURCE);
    }

    @Override
    protected Object getControlObject() {
        Root root = new Root();
        root.customers = new ArrayList<Customer>();
        root.orders = new ArrayList<Order>();
        
        Order order = new Order();
        order.id = "123";
        order.itemId = "456";
        root.orders.add(order);
        
        Customer customer = new Customer();
        customer.info = "value";
        customer.order = order;
        root.customers.add(customer);
        
        return root;
    }

}
