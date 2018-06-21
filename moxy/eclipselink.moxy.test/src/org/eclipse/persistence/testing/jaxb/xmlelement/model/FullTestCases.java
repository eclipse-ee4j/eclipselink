/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.4 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmlelement.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class FullTestCases extends JAXBWithJSONTestCases{

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelement/model/full.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelement/model/full.json";

    public FullTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[]{Order.class});
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }

    protected Object getControlObject(){
        Order o = new Order();
        String[] comments = new String[]{"comment1","comment2","comment3", ""};
        o.setComments(comments);
        o.setId(1);
        o.someClass = Integer.class;

        Customer customer = new Customer();
        customer.setFirstName("Jane");
        customer.setLastName("Smith");
        Address address = new Address();
        address.setId("1");
        address.setStreet("Main Street");
        address.setCity("Ottawa");
        address.setCoords(new double[] { 1.111, 2.222 });
        customer.setAddress(address);

        ArrayList<Integer> orderNums = new ArrayList<Integer>();
        orderNums.add(10);
        orderNums.add(20);
        orderNums.add(30);

        customer.setOrderNumbers(orderNums);
        o.setCustomer(customer);

        List<Item> items = new ArrayList<Item>();
        Item item1 = new Item();
        item1.setCost(new BigDecimal("5.00"));
        item1.setPrice(new BigDecimal("7.99"));
        item1.setId(1);
        item1.setDescription(new String[]{"nice item","comes in blue or red"});

        Item item2 = new Item();
        item2.setCost(new BigDecimal("10.00"));
        item2.setPrice(new BigDecimal("15.99"));
        item2.setId(2);
        item2.setDescription(new String[]{"big item","very heavy"});

        items.add(item1);
        items.add(item2);

        o.setItems(items);

        return o;
    }

}
