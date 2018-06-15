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
//     Praba Vijayaratnam - 2.4 - initial implementation
package org.eclipse.persistence.testing.jaxb.javadoc.xmltype;

// Example 3

import java.io.IOException;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlTypeAnonymousLocalElementTest extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/javadoc/xmltype/xmltypeexample4.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/javadoc/xmltype/xmltypeexample4.json";

    public XmlTypeAnonymousLocalElementTest(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[2];
        classes[0] = Address4.class;
        classes[1] = Invoice.class;
        setClasses(classes);
    }

    protected Object getControlObject() {
        Address4 addr4 = new Address4();
        addr4.setName("JOE");
        addr4.setCity("OTTAWA");
        addr4.setState("ON");
        addr4.setStreet("123 ONE WAY STREET NORTH");
        Invoice inv = new Invoice();
        inv.addr = addr4;
        return inv;
    }

}
