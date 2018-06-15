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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.xmlroot.complex;

import javax.xml.namespace.QName;
import javax.xml.parsers.*;
import junit.framework.AssertionFailedError;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.testing.oxm.OXTestCase;
import org.eclipse.persistence.testing.oxm.xmlroot.Person;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class MarshalToNodeTestCases extends OXTestCase {

    private DocumentBuilder documentBuilder;

    public MarshalToNodeTestCases(String name) throws Exception {
        super(name);
    }

    public Object getControlObject() {
        XMLRoot xmlRoot = new XMLRoot();
        xmlRoot.setLocalName("ROOT-OBJECT");
        xmlRoot.setNamespaceURI("http://www.example.org/");
        xmlRoot.setSchemaType(new QName("test", "person"));
        xmlRoot.setObject(new Person());
        return xmlRoot;
    }

    public Document getControlDocument() {
        Document document = documentBuilder.newDocument();
        Element rootElement = document.createElementNS("http://www.example.org/", "abc:ROOT-ELEMENT");
        document.appendChild(rootElement);
        Element rootObject = document.createElementNS("http://www.example.org/", "abc:ROOT-OBJECT");
        rootObject.setAttributeNS(javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI, javax.xml.XMLConstants.XMLNS_ATTRIBUTE + ":" + XMLConstants.SCHEMA_INSTANCE_PREFIX, javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
        rootObject.setAttributeNS(javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, XMLConstants.SCHEMA_INSTANCE_PREFIX + ":type", "oxm:person");
        rootObject.setAttributeNS(javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI, javax.xml.XMLConstants.XMLNS_ATTRIBUTE + ":oxm", "test");
        rootElement.appendChild(rootObject);
        return document;
    }

    public void setUp() throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        documentBuilder = dbf.newDocumentBuilder();
    }

    public void testMarshalToNode() throws Exception {
        Document testDocument = documentBuilder.newDocument();
        Element rootElement = testDocument.createElementNS("http://www.example.org/", "abc:ROOT-ELEMENT");
        testDocument.appendChild(rootElement);

        XMLContext xmlContext = getXMLContext(new XMLRootComplexProject());
        XMLMarshaller xmlMarshaller = xmlContext.createMarshaller();
        xmlMarshaller.setFormattedOutput(false);
        xmlMarshaller.marshal(getControlObject(), rootElement);

        Document controlDocument = getControlDocument();

        try {
            this.assertXMLIdentical(controlDocument, testDocument);
        } catch(AssertionFailedError e) {
            // Some parser implementations add another namespace declaration for "http://www.example.org/"
            ((Element)controlDocument.getDocumentElement().getFirstChild()).setAttributeNS(javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI, javax.xml.XMLConstants.XMLNS_ATTRIBUTE + ":abc", "http://www.example.org/");
            this.assertXMLIdentical(controlDocument, testDocument);
        }
    }
}
