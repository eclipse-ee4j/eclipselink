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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import junit.framework.TestCase;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.oxm.UnmarshalXPathEngine;
import org.eclipse.persistence.internal.oxm.XPathEngine;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.record.XMLRecord;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XPathEngineFindTestCases extends TestCase {
    private Document controlDocument;
    private Element searchNode;

    public XPathEngineFindTestCases(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/oxm/xpathengine/AddressBook.xml");

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder parser = builderFactory.newDocumentBuilder();
        controlDocument = parser.parse(inputStream);
        searchNode = (Element)controlDocument.getDocumentElement();
    }

    // ==========================================================================================

    /**
     * Execute the 'self' xpath on the control document - should return the
     * root element <addressbook>, a.k.a. serachNode
     */
    public void testFindSelf() throws Exception {
        XMLField field = new XMLField(".");

        // should return 'searchNode'
        Node node = (Node)UnmarshalXPathEngine.getInstance().selectSingleNode(searchNode, field, field.getNamespaceResolver());

        assertTrue("Self xpath test failed", (node != null) && (node == searchNode));
    }

    /**
     * Execute the 'self' xpath on the control document - should return a
     * collection containing the root element <addressbook>, a.k.a. serachNode
     */
    public void testFindSelfCollection() throws Exception {
        XMLField field = new XMLField(".");

        // should return 'searchNode'
        NodeList nodeList = UnmarshalXPathEngine.getInstance().selectNodes(searchNode, field, field.getNamespaceResolver());
        Node node = null;
        if ((nodeList != null) && (nodeList.getLength() > 0)) {
            node = nodeList.item(0);
        }

        assertTrue("Self xpath test failed", (node != null) && (node == searchNode));
    }

    // ==========================================================================================
    public void testFindElement() throws Exception {
        XMLField xmlField = new XMLField("address/postal-code");
        Node node = (Node)UnmarshalXPathEngine.getInstance().selectSingleNode(searchNode, xmlField, xmlField.getNamespaceResolver());

        NodeList controlNodes = searchNode.getElementsByTagName("postal-code");

        if (controlNodes.getLength() == 0) {
            assertTrue("XPath found 1 elements, but document contained 0.", node == null);
        } else {
            assertTrue("XPath found 0 elements, but document contained " + controlNodes.getLength() + ".", node != null);
        }
    }

    // ==========================================================================================
    public void testFindCollection() throws Exception {
        XMLField xmlField = new XMLField("address/postal-code");
        NodeList xpathNodes = UnmarshalXPathEngine.getInstance().selectNodes(searchNode, xmlField, xmlField.getNamespaceResolver());

        NodeList controlNodes = searchNode.getElementsByTagName("postal-code");

        assertTrue("XPath found " + xpathNodes.getLength() + " elements, but document contained  " + controlNodes.getLength() + ".", xpathNodes.getLength() == controlNodes.getLength());

        for (int i = 0; i < xpathNodes.getLength(); i++) {
            Object xpathNode = xpathNodes.item(i);
            Object controlNode = controlNodes.item(i);

            assertEquals("XPath did not return the correct elements.", xpathNode, controlNode);
        }
    }

    // ==========================================================================================
    public void testFindIndexedPathElements() throws Exception {
        XMLField xmlField = new XMLField("address/phone[2]");
        NodeList xpathNodes = UnmarshalXPathEngine.getInstance().selectNodes(searchNode, xmlField, xmlField.getNamespaceResolver());

        assertTrue("XPath found " + xpathNodes.getLength() + " elements, but document contained  2.", xpathNodes.getLength() == 2);

        NodeList controlNodes = searchNode.getElementsByTagName("phone");

        assertEquals("XPath did not return the correct elements.", xpathNodes.item(0), controlNodes.item(1));
        assertEquals("XPath did not return the correct elements.", xpathNodes.item(1), controlNodes.item(4));
    }

    // ==========================================================================================
    public void testFindSingleIndexedPathElement() throws Exception {
        XMLField xmlField = new XMLField("address/phone[2]");
        Node xpathNode = (Node)UnmarshalXPathEngine.getInstance().selectSingleNode(searchNode, xmlField, xmlField.getNamespaceResolver());

        assertNotNull("Element was not found.", xpathNode);
        //assertTrue("XPath found " + xpathNodes.getLength() + " elements, but document contained  2.", xpathNodes.getLength() == 2);
        NodeList controlNodes = searchNode.getElementsByTagName("phone");

        //Node controlNode = controlNodes.item(0);
        assertEquals("XPath did not return the correct elements.", xpathNode, controlNodes.item(1));

        //assertEquals("XPath did not return the correct elements.", xpathNodes.item(0), controlNodes.item(1));
        //assertEquals("XPath did not return the correct elements.", xpathNodes.item(1), controlNodes.item(4));
    }

    // ==========================================================================================
    public void testFindIndexedElements() throws Exception {
        XMLField xmlField = new XMLField("address[2]");
        NodeList xpathNodes = UnmarshalXPathEngine.getInstance().selectNodes(searchNode, xmlField, xmlField.getNamespaceResolver());

        assertTrue("XPath found " + xpathNodes.getLength() + " elements, but document contained  1.", xpathNodes.getLength() == 1);

        NodeList controlNodes = searchNode.getElementsByTagName("address");
        Object controlNode = controlNodes.item(1);
        Object xpathNode = xpathNodes.item(0);

        assertEquals("XPath did not return the correct elements.", xpathNode, controlNode);
    }

    // ==========================================================================================
    public void testFindIndexedAttributes() throws Exception {
        XMLField xmlField = new XMLField("address/addressee/@initial");
        NodeList xpathNodes = UnmarshalXPathEngine.getInstance().selectNodes(searchNode, xmlField, xmlField.getNamespaceResolver());

        assertTrue("XPath found " + xpathNodes.getLength() + " attributes, but document contained  2.", xpathNodes.getLength() == 2);

        NodeList controlNodes = searchNode.getElementsByTagName("addressee");
        for (int i = 0; i < controlNodes.getLength(); i++) {
            Element controlElement = (Element)controlNodes.item(i);
            Attr controlNode = controlElement.getAttributeNode("initial");
            Attr xpathNode = (Attr)xpathNodes.item(i);

            assertEquals("XPath did not return the correct attributes.", xpathNode, controlNode);
        }
    }

    public void testFindAttribute() throws Exception {
        XMLField xmlField = new XMLField("address/addressee/@age");
        Object xpathNode = UnmarshalXPathEngine.getInstance().selectSingleNode(searchNode, xmlField, xmlField.getNamespaceResolver());
        assertTrue("XPath found the attribute unexpectedly ", xpathNode == null || xpathNode == org.eclipse.persistence.oxm.record.XMLRecord.noEntry);
    }

    public void testFindAttributeWithNamespace() throws Exception {
        InputStream inputStreamNS = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/oxm/xpathengine/AddressBookNS.xml");
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder parser = builderFactory.newDocumentBuilder();
        Document controlDocumentNS = parser.parse(inputStreamNS);
        Element searchNodeNS = (Element)controlDocumentNS.getDocumentElement();

        XMLField field = new XMLField("address/addressee/@testns:test");
        NamespaceResolver resolver = new NamespaceResolver();
        resolver.put("testns", "www.example.com/some-dir/someschema.xsd");
        field.setNamespaceResolver(resolver);

        Object xpathNode = UnmarshalXPathEngine.getInstance().selectSingleNode(searchNodeNS, field, resolver);
        assertTrue("XPath found the attribute unexpectedly ", xpathNode == null || xpathNode == XMLRecord.noEntry);
    }

    public void testFindInvalidIndexString() {
        try {
            String fieldName = "address[3.0]";
            XMLField field = new XMLField(fieldName);
            Node node = (Node)UnmarshalXPathEngine.getInstance().selectSingleNode(searchNode, field, field.getNamespaceResolver());
            assertTrue("Node should have been null.", node == null);
        } catch (ValidationException e) {
            String xmlPlatform = System.getProperty(XMLPlatformFactory.XML_PLATFORM_PROPERTY, XMLPlatformFactory.XDK_PLATFORM_CLASS_NAME);
            assertEquals("This test should pass when the JAXPPlatform is not used.", XMLPlatformFactory.JAXP_PLATFORM_CLASS_NAME, xmlPlatform);

        }
    }
}
