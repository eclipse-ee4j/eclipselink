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

/**
 * Test marshalling the control object to each of the possible outputs
 * Test attempting to marshal an unmapped object and an object which can not be a root element
 */
public class XMLMarshalTestCases extends OXTestCase {
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

    public XMLMarshalTestCases(String name) {
        super(name);
     }

    public static void main(String[] args) {
        
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.xmlmarshaller.XMLMarshalTestCases" };
        TestRunner.main(arguments);
    }

    public void setUp() throws Exception {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        builderFactory.setIgnoringElementContentWhitespace(true);
        parser = builderFactory.newDocumentBuilder();
        context = getXMLContext(new XMLMarshallerTestProject());
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

    
        public void testMarshalObjectToWriterNoNamespace() throws Exception {
            StringWriter writer = new StringWriter();

            marshaller.setNoNamespaceSchemaLocation("sessions_10_0_3.xsd");
            marshaller.marshal(controlObject, writer);

            StringReader reader = new StringReader(writer.toString());
            InputSource inputSource = new InputSource(reader);
            parser.parse(inputSource);
        }

        public void testMarshalObjectToWriterSchemaLocation() throws Exception {
            StringWriter writer = new StringWriter();

            marshaller.setSchemaLocation("sessions_10_0_3.xsd");
            marshaller.marshal(controlObject, writer);

            StringReader reader = new StringReader(writer.toString());
            InputSource inputSource = new InputSource(reader);
            parser.parse(inputSource);
        }

        public void testMarshalObjectToWriter() throws Exception {
            StringWriter writer = new StringWriter();

            marshaller.marshal(controlObject, writer);

            StringReader reader = new StringReader(writer.toString());
            InputSource inputSource = new InputSource(reader);
            Document testDocument = parser.parse(inputSource);
            log(testDocument);
            removeEmptyTextNodes(testDocument);

            assertXMLIdentical(controlDocument, testDocument);
        }
    
    public void testMarshalObjectToElement() throws Exception {
        Document marshalToNodeControl = setupControlDocument(MARSHAL_TO_NODE_RESOURCE);

        Document document = parser.newDocument();
        Element element = document.createElement("root");

        marshaller.marshal(controlObject, element);
        document.appendChild(element);

        log(marshalToNodeControl);
        log(document);

        assertXMLIdentical(marshalToNodeControl, document);
    }
       
        public void testMarshalObjectToDocument() {
            Document document = parser.newDocument();
            marshaller.marshal(controlObject, document);

            log(controlDocument);
            log(document);

            assertXMLIdentical(controlDocument, document);
        }

        public void testMarshalObjectToDocumentFragment() throws Exception {
            Document marshalToNodeControl = setupControlDocument(MARSHAL_TO_NODE_RESOURCE);

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

        public void testMarshalObjectToAttr() {
            Document document = parser.newDocument();
            Attr attr = document.createAttribute("name");
            try {
                marshaller.marshal(controlObject, attr);
            } catch (XMLMarshalException exception) {
                assertTrue("An unexpected XMLMarshalException was caught.", exception.getErrorCode() == XMLMarshalException.MARSHAL_EXCEPTION);
                return;
            }
            assertTrue("An XMLValidation should have been caught but wasn't.", false);
        }

        public void testMarshalObjectToCDATASection() {
            Document document = parser.newDocument();
            CDATASection cdataSection = document.createCDATASection("name");
            try {
                marshaller.marshal(controlObject, cdataSection);
            } catch (XMLMarshalException exception) {
                assertTrue("An unexpected XMLMarshalException was caught.", exception.getErrorCode() == XMLMarshalException.MARSHAL_EXCEPTION);
                return;
            }
            assertTrue("An XMLValidation should have been caught but wasn't.", false);
        }

        public void testMarshalObjectToComment() {
            Document document = parser.newDocument();
            Comment comment = document.createComment("name");
            try {
                marshaller.marshal(controlObject, comment);
            } catch (XMLMarshalException exception) {
                assertTrue("An unexpected XMLMarshalException was caught.", exception.getErrorCode() == XMLMarshalException.MARSHAL_EXCEPTION);
                return;
            }
            assertTrue("An XMLValidation should have been caught but wasn't.", false);
        }

        public void testMarshalObjectToDocumentType() {
            DOMImplementation domImplementation = parser.getDOMImplementation();
            DocumentType documentType = domImplementation.createDocumentType("A", "B", "C");
            try {
                marshaller.marshal(controlObject, documentType);
            } catch (XMLMarshalException exception) {
                assertTrue("An unexpected XMLMarshalException was caught.", exception.getErrorCode() == XMLMarshalException.MARSHAL_EXCEPTION);
                return;
            }
            assertTrue("An XMLValidation should have been caught but wasn't.", false);
        }

        public void testMarshalObjectToEntity() {
        }

        public void testMarshalObjectToEntityReference() {
            Document document = parser.newDocument();
            EntityReference entityReference = document.createEntityReference("name");
            try {
                marshaller.marshal(controlObject, entityReference);
            } catch (XMLMarshalException exception) {
                assertTrue("An unexpected XMLMarshalException was caught.", exception.getErrorCode() == XMLMarshalException.MARSHAL_EXCEPTION);
                return;
            }
            assertTrue("An XMLValidation should have been caught but wasn't.", false);
        }

        public void testMarshalObjectToNotation() {
        }

        public void testMarshalObjectToProcessingInstruction() {
            Document document = parser.newDocument();
            ProcessingInstruction processingInstruction = document.createProcessingInstruction("target", "data");
            try {
                marshaller.marshal(controlObject, processingInstruction);
            } catch (XMLMarshalException exception) {
                assertTrue("An unexpected XMLMarshalException was caught.", exception.getErrorCode() == XMLMarshalException.MARSHAL_EXCEPTION);
                return;
            }
            assertTrue("An XMLValidation should have been caught but wasn't.", false);
        }

        public void testMarshalObjectToText() {
            Document document = parser.newDocument();
            Text text = document.createTextNode("name");
            try {
                marshaller.marshal(controlObject, text);
            } catch (XMLMarshalException exception) {
                assertTrue("An unexpected XMLMarshalException was caught.", exception.getErrorCode() == XMLMarshalException.MARSHAL_EXCEPTION);
                return;
            }
            assertTrue("An XMLValidation should have been caught but wasn't.", false);
        }

        public void testMarshalObjectToOutputStream() throws Exception {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            marshaller.marshal(controlObject, outStream);

            StringReader reader = new StringReader(outStream.toString());
            InputSource inputSource = new InputSource(reader);
            Document testDocument = parser.parse(inputSource);
            removeEmptyTextNodes(testDocument);

            log(controlDocument);
            log(testDocument);

            assertXMLIdentical(controlDocument, testDocument);
        }

        public void testMarshalObjectToDOMResult() {
            Document document = parser.newDocument();

            DOMResult result = new DOMResult(document);
            marshaller.marshal(controlObject, result);

            log(controlDocument);
            log(document);

            assertXMLIdentical(controlDocument, document);
        }

        public void testMarshalObjectToSAXResult() {
            SAXDocumentBuilder builder = new SAXDocumentBuilder();
            SAXResult result = new SAXResult(builder);
            marshaller.marshal(controlObject, result);

            log(controlDocument);
            log(builder.getDocument());

            assertXMLIdentical(controlDocument, builder.getDocument());
        }

        public void testMarshalObjectToStreamResult() throws Exception {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            StreamResult result = new StreamResult(outStream);
            marshaller.marshal(controlObject, result);

            StringReader reader = new StringReader(outStream.toString());
            InputSource inputSource = new InputSource(reader);
            Document testDocument = parser.parse(inputSource);
            removeEmptyTextNodes(testDocument);

            log(controlDocument);
            log(testDocument);

            assertXMLIdentical(controlDocument, testDocument);
        }

        public void testMarshalObjectToStreamResult2() throws Exception {
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            marshaller.marshal(controlObject, result);

            StringReader reader = new StringReader(writer.toString());
            InputSource inputSource = new InputSource(reader);
            Document testDocument = parser.parse(inputSource);
            removeEmptyTextNodes(testDocument);

            log(controlDocument);
            log(testDocument);

            assertXMLIdentical(controlDocument, testDocument);
        }

        public void testMarshalObjectToStreamResult3() throws Exception {
            File file = new File("output.txt");
            StreamResult result = new StreamResult(file);
            marshaller.marshal(controlObject, result);

            FileReader reader = new FileReader(file);
            InputSource inputSource = new InputSource(reader);
            Document testDocument = parser.parse(inputSource);
            removeEmptyTextNodes(testDocument);

            log(controlDocument);
            log(testDocument);

            assertXMLIdentical(controlDocument, testDocument);
        }

        public void testMarshalObjectToStreamResult4() throws Exception {
            File file = new File("output.txt");
            StreamResult result = new StreamResult(file.toURI().toURL().toString());
            marshaller.marshal(controlObject, result);

            FileReader reader = new FileReader(file);
            InputSource inputSource = new InputSource(reader);
            Document testDocument = parser.parse(inputSource);
            removeEmptyTextNodes(testDocument);

            log(controlDocument);
            log(testDocument);

            assertXMLIdentical(controlDocument, testDocument);
        }

        public void testMarshalObjectToContentHandler() {
            SAXDocumentBuilder builder = new SAXDocumentBuilder();

            marshaller.marshal(controlObject, builder);

            log(controlDocument);
            log(builder.getDocument());

            assertXMLIdentical(controlDocument, builder.getDocument());
        }

        public void testMarshalNonRootObjectToNode() throws Exception {
            Document testDocument = parser.newDocument();
            Element rootElement = testDocument.createElement("root");
            testDocument.appendChild(rootElement);

            EmailAddress emailAddress = new EmailAddress();
            emailAddress.setDomain("domain");
            emailAddress.setUserID("user");
            marshaller.marshal(emailAddress, rootElement);

            Document controlDocument = setupControlDocument(MARSHAL_NON_ROOT_OBJECT_TO_NODE);
            assertXMLIdentical(controlDocument, testDocument);
        }

        /**
         * We should throw an exception if users try to marshal an object which is
         * not an element that has a defaultrootelement specified on its descriptor
         */
        public void testMarshalNonRootObjectToWriter() {
            StringWriter writer = new StringWriter();
            EmailAddress emailAddress = new EmailAddress();
            emailAddress.setDomain("domain");
            emailAddress.setUserID("user");
            marshaller.setFormattedOutput(false);
            marshaller.setFragment(true);
            marshaller.marshal(emailAddress, writer);
            this.assertEquals("<user-id>user</user-id><domain>domain</domain>", writer.toString());
        }

        //Null Test Cases=========================================================================================
        public void testMarshalObjectToNullContentHandler() {
            SAXDocumentBuilder output = null;
            try {
                marshaller.marshal(controlObject, output);
            } catch (XMLMarshalException exception) {
                assertTrue("An unexpected XMLMarshalException was caught.", exception.getErrorCode() == XMLMarshalException.NULL_ARGUMENT);
                return;
            }

            assertTrue("An XMLValidation should have been caught but wasn't.", false);
        }

        public void testMarshalNullObjectToContentHandler() {
            SAXDocumentBuilder output = new SAXDocumentBuilder();
            try {
                marshaller.marshal(null, output);
            } catch (XMLMarshalException exception) {
                assertTrue("An unexpected XMLMarshalException was caught.", exception.getErrorCode() == XMLMarshalException.NULL_ARGUMENT);
                return;
            }

            assertTrue("An XMLValidation should have been caught but wasn't.", false);
        }

        public void testMarshalObjectToNullNode() {
            Node output = null;
            try {
                marshaller.marshal(controlObject, output);
            } catch (XMLMarshalException exception) {
                assertTrue("An unexpected XMLMarshalException was caught.", exception.getErrorCode() == XMLMarshalException.NULL_ARGUMENT);
                return;
            }

            assertTrue("An XMLValidation should have been caught but wasn't.", false);
        }

        public void testMarshalNullObjectToNode() {
            Node output = parser.newDocument();
            try {
                marshaller.marshal(null, output);
            } catch (XMLMarshalException exception) {
                assertTrue("An unexpected XMLMarshalException was caught.", exception.getErrorCode() == XMLMarshalException.NULL_ARGUMENT);
                return;
            }

            assertTrue("An XMLValidation should have been caught but wasn't.", false);
        }

        public void testMarshalObjectToNullOutputStream() {
            OutputStream output = null;
            try {
                marshaller.marshal(controlObject, output);
            } catch (XMLMarshalException exception) {
                assertTrue("An unexpected XMLMarshalException was caught.", exception.getErrorCode() == XMLMarshalException.NULL_ARGUMENT);
                return;
            }

            assertTrue("An XMLValidation should have been caught but wasn't.", false);
        }

        public void testMarshalNullObjectToOutputStream() {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            try {
                marshaller.marshal(null, output);
            } catch (XMLMarshalException exception) {
                assertTrue("An unexpected XMLMarshalException was caught.", exception.getErrorCode() == XMLMarshalException.NULL_ARGUMENT);
                return;
            }

            assertTrue("An XMLValidation should have been caught but wasn't.", false);
        }

        public void testMarshalObjectToNullResult() {
            DOMResult output = null;
            try {
                marshaller.marshal(controlObject, output);
            } catch (XMLMarshalException exception) {
                assertTrue("An unexpected XMLMarshalException was caught.", exception.getErrorCode() == XMLMarshalException.NULL_ARGUMENT);
                return;
            }

            assertTrue("An XMLValidation should have been caught but wasn't.", false);
        }

        public void testMarshalNullObjectToResult() {
            DOMResult output = new DOMResult(parser.newDocument());
            try {
                marshaller.marshal(null, output);
            } catch (XMLMarshalException exception) {
                assertTrue("An unexpected XMLMarshalException was caught.", exception.getErrorCode() == XMLMarshalException.NULL_ARGUMENT);
                return;
            }

            assertTrue("An XMLValidation should have been caught but wasn't.", false);
        }

        public void testMarshalObjectToNullWriter() {
            Writer output = null;
            try {
                marshaller.marshal(controlObject, output);
            } catch (XMLMarshalException exception) {
                assertTrue("An unexpected XMLMarshalException was caught.", exception.getErrorCode() == XMLMarshalException.NULL_ARGUMENT);
                return;
            }

            assertTrue("An XMLValidation should have been caught but wasn't.", false);
        }

        public void testMarshalNullObjectToWriter() {
            StringWriter output = new StringWriter();
            try {
                marshaller.marshal(null, output);
            } catch (XMLMarshalException exception) {
                assertTrue("An unexpected XMLMarshalException was caught.", exception.getErrorCode() == XMLMarshalException.NULL_ARGUMENT);
                return;
            }

            assertTrue("An XMLValidation should have been caught but wasn't.", false);
        }

        public void testMarshalWithNoNamespaceResolver() {
            XMLDescriptor descriptor = ((XMLDescriptor)context.getSession(0).getProject().getDescriptor(Employee.class));
            String rootElement = descriptor.getDefaultRootElement();
            descriptor.setDefaultRootElement("thens:" + rootElement);

            DefaultHandler output = new DefaultHandler();
            try {
                marshaller.marshal(controlObject, output);
            } catch (XMLMarshalException exception) {
                assertTrue("An unexpected XMLMarshalException was caught.", exception.getErrorCode() == XMLMarshalException.NAMESPACE_RESOLVER_NOT_SPECIFIED);
                return;
            }

            assertTrue("An XMLValidation should have been caught but wasn't.", false);
        }      

        public void testMarshalToElementWithNoNamespaceResolver() {
            XMLDescriptor descriptor = ((XMLDescriptor)context.getSession(0).getProject().getDescriptor(Employee.class));
            String rootElement = descriptor.getDefaultRootElement();
            descriptor.setDefaultRootElement("thens:" + rootElement);

            try {
                Document document = parser.newDocument();
                Element element = document.createElement("root");
                document.appendChild(element);

                marshaller.marshal(controlObject, element);
            } catch (XMLMarshalException exception) {
                assertTrue("An unexpected XMLMarshalException was caught.", exception.getErrorCode() == XMLMarshalException.NAMESPACE_RESOLVER_NOT_SPECIFIED);
                return;
            }

            assertTrue("An XMLValidation should have been caught but wasn't.", false);
        }
}
