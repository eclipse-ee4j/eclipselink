/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.4 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlelement.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class EmptyCollectionTestCases extends JAXBWithJSONTestCases{

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelement/model/empty-collection.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelement/model/empty-collection.json";

    public EmptyCollectionTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[]{Order.class});
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }

    protected Object getControlObject(){
        Order o = new Order();
        String[] comments = new String[0];
        o.setComments(comments);
        o.setId(1);

        Customer customer = new Customer();
        customer.setFirstName("Jane");
        customer.setLastName("Smith");
        Address address = new Address();
        address.setStreet("Main Street");
        address.setCity("Ottawa");
        customer.setAddress(address);
        o.setCustomer(customer);

        List<Item> items = new ArrayList<Item>();
        o.setItems(items);

        return o;
    }

}