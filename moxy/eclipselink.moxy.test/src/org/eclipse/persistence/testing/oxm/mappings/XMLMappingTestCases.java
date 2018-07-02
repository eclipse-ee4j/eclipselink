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
package org.eclipse.persistence.testing.oxm.mappings;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Calendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.validation.Schema;
import javax.xml.validation.TypeInfoProvider;
import javax.xml.validation.Validator;
import javax.xml.validation.ValidatorHandler;

import org.eclipse.persistence.internal.oxm.record.XMLEventReaderInputSource;
import org.eclipse.persistence.internal.oxm.record.XMLEventReaderReader;
import org.eclipse.persistence.internal.oxm.record.XMLStreamReaderInputSource;
import org.eclipse.persistence.internal.oxm.record.XMLStreamReaderReader;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshallerHandler;
import org.eclipse.persistence.platform.xml.SAXDocumentBuilder;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.OXTestCase;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public abstract class XMLMappingTestCases extends OXTestCase {

    protected Document controlDocument;
    protected Document writeControlDocument;
    protected XMLMarshaller xmlMarshaller;
    protected XMLUnmarshaller xmlUnmarshaller;
    protected XMLContext xmlContext;
    public String resourceName;
    protected DocumentBuilder parser;
    protected Project project;
    protected String controlDocumentLocation;
    protected String writeControlDocumentLocation;
    protected boolean expectsMarshalException;

    private boolean shouldRemoveEmptyTextNodesFromControlDoc = true;

    public XMLMappingTestCases(String name) throws Exception {
        super(name);
        setupParser();
    }

    public boolean isUnmarshalTest() {
        return true;
    }

    public void setupControlDocs() throws Exception{
        if(this.controlDocumentLocation != null) {
            InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(controlDocumentLocation);
            resourceName = controlDocumentLocation;
            controlDocument = parser.parse(inputStream);
            if (shouldRemoveEmptyTextNodesFromControlDoc()) {
                removeEmptyTextNodes(controlDocument);
            }
            removeCopyrightNode(controlDocument);
            inputStream.close();
        }

        if(this.writeControlDocumentLocation != null) {
            InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(writeControlDocumentLocation);
            writeControlDocument = parser.parse(inputStream);
            if (shouldRemoveEmptyTextNodesFromControlDoc()) {
                removeEmptyTextNodes(writeControlDocument);
            }
            removeCopyrightNode(writeControlDocument);
            inputStream.close();
        }
    }

    public void setUp() throws Exception {
        setupParser();

        setupControlDocs();

        xmlContext = getXMLContext(project);
        xmlMarshaller = createMarshaller();
        xmlUnmarshaller = xmlContext.createUnmarshaller();
    }

    protected XMLMarshaller createMarshaller() {
        XMLMarshaller xmlMarshaller = xmlContext.createMarshaller();
        xmlMarshaller.setFormattedOutput(false);
        return xmlMarshaller;
    }

    public void tearDown() {
        parser = null;
        xmlContext = null;
        xmlMarshaller = null;
        xmlUnmarshaller = null;
        controlDocument = null;
        controlDocumentLocation = null;
    }

    protected void setupParser() {
        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            builderFactory.setNamespaceAware(true);
            builderFactory.setIgnoringElementContentWhitespace(true);
            parser = builderFactory.newDocumentBuilder();
        } catch (Exception e) {
            e.printStackTrace();
            fail("An exception occurred during setup");
        }
    }

    protected void setSession(String sessionName) {
        xmlContext = getXMLContext(sessionName);
        xmlMarshaller = xmlContext.createMarshaller();
        xmlMarshaller.setFormattedOutput(false);
        xmlUnmarshaller = xmlContext.createUnmarshaller();
    }

    protected void setProject(Project project) {
        this.project = project;
    }

    protected Document getControlDocument() {
        return controlDocument;
    }

    /**
     * Override this function to implement different read/write control documents.
     * @return
     * @throws Exception
     */
    protected Document getWriteControlDocument() throws Exception {
        if(writeControlDocument != null){
            return writeControlDocument;
        }
        return getControlDocument();
    }

    protected void setControlDocument(String xmlResource) throws Exception {
        this.controlDocumentLocation = xmlResource;
    }

    /**
     * Provide an alternative write version of the control document when rountrip is not enabled.
     * If this function is not called and getWriteControlDocument() is not overridden then the write and read control documents are the same.
     * @param xmlResource
     * @throws Exception
     */
    protected void setWriteControlDocument(String xmlResource) throws Exception {
        writeControlDocumentLocation = xmlResource;
    }

    abstract protected Object getControlObject();

    /*
     * Returns the object to be used in a comparison on a read
     * This will typically be the same object used to write
     */
    public Object getReadControlObject() {
        return getControlObject();
    }

    /*
     * Returns the object to be written to XML which will be compared
     * to the control document.
     */
    public Object getWriteControlObject() {
        return getControlObject();
    }



    public void testXMLToObjectFromInputStream() throws Exception {
        if(isUnmarshalTest()) {
            InputStream instream = Thread.currentThread().getContextClassLoader().getSystemResourceAsStream(resourceName);
            Object testObject = xmlUnmarshaller.unmarshal(instream);
            instream.close();
            xmlToObjectTest(testObject);
        }
    }

    public void testXMLToObjectFromNode() throws Exception {
        if(isUnmarshalTest()) {
            InputStream instream = Thread.currentThread().getContextClassLoader().getSystemResourceAsStream(resourceName);
            Node node  = parser.parse(instream);
            Object testObject = xmlUnmarshaller.unmarshal(node);
            instream.close();
            xmlToObjectTest(testObject);
        }
    }

    public void testXMLToObjectFromXMLStreamReader() throws Exception {
        if(isUnmarshalTest()  && null != XML_INPUT_FACTORY) {
            InputStream instream = Thread.currentThread().getContextClassLoader().getSystemResourceAsStream(resourceName);
            XMLStreamReader xmlStreamReader = XML_INPUT_FACTORY.createXMLStreamReader(instream);
            XMLStreamReaderReader staxReader = new XMLStreamReaderReader();
            staxReader.setErrorHandler(xmlUnmarshaller.getErrorHandler());
            XMLStreamReaderInputSource inputSource = new XMLStreamReaderInputSource(xmlStreamReader);
            Object testObject = xmlUnmarshaller.unmarshal(staxReader, inputSource);

            instream.close();
            xmlToObjectTest(testObject);
        }
    }

    public void testXMLToObjectFromXMLEventReader() throws Exception {
        if(isUnmarshalTest()  && null != XML_INPUT_FACTORY) {
            InputStream instream = Thread.currentThread().getContextClassLoader().getSystemResourceAsStream(resourceName);
            XMLEventReader xmlEventReader = XML_INPUT_FACTORY.createXMLEventReader(instream);
            XMLEventReaderReader staxReader = new XMLEventReaderReader();
            staxReader.setErrorHandler(xmlUnmarshaller.getErrorHandler());
            XMLEventReaderInputSource inputSource = new XMLEventReaderInputSource(xmlEventReader);
            Object testObject = xmlUnmarshaller.unmarshal(staxReader, inputSource);

            instream.close();
            xmlToObjectTest(testObject);
        }
    }

    public void xmlToObjectTest(Object testObject) throws Exception {
        log("\n**xmlToObjectTest**");
        log("Expected:");
        Object controlObject = getReadControlObject();
        if(null == controlObject) {
            log((String) null);
        } else {
            log(controlObject.toString());
        }
        log("Actual:");
        if(null == testObject) {
            log((String) null);
        } else {
            log(testObject.toString());
        }

        if ((getReadControlObject() instanceof XMLRoot) && (testObject instanceof XMLRoot)) {
            XMLRoot controlObj = (XMLRoot)getReadControlObject();
            XMLRoot testObj = (XMLRoot)testObject;
            compareXMLRootObjects(controlObj, testObj);
        } else {
            assertEquals(getReadControlObject(), testObject);
        }
    }

    public static void compareXMLRootObjects(XMLRoot controlObj, XMLRoot testObj) {
        assertEquals(controlObj.getLocalName(), testObj.getLocalName());
        assertEquals(controlObj.getNamespaceURI(), testObj.getNamespaceURI());
        if (null != controlObj.getObject() && null != testObj.getObject() && controlObj.getObject() instanceof java.util.Calendar && testObj.getObject() instanceof java.util.Calendar) {
            assertTrue(((Calendar)controlObj.getObject()).getTimeInMillis() == ((Calendar)testObj.getObject()).getTimeInMillis());
        } else {
            assertEquals(controlObj.getObject(), testObj.getObject());
        }
        assertEquals(controlObj.getSchemaType(), testObj.getSchemaType());
    }

    public void objectToXMLDocumentTest(Document testDocument) throws Exception {
        log("**objectToXMLDocumentTest**");
        log("Expected:");
        log(getWriteControlDocument());
        log("\nActual:");
        log(testDocument);
        assertXMLIdentical(getWriteControlDocument(), testDocument);
    }

    public void testObjectToXMLDocument() throws Exception {
        Object objectToWrite = getWriteControlObject();
        XMLDescriptor desc = null;
        if (objectToWrite instanceof XMLRoot) {
            XMLRoot xmlRoot = (XMLRoot) objectToWrite;
            if(null != xmlRoot.getObject()) {
                desc = (XMLDescriptor)xmlContext.getSession(0).getProject().getDescriptor(((XMLRoot)objectToWrite).getObject().getClass());
            }
        } else {
            desc = (XMLDescriptor)xmlContext.getSession(0).getProject().getDescriptor(objectToWrite.getClass());
        }
        int sizeBefore = getNamespaceResolverSize(desc);
        Document testDocument;
        try {
            testDocument = xmlMarshaller.objectToXML(objectToWrite);
        } catch(Exception e) {
            assertMarshalException(e);
            return;
        }
        if(expectsMarshalException){
            fail("An exception should have occurred but didn't.");
            return;
        }
        int sizeAfter = getNamespaceResolverSize(desc);
        assertEquals(sizeBefore, sizeAfter);
        objectToXMLDocumentTest(testDocument);
    }

    public void testObjectToXMLStringWriter() throws Exception {
        StringWriter writer = new StringWriter();
        Object objectToWrite = getWriteControlObject();
        XMLDescriptor desc = null;
        if (objectToWrite instanceof XMLRoot) {
            XMLRoot xmlRoot = (XMLRoot) objectToWrite;
            if(null != xmlRoot.getObject()) {
                desc = (XMLDescriptor)xmlContext.getSession(0).getProject().getDescriptor(((XMLRoot)objectToWrite).getObject().getClass());
            }
        } else {
            desc = (XMLDescriptor)xmlContext.getSession(0).getProject().getDescriptor(objectToWrite.getClass());
        }

        int sizeBefore = getNamespaceResolverSize(desc);

        try {
            xmlMarshaller.marshal(objectToWrite, writer);
        } catch(Exception e) {
            assertMarshalException(e);
            return;
        }
        if(expectsMarshalException){
            fail("An exception should have occurred but didn't.");
            return;
        }

        int sizeAfter = getNamespaceResolverSize(desc);

        assertEquals(sizeBefore, sizeAfter);

        StringReader reader = new StringReader(writer.toString());
        InputSource inputSource = new InputSource(reader);
        Document testDocument = parser.parse(inputSource);
        writer.close();
        reader.close();

        objectToXMLDocumentTest(testDocument);
    }

    public void testValidatingMarshal() throws Exception {
        StringWriter writer = new StringWriter();
        Object objectToWrite = getWriteControlObject();
        XMLDescriptor desc = null;
        if (objectToWrite instanceof XMLRoot) {
            XMLRoot xmlRoot = (XMLRoot) objectToWrite;
            if(null != xmlRoot.getObject()) {
                desc = (XMLDescriptor)xmlContext.getSession(0).getProject().getDescriptor(((XMLRoot)objectToWrite).getObject().getClass());
            }
        } else {
            desc = (XMLDescriptor)xmlContext.getSession(0).getProject().getDescriptor(objectToWrite.getClass());
        }

        int sizeBefore = getNamespaceResolverSize(desc);

        XMLMarshaller validatingMarshaller = createMarshaller();
        validatingMarshaller.setSchema(FakeSchema.INSTANCE);
        try {
            validatingMarshaller.marshal(objectToWrite, writer);
        } catch(Exception e) {
            assertMarshalException(e);
            return;
        }
        if(expectsMarshalException){
            fail("An exception should have occurred but didn't.");
            return;
        }


        StringReader reader = new StringReader(writer.toString());
        InputSource inputSource = new InputSource(reader);
        Document testDocument = parser.parse(inputSource);
        writer.close();
        reader.close();

        objectToXMLDocumentTest(testDocument);
    }

    public void testObjectToOutputStream() throws Exception {
        Object objectToWrite = getWriteControlObject();
        XMLDescriptor desc = null;
        if (objectToWrite instanceof XMLRoot) {
            XMLRoot xmlRoot = (XMLRoot) objectToWrite;
            if(null != xmlRoot.getObject()) {
                desc = (XMLDescriptor)xmlContext.getSession(0).getProject().getDescriptor(((XMLRoot)objectToWrite).getObject().getClass());
            }
        } else {
            desc = (XMLDescriptor)xmlContext.getSession(0).getProject().getDescriptor(objectToWrite.getClass());
        }

        int sizeBefore = getNamespaceResolverSize(desc);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            xmlMarshaller.marshal(objectToWrite, stream);
        } catch(Exception e) {
            assertMarshalException(e);
            return;
        }
        if(expectsMarshalException){
            fail("An exception should have occurred but didn't.");
            return;
        }
        int sizeAfter = getNamespaceResolverSize(desc);

        assertEquals(sizeBefore, sizeAfter);

        InputStream is = new ByteArrayInputStream(stream.toByteArray());
        Document testDocument = parser.parse(is);
        stream.close();
        is.close();

        objectToXMLDocumentTest(testDocument);
    }

    public void testObjectToOutputStreamASCIIEncoding() throws Exception {
        Object objectToWrite = getWriteControlObject();
        XMLDescriptor desc = null;
        if (objectToWrite instanceof XMLRoot) {
            XMLRoot xmlRoot = (XMLRoot) objectToWrite;
            if(null != xmlRoot.getObject()) {
                desc = (XMLDescriptor)xmlContext.getSession(0).getProject().getDescriptor(((XMLRoot)objectToWrite).getObject().getClass());
            }
        } else {
            desc = (XMLDescriptor)xmlContext.getSession(0).getProject().getDescriptor(objectToWrite.getClass());
        }

        int sizeBefore = getNamespaceResolverSize(desc);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        try {
            xmlMarshaller.setEncoding("US-ASCII");
            xmlMarshaller.marshal(objectToWrite, stream);
        } catch(Exception e) {
            assertMarshalException(e);
            return;
        }
        if(expectsMarshalException){
            fail("An exception should have occurred but didn't.");
            return;
        }


        int sizeAfter = getNamespaceResolverSize(desc);

        assertEquals(sizeBefore, sizeAfter);

        InputStream is = new ByteArrayInputStream(stream.toByteArray());
        Document testDocument = parser.parse(is);
        stream.close();
        is.close();

        objectToXMLDocumentTest(testDocument);
    }

    public void testObjectToXMLStreamWriter() throws Exception {
        if(XML_OUTPUT_FACTORY != null && staxResultClass != null) {
            StringWriter writer = new StringWriter();
            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            factory.setProperty(factory.IS_REPAIRING_NAMESPACES, new Boolean(false));
            XMLStreamWriter streamWriter= factory.createXMLStreamWriter(writer);

            Object objectToWrite = getWriteControlObject();
            XMLDescriptor desc = null;
            if (objectToWrite instanceof XMLRoot) {
                XMLRoot xmlRoot = (XMLRoot) objectToWrite;
                if(null != xmlRoot.getObject()) {
                    desc = (XMLDescriptor)xmlContext.getSession(0).getProject().getDescriptor(((XMLRoot)objectToWrite).getObject().getClass());
                }
            } else {
                desc = (XMLDescriptor)xmlContext.getSession(0).getProject().getDescriptor(objectToWrite.getClass());
            }

            int sizeBefore = getNamespaceResolverSize(desc);

            Result result = (Result)PrivilegedAccessHelper.invokeConstructor(staxResultStreamWriterConstructor, new Object[]{streamWriter});
            try {
                xmlMarshaller.marshal(objectToWrite, result);
            } catch(Exception e) {
                assertMarshalException(e);
                return;
            }
            if(expectsMarshalException){
                fail("An exception should have occurred but didn't.");
                return;
            }


            streamWriter.flush();
            int sizeAfter = getNamespaceResolverSize(desc);
            assertEquals(sizeBefore, sizeAfter);
            StringReader reader = new StringReader(writer.toString());
            InputSource inputSource = new InputSource(reader);
            Document testDocument = parser.parse(inputSource);
            writer.close();
            reader.close();
            objectToXMLDocumentTest(testDocument);
        }
    }

    public void testObjectToXMLEventWriter() throws Exception {
        if(XML_OUTPUT_FACTORY != null && staxResultClass != null) {
            StringWriter writer = new StringWriter();
            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            factory.setProperty(factory.IS_REPAIRING_NAMESPACES, new Boolean(false));
            XMLEventWriter eventWriter= factory.createXMLEventWriter(writer);

            Object objectToWrite = getWriteControlObject();
            XMLDescriptor desc = null;
            if (objectToWrite instanceof XMLRoot) {
                XMLRoot xmlRoot = (XMLRoot) objectToWrite;
                if(null != xmlRoot.getObject()) {
                    desc = (XMLDescriptor)xmlContext.getSession(0).getProject().getDescriptor(((XMLRoot)objectToWrite).getObject().getClass());
                }
            } else {
                desc = (XMLDescriptor)xmlContext.getSession(0).getProject().getDescriptor(objectToWrite.getClass());
            }

            int sizeBefore = getNamespaceResolverSize(desc);

            Result result = (Result)PrivilegedAccessHelper.invokeConstructor(staxResultEventWriterConstructor, new Object[]{eventWriter});
            try {
                xmlMarshaller.marshal(objectToWrite, result);
            } catch(Exception e) {
                assertMarshalException(e);
                return;
            }
            if(expectsMarshalException){
                fail("An exception should have occurred but didn't.");
                return;
            }


            eventWriter.flush();
            int sizeAfter = getNamespaceResolverSize(desc);
            assertEquals(sizeBefore, sizeAfter);
            StringReader reader = new StringReader(writer.toString());
            InputSource inputSource = new InputSource(reader);
            Document testDocument = parser.parse(inputSource);
            writer.close();
            reader.close();
            objectToXMLDocumentTest(testDocument);
        }
    }

    protected int getNamespaceResolverSize(XMLDescriptor desc){
       int size = -1;
        if (desc != null) {
            NamespaceResolver nr = desc.getNamespaceResolver();
            if (nr != null) {
                size = nr.getNamespaces().size();
            }else{
              size =0;
            }
        }
        return size;
    }

    public void testObjectToContentHandler() throws Exception {
        SAXDocumentBuilder builder = new SAXDocumentBuilder();
        Object objectToWrite = getWriteControlObject();
        XMLDescriptor desc = null;
        if (objectToWrite instanceof XMLRoot) {
            XMLRoot xmlRoot = (XMLRoot) objectToWrite;
            if(null != xmlRoot.getObject()) {
                desc = (XMLDescriptor)xmlContext.getSession(0).getProject().getDescriptor(((XMLRoot)objectToWrite).getObject().getClass());
            }
        } else {
            desc = (XMLDescriptor)xmlContext.getSession(0).getProject().getDescriptor(objectToWrite.getClass());
        }
        int sizeBefore = getNamespaceResolverSize(desc);

        try {
            xmlMarshaller.marshal(objectToWrite, builder);
        } catch(Exception e) {
            assertMarshalException(e);
            return;
        }
        if(expectsMarshalException){
            fail("An exception should have occurred but didn't.");
            return;
        }


        int sizeAfter = getNamespaceResolverSize(desc);

        assertEquals(sizeBefore, sizeAfter);

        Document controlDocument = getWriteControlDocument();
        Document testDocument = builder.getDocument();

        log("**testObjectToContentHandler**");
        log("Expected:");
        log(controlDocument);
        log("\nActual:");
        log(testDocument);

        //Diff diff = new Diff(controlDocument, testDocument);
        //this.assertXMLEqual(diff, true);
        assertXMLIdentical(controlDocument, testDocument);
    }

    public void testXMLToObjectFromURL() throws Exception {
        if(isUnmarshalTest()) {
            java.net.URL url = Thread.currentThread().getContextClassLoader().getResource(resourceName);
            Object testObject = xmlUnmarshaller.unmarshal(url);
            xmlToObjectTest(testObject);
        }
    }

    public void testUnmarshallerHandler() throws Exception {
        if(isUnmarshalTest()) {
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            saxParserFactory.setNamespaceAware(true);
            SAXParser saxParser = saxParserFactory.newSAXParser();
            XMLReader xmlReader = saxParser.getXMLReader();

            XMLUnmarshallerHandler xmlUnmarshallerHandler = xmlUnmarshaller.getUnmarshallerHandler();
            xmlReader.setContentHandler(xmlUnmarshallerHandler);

            InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
            InputSource inputSource = new InputSource(inputStream);
            xmlReader.parse(inputSource);

            xmlToObjectTest(xmlUnmarshallerHandler.getResult());
        }
    }

    public boolean shouldRemoveEmptyTextNodesFromControlDoc() {
        return shouldRemoveEmptyTextNodesFromControlDoc;
    }

    public void setShouldRemoveEmptyTextNodesFromControlDoc(boolean value) {
        this.shouldRemoveEmptyTextNodesFromControlDoc = value;
    }

    public void assertMarshalException(Exception exception) throws Exception {
        throw exception;
    }

    public static class FakeSchema extends Schema {

        public static FakeSchema INSTANCE = new FakeSchema();

        private ValidatorHandler validatorHandler;

        private FakeSchema() {
            validatorHandler = new FakeValidatorHandler();
        }

        @Override
        public Validator newValidator() {
            return null;
        }

        @Override
        public ValidatorHandler newValidatorHandler() {
            return validatorHandler;
        }

    }

    private static class FakeValidatorHandler extends ValidatorHandler {

        public void setDocumentLocator(Locator locator) {
        }

        public void startDocument() throws SAXException {
        }

        public void endDocument() throws SAXException {
        }

        public void startPrefixMapping(String prefix, String uri) throws SAXException {
        }

        public void endPrefixMapping(String prefix) throws SAXException {
        }

        public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        }

        public void endElement(String uri, String localName, String qName) throws SAXException {
        }

        public void characters(char[] ch, int start, int length) throws SAXException {
        }

        public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        }

        public void processingInstruction(String target, String data) throws SAXException {
        }

        public void skippedEntity(String name) throws SAXException {
        }

        @Override
        public void setContentHandler(ContentHandler receiver) {
        }

        @Override
        public ContentHandler getContentHandler() {
            return null;
        }

        @Override
        public void setErrorHandler(ErrorHandler errorHandler) {
        }

        @Override
        public ErrorHandler getErrorHandler() {
            return null;
        }

        @Override
        public void setResourceResolver(LSResourceResolver resourceResolver) {
        }

        @Override
        public LSResourceResolver getResourceResolver() {
            return null;
        }

        @Override
        public TypeInfoProvider getTypeInfoProvider() {
            return null;
        }
    }

}
