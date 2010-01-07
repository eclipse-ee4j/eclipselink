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
package org.eclipse.persistence.testing.oxm.mappings;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Constructor;

import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;

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
import org.eclipse.persistence.oxm.record.MarshalRecord;
import org.eclipse.persistence.platform.xml.SAXDocumentBuilder;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.OXTestCase;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
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
            InputStream inputStream = ClassLoader.getSystemResourceAsStream(controlDocumentLocation);
            resourceName = controlDocumentLocation;
            controlDocument = parser.parse(inputStream);
            if (shouldRemoveEmptyTextNodesFromControlDoc()) {
                removeEmptyTextNodes(controlDocument);
            }
            inputStream.close();
        }

        if(this.writeControlDocumentLocation != null) {
            InputStream inputStream = ClassLoader.getSystemResourceAsStream(writeControlDocumentLocation);
            writeControlDocument = parser.parse(inputStream);
            if (shouldRemoveEmptyTextNodesFromControlDoc()) {
                removeEmptyTextNodes(writeControlDocument);
            }
            inputStream.close();
        }
    }

    public void setUp() throws Exception {
        setupParser();

        setupControlDocs();

        xmlContext = getXMLContext(project);
        xmlMarshaller = xmlContext.createMarshaller();
        xmlMarshaller.setFormattedOutput(false);
        xmlUnmarshaller = xmlContext.createUnmarshaller();

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
            InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
            Object testObject = xmlUnmarshaller.unmarshal(instream);
            instream.close();
            xmlToObjectTest(testObject);
        }
    }

    public void testXMLToObjectFromXMLStreamReader() throws Exception {
        if(isUnmarshalTest()  && null != XML_INPUT_FACTORY) {
            InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
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
            InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
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
        assertEquals(controlObj.getObject(), testObj.getObject());
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
        Document testDocument = xmlMarshaller.objectToXML(objectToWrite);
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

        xmlMarshaller.marshal(objectToWrite, writer);

        int sizeAfter = getNamespaceResolverSize(desc);

        assertEquals(sizeBefore, sizeAfter);

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
        xmlMarshaller.marshal(objectToWrite, stream);

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
        
        xmlMarshaller.marshal(objectToWrite, stream);
        xmlMarshaller.setEncoding("US-ASCII");
       
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
            xmlMarshaller.marshal(objectToWrite, result);

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
            xmlMarshaller.marshal(objectToWrite, result);

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

        xmlMarshaller.marshal(objectToWrite, builder);

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
            java.net.URL url = ClassLoader.getSystemResource(resourceName);
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

            InputStream inputStream = ClassLoader.getSystemResourceAsStream(resourceName);
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

}
