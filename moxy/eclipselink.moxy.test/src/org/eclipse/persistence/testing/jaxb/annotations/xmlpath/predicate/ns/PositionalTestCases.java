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
//     Blaise Doughan - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlpath.predicate.ns;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class PositionalTestCases extends JAXBTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlpath/predicate/ns/positional.xml";
    private static final String CONTROL_FIRST_NAME = "First";
    private static final String CONTROL_LAST_NAME = "Last";
    private static final String CONTROL_STREET_1 = "123 A Street";
    private static final String CONTROL_STREET_2 = "456 B Road";

    public PositionalTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {PositionalRoot.class});
        setControlDocument(XML_RESOURCE);
    }

    @Override
    protected PositionalRoot getControlObject() {
        PositionalRoot root = new PositionalRoot();

        root.setFirstName(CONTROL_FIRST_NAME);
        root.setLastName(CONTROL_LAST_NAME);

        Address billingAddress = new Address();
        billingAddress.setStreet(CONTROL_STREET_1);
        root.setBillingAddress(billingAddress);

        Address shippingAddress = new Address();
        shippingAddress.setStreet(CONTROL_STREET_2);
        root.setShippingAddress(shippingAddress);

        return root;
    }

    @Override
    public boolean isUnmarshalTest() {
        return false;
    }

}
