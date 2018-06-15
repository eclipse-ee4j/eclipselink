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
package org.eclipse.persistence.testing.jaxb.javadoc.xmlelements;

import java.util.List;
import java.util.ArrayList;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlElementsListOfElementWrappedTest extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/javadoc/xmlelements/xmlelementslistwrapped.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/javadoc/xmlelements/xmlelementslistwrapped.json";

    private final static int CONTROL_ID = 10;

    public XmlElementsListOfElementWrappedTest(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[1];
        classes[0] = XmlElementsListOfElementWrapped.class;
        setClasses(classes);
    }

    protected Object getControlObject() {
        XmlElementsListOfElementWrapped example = new XmlElementsListOfElementWrapped();
        example.items = new ArrayList();
        example.items.add(new Integer(1));
        example.items.add(new Float(2.5));
        return example;
    }
}
