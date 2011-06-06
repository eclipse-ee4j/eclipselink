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
 * dmccann - June 17/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.jaxb.JaxbClassLoader;
import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.compiler.Generator;
import org.eclipse.persistence.jaxb.javamodel.reflection.JavaModelImpl;
import org.eclipse.persistence.jaxb.javamodel.reflection.JavaModelInputImpl;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.platform.xml.XMLComparer;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.jaxb.JAXBXMLComparer;
import org.eclipse.persistence.testing.oxm.OXTestCase;
import org.w3c.dom.Document;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.InputSource;

import junit.framework.TestCase;

/**
 * Tests Externalized Metadata functionality.
 *
 */
public class ExternalizedMetadataTestCases extends TestCase {
    protected static String tmpdir = (System.getenv("T_WORK") == null ? "" : (System.getenv("T_WORK") + "/"));
    protected static ClassLoader loader = Thread.currentThread().getContextClassLoader();
    protected static String EMPTY_NAMESPACE = "";
    protected JAXBContext jaxbContext;
    protected DocumentBuilder parser;

    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public ExternalizedMetadataTestCases(String name) {
        super(name);
    }
    
    public void setUp() throws Exception {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        builderFactory.setIgnoringElementContentWhitespace(true);
        parser = builderFactory.newDocumentBuilder();
    }
    
    /**
     * Convenience method that will return the JAXBContext instance created during 
     * the last generateSchema call.
     * 
     * Assumes a prior call to generateSchema has been made.
     * 
     * @return
     */
    public JAXBContext getJAXBContext() {
        return jaxbContext;
    }

    /**
     * Generate the schema(s) for a given set of classes.  Any eclipselink-oxm.xml file(s)
     * found in the package(s) will be applied by default.
     * 
     * @param classes
     * @param expectedSchemaCount
     */
    public MySchemaOutputResolver generateSchema(Class[] classes, int expectedSchemaCount) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        MySchemaOutputResolver outputResolver = new MySchemaOutputResolver();
        try {
            generateSchema(classes, null, outputResolver, classLoader); 
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Schema generation failed unexpectedly: " + ex.toString());
        }
        assertTrue("No schemas were generated", outputResolver.schemaFiles.size() > 0);
        assertTrue("Expected schema generation count to be ["+expectedSchemaCount+"], but was [" + outputResolver.schemaFiles.size() + "]", outputResolver.schemaFiles.size() == expectedSchemaCount);
        return outputResolver;
    }

    /**
     * Generate the schema using the CONTEXT_PATH.
     *
     * @param contextPath
     * @param expectedSchemaCount
     */
    public MySchemaOutputResolver generateSchema(String contextPath, int expectedSchemaCount) {
        MySchemaOutputResolver outputResolver = new MySchemaOutputResolver();
        try {
            generateSchema(contextPath, outputResolver);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Schema generation failed unexpectedly: " + ex.toString());
        }
        assertTrue("No schemas were generated", outputResolver.schemaFiles.size() > 0);
        assertTrue("Expected schema generation count to be ["+expectedSchemaCount+"], but was [" + outputResolver.schemaFiles.size() + "]", outputResolver.schemaFiles.size() == expectedSchemaCount);
        return outputResolver;
    }

    /**
     * Generate the schema using the CONTEXT_PATH.
     *
     * @param contextPath
     * @param expectedSchemaCount
     * @param resolver
     */
    public void generateSchema(String contextPath, int expectedSchemaCount, MyStreamSchemaOutputResolver outputResolver) {
        try {
            generateSchema(contextPath, outputResolver);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Schema generation failed unexpectedly: " + ex.toString());
        }
        assertTrue("No schemas were generated", outputResolver.schemaFiles.size() > 0);
        assertTrue("Expected schema generation count to be ["+expectedSchemaCount+"], but was [" + outputResolver.schemaFiles.size() + "]", outputResolver.schemaFiles.size() == expectedSchemaCount);
    }

    /**
     * Generate the schema(s) for a given context path, and apply the eclipselink-oxm.xml
     * file(s) in the properties map.
     * 
     * @param contextPath
     * @param properties
     * @param expectedSchemaCount
     */
    public MySchemaOutputResolver generateSchema(String contextPath, Map<String, Map<String, Source>> properties, int expectedSchemaCount) {
        MySchemaOutputResolver outputResolver = new MySchemaOutputResolver();
        try {
            generateSchema(contextPath, outputResolver, properties);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Schema generation failed unexpectedly: " + ex.toString());
        }
        assertTrue("No schemas were generated", outputResolver.schemaFiles.size() > 0);
        assertTrue("Expected schema generation count to be ["+expectedSchemaCount+"], but was [" + outputResolver.schemaFiles.size() + "]", outputResolver.schemaFiles.size() == expectedSchemaCount);
        return outputResolver;
    }

    /**
     * Generate the schema(s) for a given set of classes, and apply the eclipselink-oxm.xml 
     * file found on the path.  The eclipselink-oxm.xml will be stored in the property map 
     * using the contextPath as a key (maps package name to xml metadata file).
     * 
     * @param contextPath used as key for storing eclipselink-oxm.xml file Source in properties map
     * @param iStream eclipselink-oxm.xml file as a stream
     * @param expectedSchemaCount
     */
    private MySchemaOutputResolver generateSchema(String contextPath, InputStream iStream, int expectedSchemaCount) {
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put(contextPath, new StreamSource(iStream));
        validateBindingsFileAgainstSchema(iStream);
        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);
        return generateSchema(contextPath, properties, expectedSchemaCount);
    }

    /**
     * Generate the schema(s) for a given set of classes, and apply the eclipselink-oxm.xml 
     * file found on the path.  The eclipselink-oxm.xml will be stored in the property map 
     * using the contextPath as a key (maps package name to xml metadata file).
     * 
     * @param contextPath used as key for storing eclipselink-oxm.xml file Source in properties map
     * @param path eclipselink-oxm.xml file will be searched for on this path
     * @param expectedSchemaCount
     */
    public MySchemaOutputResolver generateSchema(String contextPath, String path, int expectedSchemaCount) {
        String metadataFile = path + "eclipselink-oxm.xml";
        return generateSchemaWithFileName(contextPath, metadataFile, expectedSchemaCount);
    }
    
    public MySchemaOutputResolver generateSchemaWithFileName(String contextPath, String metadataFile, int expectedSchemaCount) {        
        InputStream iStream = loader.getResourceAsStream(metadataFile);
        InputStream iStreamCopy = loader.getResourceAsStream(metadataFile);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + metadataFile + "]");
        }
        validateBindingsFileAgainstSchema(iStreamCopy);
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put(contextPath, new StreamSource(iStream));
        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);
        return generateSchema(contextPath, properties, expectedSchemaCount);
    }
    
    /**
     * Generate the schema(s) for a given set of classes, and apply the eclipselink-oxm.xml 
     * file found on the path.  The eclipselink-oxm.xml will be stored in the property map 
     * using the contextPath as a key (maps package name to xml metadata file).
     * 
     * @param classes
     * @param contextPath used as key for storing eclipselink-oxm.xml file Source in properties map
     * @param iStream eclipselink-oxm.xml file as a stream
     * @param expectedSchemaCount
     */
    private MySchemaOutputResolver generateSchema(Class[] classes, String contextPath, InputStream iStream, int expectedSchemaCount) {
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put(contextPath, new StreamSource(iStream));
        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);
        MySchemaOutputResolver outputResolver = new MySchemaOutputResolver();
        try {
            generateSchema(classes, properties, outputResolver, loader); 
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Schema generation failed unexpectedly: " + ex.toString());
        }
        assertTrue("No schemas were generated", outputResolver.schemaFiles.size() > 0);
        assertTrue("Expected schema generation count to be ["+expectedSchemaCount+"], but was [" + outputResolver.schemaFiles.size() + "]", outputResolver.schemaFiles.size() == expectedSchemaCount);
        return outputResolver;
    }

    /**
     * Generate the schema(s) for a given set of classes, and apply the eclipselink-oxm.xml 
     * file found on the path.  The eclipselink-oxm.xml will be stored in the property map 
     * using the contextPath as a key (maps package name to xml metadata file).
     * 
     * @param classes
     * @param contextPath used as key for storing eclipselink-oxm.xml file Source in properties map
     * @param path eclipselink-oxm.xml file will be searched for on this path
     * @param expectedSchemaCount
     */
    public MySchemaOutputResolver generateSchema(Class[] classes, String contextPath, String path, int expectedSchemaCount) {
        String metadataFile = path + "eclipselink-oxm.xml";
        return generateSchemaWithFileName(classes, contextPath, metadataFile, expectedSchemaCount);
    }

    public void generateSchemaWithFileName(Class[] classes, String contextPath, String metadataFile, int expectedSchemaCount, MyStreamSchemaOutputResolver outputResolver) {
        InputStream iStream = loader.getResourceAsStream(metadataFile);
        InputStream iStreamCopy = loader.getResourceAsStream(metadataFile);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + metadataFile + "]");
        }        
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put(contextPath, new StreamSource(iStream));
        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);
        
        validateBindingsFileAgainstSchema(iStreamCopy);
        
        try {
            generateSchema(classes, properties, outputResolver, loader); 
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Schema generation failed unexpectedly: " + ex.toString());
        }
        if(expectedSchemaCount >0){
            assertTrue("No schemas were generated", outputResolver.schemaFiles.size() > 0);
        }
        assertTrue("Expected schema generation count to be ["+expectedSchemaCount+"], but was [" + outputResolver.schemaFiles.size() + "]", outputResolver.schemaFiles.size() == expectedSchemaCount);
    }
    
    public MySchemaOutputResolver generateSchemaWithFileName(Class[] classes, String contextPath, String metadataFile, int expectedSchemaCount) {
        InputStream iStream = loader.getResourceAsStream(metadataFile);
        InputStream iStreamCopy = loader.getResourceAsStream(metadataFile);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + metadataFile + "]");
        }        
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put(contextPath, new StreamSource(iStream));
        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);
        MySchemaOutputResolver outputResolver = new MySchemaOutputResolver();
        
        validateBindingsFileAgainstSchema(iStreamCopy);
        
        try {
            generateSchema(classes, properties, outputResolver, loader); 
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Schema generation failed unexpectedly: " + ex.toString());
        }
        if(expectedSchemaCount >0){
            assertTrue("No schemas were generated", outputResolver.schemaFiles.size() > 0);
        }
        assertTrue("Expected schema generation count to be ["+expectedSchemaCount+"], but was [" + outputResolver.schemaFiles.size() + "]", outputResolver.schemaFiles.size() == expectedSchemaCount);
        return outputResolver;
    }
    
    /**
     * Generate the schema(s) for a given set of types, and apply the eclipselink-oxm.xml 
     * file found on the path.  The eclipselink-oxm.xml will be stored in the property map 
     * using the contextPath as a key (maps package name to xml metadata file).
     * 
     * @param types
     * @param contextPath used as key for storing eclipselink-oxm.xml file Source in properties map
     * @param path eclipselink-oxm.xml file will be searched for on this path
     * @param expectedSchemaCount
     */
    public MySchemaOutputResolver generateSchema(Type[] types, String contextPath, String path, int expectedSchemaCount) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String metadataFile = path + "eclipselink-oxm.xml";
        
        InputStream iStream = classLoader.getResourceAsStream(metadataFile);
        InputStream iStreamCopy = classLoader.getResourceAsStream(metadataFile);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + metadataFile + "]");
        }
        validateBindingsFileAgainstSchema(iStreamCopy);
        
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put(contextPath, new StreamSource(iStream));
        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);
        MySchemaOutputResolver outputResolver = new MySchemaOutputResolver();
        try {
            generateSchema(types, properties, outputResolver, classLoader); 
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Schema generation failed unexpectedly: " + ex.toString());
        }
        assertTrue("No schemas were generated", outputResolver.schemaFiles.size() > 0);
        assertTrue("Expected schema generation count to be ["+expectedSchemaCount+"], but was [" + outputResolver.schemaFiles.size() + "]", outputResolver.schemaFiles.size() == expectedSchemaCount);
        return outputResolver;
    }

    /**
     * Generate one or more schemas from a context path.
     * 
     * @param contextPath
     * @param outputResolver
     */
    protected void generateSchema(String contextPath, SchemaOutputResolver outputResolver, Map<String, Map<String, Source>> properties) throws Exception {
        try {
            jaxbContext = (JAXBContext) JAXBContextFactory.createContext(contextPath, loader, properties);
            jaxbContext.generateSchema(outputResolver);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generate one or more schemas from a context path.
     * 
     * @param contextPath
     * @param outputResolver
     */
    protected void generateSchema(String contextPath, SchemaOutputResolver outputResolver) throws Exception {
        try {
            jaxbContext = (JAXBContext) JAXBContextFactory.createContext(contextPath, loader);
            jaxbContext.generateSchema(outputResolver);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generate one or more schemas from an array of classes and a Map containing zero or more
     * eclipselink-oxm.xml entries.
     * 
     * @param classesToBeBound
     * @param properties
     * @param outputResolver
     * @param classLoader
     */
    protected void generateSchema(Class[] classesToBeBound, java.util.Map properties, SchemaOutputResolver outputResolver, ClassLoader classLoader) throws Exception {
        try {
            jaxbContext = (JAXBContext) JAXBContextFactory.createContext(classesToBeBound, properties, classLoader);
            jaxbContext.generateSchema(outputResolver);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }
        
    /**
     * Generate one or more schemas from an array of types and a Map containing zero or more
     * eclipselink-oxm.xml entries.
     * 
     * @param typesToBeBound
     * @param properties
     * @param outputResolver
     * @param classLoader
     */
    protected void generateSchema(Type[] typesToBeBound, java.util.Map properties, SchemaOutputResolver outputResolver, ClassLoader classLoader) throws Exception {
        try {
            jaxbContext = (JAXBContext) JAXBContextFactory.createContext(typesToBeBound, properties, classLoader);
            jaxbContext.generateSchema(outputResolver);
        } catch (JAXBException e) {
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
    protected String validateAgainstSchema(String src, String namespace, MySchemaOutputResolver outputResolver) {
        SchemaFactory sFact = SchemaFactory.newInstance(XMLConstants.SCHEMA_URL);
        Schema theSchema;
        try {
            theSchema = sFact.newSchema(outputResolver.schemaFiles.get(namespace));
            Validator validator = theSchema.newValidator();
            StreamSource ss = new StreamSource(new File(src));             
            validator.validate(ss);
        } catch (Exception e) {
            //e.printStackTrace();
            if (e.getMessage() == null) {
                return "An unknown exception occurred.";
            }
            return e.getMessage();
        }
        return null;
    }
        
    /**
     * Validates a given instance doc against a given schema.
     * 
     * @param src
     * @param schema
     */
    protected String validateAgainstSchema(String src, String schema) {
        SchemaFactory sFact = SchemaFactory.newInstance(XMLConstants.SCHEMA_URL);
        Schema theSchema;
        try {
            theSchema = sFact.newSchema(new File(schema));
            Validator validator = theSchema.newValidator();
            StreamSource ss = new StreamSource(new File(src));             
            validator.validate(ss);
        } catch (Exception e) {
            //e.printStackTrace();
            if (e.getMessage() == null) {
                return "An unknown exception occurred.";
            }
            return e.getMessage();
        }
        return null;
    }

    /**
     * Validates a given instance doc against the generated schema.
     * 
     * @param src
     * @param schemaIndex index in output resolver's list of generated schemas
     * @param outputResolver contains one or more schemas to validate against
     */
    protected String validateAgainstSchema(String src, String namespace, MyStreamSchemaOutputResolver outputResolver) {
        SchemaFactory sFact = SchemaFactory.newInstance(XMLConstants.SCHEMA_URL);
        sFact.setResourceResolver(new ResourceResolver(outputResolver));
        Schema theSchema;
        try {
            Writer sw = outputResolver.schemaFiles.get(namespace);
            theSchema = sFact.newSchema(new StreamSource(new StringReader(sw.toString())));
            Validator validator = theSchema.newValidator();
            StreamSource ss = new StreamSource(new File(src));             
            validator.validate(ss);
        } catch (Exception e) {
            //e.printStackTrace();
            if (e.getMessage() == null) {
                return "An unknown exception occurred.";
            }
            return e.getMessage();
        }
        return null;
    }

    /**
     * Class responsible from resolving schema imports during schema
     * validation.
     *
     */
    class ResourceResolver implements LSResourceResolver {
        private MyStreamSchemaOutputResolver oResolver;
        
        public ResourceResolver(MyStreamSchemaOutputResolver resolver) {
            oResolver = resolver;
        }
        public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseUri) {
            return new MyLSInput(namespaceURI, oResolver);
        }
    }

    /**
     * Class which will be returned to from resolveResource() call in 
     * ResourceResolver.
     * 
     */
    class MyLSInput implements LSInput {
        private String sValue;
        private MyStreamSchemaOutputResolver oResolver;

        public MyLSInput(String value, MyStreamSchemaOutputResolver resolver) {
            sValue = value;
            oResolver = resolver;
        }
        public void setSystemId(String arg0) {}
        public void setStringData(String arg0) {}
        public void setPublicId(String arg0) {}
        public void setEncoding(String arg0) {}
        public void setCharacterStream(Reader arg0) {}
        public void setCertifiedText(boolean arg0) {}
        public void setByteStream(InputStream arg0) {}
        public void setBaseURI(String arg0) {}
        public String getSystemId() {return null;}
        public String getStringData() {
            return oResolver.schemaFiles.get(sValue).toString();
        }
        public String getPublicId() {return null;}
        public String getEncoding() {return null;}
        public Reader getCharacterStream() {return null;}
        public boolean getCertifiedText() {return false;}
        public InputStream getByteStream() {return null;}
        public String getBaseURI() {return null;}
    }

    /**
     * Validates a given bindings file against the eclipselink oxm schema.
     * 
     * @param src
     */
    protected void validateBindingsFileAgainstSchema(InputStream src) {
    	String result = null;
        SchemaFactory sFact = SchemaFactory.newInstance(XMLConstants.SCHEMA_URL);
        Schema theSchema;
        try {
            InputStream bindingsFileXSDInputStream = getClass().getClassLoader().getResourceAsStream("eclipselink_oxm_2_3.xsd");
            if (bindingsFileXSDInputStream == null){
                bindingsFileXSDInputStream = getClass().getClassLoader().getResourceAsStream("org/eclipse/persistence/jaxb/eclipselink_oxm_2_3.xsd");
            }
            if (bindingsFileXSDInputStream == null){
                fail("ERROR LOADING eclipselink_oxm_2_3.xsd");
            }
            Source bindingsFileXSDSource = new StreamSource(bindingsFileXSDInputStream);
            theSchema = sFact.newSchema(bindingsFileXSDSource);
            Validator validator = theSchema.newValidator();
                   
            StreamSource ss = new StreamSource(src);             
            validator.validate(ss);
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage() == null) {
                result = "An unknown exception occurred.";
            }
            result = e.getMessage();
        }
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }

    /**
     * Compare two schemas for equality.  
     * 
     * @param testSchema
     * @param controlSchema
     */
    public static void compareSchemas(File testSchema, File controlSchema) {
        if (testSchema == null || controlSchema == null) {
            fail("Can't compare null schema file.");
        }
        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            builderFactory.setIgnoringElementContentWhitespace(true);
            builderFactory.setNamespaceAware(true);
            DocumentBuilder parser = builderFactory.newDocumentBuilder();
            InputStream stream = new FileInputStream(testSchema);
            Document control = parser.parse(stream);
            stream = new FileInputStream(controlSchema);
            Document test = parser.parse(stream);
            JAXBXMLComparer xmlComparer = new JAXBXMLComparer();
            if (!xmlComparer.isSchemaEqual(control, test)) {
                fail("The test schema did not match the control schema.\nEXPECTED:\n" + getFileAsText(controlSchema) + "\nACTUAL:\n" + getFileAsText(testSchema));
            }
        } catch (Exception x) {
            fail("An error occurred during schema comparison: " + x.getMessage());
        }
    }
    
    /**
     * Compare two schemas for equality.  
     * 
     * @param testSchema
     * @param controlSchema
     */
    public static void compareSchemas(String testSchema, File controlSchema) {
        if (testSchema == null || controlSchema == null) {
            fail("Can't compare null schema file.");
        }
        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            builderFactory.setIgnoringElementContentWhitespace(true);
            builderFactory.setNamespaceAware(true);
            DocumentBuilder parser = builderFactory.newDocumentBuilder();
            InputStream stream = new FileInputStream(controlSchema);

            Document control = parser.parse(stream);
            
            InputSource source = new InputSource(new StringReader(testSchema));
            Document test = parser.parse(source);
            
            JAXBXMLComparer xmlComparer = new JAXBXMLComparer();
            if (!xmlComparer.isSchemaEqual(control, test)) {
                fail("The test schema did not match the control schema.\nEXPECTED:\n" + getFileAsText(controlSchema) + "\nACTUAL:\n" + testSchema);
            }
        } catch (Exception x) {
            fail("An error occurred during schema comparison: " + x.getMessage());
        }
    }

    public static boolean compareDocuments(Document ctrlDoc, Document testDoc) {
        return new XMLComparer().isNodeEqual(ctrlDoc, testDoc);
    }
    
    public static String getFileAsText(String filename) {
        return getFileAsText(new File(filename));
    }
    
    public static String getFileAsText(File file) {
        StringBuilder text = new StringBuilder();
        String NL = System.getProperty("line.separator");
        Scanner scanner = null;
        try {
            scanner = new Scanner(new FileInputStream(file));
            while (scanner.hasNextLine()) {
                text.append(scanner.nextLine() + NL);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
        return text.toString();
    }
    
    /**
     * SchemaOutputResolver for writing out the generated schema.  Returns a StreamResult
     * wrapping a File.
     *
     */
    public static class MySchemaOutputResolver extends SchemaOutputResolver {
        // keep a list of processed schemas for the validation phase of the test(s)
        public Map<String, File> schemaFiles;
        
        public MySchemaOutputResolver() {
            schemaFiles = new HashMap<String, File>();
        }
        
        public Result createOutput(String namespaceURI, String suggestedFileName) throws IOException {
            //return new StreamResult(System.out);
            if (namespaceURI == null) {
                namespaceURI = EMPTY_NAMESPACE;
            }
            
            File schemaFile = new File(tmpdir + suggestedFileName);
            schemaFiles.put(namespaceURI, schemaFile);
            return new StreamResult(schemaFile);
        }
    }
    
    /**
     * SchemaOutputResolver for writing out the generated schema.  Returns a StreamResult
     * wrapping a StringWriter.
     *
     */
    public static class MyStreamSchemaOutputResolver extends SchemaOutputResolver {
        // keep a list of processed schemas for the validation phase of the test(s)
        public Map<String, Writer> schemaFiles;
        
        public MyStreamSchemaOutputResolver() {
            schemaFiles = new HashMap<String, Writer>();
        }
        
        public Result createOutput(String namespaceURI, String suggestedFileName) throws IOException {
            //return new StreamResult(System.out);
            if (namespaceURI == null) {
                namespaceURI = EMPTY_NAMESPACE;
            }
            
            StringWriter sw = new StringWriter();
            schemaFiles.put(namespaceURI, sw);
            Result res = new StreamResult(sw);
            res.setSystemId(suggestedFileName);
            return res;
        }
    }

    /**
     * Convenience method that returns a newly created XMLContext based on an array of classes.
     * 
     * @param classes
     * @return
     */
    protected XMLContext createXmlContext(Class[] classes) {
        try {
            ClassLoader classLoader = new JaxbClassLoader(Thread.currentThread().getContextClassLoader());
            Generator generator = new Generator(new JavaModelInputImpl(classes, new JavaModelImpl(classLoader)));
            Project proj = generator.generateProject();
            ConversionManager manager = new ConversionManager();
            manager.setLoader(classLoader);
            for (Iterator<ClassDescriptor> descriptorIt = proj.getOrderedDescriptors().iterator(); descriptorIt.hasNext(); ) {
                ClassDescriptor descriptor = descriptorIt.next();
                if (descriptor.getJavaClass() == null) {
                    descriptor.setJavaClass(manager.convertClassNameToClass(descriptor.getJavaClassName()));
                }
            }
            return new XMLContext(proj, classLoader);
        } catch (Exception e) {
            e.printStackTrace();
            fail("XmlContext creation failed");
        }
        return null;
    }
    
    protected Document getControlDocument(String xmlResource) throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(xmlResource);
        Document document = parser.parse(inputStream);
        OXTestCase.removeEmptyTextNodes(document);
        return document;
    }
    
    /**
     * Convenience method for creating a JAXBContext.  The XML document is
     * validated against the metadata schema.
     * 
     * @param classes
     * @param contextPath
     * @param metadataFile
     * @return
     */
    public JAXBContext createContext(Class[] classes, String contextPath, String metadataFile) throws JAXBException {
        // validate instance document against the metadata schema
        InputStream iStream = loader.getResourceAsStream(metadataFile);
        InputStream iStreamCopy = loader.getResourceAsStream(metadataFile);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + metadataFile + "]");
        }        
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put(contextPath, new StreamSource(iStream));
        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);
        MySchemaOutputResolver outputResolver = new MySchemaOutputResolver();
        
        validateBindingsFileAgainstSchema(iStreamCopy);

        // create the JAXBContext
        jaxbContext = (JAXBContext) JAXBContextFactory.createContext(classes, properties, loader);
        return jaxbContext;
    }
}
