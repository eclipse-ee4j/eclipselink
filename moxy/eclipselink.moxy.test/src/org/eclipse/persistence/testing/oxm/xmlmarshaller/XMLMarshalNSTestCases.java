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
 *     Denise Smith - October 20, 2009
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.xmlmarshaller;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.textui.TestRunner;

import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.testing.oxm.OXTestCase;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;

public class XMLMarshalNSTestCases extends OXTestCase {
    private final static int CONTROL_EMPLOYEE_ID = 123;
    private final static String CONTROL_EMAIL_ADDRESS_USER_ID = "jane.doe";
    private final static String CONTROL_EMAIL_ADDRESS_DOMAIN = "example.com";
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/xmlmarshaller/Employee.xml";
    private final static String MARSHAL_TO_NODE_RESOURCE = "org/eclipse/persistence/testing/oxm/xmlmarshaller/MarshalToNode.xml";
    private final static String MARSHAL_TO_NODE_NS_RESOURCE = "org/eclipse/persistence/testing/oxm/xmlmarshaller/MarshalToNodeNS.xml";
    private final static String MARSHAL_NON_ROOT_OBJECT_TO_NODE = "org/eclipse/persistence/testing/oxm/xmlmarshaller/MarshalNonRootObjectToNode.xml";
    private Object controlObject;
    private Document controlDocument;
    private DocumentBuilder parser;
    private XMLMarshaller marshaller;
    private XMLContext context;

    public XMLMarshalNSTestCases(String name) {
        super(name);
     }

    public static void main(String[] args) {
        
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.xmlmarshaller.XMLMarshalNSTestCases" };
        TestRunner.main(arguments);
    }

    public void setUp() throws Exception {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        builderFactory.setIgnoringElementContentWhitespace(true);
        parser = builderFactory.newDocumentBuilder();
        context = getXMLContext(new XMLMarshallerNSTestProject());
        marshaller = context.createMarshaller();
        controlObject = setupControlObject();
        controlDocument = setupControlDocument(XML_RESOURCE);
    }

    protected Document setupControlDocument(String xmlResource) throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(xmlResource);
        Document document = parser.parse(inputStream);
        removeEmptyTextNodes(document);
        return document;
    }

    protected Employee setupControlObject() {
        Employee employee = new Employee();
        employee.setID(CONTROL_EMPLOYEE_ID);

        EmailAddress emailAddress = new EmailAddress();
        emailAddress.setUserID(CONTROL_EMAIL_ADDRESS_USER_ID);
        emailAddress.setDomain(CONTROL_EMAIL_ADDRESS_DOMAIN);
        employee.setEmailAddress(emailAddress);

        return employee;
    }
    
    public void testMarshalObjectToDocumentFragmentNS() throws Exception {
        Document marshalToNodeControl = setupControlDocument(MARSHAL_TO_NODE_NS_RESOURCE);

        Document document = parser.newDocument();
        Element rootElement = document.createElement("root");
        document.appendChild(rootElement);
        DocumentFragment documentFragment = document.createDocumentFragment();

        marshaller.marshal(controlObject, documentFragment);
        rootElement.appendChild(documentFragment);

        log(marshalToNodeControl);
        log(document);

        assertXMLIdentical(marshalToNodeControl, document);
    }
    
    public void testMarshalObjectToElementNS() throws Exception {
        Document marshalToNodeControl = setupControlDocument(MARSHAL_TO_NODE_NS_RESOURCE);

        Document document = parser.newDocument();
        Element element = document.createElement("root");
        document.appendChild(element);

        marshaller.marshal(controlObject, element);

        log(marshalToNodeControl);
        log(document);

        assertXMLIdentical(marshalToNodeControl, document);
    }

}