/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
import java.io.StringWriter;
import java.io.PrintWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.internal.oxm.XPathEngine;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.platform.xml.XMLPlatformException;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.platform.xml.XMLTransformer;
import org.eclipse.persistence.sessions.factories.SessionManager;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class XPathEngineReplaceTestCases extends org.eclipse.persistence.testing.oxm.XMLTestCase {

    private Document controlDocument;
    private Element searchNode;
    private AbstractSession session;

    public XPathEngineReplaceTestCases(String name) {
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

    public void testReplaceAttributeValue() throws Exception {
        Element backupNode = (Element) searchNode.cloneNode(true);

        String fieldName = "address/@type";
        XMLField field = new XMLField(fieldName);
        int nodesReplaced = XPathEngine.getInstance().replaceValue(field, searchNode, "FOO", session).getLength();

        NodeList verifyNodes = searchNode.getElementsByTagName("address");

        for (int i = 0; i < verifyNodes.getLength(); i++) {
            Element next = (Element) verifyNodes.item(i);
            String type = next.getAttribute("type");
            assertTrue("XPath did not replace all of the attribute values.", type.equals("FOO"));
        }

        searchNode = backupNode;
    }

    // ==========================================================================================

    public void testReplaceIndexedElementAttributeValue() throws Exception {
        Element backupNode = (Element) searchNode.cloneNode(true);

        String fieldName = "address/phone/@area-code";
        XMLField field = new XMLField(fieldName);
        int nodesReplaced = XPathEngine.getInstance().replaceValue(field, searchNode, "902", session).getLength();

        NodeList verifyNodes = searchNode.getElementsByTagName("phone");

        for (int i = 0; i < verifyNodes.getLength(); i++) {
            Element next = (Element) verifyNodes.item(i);
            String type = next.getAttribute("area-code");
            assertTrue("XPath did not replace all of the attribute values.", type.equals("902"));
        }

        searchNode = backupNode;
    }

    // ==========================================================================================

    public void testReplaceElementStringValue() throws Exception {
        Element backupNode = (Element) searchNode.cloneNode(true);
        String fieldName = "address/postal-code";
        XMLField field = new XMLField(fieldName);
        int nodesReplaced = XPathEngine.getInstance().replaceValue(field, searchNode, "90210", session).getLength();

        NodeList verifyNodes = searchNode.getElementsByTagName("postal-code");
        for (int i = 0; i < verifyNodes.getLength(); i++) {
            Element next = (Element) verifyNodes.item(i);
            String type = next.getFirstChild().getNodeValue();
            assertTrue("XPath did not replace all of the element values.", type.equals("90210"));
        }

        searchNode = backupNode;
    }

    // ==========================================================================================

    public void testReplaceElementValue() throws Exception {
        Element backupNode = (Element) searchNode.cloneNode(true);

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        Document document = builderFactory.newDocumentBuilder().newDocument();
        Element cityElement = (Element) document.createElement("city");
        Element nameElement = (Element) document.createElement("name");

        Text nameText = document.createTextNode("thename");

        nameElement.appendChild(nameText);
        Element codeElement = (Element) document.createElement("code");

        Text codeText = document.createTextNode("thecode");

        codeElement.appendChild(codeText);
        cityElement.appendChild(nameElement);
        cityElement.appendChild(codeElement);

        String fieldName = "address/city";
        XMLField field = new XMLField(fieldName);
        int nodesReplaced = XPathEngine.getInstance().replaceValue(field, searchNode, cityElement, session).getLength();

        NodeList verifyNodes = searchNode.getElementsByTagName("city");
        assertTrue("XPath did not replace all of the element values.", verifyNodes.getLength() == nodesReplaced);

        StringWriter stringWriter2 = new StringWriter();
        PrintWriter writer2 = new PrintWriter(stringWriter2);
        try {
            XMLTransformer xmlTransformer = XMLPlatformFactory.getInstance().getXMLPlatform().newXMLTransformer();
            xmlTransformer.transform(cityElement, writer2);
        } catch (XMLPlatformException exception) {
            fail("An XMLPlatformException was thrown");
            return;
        }

        for (int i = 0; i < nodesReplaced; i++) {
            StringWriter stringWriter1 = new StringWriter();
            PrintWriter writer1 = new PrintWriter(stringWriter1);
            try {
                Element nextElement = (Element) verifyNodes.item(i);
                XMLTransformer xmlTransformer = XMLPlatformFactory.getInstance().getXMLPlatform().newXMLTransformer();
                xmlTransformer.transform(nextElement, writer1);
            } catch (XMLPlatformException exception) {
                fail("An XMLPlatformException was thrown");
                return;
            }
            assertTrue(stringWriter1.toString().equals(stringWriter2.toString()));
        }

        searchNode = backupNode;
    }

    // ==========================================================================================

    public void testReplaceElementValueDifferentNodeName() throws Exception {
        Element backupNode = (Element) searchNode.cloneNode(true);

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        Document document = builderFactory.newDocumentBuilder().newDocument();

        Element controlElement = (Element) document.createElement("city");
        Element townElement = (Element) document.createElement("town");

        Element nameElement = (Element) document.createElement("name");

        Text nameText = document.createTextNode("thename");
        nameElement.appendChild(nameText);

        Attr codeAttribute = (Attr) document.createAttribute("code");
        codeAttribute.setNodeValue("thecode");

        // controlElement is just used for comparison at the end of this test
        // case
        controlElement.appendChild(nameElement.cloneNode(true));
        controlElement.setAttributeNode((Attr) codeAttribute.cloneNode(true));

        townElement.appendChild(nameElement);
        townElement.setAttributeNode(codeAttribute);

        String fieldName = "address/city";
        XMLField field = new XMLField(fieldName);
        int nodesReplaced = XPathEngine.getInstance().replaceValue(field, searchNode, townElement, session).getLength();

        NodeList verifyNodes = searchNode.getElementsByTagName("city");
        assertTrue("XPath did not replace all of the element values.", verifyNodes.getLength() == nodesReplaced);

        StringWriter stringWriter2 = new StringWriter();
        PrintWriter writer2 = new PrintWriter(stringWriter2);

        try {
            XMLTransformer xmlTransformer = XMLPlatformFactory.getInstance().getXMLPlatform().newXMLTransformer();
            xmlTransformer.transform(controlElement, writer2);
        } catch (XMLPlatformException exception) {
            fail("An XMLPlatformException was thrown");
            return;
        }
        for (int i = 0; i < nodesReplaced; i++) {
            StringWriter stringWriter1 = new StringWriter();
            PrintWriter writer1 = new PrintWriter(stringWriter1);
            try {
                Element nextElement = (Element) verifyNodes.item(i);
                XMLTransformer xmlTransformer = XMLPlatformFactory.getInstance().getXMLPlatform().newXMLTransformer();
                xmlTransformer.transform(nextElement, writer1);
            } catch (XMLPlatformException exception) {
                fail("An XMLPlatformException was thrown");
                return;
            }
            assertTrue(stringWriter1.toString().equals(stringWriter2.toString()));
        }

        searchNode = backupNode;
    }

    // ==========================================================================================

    public void testReplaceElementValueWithChildren() throws Exception {
        Element backupNode = (Element) searchNode.cloneNode(true);

        String fieldName = "address/city";
        XMLField field = new XMLField(fieldName);
        int nodesReplaced = XPathEngine.getInstance().replaceValue(field, searchNode, "Halifax", session).getLength();

        NodeList verifyNodes = searchNode.getElementsByTagName("city");

        assertTrue("XPath did not replace all of the element values.", verifyNodes.getLength() == nodesReplaced);

        verifyNodes = searchNode.getElementsByTagName("code");

        assertTrue("XPath did not replace all children.", verifyNodes.getLength() == 0);

        searchNode = backupNode;
    }

    // ==========================================================================================

    public void testReplaceIndexedElementValue() throws Exception {
        Element backupNode = (Element) searchNode.cloneNode(true);
        String fieldName = "address/phone[3]";
        XMLField field = new XMLField(fieldName);
        int nodesReplaced = XPathEngine.getInstance().replaceValue(field, searchNode, "123-4567", session).getLength();

        assertTrue("XPath did not replace any element values.", nodesReplaced != 0);

        NodeList verifyNodes = searchNode.getElementsByTagName("phone");
        Element verifyElement = (Element) verifyNodes.item(2);
        String verifyNumber = verifyElement.getFirstChild().getNodeValue();
        assertTrue("XPath did not replace all of the attribute values.", verifyNumber == "123-4567");

        searchNode = backupNode;
    }

}
