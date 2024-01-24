/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.oxm.xpathengine;

import org.eclipse.persistence.internal.oxm.XPathEngine;
import org.eclipse.persistence.oxm.XMLField;
import org.junit.Assert;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;

public class XPathEngineRemoveTestCases extends org.eclipse.persistence.testing.oxm.XMLTestCase {

    private Document controlDocument;
    private Element searchNode;

    public XPathEngineRemoveTestCases(String name) {
        super(name);
    }

    @Override
    public void setUp() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/oxm/xpathengine/AddressBook.xml");
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder parser = builderFactory.newDocumentBuilder();
        controlDocument = parser.parse(inputStream);

        searchNode = controlDocument.getDocumentElement();
    }

    // ========================================================================================

    public void testRemoveElement() throws Exception {
        Element backupNode = (Element) searchNode.cloneNode(true);
        String fieldName = "address/phone";
        XMLField field = new XMLField(fieldName);
        XPathEngine.getInstance().remove(field, searchNode);

        NodeList verifyNodes = searchNode.getElementsByTagName("phone");

        assertEquals("XPath did not remove all of the elements.", 0, verifyNodes.getLength());

        searchNode = backupNode;
    }

    // ==========================================================================================

    public void testRemoveIndexedElement() throws Exception {
        Element backupNode = (Element) searchNode.cloneNode(true);
        String fieldName = "address/phone[3]";
        XMLField field = new XMLField(fieldName);
        XPathEngine.getInstance().remove(field, searchNode);

        NodeList verifyNodes = searchNode.getElementsByTagName("phone");

        for (int i = 0; i < verifyNodes.getLength(); i++) {
            Element nextNode = (Element) verifyNodes.item(i);
            String areaCode = nextNode.getAttribute("area-code");
            Assert.assertNotEquals("XPath did not remove the element.", "214", areaCode);
        }
        searchNode = backupNode;
    }

    // ==========================================================================================

    public void testRemoveIndexedElementsForced() throws Exception {
        Element backupNode = (Element) searchNode.cloneNode(true);
        String fieldName = "address/phone[3]";
        XMLField field = new XMLField(fieldName);
        XPathEngine.getInstance().remove(field, searchNode, true);

        NodeList verifyNodes = searchNode.getElementsByTagName("phone");

        assertEquals("XPath did not truly remove the element.", 4, verifyNodes.getLength());
        for (int i = 0; i < verifyNodes.getLength(); i++) {
            Element nextNode = (Element) verifyNodes.item(i);
            String areaCode = nextNode.getAttribute("area-code");
            Assert.assertNotEquals("XPath did not remove the element.", "214", areaCode);
        }

        searchNode = backupNode;
    }

    // ==========================================================================================

    public void testRemoveMultipleIndexedElements() throws Exception {
        Element backupNode = (Element) searchNode.cloneNode(true);
        String fieldName = "address/phone[2]";
        XMLField field = new XMLField(fieldName);
        XPathEngine.getInstance().remove(field, searchNode);

        NodeList verifyNodes = searchNode.getElementsByTagName("phone");
        for (int i = 0; i < verifyNodes.getLength(); i++) {
            Element nextNode = (Element) verifyNodes.item(i);
            String areaCode = nextNode.getAttribute("area-code");
            Assert.assertNotEquals("XPath did not remove the element.", "555-7298", areaCode);
            Assert.assertNotEquals("XPath did not remove the element.", "212-555-1530", areaCode);
        }

        searchNode = backupNode;
    }

    // ==========================================================================================

    public void testRemoveAttribute() throws Exception {
        Element backupNode = (Element) searchNode.cloneNode(true);
        String fieldName = "address/@type";
        XMLField field = new XMLField(fieldName);
        XPathEngine.getInstance().remove(field, searchNode);

        NodeList verifyNodes = searchNode.getElementsByTagName("address");
        for (int i = 0; i < verifyNodes.getLength(); i++) {
            Element nextElement = (Element) verifyNodes.item(i);
            Attr typeAttribute = nextElement.getAttributeNode("type");
            assertNull("XPath did not remove all of the attributes.", typeAttribute);
        }

        searchNode = backupNode;
    }

    // ==========================================================================================

    public void testRemoveReturnedNodeList() throws Exception {
        Element backupNode = (Element) searchNode.cloneNode(true);
        String fieldName = "address/phone";
        XMLField field = new XMLField(fieldName);
        NodeList nodes = org.eclipse.persistence.internal.oxm.XPathEngine.getInstance().remove(field, searchNode);

        assertEquals("XPathEngine did not return a correct NodeList.", 5, nodes.getLength());

        searchNode = backupNode;
    }

}
