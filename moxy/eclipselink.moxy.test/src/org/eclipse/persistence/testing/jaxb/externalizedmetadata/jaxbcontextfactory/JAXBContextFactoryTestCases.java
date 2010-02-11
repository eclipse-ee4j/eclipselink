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
 * dmccann - June 17/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.stringarray.a.BeanA;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.stringarray.b.BeanB;

/**
 * Tests various JAXBContext creation methods.
 *  
 */
public class JAXBContextFactoryTestCases extends ExternalizedMetadataTestCases {
    private MySchemaOutputResolver outputResolver;
    private static final String CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory";
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/jaxbcontextfactory/";
    private static final String ARRAY_NAMESPACE = "http://jaxb.dev.java.net/array";
    private static final String BEAN_NAMESPACE = "defaultTns";
    
    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public JAXBContextFactoryTestCases(String name) {
        super(name);
    }
    
    /**
     * Tests override via eclipselink-oxm.xml.  Here, the metadata file is not
     * handed in via properties or context path, but looked up by package in
     * the context factory.  An @XmlTransient override will be performed 
     * on Employee.lastName to ensure the xml file was picked up properly.
     * 
     * Positive test.
     */
    public void testLoadXmlFileViaPackage() {
        outputResolver = generateSchema(new Class[] { Employee.class }, CONTEXT_PATH, PATH, 1);
        String src = PATH + "employee.xml";
        String result = validateAgainstSchema(src, EMPTY_NAMESPACE, outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }
        
    /**
     * Tests override via eclipselink-oxm.xml.  Here, the metadata file is not
     * handed in via properties or context path, but looked up by package in
     * the context factory.  An @XmlTransient override will be performed 
     * on Address to ensure the xml file was picked up properly.
     * 
     * 1 x Positive test, 1x Negative test
     */
    public void testLoadMultipleXmlFilesViaSamePackage() {
        outputResolver = generateSchema(new Class[] { Employee.class, Address.class }, CONTEXT_PATH, PATH, 1);
        String src = PATH + "address.xml";
        String result = validateAgainstSchema(src, null, outputResolver);
        // address is set to transient in Xml, should fail
        assertTrue("Schema validation passed unxepectedly", result != null);
        src = PATH + "employee.xml";
        result = validateAgainstSchema(src, EMPTY_NAMESPACE, outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }

    /**
     * Tests override via eclipselink-oxm.xml.  Here, the metadata files are not
     * handed in via properties or context path, but looked up by package in
     * the context factory.  Various overrides will be performed to ensure 
     * the xml files were picked up properly.
     * 
     * 1 x Positive tests, 1x Negative test
     */
    public void testLoadMultipleXmlFilesViaDifferentPackage() {
        outputResolver = generateSchema(new Class[] { Employee.class, Address.class, }, CONTEXT_PATH, PATH, 1);
 
        String src = PATH + "employee.xml";
        String result = validateAgainstSchema(src, EMPTY_NAMESPACE, outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
        src = PATH + "address.xml";
        result = validateAgainstSchema(src, null, outputResolver);
        // address is set to transient in Xml, should fail
        assertTrue("Schema validation passed unxepectedly", result != null);
    }

    /**
     * Test loading metadata via properties map.
     * 
     * Positive test.
     */
    public void testLoadXmlFilesViaProperties() {
        String contextPath = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.properties.foo:org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.properties.bar";
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String metadataFile = PATH + "properties/bar/eclipselink-oxm.xml";
        
        InputStream iStream = classLoader.getResourceAsStream(metadataFile);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + metadataFile + "]");
        }
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.properties.bar", new StreamSource(iStream));

        metadataFile = PATH + "properties/foo/eclipselink-oxm.xml";
        
        iStream = classLoader.getResourceAsStream(metadataFile);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + metadataFile + "]");
        }
        metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.properties.foo", new StreamSource(iStream));

        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);
        
        outputResolver = generateSchema(contextPath, properties, 1);
        
        // validate schema against control schema
        compareSchemas(new File(PATH + "properties/schema.xsd"), outputResolver.schemaFiles.get(EMPTY_NAMESPACE));

        // validate instance docs against schema
        String src = PATH + "properties/bar/employee.xml";
        String result = validateAgainstSchema(src, EMPTY_NAMESPACE, outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
        src = PATH + "properties/foo/home-address.xml";
        result = validateAgainstSchema(src, EMPTY_NAMESPACE, outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }
    
    public void testArrayOfTypes() {
        try {
            Field addressesField = org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.arrayoftypes.Employee.class.getDeclaredField("addresses");
            Type[] types = new Type[2];
            types[0] = addressesField.getGenericType(); 
            types[1] = org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.arrayoftypes.Employee.class;
            String contextPath = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.arrayoftypes";
            String path = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/jaxbcontextfactory/arrayoftypes/";
            outputResolver = generateSchema(types, contextPath, path, 1);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        } 
    }

    /**
     * Test processing an eclipselink-oxm.xml file with no JavaTypes.
     * 
     */
    public void testBindingsFileWithNoTypes() {
        String metadataFile = PATH + "eclipselink-oxm-no-types.xml";
        generateSchemaWithFileName(new Class[] {}, "org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory", metadataFile, 0);               
    }
    
    /**
     * Test passing a String[] into the context factory via Type[].
     * 
     */
    public void testStringArrayInTypesToBeBound() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        String metadataFile = PATH + "stringarray/a/eclipselink-oxm.xml";
        InputStream iStream = classLoader.getResourceAsStream(metadataFile);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + metadataFile + "]");
        }
        
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.stringarray.a", new StreamSource(iStream));

        metadataFile = PATH + "stringarray/b/eclipselink-oxm.xml";
        iStream = classLoader.getResourceAsStream(metadataFile);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + metadataFile + "]");
        }
        metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.stringarray.b", new StreamSource(iStream));

        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);
        
        MySchemaOutputResolver outputResolver = new MySchemaOutputResolver();
        JAXBContext jaxbContext;
        try {
            Type[] types = { 
                    BeanA.class, 
                    BeanB.class,
                    String[].class
                };

            jaxbContext = (JAXBContext) JAXBContextFactory.createContext(types, properties, loader);
            jaxbContext.generateSchema(outputResolver);
            String controlSchema = PATH + "stringarray/bean_schema.xsd";
            compareSchemas(outputResolver.schemaFiles.get(BEAN_NAMESPACE), new File(controlSchema));
            controlSchema = PATH + "stringarray/string_array_schema.xsd";
            compareSchemas(outputResolver.schemaFiles.get(ARRAY_NAMESPACE), new File(controlSchema));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    /**
     * Test passing a String[] into the context factory via Class[].
     * 
     */
    public void testStringArrayInClassesToBeBound() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        String metadataFile = PATH + "stringarray/a/eclipselink-oxm.xml";
        InputStream iStream = classLoader.getResourceAsStream(metadataFile);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + metadataFile + "]");
        }
        
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.stringarray.a", new StreamSource(iStream));

        metadataFile = PATH + "stringarray/b/eclipselink-oxm.xml";
        iStream = classLoader.getResourceAsStream(metadataFile);
        if (iStream == null) {
            fail("Couldn't load metadata file [" + metadataFile + "]");
        }
        metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.jaxbcontextfactory.stringarray.b", new StreamSource(iStream));

        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);
        
        MySchemaOutputResolver outputResolver = new MySchemaOutputResolver();
        JAXBContext jaxbContext;
        try {
            Class<?>[] types = { 
                    BeanA.class, 
                    BeanB.class,
                    String[].class
                };

            jaxbContext = (JAXBContext) JAXBContextFactory.createContext(types, properties, loader);
            jaxbContext.generateSchema(outputResolver);
            String controlSchema = PATH + "stringarray/bean_schema.xsd";
            compareSchemas(outputResolver.schemaFiles.get(BEAN_NAMESPACE), new File(controlSchema));
            controlSchema = PATH + "stringarray/string_array_schema.xsd";
            compareSchemas(outputResolver.schemaFiles.get(ARRAY_NAMESPACE), new File(controlSchema));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}