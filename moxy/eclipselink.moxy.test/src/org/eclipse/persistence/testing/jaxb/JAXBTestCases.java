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
package org.eclipse.persistence.testing.jaxb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBUnmarshallerHandler;
import org.eclipse.persistence.jaxb.compiler.Generator;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.platform.xml.SAXDocumentBuilder;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public abstract class JAXBTestCases extends XMLMappingTestCases {
    protected JAXBContext jaxbContext;
    protected Marshaller jaxbMarshaller;
    protected Unmarshaller jaxbUnmarshaller;
    Generator generator;
    protected ClassLoader classLoader;
    protected Source bindingsFileXSDSource;

    public JAXBTestCases(String name) throws Exception {
        super(name);
    }

    public XMLContext getXMLContext(Project project) {
        return new XMLContext(project, classLoader);
    }

    public void setUp() throws Exception {
        setupParser();
        setupControlDocs();

        InputStream bindingsFileXSDInputStream = getClass().getClassLoader().getResourceAsStream("eclipselink_oxm_2_1.xsd");
        if(bindingsFileXSDInputStream == null){
            bindingsFileXSDInputStream = getClass().getClassLoader().getResourceAsStream("xsd/eclipselink_oxm_2_1.xsd");
        }
        if(bindingsFileXSDInputStream == null){
            fail("ERROR LOADING eclipselink_oxm_2_1.xsd");
        }
        bindingsFileXSDSource = new StreamSource(bindingsFileXSDInputStream);
    }

    public void tearDown() {
        super.tearDown();
        jaxbContext = null;
        jaxbMarshaller = null;
        jaxbUnmarshaller = null;
    }

    protected void setProject(Project project) {
        this.project = project;
    }

    public void setClasses(Class[] newClasses) throws Exception {

        classLoader = Thread.currentThread().getContextClassLoader();
        jaxbContext = JAXBContextFactory.createContext(newClasses, null, classLoader);
        xmlContext = ((org.eclipse.persistence.jaxb.JAXBContext)jaxbContext).getXMLContext();
        setProject(xmlContext.getSession(0).getProject());
        jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbUnmarshaller = jaxbContext.createUnmarshaller();

    }

    public void setTypes(Type[] newTypes) throws Exception {

        classLoader = Thread.currentThread().getContextClassLoader();

        Map props = getProperties();
        if(props != null){
            Map overrides = (Map) props.get(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY);
            if(overrides != null){
                Iterator valuesIter = overrides.values().iterator();
                while(valuesIter.hasNext()){
                    Source theSource = (Source) valuesIter.next();
                    validateBindingsFileAgainstSchema(theSource);
                }
            }
        }

        jaxbContext = JAXBContextFactory.createContext(newTypes, props, classLoader);

        xmlContext = ((org.eclipse.persistence.jaxb.JAXBContext)jaxbContext).getXMLContext();
        setProject(xmlContext.getSession(0).getProject());
        jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbUnmarshaller = jaxbContext.createUnmarshaller();
    }

    protected Map getProperties() throws Exception{
        return null;
    }

    public JAXBContext getJAXBContext() {
        return jaxbContext;
    }

    public Marshaller getJAXBMarshaller() {
        return jaxbMarshaller;
    }

    public Unmarshaller getJAXBUnmarshaller() {
        return jaxbUnmarshaller;
    }

    public void testXMLToObjectFromInputStream() throws Exception {
        InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
        Object testObject = jaxbUnmarshaller.unmarshal(instream);
        instream.close();
        xmlToObjectTest(testObject);
    }

    public void testRoundTrip() throws Exception{
        if(isUnmarshalTest()) {
            InputStream instream = null;
            if(writeControlDocumentLocation !=null){
                instream = ClassLoader.getSystemResourceAsStream(writeControlDocumentLocation);
            }else{
                instream = ClassLoader.getSystemResourceAsStream(resourceName);
            }
                Object testObject = jaxbUnmarshaller.unmarshal(instream);
                instream.close();
                xmlToObjectTest(testObject);

                objectToXMLStringWriter(testObject);
        }
    }

    public void testObjectToOutputStream() throws Exception {
        Object objectToWrite = getWriteControlObject();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        XMLDescriptor desc = null;
        if (objectToWrite instanceof XMLRoot) {
            desc = (XMLDescriptor)xmlContext.getSession(0).getProject().getDescriptor(((XMLRoot)objectToWrite).getObject().getClass());
        } else {
            desc = (XMLDescriptor)xmlContext.getSession(0).getProject().getDescriptor(objectToWrite.getClass());
        }

        int sizeBefore = getNamespaceResolverSize(desc);

        jaxbMarshaller.marshal(objectToWrite, stream);

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
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        XMLDescriptor desc = null;
        if (objectToWrite instanceof XMLRoot) {
            desc = (XMLDescriptor)xmlContext.getSession(0).getProject().getDescriptor(((XMLRoot)objectToWrite).getObject().getClass());
        } else {
            desc = (XMLDescriptor)xmlContext.getSession(0).getProject().getDescriptor(objectToWrite.getClass());
        }

        int sizeBefore = getNamespaceResolverSize(desc);
        String originalEncoding = (String)jaxbMarshaller.getProperty(Marshaller.JAXB_ENCODING);
        jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "US-ASCII");
        jaxbMarshaller.marshal(objectToWrite, stream);
        jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, originalEncoding);
        int sizeAfter = getNamespaceResolverSize(desc);

        assertEquals(sizeBefore, sizeAfter);

        InputStream is = new ByteArrayInputStream(stream.toByteArray());
        Document testDocument = parser.parse(is);
        stream.close();
        is.close();

        objectToXMLDocumentTest(testDocument);
    }


    public void testObjectToXMLStringWriter() throws Exception {
        objectToXMLStringWriter(getWriteControlObject());
    }

    @Override
    public void testValidatingMarshal() {
    }

    public void objectToXMLStringWriter(Object objectToWrite) throws Exception {
        StringWriter writer = new StringWriter();

        XMLDescriptor desc = null;
        if (objectToWrite instanceof XMLRoot) {
            desc = (XMLDescriptor)xmlContext.getSession(0).getProject().getDescriptor(((XMLRoot)objectToWrite).getObject().getClass());
        } else {
            desc = (XMLDescriptor)xmlContext.getSession(0).getProject().getDescriptor(objectToWrite.getClass());
        }

        int sizeBefore = getNamespaceResolverSize(desc);

        jaxbMarshaller.marshal(objectToWrite, writer);

        int sizeAfter = getNamespaceResolverSize(desc);

        assertEquals(sizeBefore, sizeAfter);

        StringReader reader = new StringReader(writer.toString());
        InputSource inputSource = new InputSource(reader);
        Document testDocument = parser.parse(inputSource);
        writer.close();
        reader.close();

        objectToXMLDocumentTest(testDocument);
    }

    public void testObjectToXMLStreamWriter() throws Exception {
        if(XML_OUTPUT_FACTORY != null) {
            StringWriter writer = new StringWriter();

            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            factory.setProperty(factory.IS_REPAIRING_NAMESPACES, new Boolean(false));
            XMLStreamWriter streamWriter= factory.createXMLStreamWriter(writer);

            Object objectToWrite = getWriteControlObject();
            XMLDescriptor desc = null;
            if (objectToWrite instanceof XMLRoot) {
                desc = (XMLDescriptor)xmlContext.getSession(0).getProject().getDescriptor(((XMLRoot)objectToWrite).getObject().getClass());
            } else {
                desc = (XMLDescriptor)xmlContext.getSession(0).getProject().getDescriptor(objectToWrite.getClass());
            }

            int sizeBefore = getNamespaceResolverSize(desc);
            jaxbMarshaller.marshal(objectToWrite, streamWriter);

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
        if(XML_OUTPUT_FACTORY != null) {
            StringWriter writer = new StringWriter();

            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            factory.setProperty(factory.IS_REPAIRING_NAMESPACES, new Boolean(false));
            XMLEventWriter eventWriter= factory.createXMLEventWriter(writer);

            Object objectToWrite = getWriteControlObject();
            XMLDescriptor desc = null;
            if (objectToWrite instanceof XMLRoot) {
                desc = (XMLDescriptor)xmlContext.getSession(0).getProject().getDescriptor(((XMLRoot)objectToWrite).getObject().getClass());
            } else {
                desc = (XMLDescriptor)xmlContext.getSession(0).getProject().getDescriptor(objectToWrite.getClass());
            }

            int sizeBefore = getNamespaceResolverSize(desc);
            jaxbMarshaller.marshal(objectToWrite, eventWriter);

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

    public void testObjectToContentHandler() throws Exception {
        SAXDocumentBuilder builder = new SAXDocumentBuilder();
        Object objectToWrite = getWriteControlObject();
        XMLDescriptor desc = null;
        if (objectToWrite instanceof XMLRoot) {
            desc = (XMLDescriptor)xmlContext.getSession(0).getProject().getDescriptor(((XMLRoot)objectToWrite).getObject().getClass());
        } else {
            desc = (XMLDescriptor)xmlContext.getSession(0).getProject().getDescriptor(objectToWrite.getClass());
        }
        int sizeBefore = getNamespaceResolverSize(desc);

        jaxbMarshaller.marshal(objectToWrite, builder);

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
        java.net.URL url = ClassLoader.getSystemResource(resourceName);
        Object testObject = jaxbUnmarshaller.unmarshal(url);
        xmlToObjectTest(testObject);
    }

    public void testXMLToObjectFromXMLStreamReader() throws Exception {
        if(null != XML_INPUT_FACTORY) {
            InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
            XMLStreamReader xmlStreamReader = XML_INPUT_FACTORY.createXMLStreamReader(instream);
            Object testObject = jaxbUnmarshaller.unmarshal(xmlStreamReader);
            instream.close();
            xmlToObjectTest(testObject);
        }
    }

    public void testXMLToObjectFromXMLEventReader() throws Exception {
        if(null != XML_INPUT_FACTORY) {
            InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
            XMLEventReader xmlEventReader = XML_INPUT_FACTORY.createXMLEventReader(instream);
            Object testObject = jaxbUnmarshaller.unmarshal(xmlEventReader);
            instream.close();
            xmlToObjectTest(testObject);
        }
    }

    public void testObjectToXMLDocument() throws Exception {
        Object objectToWrite = getWriteControlObject();
        XMLDescriptor desc = null;
        if (objectToWrite instanceof XMLRoot) {
            desc = (XMLDescriptor)xmlContext.getSession(0).getProject().getDescriptor(((XMLRoot)objectToWrite).getObject().getClass());
        } else {
            desc = (XMLDescriptor)xmlContext.getSession(0).getProject().getDescriptor(objectToWrite.getClass());
        }
        int sizeBefore = getNamespaceResolverSize(desc);
        Document testDocument = XMLPlatformFactory.getInstance().getXMLPlatform().createDocument();
        jaxbMarshaller.marshal(objectToWrite, testDocument);
        int sizeAfter = getNamespaceResolverSize(desc);
        assertEquals(sizeBefore, sizeAfter);
        objectToXMLDocumentTest(testDocument);
    }


    public void xmlToObjectTest(Object testObject) throws Exception {
        log("\n**xmlToObjectTest**");
        log("Expected:");
        log(getReadControlObject().toString());
        log("Actual:");
        log(testObject.toString());

        if ((getReadControlObject() instanceof JAXBElement) && (testObject instanceof JAXBElement)) {
            JAXBElement controlObj = (JAXBElement)getReadControlObject();
            JAXBElement testObj = (JAXBElement)testObject;
            compareJAXBElementObjects(controlObj, testObj);
        } else {
            super.xmlToObjectTest(testObject);
        }
    }

    public void compareJAXBElementObjects(JAXBElement controlObj, JAXBElement testObj) {
        assertEquals(controlObj.getName().getLocalPart(), testObj.getName().getLocalPart());
        assertEquals(controlObj.getName().getNamespaceURI(), testObj.getName().getNamespaceURI());
        assertEquals(controlObj.getDeclaredType(), testObj.getDeclaredType());

        Object controlValue = controlObj.getValue();
        Object testValue = testObj.getValue();

        if(controlValue == null) {
            if(testValue == null){
                return;
            }
            fail("Test value should have been null");
        }else{
            if(testValue == null){
                fail("Test value should not have been null");
            }
        }

        if(controlValue.getClass().isArray()){
            compareArrays(controlValue, testValue);
        }
        else if (controlValue instanceof Collection){
            Collection controlCollection = (Collection)controlValue;
            Collection testCollection = (Collection)testValue;
            Iterator<Object> controlIter = controlCollection.iterator();
            Iterator<Object> testIter = testCollection.iterator();
            assertEquals(controlCollection.size(), testCollection.size());
            while(controlIter.hasNext()){
                Object nextControl = controlIter.next();
                Object nextTest = testIter.next();
                compareValues(nextControl, nextTest);
            }
        }else{
            compareValues(controlValue, testValue);
        }
    }

     protected void compareValues(Object controlValue, Object testValue){
         if(controlValue instanceof Node && testValue instanceof Node) {
             assertXMLIdentical(((Node)controlValue).getOwnerDocument(), ((Node)testValue).getOwnerDocument());
         } else {
             assertEquals(controlValue, testValue);
         }
     }


    protected void compareArrays(Object controlValue, Object testValue) {
        assertTrue("Test array is not an Array", testValue.getClass().isArray());
        int controlSize = Array.getLength(controlValue);
        assertTrue("Control and test arrays are not the same length", controlSize == Array.getLength(testValue));
        for(int x=0; x<controlSize; x++) {
            Object controlItem = Array.get(controlValue, x);
            Object testItem = Array.get(testValue, x);
            if(null == controlItem) {
                assertEquals(null, testItem);
                Class controlItemClass = controlItem.getClass();
                if(controlItemClass.isArray()) {
                    compareArrays(controlItem, testItem);
                } else {
                    assertEquals(controlItem, testItem);
                }
            }
        }
    }

    public void testUnmarshallerHandler() throws Exception {
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        saxParserFactory.setNamespaceAware(true);
        SAXParser saxParser = saxParserFactory.newSAXParser();
        XMLReader xmlReader = saxParser.getXMLReader();

        JAXBUnmarshallerHandler jaxbUnmarshallerHandler = (JAXBUnmarshallerHandler)jaxbUnmarshaller.getUnmarshallerHandler();
        xmlReader.setContentHandler(jaxbUnmarshallerHandler);

        InputStream inputStream = ClassLoader.getSystemResourceAsStream(resourceName);
        InputSource inputSource = new InputSource(inputStream);
        xmlReader.parse(inputSource);

        xmlToObjectTest(jaxbUnmarshallerHandler.getResult());
    }

    public void testSchemaGen(List<InputStream> controlSchemas) throws Exception {
        MySchemaOutputResolver outputResolver = new MySchemaOutputResolver();
        getJAXBContext().generateSchema(outputResolver);

        List<File> generatedSchemas = outputResolver.getSchemaFiles();
        assertEquals(controlSchemas.size(), generatedSchemas.size());

        for(int i=0;i<controlSchemas.size(); i++){
            InputStream nextControlValue = controlSchemas.get(i);
            File nextGeneratedValue =generatedSchemas.get(i);

            assertNotNull("Generated Schema not found.", nextGeneratedValue);

            Document control = parser.parse(nextControlValue);
            Document test = parser.parse(nextGeneratedValue);

            JAXBXMLComparer xmlComparer = new JAXBXMLComparer();
            boolean isEqual = xmlComparer.isSchemaEqual(control, test);
            if(!isEqual){
                logDocument(control);
                logDocument(test);
            }
            assertTrue("generated schema did not match control schema", isEqual);
        }
    }

    public class MySchemaOutputResolver extends SchemaOutputResolver {
        // keep a list of processed schemas for the validation phase of the
        // test(s)
        public List<File> schemaFiles;

        public MySchemaOutputResolver() {
            schemaFiles = new ArrayList<File>();
        }

        public Result createOutput(String namespaceURI, String suggestedFileName)throws IOException {
            File schemaFile = new File(suggestedFileName);
            if(namespaceURI == null){
                namespaceURI ="";
            }
            schemaFiles.add(schemaFile);
            return new StreamResult(schemaFile);
        }

        public List<File> getSchemaFiles() {
            return schemaFiles;
        }
    }

    protected void logDocument(Document document){
       try {
              TransformerFactory transformerFactory = TransformerFactory.newInstance();
              Transformer transformer = transformerFactory.newTransformer();
              DOMSource source = new DOMSource(document);
              StreamResult result = new StreamResult(System.out);
              transformer.transform(source, result);
          } catch (Exception e) {
              e.printStackTrace();
          }
   }

    /**
     * Validates a given instance doc against the generated schema.
     *
     * @param src
     * @param schemaIndex index in output resolver's list of generated schemas
     * @param outputResolver contains one or more schemas to validate against
     */
    protected void validateBindingsFileAgainstSchema(InputStream src) {
        StreamSource ss = new StreamSource(src);
        validateBindingsFileAgainstSchema(ss);
    }

    protected void validateBindingsFileAgainstSchema(Source src) {
        String result = null;
        SchemaFactory sFact = SchemaFactory.newInstance(XMLConstants.SCHEMA_URL);
        Schema theSchema;
        try {
            InputStream bindingsFileXSDInputStream = getClass().getClassLoader().getResourceAsStream("eclipselink_oxm_2_2.xsd");
            if (bindingsFileXSDInputStream == null){
                bindingsFileXSDInputStream = getClass().getClassLoader().getResourceAsStream("xsd/eclipselink_oxm_2_2.xsd");
            }
            if (bindingsFileXSDInputStream == null){
                fail("ERROR LOADING eclipselink_oxm_2_2.xsd");
            }
            Source bindingsFileXSDSource = new StreamSource(bindingsFileXSDInputStream);
            theSchema = sFact.newSchema(bindingsFileXSDSource);
            Validator validator = theSchema.newValidator();
                   
            validator.validate(src);
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage() == null) {
                result = "An unknown exception occurred.";
            }
            result = e.getMessage();
        }
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }


}
