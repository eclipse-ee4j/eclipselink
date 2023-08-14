/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.platform;

import org.eclipse.persistence.platform.xml.XMLPlatform;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.testing.oxm.OXTestCase;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class PlatformNamespaceTestCases extends OXTestCase {
    public PlatformNamespaceTestCases(String name) {
        super(name);
    }

    private Document getDocument() throws Exception{
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        Document document = builder.getDOMImplementation().createDocument("","test",null);
        return document;
    }


    public void testNamespace() throws Exception {
        XMLPlatform xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();

        String prefix = "abc";
        String firstElementName = "first";
        String namespaceURI = "http://www.w3.org/2001/blah";

        Document document = getDocument();
        Node firstElement = document.createElementNS(namespaceURI, prefix+":"+firstElementName);
        document.getDocumentElement().appendChild(firstElement);

        log(document);

        String namespace = xmlPlatform.resolveNamespacePrefix(firstElement, prefix);

        log("\nnamespace returned was: " + namespace);

        assertEquals("Incorrect namespace returned.",namespace,namespaceURI);

    }

    public void testNamespaceOnParent() throws Exception {
        XMLPlatform xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();

        String prefix = "abc";
        String firstElementName = "first";
        String secondElementName = "second";

        String namespaceURI = "http://www.w3.org/2001/blah";

        Document document = getDocument();
        Node firstElement = document.createElementNS(namespaceURI, prefix+":"+firstElementName);
        document.getDocumentElement().appendChild(firstElement);

        Node secondElement = document.createElementNS("", secondElementName);
        firstElement.appendChild(secondElement);

        log(document);

        String namespace = xmlPlatform.resolveNamespacePrefix(secondElement, prefix);
        log("\nnamespace returned was: " + namespace);

        assertEquals("Incorrect namespace returned.",namespace,namespaceURI);

    }

    public void testNamespaceNotDeclared() throws Exception {
        XMLPlatform xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();

        String prefix = "abc";
        String firstElementName = "first";
        String secondElementName = "second";

        Document document = getDocument();
        Node firstElement = document.createElementNS("", firstElementName);
        document.getDocumentElement().appendChild(firstElement);

        Node secondElement = document.createElementNS("", secondElementName);
        firstElement.appendChild(secondElement);

        log(document);

        String namespace = xmlPlatform.resolveNamespacePrefix(secondElement, prefix);
        log("\nnamespace returned was: " + namespace);

        assertNull("Namespace should have been null but wasn't.", namespace);
    }
}
