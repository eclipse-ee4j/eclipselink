/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Blaise Doughan - 2.4 - initial implementation
package org.eclipse.persistence.testing.jaxb.javadoc.xmlseealso;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlSeeAlsoTest2 extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/javadoc/xmlseealso/xmlseealso2.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/javadoc/xmlseealso/xmlseealso2.json";

    public XmlSeeAlsoTest2(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[1];
        classes[0] = Animal.class;
        setClasses(classes);
    }

    protected Object getControlObject() {
        Cat exampleC = new Cat();
        exampleC.owner = "JANE DOE";
        exampleC.name = "meow";
        exampleC.sleephours = 10;
        return new JAXBElement<Animal>(new QName("animal"), Animal.class, exampleC);
    }

    public Object getReadControlObject() {
        Cat exampleC = new Cat();
        exampleC.owner = "JANE DOE";
        exampleC.name = "meow";
        exampleC.sleephours = 10;
        return exampleC;
    }

    public void testRoundTrip(){};

}
