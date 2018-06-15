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
//     Matt MacIvor - 2.4.1 - Initial Implementation
package org.eclipse.persistence.testing.jaxb.xmlelements;

import java.util.ArrayList;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class SameFieldCollectionLinkTestCases extends JAXBWithJSONTestCases{

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelements/customer_link_collection.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelements/customer_link_collection.json";

    public SameFieldCollectionLinkTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setClasses(new Class[]{CustomerCollection.class});
    }

    @Override
    protected Object getControlObject() {
        CustomerCollection cust = new CustomerCollection();
        cust.address = new ArrayList<Object>();

        Link link = new Link();
        link.href = "link_location_1";
        cust.address.add(link);

        link = new Link();
        link.href = "link_location_2";
        cust.address.add(link);
        return cust;
    }

    @Override
    public boolean isUnmarshalTest() {
        return false;
    }
}
