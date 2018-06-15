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
// Matt MacIvor - July 4th 2011
package org.eclipse.persistence.testing.jaxb.xmlmixed;

import java.util.ArrayList;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlMixedTestCases extends JAXBTestCases { // extends JAXBWithJSONTestCases

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlmixed/root.xml";
    private final static String XML_RESOURCE_WRITE = "org/eclipse/persistence/testing/jaxb/xmlmixed/root-write.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlmixed/root.json";

    public XmlMixedTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {Root.class});
        setControlDocument(XML_RESOURCE);
        setWriteControlDocument(XML_RESOURCE_WRITE);
        //setControlJSON(JSON_RESOURCE);
        jaxbMarshaller.setProperty(MarshallerProperties.JSON_ATTRIBUTE_PREFIX, "@");
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.JSON_ATTRIBUTE_PREFIX, "@");
    }

    protected Object getControlObject() {
        Root root = new Root();
        root.setAttr("attribute value");
        root.setElem("element value");
        root.setObjects(new ArrayList<Object>());
        root.getObjects().add("Text Value");
        root.getObjects().add("Text Value2");
        return root;
    }

    public Object getReadControlObject() {
        Root root = new Root();
        root.setAttr("attribute value");
        root.setElem("element value");
        root.setObjects(new ArrayList<Object>());
        root.getObjects().add("Text ValueText Value2");
        return root;
    }

    protected Object getJSONReadControlObject() {
       return getControlObject();
    }

    public void testObjectToXMLDocument() throws Exception {
    }

}
