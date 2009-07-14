/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
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
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases;

/**
 * Tests loading one or more eclipselink-oxm.xml files via package, when none are
 * found in the properties map or on a context path. *
 */
public class JAXBContextFactoryTestCases extends ExternalizedMetadataTestCases {
    private MySchemaOutputResolver outputResolver;
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/jaxbcontextfactory/";
    
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
        outputResolver = generateSchema(new Class[] { Employee.class }, 1);
        String src = PATH + "employee.xml";
        String result = validateAgainstSchema(src, 0, outputResolver);
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
        outputResolver = generateSchema(new Class[] { Employee.class, Address.class }, 1);
        String src = PATH + "address.xml";
        String result = validateAgainstSchema(src, 0, outputResolver);
        // address is set to transient in Xml, should fail
        assertTrue("Schema validation passed unxepectedly", result != null);
        src = PATH + "employee.xml";
        result = validateAgainstSchema(src, 0, outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }

    /**
     * Tests override via eclipselink-oxm.xml.  Here, the metadata files are not
     * handed in via properties or context path, but looked up by package in
     * the context factory.  Various overrides will be performed to ensure 
     * the xml files were picked up properly.
     * 
     * 2 x Positive tests, 1x Negative test
     */
    public void testLoadMultipleXmlFilesViaDifferentPackage() {
        outputResolver = generateSchema(new Class[] { Employee.class, Address.class, }, 1);
        String src = PATH + "myotherclass.xml";
        String result = validateAgainstSchema(src, 0, outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
        src = PATH + "employee.xml";
        result = validateAgainstSchema(src, 0, outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
        src = PATH + "address.xml";
        result = validateAgainstSchema(src, 0, outputResolver);
        // address is set to transient in Xml, should fail
        assertTrue("Schema validation passed unxepectedly", result != null);
    }

    /**
     * Test loading metadata via properties and context path.  Here, the eclipselink-oxm.xml file
     * for the bar package will be in the properties map, and the eclipselink-oxm.xml file for the
     * foo package will be looked up via context path.
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
        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);
        
        outputResolver = generateSchema(contextPath, properties, 1);
        
        // validate schema against control schema
        String result = compareSchemas(new File(PATH + "properties/schema.xsd"), outputResolver.schemaFiles.get(0));
        assertFalse(result, result.length() > 0);

        // validate instance docs against schema
        String src = PATH + "properties/bar/employee.xml";
        result = validateAgainstSchema(src, 0, outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
        src = PATH + "properties/foo/home-address.xml";
        result = validateAgainstSchema(src, 0, outputResolver);
        assertTrue("Schema validation failed unxepectedly: " + result, result == null);
    }
}
