/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.oxm.mappings.xmlfragmentcollection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import junit.textui.TestRunner;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XMLFragmentCollectionNamespaces3TestCases extends XMLMappingTestCases {
    public XMLFragmentCollectionNamespaces3TestCases(String name) throws Exception {
        super(name);

        NamespaceResolver nsresolver = new NamespaceResolver();
        setProject(new XMLFragmentCollectionNSProject(nsresolver));
        setControlDocument("org/eclipse/persistence/testing/oxm/mappings/xmlfragmentcollection/element_namespaces3.xml");
    }

    public Object getControlObject() {
        Employee employee = new Employee();
        employee.firstName = "Jane";
        employee.lastName = "Doe";

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setIgnoringElementContentWhitespace(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(getClass().getClassLoader().getResourceAsStream("org/eclipse/persistence/testing/oxm/mappings/xmlfragmentcollection/sub_element_namespaces3.xml"));
            Element rootElem = doc.getDocumentElement();
            NodeList children = rootElem.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                if (children.item(i).getNodeType() == Element.ELEMENT_NODE) {
                    employee.xmlnodes.add(children.item(i));
                }
            }
        } catch (Exception ex) {
        }

        return employee;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.xmlfragmentcollection.XMLFragmentCollectionNamespaces3TestCases" };
        TestRunner.main(arguments);
    }

    public void xmlToObjectTest(Object testObject) throws Exception {
        super.xmlToObjectTest(testObject);

        Object firstObject = ((Employee)testObject).xmlnodes.iterator().next();
        assertTrue(firstObject instanceof Element);
        assertEquals("xml-node", ((Element)firstObject).getNodeName());
        assertNull(((Element)firstObject).getNamespaceURI());
        Attr nsDecl = ((Element)firstObject).getAttributeNode("xmlns:" + "ns1");
        assertNull(nsDecl);

        Element childNode = (Element)((Element)firstObject).getChildNodes().item(0);
        assertNotNull(childNode);

        nsDecl = childNode.getAttributeNode("xmlns:" + "ns1");
        assertNotNull(nsDecl);
        assertEquals("http://www.example.com/test-uri", nsDecl.getValue());
    }

    public Document getWriteControlDocument() throws Exception {
        java.io.InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/oxm/mappings/xmlfragmentcollection/element_namespaces_write3.xml");
        Document writeControlDocument = parser.parse(inputStream);
        inputStream.close();
        return writeControlDocument;
    }
}
