/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Matt MacIvor - 2.4.1 - Initial Implementation
package org.eclipse.persistence.testing.jaxb.xmlelements;

import java.util.ArrayList;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class SameFieldCollectionAddressTestCases extends JAXBWithJSONTestCases{

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelements/customer_address_collection.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelements/customer_address_collection.json";

    public SameFieldCollectionAddressTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setClasses(new Class[]{CustomerCollection.class});
    }

    @Override
    protected Object getControlObject() {
        CustomerCollection cust = new CustomerCollection();
        cust.address = new ArrayList<Object>();
        Address addr = new Address();
        addr.city = "Ottawa";
        addr.street = "123 Ottawa Street";
        cust.address.add(addr);

        addr = new Address();
        addr.city = "Ottawa";
        addr.street = "456 Kanata Street";
        cust.address.add(addr);
        return cust;
    }

    @Override
    public boolean isUnmarshalTest() {
        return false;
    }

}
