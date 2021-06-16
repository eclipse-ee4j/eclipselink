/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Praba Vijayaratnam - 2.4 - initial implementation
package org.eclipse.persistence.testing.jaxb.javadoc.xmltype;

// Example 3

import java.io.IOException;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlTypeAnonymousTypeTest extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/javadoc/xmltype/xmltypeexample3.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/javadoc/xmltype/xmltypeexample3.json";

    public XmlTypeAnonymousTypeTest(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[1];
        classes[0] = Address3.class;
        setClasses(classes);
    }

    protected Object getControlObject() {
        Address3 addr = new Address3();
        addr.setName("JOE");
        addr.setCity("OTTAWA");
        addr.setState("ON");
        addr.setStreet("123 ONE WAY STREET NORTH");
        return addr;
    }

}
