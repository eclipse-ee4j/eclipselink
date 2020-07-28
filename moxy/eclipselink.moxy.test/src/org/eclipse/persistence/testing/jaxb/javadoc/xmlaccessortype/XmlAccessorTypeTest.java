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
//     Praba Vijayaratnam - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.javadoc.xmlaccessortype;

import java.util.ArrayList;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class XmlAccessorTypeTest extends JAXBTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/javadoc/xmlaccessortype/xmlaccessortype.xml";

    public XmlAccessorTypeTest(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        Class[] classes = new Class[1];
        classes[0] = Address.class;
        setClasses(classes);
    }

    protected Object getControlObject() {
        Address address = new Address();
        address.setID(101);
        address.lastName="Doe";
        address.city = "Ottawa";
        address.country = "Canada";
        address.street="One Way Street North";
        return address;
    }
}
