/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.xpathengine;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.internal.oxm.XPathEngine;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XPathEngineRemoveTestCases extends org.eclipse.persistence.testing.oxm.XMLTestCase {

    private Document controlDocument;
    private Element searchNode;

    public XPathEngineRemoveTestCases(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/oxm/xpathengine/AddressBook.xml");
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder parser = builderFactory.newDocumentBuilder();
        controlDocument = parser.parse(inputStream);

        searchNode = (Element) controlDocument.getDocumentElement();
    }

    // ========================================================================================

    public void testRemoveElement() throws Exception {
        Element backupNode = (Element) searchNode.cloneNode(true);
        String fieldName = "address/phone";
        XMLField field = new XMLField(fieldName);
        XPathEngine.getInstance().remove(field, searchNode);

        NodeList verifyNodes = searchNode.getElementsByTagName("phone");

        assertTrue("XPath did not remove all of the elements.", verifyNodes.getLength() == 0);

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
            assertFalse("XPath did not remove the element.", areaCode.equals("214"));
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

        assertTrue("XPath did not truly remove the element.", verifyNodes.getLength() == 4);
        for (int i = 0; i < verifyNodes.getLength(); i++) {
            Element nextNode = (Element) verifyNodes.item(i);
            String areaCode = nextNode.getAttribute("area-code");
            assertFalse("XPath did not remove the element.", areaCode.equals("214"));
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
            assertFalse("XPath did not remove the element.", areaCode.equals("555-7298"));
            assertFalse("XPath did not remove the element.", areaCode.equals("212-555-1530"));
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
            assertTrue("XPath did not remove all of the attributes.", typeAttribute == null);
        }

        searchNode = backupNode;
    }

    // ==========================================================================================

    public void testRemoveReturnedNodeList() throws Exception {
        Element backupNode = (Element) searchNode.cloneNode(true);
        String fieldName = "address/phone";
        XMLField field = new XMLField(fieldName);
        NodeList nodes = org.eclipse.persistence.internal.oxm.XPathEngine.getInstance().remove(field, searchNode);

        assertTrue("XPathEngine did not return a correct NodeList.", nodes.getLength() == 5);

        searchNode = backupNode;
    }

}
