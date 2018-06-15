/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Blaise Doughan - 2.4.2 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmlanyelement.ns.qualified;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DefaultNamespaceTestCases extends JAXBTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlanyelement/ns/qualified/DefaultNamespace.xml";

    public DefaultNamespaceTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {DefaultNamespaceRoot.class});
        setControlDocument(XML_RESOURCE);
    }

    @Override
    protected DefaultNamespaceRoot getControlObject()  {
        try {
            DefaultNamespaceRoot root = new DefaultNamespaceRoot();

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.newDocument();

            Element noNamespaceElement1 = document.createElement("noNamespaceElement1");
            document.appendChild(noNamespaceElement1);
            root.any = noNamespaceElement1;

            Element defaultNamespace1 = document.createElementNS("http://www.example.com", "defaultNamespace1");
            noNamespaceElement1.appendChild(defaultNamespace1);

            //defaultNamespace1.setAttribute("noNamespaceAttr", "blah");

            Element defaultNamespace2 = document.createElementNS("http://www.example.com", "defaultNamespace2");
            defaultNamespace1.appendChild(defaultNamespace2);

            Element differentDefaultNamespace = document.createElementNS("urn:different", "differentDefaultNamespace");
            defaultNamespace2.appendChild(differentDefaultNamespace);

            Element noNamespaceElement2 = document.createElement("noNamespaceElement2");
            //noNamespaceElement2.setAttributeNS("urn:baz", "baz:att", "Hello World");
            differentDefaultNamespace.appendChild(noNamespaceElement2);

            Element prefixNamespaceElement1 = document.createElementNS("http://www.example.com", "abc:prefixNamespaceElement1");
            noNamespaceElement1.appendChild(prefixNamespaceElement1);

            Element prefixNamespaceElement2 = document.createElementNS("http://www.example.com", "abc:prefixNamespaceElement2");
            prefixNamespaceElement1.appendChild(prefixNamespaceElement2);

            return root;
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isUnmarshalTest() {
        return false;
    }

    @Override
    public void testObjectToXMLStreamWriter() throws Exception {
    }

    @Override
    public void testObjectToXMLStreamWriterRecord() throws Exception {
    }

    @Override
    public void testObjectToXMLEventWriter() throws Exception {
    }

}
