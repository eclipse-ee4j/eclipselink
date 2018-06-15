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
//    Denise Smith - April 2013
package org.eclipse.persistence.testing.jaxb.jaxbelement.subclass;

import javax.xml.namespace.QName;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class JAXBElementSubclassObjectTestCases extends JAXBWithJSONTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/jaxbelement/subclass/foo.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/jaxbelement/subclass/foo.json";

    public JAXBElementSubclassObjectTestCases(String name) throws Exception {
        super(name);
        this.setClasses(new Class[] {Foo.class, ObjectFactoryFoo.class});
        this.setControlDocument(XML_RESOURCE);
        this.setControlJSON(JSON_RESOURCE);
    }

    @Override
    protected Object getControlObject() {
        Document doc = parser.newDocument();
        Element elem = doc.createElementNS("","foo");
        elem.setTextContent("SomeString");
        Foo foo = new Foo(elem);
        return foo;
    }
}
