/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.oxm.XPathEngine;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.platform.xml.XMLPlatformException;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.platform.xml.XMLTransformer;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class XPathEngineCreateTestCases extends org.eclipse.persistence.testing.oxm.XMLTestCase {

    private Document controlDocument;
    private Element searchNode;
    private AbstractSession session;

    public XPathEngineCreateTestCases(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/oxm/xpathengine/AddressBook.xml");

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder parser = builderFactory.newDocumentBuilder();
        controlDocument = parser.parse(inputStream);

        searchNode = (Element) controlDocument.getDocumentElement();

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

        assertTrue("XPath failed to create the correct number of elements.", verifyNodes.getLength() == 2);

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

        assertTrue("XPath failed to create the element.", verifyNodes != null);
        assertTrue("XPath failed to create the element.", verifyNodes.getLength() == 1);

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

        assertTrue("XPath created the element and should not have.", createdNode == null);
        assertTrue("XPath created the element and should not have.", verifyNodes.getLength() == 0);

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

        assertTrue("XPath failed to create the correct number of child elements.", verifyNodes.getLength() == 2);

        searchNode = backupNode;
    }

    // ==========================================================================================

    public void testCreateElementWithElementValue() throws Exception {
        Element backupNode = (Element) searchNode.cloneNode(true);

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        Document document = builderFactory.newDocumentBuilder().newDocument();

        Element elementValue = (Element) document.createElement("newChildWithElementValue");
        Element elementValueChild = (Element) document.createElement("newChildElementValue");

        Text textNode = document.createTextNode("theValue");
        elementValueChild.appendChild(textNode);
        elementValue.appendChild(elementValueChild);

        String fieldName = "newElement/newChildWithElementValue";
        XMLField field = new XMLField(fieldName);

        Node created = XPathEngine.getInstance().create(field, searchNode, elementValue, session);
        NodeList verifyNodes = searchNode.getElementsByTagName("newChildWithElementValue");

        assertTrue("XPath failed to create the child elements.", verifyNodes != null);
        assertTrue("XPath failed to create the correct number of child elements.", verifyNodes.getLength() == 1);

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
            xmlTransformer.transform((Element) verifyNodes.item(0), writer2);
        } catch (XMLPlatformException exception) {
            fail("An XMLPlatformException was thrown");
            return;
        }
        
        assertTrue(stringWriter1.toString().equals(stringWriter2.toString()));
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

        assertTrue("XPath failed to create the element.", verifyNodes != null);
        assertTrue("XPath failed to create the correct number of elements.", verifyNodes.getLength() == 10);
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

        assertTrue("XPath failed to create the element.", verifyNodes != null);
        assertTrue("XPath failed to create the correct number of elements.", verifyNodes.getLength() == 10);

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

        assertTrue("XPath failed to create the correct number of child elements.", verifyNodes.getLength() == 2);

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

        assertTrue("XPath failed to create the attribute.", verifyNode != null);

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

        assertTrue("XPath failed to create the attribute.", verifyNode != null);

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
        assertTrue("XPath failed to create the indexed elements.", verifyNodes.getLength() == 5);
        assertTrue("XPath failed to create the attribute.", verifyNode != null);

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
        assertTrue("XPath created too many attributes, should only have created one.", verifyNodes.getLength() == 1);

        Element element = (Element) verifyNodes.item(0);

        assertTrue("XPath did not create both attributes.", element.getAttributes().getLength() == 2);

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
            assertTrue("An unexpected XMLMarshalException was caught. " + validationException.getMessage(), validationException.getErrorCode() == XMLMarshalException.INVALID_XPATH_INDEX_STRING);
            return;
        } catch (Exception e) {
            this.fail("An unexpected Exception was caught. " + e.getMessage());
            return;
        }
        searchNode = backupNode;
        assertTrue(false);
    }
}
