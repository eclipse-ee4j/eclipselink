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
// Denise Smith - October 2012
package org.eclipse.persistence.testing.jaxb.binder.xmlanyelement;

import java.io.InputStream;

import javax.xml.bind.Binder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.sun.istack.Builder;

public class XMLBinderTestCases extends JAXBWithJSONTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/binder/xmlanyelement/input.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/binder/xmlanyelement/input.json";

    public XMLBinderTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setClasses(new Class[]{Input.class});
    }

    public void testXMLBinder() throws Exception{
        InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
        Document document = parser.parse(instream);

        Binder<Node> binder = jaxbContext.createBinder(Node.class);
        Object testObject =  binder.unmarshal(document);

        xmlToObjectTest(testObject);

        Node updated = binder.updateXML(testObject);

        Document doc = parser.newDocument();
        Node imported = doc.importNode(updated, true);
        doc.appendChild(imported);
        objectToXMLDocumentTest(doc);

    }


    @Override
    protected Object getControlObject() {
        Input input = new Input();
        Document doc = parser.newDocument();
        Element newElement = doc.createElement("unmapped");
        doc.appendChild(newElement);
        input.elements = new Object[1];
        input.elements[0] = newElement;
        return input;
    }

}
