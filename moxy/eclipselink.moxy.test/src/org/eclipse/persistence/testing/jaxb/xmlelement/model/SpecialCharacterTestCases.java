/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Blaise Doughan - 2.4 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmlelement.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class SpecialCharacterTestCases extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelement/model/special-character.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelement/model/special-character.json";

    public SpecialCharacterTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[]{Order.class});
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }

    protected Object getControlObject(){
        Order o = new Order();
        String[] comments = new String[]{"com<ment1","com&ment2","com\"ment3"};
        o.setComments(comments);
        o.setId(1);

        Customer customer = new Customer();
        customer.setFirstName("Ja<ne");
        customer.setLastName("Smi&th");
        Address address = new Address();
        address.setStreet("Main\"Street");
        address.setCity("Ottawa");
        customer.setAddress(address);
        o.setCustomer(customer);

        List<Item> items = new ArrayList<Item>();
        o.setItems(items);

        return o;
    }

}
