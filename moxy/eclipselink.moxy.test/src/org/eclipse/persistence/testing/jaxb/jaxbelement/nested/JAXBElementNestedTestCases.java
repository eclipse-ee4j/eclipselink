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
//     Denise Smith - July 15, 2009

package org.eclipse.persistence.testing.jaxb.jaxbelement.nested;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class JAXBElementNestedTestCases extends JAXBWithJSONTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/jaxbelement/nested/root.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/jaxbelement/nested/root.json";

    public JAXBElementNestedTestCases(String name) throws Exception {
        super(name);
        Class[] classes = new Class[2];
        classes[0] = ObjectFactory.class;
        classes[1] = Root.class;
        setClasses(classes);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }

    public Object getWriteControlObject() {
        Root root = new Root();

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        builderFactory.setIgnoringElementContentWhitespace(true);
        Document doc;
        try {
            doc = builderFactory.newDocumentBuilder().newDocument();
            Element elem = doc.createElementNS("someuri", "ns0:elem2");
            QName qname = new QName("someuri", "elem2");

            JAXBElement jaxbElement = new JAXBElement(qname, Object.class, elem);
            root.setElem1(jaxbElement);

        } catch (Exception e) {
            fail("An exception occurred in getControlObject");
            e.printStackTrace();
        }
        return root;
    }

    public Object getControlObject() {
        Root root = new Root();

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        builderFactory.setIgnoringElementContentWhitespace(true);
        Document doc;
        try {
            doc = builderFactory.newDocumentBuilder().newDocument();

            Element elem = doc.createElementNS("someuri", "ns0:elem2");
            elem.setAttributeNS(javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI,javax.xml.XMLConstants.XMLNS_ATTRIBUTE + ":ns0", "someuri");

            QName qname = new QName("someuri", "elem2");

            JAXBElement jaxbElement = new JAXBElement(qname, Object.class, elem);
            root.setElem1(jaxbElement);

        } catch (Exception e) {
            fail("An exception occurred in getControlObject");
            e.printStackTrace();
        }
        return root;
    }

    public Object getJSONReadControlObject() {
        //no namespace info
        Root root = new Root();

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        builderFactory.setIgnoringElementContentWhitespace(true);
        Document doc;
        try {
            doc = builderFactory.newDocumentBuilder().newDocument();

            Element elem = doc.createElement("elem2");

            QName qname = new QName("someuri", "elem2");

            JAXBElement jaxbElement = new JAXBElement(qname, Object.class, elem);
            root.setElem1(jaxbElement);

        } catch (Exception e) {
            fail("An exception occurred in getControlObject");
            e.printStackTrace();
        }
        return root;
    }

}
