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

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.oxm.XPathEngine;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.platform.xml.XMLPlatformException;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.platform.xml.XMLTransformer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

public class XPathEngineCreateTestCases extends org.eclipse.persistence.testing.oxm.XMLTestCase {

    private Document controlDocument;
    private Element searchNode;
    private AbstractSession session;

    public XPathEngineCreateTestCases(String name) {
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

        XMLContext ctx = new XMLContext(new org.eclipse.persistence.testing.oxm.xmlmarshaller.XMLMarshallerTestProject());
        session = (AbstractSession) ctx.getSession(0);
    }

    // ==========================================================================================

    public void testCreateElement() throws Exception {
        Element backupNode = (Element) searchNode.cloneNode(true);

        String fieldName = "newElement";
        XMLField field = new XMLField(fieldName);
        XPathEngine.getInstance().create(field, searchNode, session);
        XPathEngine.getInstance().create(field, searchNode, session);

        NodeList verifyNodes = searchNode.getElementsByTagName(fieldName);

        assertTrue("XPath failed to create the element.", verifyNodes.getLength() > 0);

        assertEquals("XPath failed to create the correct number of elements.", 2, verifyNodes.getLength());

        searchNode = backupNode;
    }

    // ==========================================================================================

    public void testCreateElementWithValue() throws Exception {
        Element backupNode = (Element) searchNode.cloneNode(true);

        String newElementValue = "newValue";

        String fieldName = "newElement";
        XMLField field = new XMLField(fieldName);
        Node createdNode = XPathEngine.getInstance().create(field, searchNode, newElementValue, session);

        NodeList verifyNodes = searchNode.getElementsByTagName(fieldName);

        assertNotNull("XPath failed to create the element.", verifyNodes);
        assertEquals("XPath failed to create the element.", 1, verifyNodes.getLength());

        assertEquals("New element's value was not created.", verifyNodes.item(0).getFirstChild().getNodeValue(), newElementValue);

        searchNode = backupNode;
    }

    // ==========================================================================================
    public void testCreateElementWithNullValue() throws Exception {
        Element backupNode = (Element) searchNode.cloneNode(true);
        ArrayList value = new ArrayList();

        String fieldName = "newElement";
        XMLField field = new XMLField(fieldName);
        Node createdNode = XPathEngine.getInstance().create(field, searchNode, value, session);

        NodeList verifyNodes = searchNode.getElementsByTagName(fieldName);

        assertNull("XPath created the element and should not have.", createdNode);
        assertEquals("XPath created the element and should not have.", 0, verifyNodes.getLength());

        searchNode = backupNode;
    }

    // ==========================================================================================
    public void testCreateElementWithChildElements() throws Exception {
        Element backupNode = (Element) searchNode.cloneNode(true);

        String fieldName = "newElement/newChild";
        XMLField field = new XMLField(fieldName);
        XPathEngine.getInstance().create(field, searchNode, session);
        XPathEngine.getInstance().create(field, searchNode, session);

        NodeList verifyNodes = searchNode.getElementsByTagName("newChild");

        assertEquals("XPath failed to create the correct number of child elements.", 2, verifyNodes.getLength());

        searchNode = backupNode;
    }

    // ==========================================================================================

    public void testCreateElementWithElementValue() throws Exception {
        Element backupNode = (Element) searchNode.cloneNode(true);

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        Document document = builderFactory.newDocumentBuilder().newDocument();

        Element elementValue = document.createElement("newChildWithElementValue");
        Element elementValueChild = document.createElement("newChildElementValue");

        Text textNode = document.createTextNode("theValue");
        elementValueChild.appendChild(textNode);
        elementValue.appendChild(elementValueChild);

        String fieldName = "newElement/newChildWithElementValue";
        XMLField field = new XMLField(fieldName);

        Node created = XPathEngine.getInstance().create(field, searchNode, elementValue, session);
        NodeList verifyNodes = searchNode.getElementsByTagName("newChildWithElementValue");

        assertNotNull("XPath failed to create the child elements.", verifyNodes);
        assertEquals("XPath failed to create the correct number of child elements.", 1, verifyNodes.getLength());

        StringWriter stringWriter1 = new StringWriter();
        PrintWriter writer1 = new PrintWriter(stringWriter1);
        try {
            XMLTransformer xmlTransformer = XMLPlatformFactory.getInstance().getXMLPlatform().newXMLTransformer();
            xmlTransformer.transform(elementValue, writer1);
        } catch (XMLPlatformException exception) {
            fail("An XMLPlatformException was thrown");
            return;
        }

        StringWriter stringWriter2 = new StringWriter();
        PrintWriter writer2 = new PrintWriter(stringWriter2);
        try {
            XMLTransformer xmlTransformer = XMLPlatformFactory.getInstance().getXMLPlatform().newXMLTransformer();
            xmlTransformer.transform(verifyNodes.item(0), writer2);
        } catch (XMLPlatformException exception) {
            fail("An XMLPlatformException was thrown");
            return;
        }

        assertEquals(stringWriter1.toString(), stringWriter2.toString());
        searchNode = backupNode;
    }

    // ==========================================================================================

    public void testCreateIndexedElement() throws Exception {
        Element backupNode = (Element) searchNode.cloneNode(true);

        String fieldName = "newIndexedElement[10]";
        XMLField field = new XMLField(fieldName);
        XPathEngine.getInstance().create(field, searchNode, session);

        NodeList verifyNodes = searchNode.getElementsByTagName("newIndexedElement");

        searchNode = backupNode;
    }

    // ==========================================================================================

    public void testCreateIndexedElementWithValue() throws Exception {
        Element backupNode = (Element) searchNode.cloneNode(true);

        String fieldName = "newIndexedElement[10]";
        XMLField field = new XMLField(fieldName);

        String newElementValue = "newValue";

        Node createdNode = XPathEngine.getInstance().create(field, searchNode, newElementValue, session);

        NodeList verifyNodes = searchNode.getElementsByTagName("newIndexedElement");

        assertNotNull("XPath failed to create the element.", verifyNodes);
        assertEquals("XPath failed to create the correct number of elements.", 10, verifyNodes.getLength());
        Node verifyNode = verifyNodes.item(9);
        assertEquals("New element's value was not created.", verifyNode.getFirstChild().getNodeValue(), newElementValue);

        searchNode = backupNode;
    }

    // ==========================================================================================

    public void testCreateIndexedElementWithValueAlreadyExists() throws Exception {
        Element backupNode = (Element) searchNode.cloneNode(true);

        String newElementValue = "newValue";

        String fieldName = "newIndexedElement[10]";
        XMLField field = new XMLField(fieldName);

        Node createdNodeExisting = XPathEngine.getInstance().create(field, searchNode, newElementValue, session);
        Node createdNode = XPathEngine.getInstance().create(field, searchNode, newElementValue, session);

        NodeList verifyNodes = searchNode.getElementsByTagName("newIndexedElement");

        assertNotNull("XPath failed to create the element.", verifyNodes);
        assertEquals("XPath failed to create the correct number of elements.", 10, verifyNodes.getLength());

        Node verifyNode = verifyNodes.item(9);
        assertEquals("New element's value was not created.", verifyNode.getFirstChild().getNodeValue(), newElementValue);

        searchNode = backupNode;
    }

    // ==========================================================================================

    public void testCreateIndexedElementWithChildElements() throws Exception {
        Element backupNode = (Element) searchNode.cloneNode(true);

        String fieldName = "newIndexedElement[1]/newChild";
        XMLField field = new XMLField(fieldName);
        XPathEngine.getInstance().create(field, searchNode, session);
        XPathEngine.getInstance().create(field, searchNode, session);

        NodeList parentNodes = searchNode.getElementsByTagName("newIndexedElement");
        Element indexedElement = (Element) parentNodes.item(0);

        NodeList verifyNodes = indexedElement.getElementsByTagName("newChild");

        assertEquals("XPath failed to create the correct number of child elements.", 2, verifyNodes.getLength());

        searchNode = backupNode;
    }

    // ==========================================================================================

    public void testCreateAttribute() throws Exception {
        Element backupNode = (Element) searchNode.cloneNode(true);

        String fieldName = "address/@newAttribute";
        XMLField field = new XMLField(fieldName);
        XPathEngine.getInstance().create(field, searchNode, session);

        NodeList verifyNodes = searchNode.getElementsByTagName("address");
        Element verifyNode = (Element) verifyNodes.item(0);
        verifyNode.getAttributeNode("newAttribute");

        assertNotNull("XPath failed to create the attribute.", verifyNode);

        searchNode = backupNode;
    }

    // ==========================================================================================

    public void testCreateAttributeWithValue() throws Exception {
        Element backupNode = (Element) searchNode.cloneNode(true);

        String newAttributeValue = "newValue";

        String fieldName = "address/@newAttribute";
        XMLField field = new XMLField(fieldName);

        XPathEngine.getInstance().create(field, searchNode, newAttributeValue, session);

        NodeList verifyNodes = searchNode.getElementsByTagName("address");
        int length = verifyNodes.getLength();
        Element verifyElement = (Element) verifyNodes.item(1);

        Node verifyNode = verifyElement.getAttributeNode("newAttribute");

        assertNotNull("XPath failed to create the attribute.", verifyNode);

        assertEquals("New attribute's value was not created.", newAttributeValue, verifyElement.getAttribute("newAttribute"));

        searchNode = backupNode;
    }

    // ==========================================================================================

    public void testCreateAttributeOnIndexedElement() throws Exception {
        Element backupNode = (Element) searchNode.cloneNode(true);

        String fieldName = "newIndexedElement[5]/@newAttribute";
        XMLField field = new XMLField(fieldName);
        XPathEngine.getInstance().create(field, searchNode, session);

        NodeList verifyNodes = searchNode.getElementsByTagName("newIndexedElement");
        Element verifyElement = (Element) verifyNodes.item(4);
        Node verifyNode = verifyElement.getAttributeNode("newAttribute");

        assertTrue("XPath failed to create the attribute.", verifyNodes.getLength() != 0);
        assertEquals("XPath failed to create the indexed elements.", 5, verifyNodes.getLength());
        assertNotNull("XPath failed to create the attribute.", verifyNode);

        searchNode = backupNode;
    }

    // ==========================================================================================

    public void testCreateElementAndMultipleAttributes() throws Exception {
        Element backupNode = (Element) searchNode.cloneNode(true);

        String fieldName = "newElement/@firstAttribute";
        XMLField field = new XMLField(fieldName);
        XPathEngine.getInstance().create(field, searchNode, session);

        String fieldName2 = "newElement/@secondAttribute";
        XMLField field2 = new XMLField(fieldName2);
        XPathEngine.getInstance().create(field2, searchNode, session);

        NodeList verifyNodes = searchNode.getElementsByTagName("newElement");

        assertTrue("XPath failed to create the attribute.", verifyNodes.getLength() != 0);
        assertEquals("XPath created too many attributes, should only have created one.", 1, verifyNodes.getLength());

        Element element = (Element) verifyNodes.item(0);

        assertEquals("XPath did not create both attributes.", 2, element.getAttributes().getLength());

        searchNode = backupNode;
    }

    // ==========================================================================================
    public void testCreateInvalidIndexedElement() throws Exception {
        Element backupNode = (Element) searchNode.cloneNode(true);

        try {
            String fieldName = "newIndexedElement[10.0]";
            XMLField field = new XMLField(fieldName);
            XPathEngine.getInstance().create(field, searchNode, session);
        } catch (XMLMarshalException validationException) {
            assertEquals("An unexpected XMLMarshalException was caught. " + validationException.getMessage(), XMLMarshalException.INVALID_XPATH_INDEX_STRING, validationException.getErrorCode());
            return;
        } catch (Exception e) {
            fail("An unexpected Exception was caught. " + e.getMessage());
            return;
        }
        searchNode = backupNode;
        fail();
    }
}
