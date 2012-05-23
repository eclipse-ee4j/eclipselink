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
 *     Blaise Doughan - 2.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmlpath.predicate.ns;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class XPathsTestCases extends JAXBTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlpath/predicate/ns/xmlpaths.xml";
    private static final String CONTROL_STREET = "123 A Street";
    private static final String CONTROL_STRING = "String";
    private static final String CONTROL_PHONE_NUMBER = "555-1111";

    public XPathsTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {XPathsRoot.class, Address.class});
        setControlDocument(XML_RESOURCE);
    }

    @Override
    protected Object getControlObject() {
        XPathsRoot root = new XPathsRoot();

        Address address = new Address();
        address.setStreet(CONTROL_STREET);
        root.setItem(address);

        root.getItems().add(CONTROL_STRING);
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setValue(CONTROL_PHONE_NUMBER);
        root.getItems().add(phoneNumber);

        return root;
    }

}