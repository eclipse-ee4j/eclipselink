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
package org.eclipse.persistence.testing.oxm.xmlmarshaller;

import java.io.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import junit.textui.TestRunner;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.platform.xml.SAXDocumentBuilder;
import org.eclipse.persistence.testing.oxm.OXTestCase;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

public class XMLMarshallerNoDefaultRootTestCases extends OXTestCase {
    private final static int CONTROL_EMPLOYEE_ID = 123;
    private final static String CONTROL_EMAIL_ADDRESS_USER_ID = "jane.doe";
    private final static String CONTROL_EMAIL_ADDRESS_DOMAIN = "example.com";
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/xmlmarshaller/Employee.xml";
    private final static String MARSHAL_TO_NODE_RESOURCE = "org/eclipse/persistence/testing/oxm/xmlmarshaller/MarshalToNode.xml";
    private final static String MARSHAL_TO_NODE_RESOURCE_NODEFAULT = "org/eclipse/persistence/testing/oxm/xmlmarshaller/MarshalToNodeNoDefaultRoot.xml";
    private final static String MARSHAL_TO_NODE_RESOURCE_XSIUSED = "org/eclipse/persistence/testing/oxm/xmlmarshaller/MarshalToNodeNoDefaultRootXSIUsed.xml";
    private final static String MARSHAL_TO_NODE_NS_RESOURCE = "org/eclipse/persistence/testing/oxm/xmlmarshaller/MarshalToNodeNS.xml";
    private final static String MARSHAL_NON_ROOT_OBJECT_TO_NODE = "org/eclipse/persistence/testing/oxm/xmlmarshaller/MarshalNonRootObjectToNode.xml";
    private Object controlObject;
    private Document controlDocument;
    private DocumentBuilder parser;
    private XMLMarshaller marshaller;
    private XMLContext context;
    private XMLMarshallerNoDefaultRootTestProject defaultProject;

    public XMLMarshallerNoDefaultRootTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.xmlmarshaller.XMLMarshallerNoDefaultRootTestCases" };
        TestRunner.main(arguments);
    }

    public void setUp() throws Exception {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        builderFactory.setIgnoringElementContentWhitespace(true);
        parser = builderFactory.newDocumentBuilder();
        defaultProject = new XMLMarshallerNoDefaultRootTestProject();
        context = getXMLContext(defaultProject);
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

    public void testMarshalObjectToElementWithTypeAttribute() throws Exception {
        if (!(platform == Platform.DOC_PRES)) {
            Document marshalToNodeControl = setupControlDocument(MARSHAL_TO_NODE_RESOURCE_NODEFAULT);

            Document document = parser.newDocument();
            Element element = document.createElement("root");
            element.setAttributeNS(XMLConstants.XMLNS_URL, XMLConstants.XMLNS + ":" + "xsi", XMLConstants.SCHEMA_INSTANCE_URL);
            element.setAttributeNS(XMLConstants.SCHEMA_INSTANCE_URL, "xsi:type", "test");
            log(document);

            marshaller.marshal(controlObject, element);
            document.appendChild(element);

            log(marshalToNodeControl);
            log(document);

            assertXMLIdentical(marshalToNodeControl, document);
        }
    }

    public void testMarshalObjectToElementWithXSIPrefixBeingUsed() throws Exception {
        if (!(platform == Platform.DOC_PRES)) {
            NamespaceResolver try_test = new NamespaceResolver();
            try_test.put("xsi", "http://www.a_test.com");
            ((XMLDescriptor)(defaultProject.getDescriptor(Employee.class))).setNamespaceResolver(try_test);

            Document marshalToNodeControl = setupControlDocument(MARSHAL_TO_NODE_RESOURCE_XSIUSED);

            Document document = parser.newDocument();
            Element element = document.createElement("root");

            marshaller.marshal(controlObject, element);
            document.appendChild(element);

            log(marshalToNodeControl);
            log(document);

            assertXMLIdentical(marshalToNodeControl, document);
        }
    }

    public void testMarshalObjectToElement() throws Exception {
        if (!(platform == Platform.DOC_PRES)) {
            Document marshalToNodeControl = setupControlDocument(MARSHAL_TO_NODE_RESOURCE_NODEFAULT);

            Document document = parser.newDocument();
            Element element = document.createElement("root");

            marshaller.marshal(controlObject, element);
            document.appendChild(element);

            log(marshalToNodeControl);
            log(document);

            assertXMLIdentical(marshalToNodeControl, document);
        }
    }
}
