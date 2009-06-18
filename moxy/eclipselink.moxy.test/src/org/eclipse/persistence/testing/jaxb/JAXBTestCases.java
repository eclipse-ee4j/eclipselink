/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.JAXBElement;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.oxm.XMLUnmarshallerHandler;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.platform.xml.SAXDocumentBuilder;
import org.eclipse.persistence.jaxb.compiler.Generator;
import org.eclipse.persistence.jaxb.javamodel.reflection.JavaModelImpl;
import org.eclipse.persistence.jaxb.javamodel.reflection.JavaModelInputImpl;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.eclipse.persistence.jaxb.*;
import org.eclipse.persistence.internal.jaxb.*;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public abstract class JAXBTestCases extends XMLMappingTestCases {
    Class[] classes;

    protected JAXBContext jaxbContext;
    protected Marshaller jaxbMarshaller;
    protected Unmarshaller jaxbUnmarshaller;
    Generator generator;
    protected JaxbClassLoader classLoader;
    
    public JAXBTestCases(String name) throws Exception {
        super(name);
    }

    public XMLContext getXMLContext(Project project) {
        return new XMLContext(project, classLoader);
    }

    public void setUp() throws Exception {
    	setupParser();        
        setupControlDocs();    	  
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
        this.classes = newClasses;
        this.classLoader = new JaxbClassLoader(Thread.currentThread().getContextClassLoader());        
        generator = new Generator(new JavaModelInputImpl(classes, new JavaModelImpl(this.classLoader)));
        
        Project proj = generator.generateProject();
        proj.convertClassNamesToClasses(classLoader);
        setProject(proj);
        xmlContext = getXMLContext(proj);
        // need to make sure that the java class is set properly on each 
        // descriptor when using java classname - req'd for JOT api implementation 
        ConversionManager manager = new ConversionManager();
        manager.setLoader(classLoader);
        for (Iterator<ClassDescriptor> descriptorIt = proj.getOrderedDescriptors().iterator(); descriptorIt.hasNext(); ) {
            ClassDescriptor descriptor = descriptorIt.next();
            if (descriptor.getJavaClass() == null) {
                descriptor.setJavaClass(manager.convertClassNameToClass(descriptor.getJavaClassName()));
            }
        }
        setProject(proj);
        jaxbContext = new org.eclipse.persistence.jaxb.JAXBContext(xmlContext, generator, newClasses);
        jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        
    }
    
    public void setTypes(Type[] newTypes) throws Exception {
       // this.classes = newClasses;
        this.classLoader = new JaxbClassLoader(Thread.currentThread().getContextClassLoader());
        generator = new Generator(new JavaModelInputImpl(newTypes, new JavaModelImpl(this.classLoader)));
        
        Project proj = generator.generateProject();
        proj.convertClassNamesToClasses(classLoader);
        setProject(proj);
        xmlContext = getXMLContext(proj);
        // need to make sure that the java class is set properly on each 
        // descriptor when using java classname - req'd for JOT api implementation 
        ConversionManager manager = new ConversionManager();
        manager.setLoader(classLoader);
        for (Iterator<ClassDescriptor> descriptorIt = proj.getOrderedDescriptors().iterator(); descriptorIt.hasNext(); ) {
            ClassDescriptor descriptor = descriptorIt.next();
            if (descriptor.getJavaClass() == null) {
                descriptor.setJavaClass(manager.convertClassNameToClass(descriptor.getJavaClassName()));
            }
        }
        setProject(proj);
        jaxbContext = new org.eclipse.persistence.jaxb.JAXBContext(xmlContext, generator, newTypes);
        jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        
    }
    
    

    public Class[] getClasses() {
        return classes;
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
    
    public void testObjectToXMLStringWriter() throws Exception {
        StringWriter writer = new StringWriter();
        Object objectToWrite = getWriteControlObject();
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
        
        Object controlValue = controlObj.getValue();
        Object testValue = testObj.getValue();
        
        if(controlValue.getClass().isArray()){
        	if(testValue.getClass().isArray()){
        		if(controlValue.getClass().getComponentType().isPrimitive()){
        			comparePrimitiveArrays(controlValue, testValue);
        		}else{        		
	        		assertEquals(((Object[])controlValue).length,((Object[])testValue).length);
	        		for(int i=0; i<((Object[])controlValue).length-1; i++){
	        			assertEquals(((Object[])controlValue)[i], ((Object[])testValue)[i]);
	        		}
        		}
        	}else{
        		fail("Expected an array value but was an " + testValue.getClass().getName());
        	}
        }else{        
        	assertEquals(controlValue, testValue);
        }
    }
    
    protected void comparePrimitiveArrays(Object controlValue, Object testValue){
    	fail("NEED TO COMPARE PRIMITIVE ARRAYS");
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
    
    public List<File> generateSchema() throws Exception{    
	    MySchemaOutputResolver outputResolver = new MySchemaOutputResolver();
	    jaxbContext.generateSchema(outputResolver);
	    return outputResolver.getSchemaFiles();
    }
    
	public class MySchemaOutputResolver extends SchemaOutputResolver {
	    	// keep a list of processed schemas for the validation phase of the test(s)
	    	public List<File> schemaFiles;
	    	
	    	public MySchemaOutputResolver() {
	    		schemaFiles = new ArrayList<File>();
	    	}
	    	
	        public Result createOutput(String namespaceURI, String suggestedFileName) throws IOException {
	        	File schemaFile = new File(suggestedFileName);
	        	schemaFiles.add(schemaFile);
	            return new StreamResult(schemaFile);
	        }
	        
	        public List<File> getSchemaFiles(){
	        	return schemaFiles;
	        }
	    }

    
    
}
