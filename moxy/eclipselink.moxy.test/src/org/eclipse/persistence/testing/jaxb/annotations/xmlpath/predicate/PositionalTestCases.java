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
 *     Blaise Doughan - 2.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmlpath.predicate;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class PositionalTestCases extends JAXBTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlpath/predicate/positional.xml";
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