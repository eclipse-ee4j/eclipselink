/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - August 5/2010 - 2.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.xmltransformation;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.internal.jaxb.JaxbClassLoader;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.compiler.Generator;
import org.eclipse.persistence.jaxb.javamodel.reflection.JavaModelImpl;
import org.eclipse.persistence.jaxb.javamodel.reflection.JavaModelInputImpl;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings;

import junit.framework.TestCase;


public class JAXBContextTransformationMappingTestCases extends TestCase{
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/xmltransformation/";
	
	private static final String METADATA_FILE_NO_CLASS_OR_METHOD = PATH + "no-class-or-method.xml";
    private static final String METADATA_FILE_CLASS_AND_METHOD = PATH + "both-class-and-method.xml";
    private static final String METADATA_FILE_BAD_METHOD = PATH + "bad-method.xml";
    private static final String METADATA_FILE_BAD_CLASS = PATH + "bad-class.xml";
 
    /**
     * Test exception handling:  in this case both a transformer class and
     * method name have been set.  An exception should be thrown.
     * 
     * Negative test.
     */
    public void testBothClassAndMethod() {
    	InputStream inputStream = ClassLoader.getSystemResourceAsStream(METADATA_FILE_CLASS_AND_METHOD);

		HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
	    metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.xmltransformation", new StreamSource(inputStream));
	    Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
	    properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);		
     
        try {
        	JAXBContext jaxbContext = (JAXBContext) JAXBContextFactory.createContext(new Class[]{Employee.class}, properties, Thread.currentThread().getContextClassLoader());
        } catch (JAXBException e) {
            return;
        }
        fail("The expected exception was never thrown.");
    }
    
    /**
     * Test exception handling:  in this case no transformer class or
     * method name has been set.  An exception should be thrown.
     * 
     * Negative test.
     */
    public void testNoClassOrMethod() {
    	InputStream inputStream = ClassLoader.getSystemResourceAsStream(METADATA_FILE_NO_CLASS_OR_METHOD);

		HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
	    metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.xmltransformation", new StreamSource(inputStream));
	    Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
	    properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);		
         	
       try {
        	JAXBContext jaxbContext = (JAXBContext) JAXBContextFactory.createContext(new Class[]{Employee.class}, properties, Thread.currentThread().getContextClassLoader());
            
        } catch (JAXBException e) {
            return;
        }
        fail("The expected exception was never thrown.");
    }
    
    /**
     * Test exception handling:  in this case the method name that has been 
     * set is invalid (wrong number or type of params. An exception should 
     * be thrown.
     * 
     * Negative test.
     */
    
    public void testInvalidMethod() {
    	InputStream inputStream = ClassLoader.getSystemResourceAsStream(METADATA_FILE_BAD_METHOD);

		HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
	    metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.xmltransformation", new StreamSource(inputStream));
	    Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
	    properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);		
    
        int exceptionCount = 0;
        try {
        	JAXBContext jaxbContext = (JAXBContext) JAXBContextFactory.createContext(new Class[]{Employee.class}, properties, Thread.currentThread().getContextClassLoader());
            
        } catch (JAXBException e) {
            exceptionCount++;
        }
        assertTrue("The expected exception was never thrown.", exceptionCount > 0);
        exceptionCount--;
        // test exception from SchemaGenerator
        try {
            Map<String, XmlBindings> bindings = JAXBContextFactory.getXmlBindingsFromProperties(properties, Thread.currentThread().getContextClassLoader());
            JavaModelInputImpl jModelInput = new JavaModelInputImpl(new Class[]{Employee.class}, new JavaModelImpl(new JaxbClassLoader(Thread.currentThread().getContextClassLoader(), new Class[]{Employee.class})));
            Generator generator = new Generator(jModelInput, bindings, Thread.currentThread().getContextClassLoader(), "");
            generator.generateSchema();
        } catch (Exception e) {
            exceptionCount++;
        }
        assertTrue("The expected exception was never thrown.", exceptionCount > 0);
    }
    
    /**
     * Test exception handling:  in this case an invalid transformer class
     * has been set.  An exception should be thrown.
     * 
     * Negative test.
     */
    
    public void testInvalidTransformerClass() {
    	InputStream inputStream = ClassLoader.getSystemResourceAsStream(METADATA_FILE_BAD_CLASS);

		HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
	    metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.xmltransformation", new StreamSource(inputStream));
	    Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
	    properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);		
    
        int exceptionCount = 0;
        // test exception from MappingsGenerator
        try {
        	JAXBContext jaxbContext = (JAXBContext) JAXBContextFactory.createContext(new Class[]{Employee.class}, properties, Thread.currentThread().getContextClassLoader());

        } catch (JAXBException e) {
            exceptionCount++;
        }
        assertTrue("The expected exception was never thrown.", exceptionCount > 0);
        exceptionCount--;
        // test exception from SchemaGenerator
        try {
            Map<String, XmlBindings> bindings = JAXBContextFactory.getXmlBindingsFromProperties(properties, Thread.currentThread().getContextClassLoader());
            JavaModelInputImpl jModelInput = new JavaModelInputImpl(new Class[]{Employee.class}, new JavaModelImpl(new JaxbClassLoader(Thread.currentThread().getContextClassLoader(), new Class[]{Employee.class})));
            Generator generator = new Generator(jModelInput, bindings, Thread.currentThread().getContextClassLoader(), "");
            generator.generateSchema();
        } catch (Exception e) {
            exceptionCount++;
        }
        assertTrue("The expected exception was never thrown.", exceptionCount > 0);
    }
    
}
